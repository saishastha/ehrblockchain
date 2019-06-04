/*
Copyright IBM Corp., DTCC All Rights Reserved.
Modifications by kehm

SPDX-License-Identifier: Apache-2.0
 */
package main.java.com.example.summarycontract;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

/**
 * Class to handle Summary Contract invocation
 */
public class SummaryContract extends ChaincodeBase {

    private static final Log LOG = LogFactory.getLog(SummaryContract.class);

    @Override
    public Response init(ChaincodeStub stub) {
        try {
            LOG.info("Init SummaryContract");
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
            LOG.info("Invoke SummaryContract");
            String func = stub.getFunction();
            List<String> args = stub.getParameters();
            if (func.equals("add")) {
                return add(stub, args);
            }
            if (func.equals("update")) {
                return update(stub, args);
            }
            if (func.equals("delete")) {
                return delete(stub, args);
            }
            if (func.equals("deleteReference")) {
                return deleteReference(stub, args);
            }
            if (func.equals("query")) {
                return query(stub, args);
            }
            return newErrorResponse("Function not supported! Supported functions are \"create\", \"update\", \"delete\", \"deleteReference\" or \"query\"");
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    /**
     * Create a Summary Contract for the user if it does not already exist and
     * add RRC reference to the contract
     *
     * @param stub Interface between chaincode and peer
     * @param args UserId, RRCRef and ProviderId
     * @return Error or Success Response
     */
    private Response add(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3) {
            // expecting 3 arguments in the following order: userId, rrcRef, providerId
            return newErrorResponse("Incorrect number of arguments. Expecting 3");
        }
        // get arguments
        SCInstance sc;
        String userId = args.get(0);
        String rrcRef = args.get(1);
        String providerId = args.get(2);
        String timestamp = stub.getTxTimestamp().toString();
        // check if the user already has a Summary Contract and if the user already has an RRC with the provider
        if ((stub.getStringState(userId) == null) || (stub.getStringState(userId).equalsIgnoreCase(""))) {
            sc = new SCInstance(rrcRef, providerId, timestamp);
            stub.putStringState(userId, JSONParser.getJSON(sc));
            LOG.info(String.format("SummaryContract for UserID %s not found. Created new SummaryContract: %s", userId, JSONParser.getJSON(sc)));
        } else if (JSONParser.getFromJSON(stub.getStringState(userId)).getReferences().values().stream().anyMatch((s) -> (s[0].equalsIgnoreCase(providerId)))) {
            return newErrorResponse(String.format("UserID %s already has an RRC with ProviderID %s", userId, providerId));
        } else {
            sc = JSONParser.getFromJSON(stub.getStringState(userId));
            sc.addReference(providerId, rrcRef, timestamp);
            stub.putStringState(userId, JSONParser.getJSON(sc));
            LOG.info(String.format("Summary Contract already exists. No existing RRC with ProviderID %s for UserID %s was found. Added new RRCRef: %s", providerId, userId, rrcRef));
        }
        return newSuccessResponse("Invoke Success");
    }

    /**
     * Update last edit timestamp of the selected RRC
     *
     * @param stub Interface between chaincode and peer
     * @param args RRC Reference
     * @return Error or Success Response
     */
    private Response update(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            // expecting 1 argument: rrcRef
            return newErrorResponse("Incorrect number of arguments. Expecting 1");
        }
        // get arguments
        SCInstance sc;
        String rrcRef = args.get(0);
        String timestamp = stub.getTxTimestamp().toString();
        String userId = getUserId(stub, rrcRef);
        // update last edit timestamp
        sc = JSONParser.getFromJSON(stub.getStringState(userId));
        sc.updateLastEdit(rrcRef, timestamp);
        stub.putStringState(userId, JSONParser.getJSON(sc));
        // check for update in provider's total significance
        List<byte[]> invokeArgs = new ArrayList<>();
        invokeArgs.add("update".getBytes());
        invokeArgs.add(sc.getReference(rrcRef)[0].getBytes());
        invokeArgs.add("10".getBytes());
        stub.invokeChaincode("IncentiveMechanism", invokeArgs, "providerschannel");
        LOG.info(String.format("Updated last edit for UserID %s RRC %s", userId, rrcRef));
        return newSuccessResponse("Invoke Success");
    }

    /**
     * Return user id associated with the RRC reference
     *
     * @param rrcRef RRC Reference
     * @return User ID associated with the RRC
     */
    private String getUserId(ChaincodeStub stub, String rrcRef) {
        String userId = null;
        QueryResultsIterator<KeyValue> stateByRange = stub.getQueryResult("{\n"
                + "   \"selector\": {\n"
                + "      \"_id\": {\n"
                + "         \"$gt\": null\n"
                + "      }\n"
                + "   }\n"
                + "}");
        for (KeyValue kv : stateByRange) {
            SCInstance sc = JSONParser.getFromJSON(kv.getStringValue());
            try {
                sc.getReference(rrcRef);
                userId = kv.getKey();
            } catch (NullPointerException e) {
            }
        }
        return userId;
    }

    /**
     * Delete Summary Contract associated with the user
     *
     * @param stub Interface between chaincode and peer
     * @param args UserId
     * @return Error or Success Response
     */
    private Response delete(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            // expecting 1 argument: userId
            return newErrorResponse("Incorrect number of arguments. Expecting 1");
        }
        stub.delState(args.get(0));
        LOG.info(String.format("Deleted Summary Contract for UserID %s", args.get(0)));
        return newSuccessResponse("Delete Success");
    }

    /**
     * Delete reference to user's RRC with a specified provider
     *
     * @param stub Interface between chaincode and peer
     * @param args RRC Reference
     * @return Error or Success Response
     */
    private Response deleteReference(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            // expecting 1 argument: rrcRef
            return newErrorResponse("Incorrect number of arguments. Expecting 1");
        }
        String rrcRef = args.get(0);
        String userId = getUserId(stub, rrcRef);
        if (userId == null) {
            return newErrorResponse(String.format("RRC ref %s is not associated with any Summary Contract", rrcRef));
        }
        SCInstance sc = JSONParser.getFromJSON(stub.getStringState(userId));
        sc.removeReference(rrcRef);
        stub.putStringState(userId, JSONParser.getJSON(sc));
        LOG.info(String.format("Deleted reference to RRC %s for UserID %s", rrcRef, userId));
        return newSuccessResponse("Delete Success");
    }

    /**
     * Query for SummaryContractInstance object
     *
     * @param stub Interface between chaincode and peer
     * @param args UserId
     * @return Error or Success Response with JSON representation
     */
    private Response query(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            // expecting 1 argument: userId
            return newErrorResponse("Incorrect number of arguments. Expecting 1");
        }
        if ((stub.getStringState(args.get(0))) == null || (stub.getStringState(args.get(0)).equalsIgnoreCase(""))) {
            // if user has no summary contract
            return newErrorResponse(String.format("Summary Contract for UserID %s does not exist", args.get(0)));
        }
        return newSuccessResponse("Query Success", stub.getStringState(args.get(0)).getBytes());
    }

    /**
     * Main method
     *
     * @param args Arguments
     */
    public static void main(String[] args) {
        LOG.info("SummaryContract main method called");
        new SummaryContract().start(args);
    }
}
