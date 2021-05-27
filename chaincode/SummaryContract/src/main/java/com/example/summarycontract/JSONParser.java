/* SPDX-License-Identifier: Apache-2.0 */
package main.java.com.example.summarycontract;

import com.google.gson.Gson;

/**
 * JSON Parser utilities
 *
 * @author karthik
 */
public class JSONParser {

    /**
     * Get JSON representation of the RRC
     *
     * @param object Object to parse
     * @return JSON representation of the object
     */
    public static String getJSON(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    /**
     * Get object from JSON string
     *
     * @param json JSON string
     * @return SummaryContractInstance object
     */
    public static SCInstance getFromJSON(String json) {
        Gson gson = new Gson();
        return (SCInstance) gson.fromJson(json, SCInstance.class);
    }
}
