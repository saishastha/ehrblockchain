/* SPDX-License-Identifier: Apache-2.0 */
package main.java.com.example.incentivemechanism;

import com.google.gson.Gson;

/**
 * JSON Parser utilities
 *
 * @author kehm
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
}
