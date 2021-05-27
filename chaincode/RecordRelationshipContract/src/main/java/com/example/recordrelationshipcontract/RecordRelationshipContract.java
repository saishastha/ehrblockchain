/*
Copyright IBM Corp., DTCC All Rights Reserved.
Modifications by karthik

SPDX-License-Identifier: Apache-2.0
 */
package main.java.com.example.recordrelationshipcontract;

import java.util.List;
import java.util.ArrayList;
import java.util.Base64;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

/**
 * Class to handle Record Relationship Contract invocation
 */
public class RecordRelationshipContract extends ChaincodeBase {

    private static final Log LOG = LogFactory.getLog(RecordRelationshipContract.class);

    @Override
    public Response init(ChaincodeStub stub) {
        try {
            LOG.info("Init RecordRelationshipContract");
            String func = stub.getFunction();
            if (!func.equals("init")) {
                return newErrorResponse("Function not supported!");
            }
            List<String> args = stub.getParameters();
            if (!args.isEmpty()) {
                newErrorResponse("Incorrect number of arguments. Expecting 0");
            }
            return newSuccessResponse("Init finished successfully");
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            LOG.info("Invoke RecordRelationshipContract");
            String func = stub.getFunction();
            List<String> args = stub.getParameters();
            if (func.equals("create")) {
                return create(stub, args);
            }
            if (func.equals("log")) {
                return log(stub, args);
            }
            if (func.equals("update")) {
                return update(stub, args);
            }
            if (func.equals("delete")) {
                return delete(stub, args);
            }
            if (func.equals("query")) {
                return query(stub, args);
            }
            return newErrorResponse("Invalid invoke function name. Expecting one of: [\"create\", \"log\", \"delete\", \"query\"]");
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    /**
     * Create new Record Relationship Contract
     *
     * @param stub Interface between chaincode and peer
     * @param args RRC Reference, UserId and initial Significance
     * @return Error or Success Response
     */
    private Response create(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3) {
            // expecting 3 arguments in the following order: rrcRef, userId, initialSignificance
            return newErrorResponse("Incorrect number of arguments. Expecting 3");
        }
        if (!(args.get(1).matches("[0-9]+")) && !(args.get(1).length() == 11)) {
            // expecting user id to be of length 11 and digits only
            return newErrorResponse("User ID format is not valid");
        }
        // get arguments
        String rrcRef = args.get(0);
        String userId = args.get(1);
        int initialSignificance = Integer.parseInt(args.get(2));
        String timestamp = stub.getTxTimestamp().toString();
        String creatorCert = Base64.getEncoder().encodeToString(stub.getCreator());
        String msp = new String(stub.getCreator()).split("MSP")[0].replaceFirst("\\n", "").replaceFirst("\\f", "").replaceAll("[\u0000-\u001f]", "");
        // create new RRC
        RRCInstance rrc = new RRCInstance(creatorCert, msp, initialSignificance);
        LogEntry entry = new LogEntry(Event.CREATE.toString(), "", Base64.getEncoder().encodeToString(stub.getCreator()), timestamp);
        rrc.addLogEntry(entry);
        stub.putStringState(rrcRef, JSONParser.getJSON(rrc));
        LOG.info(String.format("Created new RRC with ref %s for UserID %s with Provider %s", rrcRef, userId, msp));
        // update corresponding Summary Contract (return error if user already has an RRC with the provider)
        List<byte[]> invokeArgs = new ArrayList<>();
        invokeArgs.add("add".getBytes());
        invokeArgs.add(userId.getBytes());
        invokeArgs.add(rrcRef.getBytes());
        invokeArgs.add(msp.getBytes());
        Response response = stub.invokeChaincode("SummaryContract", invokeArgs, "providerschannel");
        if (response.getStatusCode() == 500) {
            return newErrorResponse("Invoke Failed. User already has an RRC with the provider.");
        }
        invokeArgs.clear();
        // update provider significance based on RRC value
        invokeArgs.add("update".getBytes());
        invokeArgs.add(msp.getBytes());
        invokeArgs.add(Integer.toString(rrc.getSignificance()).getBytes());
        stub.invokeChaincode("IncentiveMechanism", invokeArgs, "providerschannel");
        LOG.info("Increased significance for " + msp + " by " + rrc.getSignificance());
        rewardEndorser(stub, 100);
        return newSuccessResponse("Invoke Success");
    }

    /**
     * Add new log to the RRC
     *
     * @param stub Interface between chaincode and peer
     * @param args RRC Reference, SQL Action and Entry
     * @return Error or Success Response
     */
    private Response log(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3) {
            // expecting 3 arguments in the following order: rrcRef, SQLAction, entry
            return newErrorResponse("Incorrect number of arguments. Expecting 3");
        }
        LogEntry entry = new LogEntry(args.get(1), args.get(2), Base64.getEncoder().encodeToString(stub.getCreator()), stub.getTxTimestamp().toString());
        String rrcJSON = stub.getStringState(args.get(0));
        RRCInstance rrc = getFromJSON(rrcJSON);
        rrc.addLogEntry(entry); // add new log entry to the RRC
        if (!entry.getEvent().equals(Event.READ.toString())) {
            // for each edit to the record, increase its significance with 10
            rrc.addSignificance(10);
            //update last edit of RRC in Summary Contract
            List<byte[]> invokeArgs = new ArrayList<>();
            String invoke = "update";
            invokeArgs.add(invoke.getBytes());
            invokeArgs.add(args.get(0).getBytes());
            stub.invokeChaincode("SummaryContract", invokeArgs, "providerschannel");
        }
        stub.putStringState(args.get(0), JSONParser.getJSON(rrc)); // put updated RRC back in state
        LOG.info(String.format("Added new log entry to RRC ref %s", args.get(0)));
        rewardEndorser(stub, 100);
        return newSuccessResponse("Log Success", JSONParser.getJSON(entry).getBytes());
    }

