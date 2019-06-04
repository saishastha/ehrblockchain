/*
Copyright IBM Corp., DTCC All Rights Reserved.
Modifications by kehm

SPDX-License-Identifier: Apache-2.0
 */
package main.java.com.example.incentivemechanism;

import com.google.gson.Gson;
import java.time.Instant;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

/**
 * Class to handle the amount of significance associated with providers
 *
 */
public class IncentiveMechanism extends ChaincodeBase {

    private static final Log LOG = LogFactory.getLog(IncentiveMechanism.class);

    @Override
    public Response init(ChaincodeStub stub) {
        try {
            LOG.info("Init Incentivemechanism");
            String func = stub.getFunction();
            if (!func.equals("init")) {
                return newErrorResponse("Function not supported!");
            }
            List<String> args = stub.getParameters();
            if (!args.isEmpty()) {
                if ((args.size() % 2) != 0) {
                    return newErrorResponse("Expecting an even number of arguments");
                }
                int size = args.size() / 2;
                for (int i = 0; i < size; i++) {
                    stub.putStringState(args.get(i).toLowerCase(), JSONParser.getJSON(new Member(Integer.parseInt(args.get(i + size)), stub.getTxTimestamp().toString())));
                }
            }
            return newSuccessResponse("Init finished successfully");
        } catch (NumberFormatException e) {
            return newErrorResponse(e);
        }
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            LOG.info("Invoke Incentivemechanism");
            String func = stub.getFunction();
            List<String> args = stub.getParameters();
            if (func.equals("update")) {
                return update(stub, args);
            }
            if (func.equals("delete")) {
                return delete(stub, args);
            }
            if (func.equals("query")) {
                return query(stub, args);
            }
            if (func.equals("selectEndorser")) {
                return selectEndorser(stub);
            }
            return newErrorResponse("Invalid invoke function name. Expecting one of: [\"update\", \"delete\", \"query\", \"selectEndorser\"]");
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    /**
     * Increase provider significance
     *
     * @param stub Interface between chaincode and peer
     * @param args Provider ID and Significance increase
     * @return Error or Success Response
     */
    private Response update(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            // expecting 2 arguments in the following order: providerId, significanceIncrease
            return newErrorResponse("Incorrect number of arguments. Expecting 2");
        }
        String providerId = args.get(0).toLowerCase();
        int significance = Integer.parseInt(args.get(1));
        if ((stub.getStringState(providerId) == null) || (stub.getStringState(providerId).equalsIgnoreCase(""))) {
            // provider has no associated significance
            stub.putStringState(providerId, JSONParser.getJSON(new Member(significance, stub.getTxTimestamp().toString())));
            LOG.info(String.format("No significance associated with provider %s. Created new entry with total significance %s", providerId, significance));
        } else {
            // provider is already associated with a significance value
            Member member = getFromJSON(stub.getStringState(providerId));
            member.increaseSignificance(significance);
            member.setLastUpdate(stub.getTxTimestamp().toString());
            stub.putStringState(providerId, JSONParser.getJSON(member));
            LOG.info(String.format("Significance associated with provider %s increased by %s. New total significance is %s", providerId, significance, member.getSignificance()));
        }
        return newSuccessResponse("Invoke Success");
    }

    /**
     * Delete provider significance
     *
     * @param stub Interface between chaincode and peer
     * @param args Provider ID
     * @return Error or Success Response
     */
    private Response delete(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            // expecting 1 argument: providerId
            return newErrorResponse("Incorrect number of arguments. Expecting 1");
        }
        stub.delState(args.get(0)); // delete significance associated with provider
        LOG.info(String.format("Deleted significance associated with Provider ID %s", args.get(0)));
        return newSuccessResponse("Delete Success");
    }

    /**
     * Query for provider significance
     *
     * @param stub Interface between chaincode and peer
     * @param args Provider ID
     * @return Error or Success Response with integer
     */
    private Response query(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            // expecting 1 argument: providerId
            return newErrorResponse("Incorrect number of arguments. Expecting 1");
        }
        if ((stub.getStringState(args.get(0))) == null || (stub.getStringState(args.get(0)).equalsIgnoreCase(""))) {
            return newErrorResponse(String.format("No significance associated with Provider ID %s", args.get(0)));
        }
        LOG.info(String.format("Significance associated with Provider ID %s is %s", args.get(0), stub.getStringState(args.get(0))));
        return newSuccessResponse("Query Success", Integer.toString(getFromJSON(stub.getStringState(args.get(0))).getSignificance()).getBytes());
    }

    /**
     * Selects endorser organization based on significance (excludes the
     * organization associated with the invoker and the organization that was
     * last chosen)
     *
     * @param stub Interface between chaincode and peer
     * @return Error or Success Response with selected provider
     */
    private Response selectEndorser(ChaincodeStub stub) {
        String msp = new String(stub.getCreator()).split("MSP")[0].replaceFirst("\\n", "").replaceFirst("\\f", "").replaceAll("[\u0000-\u001f]", "");
        // get all provider-significance pairs
        QueryResultsIterator<KeyValue> stateByRange = stub.getQueryResult("{\n"
                + "   \"selector\": {\n"
                + "      \"_id\": {\n"
                + "         \"$gt\": null\n"
                + "      }\n"
                + "   }\n"
                + "}");
        KeyValue select = null; // selected endorser
        long time = 0; // time since last endorsement
        // find the provider with the lowest significance (excluding the invoker organization)
        for (KeyValue kv : stateByRange) {
            Member member = getFromJSON(new String(kv.getValue()));
            long elapsedTime = stub.getTxTimestamp().getEpochSecond() - Instant.parse(member.getLastUpdate()).getEpochSecond();
            if (((select == null) || ((member.getSignificance() < getFromJSON(new String(select.getValue())).getSignificance()) && (time < 600)) || (elapsedTime >= 600) && (elapsedTime > time)) && (!kv.getKey().equalsIgnoreCase(msp))) {
                // if provider has the lowest significance or the longest amount of time since the last endorsement (and at least 10 min)
                select = kv;
                time = elapsedTime;
            }
        }
        if (select == null) {
            LOG.error("Could not select next endorser");
            return newErrorResponse("Query returned no results");
        }
        LOG.info("Selected " + select.getKey() + " as the next endorser");
        return newSuccessResponse("Query Success", select.getKey().getBytes());
    }

    /**
     * Gets object from JSON string
     *
     * @param json JSON string
     * @return RecordRelationshipContractInstance object
     */
    private Member getFromJSON(String json) {
        Gson gson = new Gson();
        return (Member) gson.fromJson(json, Member.class);
    }

    /**
     * Main method
     *
     * @param args Arguments
     */
    public static void main(String[] args) {
        LOG.info("IncentiveMechanism main method called");
        new IncentiveMechanism().start(args);
    }
}