    /**
     * Grant or revoke access to RRC
     *
     * @param stub Interface between chaincode and peer
     * @param args RRC Reference, client ID, flag (0 for revoke, 1 for grant),
     * flag (0 for client, 1 for MSP) and access type (READ or WRITE)
     * @return Error or Success Response
     */
    private Response update(ChaincodeStub stub, List<String> args) {
        if (args.size() != 5) {
            // expecting 5 arguments in the following order: rrcRef, clientID, flag (0 for revoke, 1 for grant), flag (0 for client, 1 for MSP), accessType (READ or WRITE)
            return newErrorResponse("Incorrect number of arguments. Expecting 5");
        }
        String rrcJSON = stub.getStringState(args.get(0));
        RRCInstance rrc = getFromJSON(rrcJSON);
        String creator = rrc.getCreator().split(",")[0];
        // if invoker does not have CREATE access or invoker is trying to edit his own ACL, deny edit
        if ((!rrc.hasAccess(Base64.getEncoder().encodeToString(stub.getCreator()), Entity.CLIENT, Event.CREATE)) || (creator.equals(args.get(1)))) {
            return newErrorResponse("Edit denied");
        }
        String event = "";
        Entity entity;
        if (args.get(3).equals("0")) {
            entity = Entity.CLIENT;
        } else {
            entity = Entity.MSP;
        }
        switch (args.get(2)) {
            case "0":
                // grant permission
                if (rrc.hasAccess(args.get(1), entity, Event.valueOf(args.get(4)))) {
                    return newErrorResponse("Client already has permission");
                }
                rrc.grant(args.get(1), entity, Event.valueOf(args.get(4)));
                event = Event.GRANT.toString();
                break;
            case "1":
                // revoke permission
                if (!rrc.hasAccess(args.get(1), entity, Event.valueOf(args.get(4)))) {
                    return newErrorResponse("Client does not have this permission");
                }
                rrc.revoke(args.get(1), entity, Event.valueOf(args.get(4)));
                event = Event.REVOKE.toString();
                break;
        }
        // create log entry
        String edit = args.get(4) + " access for " + args.get(1);
        LogEntry entry = new LogEntry(event, edit, Base64.getEncoder().encodeToString(stub.getCreator()), stub.getTxTimestamp().toString());
        rrc.addLogEntry(entry);
        // write to state database
        stub.putStringState(args.get(0), JSONParser.getJSON(rrc));
        rewardEndorser(stub, 100);
        return newSuccessResponse("Update Success");
    }

    /**
     * Delete RRC and corresponding reference in Summary Contract
     *
     * @param stub Interface between chaincode and peer
     * @param args RRC reference
     * @return Error or Success Response
     */
    private Response delete(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            // expecting 1 argument: rrcRef
            return newErrorResponse("Incorrect number of arguments. Expecting 1");
        }
        String rrcJSON = stub.getStringState(args.get(0));
        RRCInstance rrc = getFromJSON(rrcJSON);
        String creator = rrc.getCreator().split(",")[0];
        if (!rrc.hasAccess(creator, Entity.CLIENT, Event.CREATE)) {
            return newErrorResponse("Delete denied");
        }
        stub.delState(args.get(0)); // delete RRC
        List<byte[]> invokeArgs = new ArrayList<>(); // arguments to invoke Summary Contract with
        String invoke = "deleteReference";
        invokeArgs.add(invoke.getBytes());
        invokeArgs.add(args.get(0).getBytes());
        stub.invokeChaincode("SummaryContract", invokeArgs, "providerschannel"); // delete reference in Summary Contract
        LOG.info(String.format("Deleted RRC with ref %s", args.get(0)));
        rewardEndorser(stub, 100);
        return newSuccessResponse("Delete Success");
    }

    /**
     * Query for RecordRelationshipInstance object
     *
     * @param stub Interface between chaincode and peer
     * @param args RRC Reference and Override flag (1 for override)
     * @return Error or Success Response with JSON representation
     */
    private Response query(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            // expecting 2 argument: rrcRef, override
            return newErrorResponse("Incorrect number of arguments. Expecting 2");
        }
        if ((stub.getStringState(args.get(0))) == null || (stub.getStringState(args.get(0)).equalsIgnoreCase(""))) {
            return newErrorResponse(String.format("RRC with ref %s does not exist", args.get(0)));
        }
        RRCInstance rrc = getFromJSON(stub.getStringState(args.get(0)));
        String msp = new String(stub.getCreator()).split("MSP")[0].replaceFirst("\\n", "").replaceFirst("\\f", "").replaceAll("[\u0000-\u001f]", "");
        LogEntry entry;
        if ((rrc.hasAccess(Base64.getEncoder().encodeToString(stub.getCreator()), Entity.CLIENT, Event.READ)) || (rrc.hasAccess(msp, Entity.MSP, Event.READ))) {
            // if client is authorized in ACL
            entry = new LogEntry(Event.READ.toString(), "", Base64.getEncoder().encodeToString(stub.getCreator()), stub.getTxTimestamp().toString());
        } else if (args.get(1).equalsIgnoreCase("1")) {
            // if client has asked for override
            entry = new LogEntry(Event.OVERRIDE.toString(), Event.OVERRIDE.toString() + " " + Event.READ.toString(), Base64.getEncoder().encodeToString(stub.getCreator()), stub.getTxTimestamp().toString());
            LOG.info(String.format("ACL override initiated by %s", Base64.getEncoder().encodeToString(stub.getCreator())));
        } else {
            return newErrorResponse(String.format("Client %s is not authorized to read this RRC", Base64.getEncoder().encodeToString(stub.getCreator())));
        }
        rrc.addLogEntry(entry);
        stub.putStringState(args.get(0), JSONParser.getJSON(rrc));
        rewardEndorser(stub, 100);
        return newSuccessResponse("Query Success", stub.getStringState(args.get(0)).getBytes());
    }

    /**
     * Update significance for endorser organization
     *
     * @param stub Interface between chaincode and peer
     * @param significance Significance increase
     */
    private void rewardEndorser(ChaincodeStub stub, int significance) {
        List<byte[]> invokeArgs = new ArrayList<>();
        invokeArgs.clear();
        invokeArgs.add("selectEndorser".getBytes());
        Response response = stub.invokeChaincode("IncentiveMechanism", invokeArgs, "providerschannel");
        byte[] endorser = response.getPayload();
        invokeArgs.clear();
        invokeArgs.add("update".getBytes());
        invokeArgs.add(endorser);
        invokeArgs.add(Integer.toString(significance).getBytes());
        stub.invokeChaincode("IncentiveMechanism", invokeArgs, "providerschannel");
        LOG.info("Increased significance for " + new String(endorser) + " by " + significance);
    }

    /**
     * Gets object from JSON string
     *
     * @param json JSON string
     * @return RecordRelationshipContractInstance object
     */
    private RRCInstance getFromJSON(String json) {
        Gson gson = new Gson();
        return (RRCInstance) gson.fromJson(json, RRCInstance.class);
    }

    /**
     * Main method
     *
     * @param args Arguments
     */
    public static void main(String[] args) {
        LOG.info("RecordRelationshipContract main method called");
        new RecordRelationshipContract().start(args);
    }
}
