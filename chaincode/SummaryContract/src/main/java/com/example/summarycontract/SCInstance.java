/* SPDX-License-Identifier: Apache-2.0 */
package main.java.com.example.summarycontract;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent a Summary Contract instance
 *
 * @author kehm
 */
public class SCInstance {

    private Map<String, String[]> references; // map of RRC references

    /**
     * Constructor for SCInstance object
     */
    public SCInstance() {
    }

    /**
     * Constructor for SCInstance object
     *
     * @param rrcRef Initial reference to RRC
     * @param providerId Provider id
     * @param timestamp Timestamp
     */
    public SCInstance(String rrcRef, String providerId, String timestamp) {
        this.references = new HashMap<>();
        addReference(providerId, rrcRef, timestamp);
    }

    /**
     * Update last edit timestamp of provider's RRC
     *
     * @param reference Reference
     * @param lastEdit New last edit
     */
    public void updateLastEdit(String reference, String lastEdit) {
        String[] rrcReference = (String[]) this.references.get(reference);
        rrcReference[1] = lastEdit;
        this.references.put(reference, rrcReference);
    }

    /**
     * Add new RRC reference to reference map
     *
     * @param providerId Provider id
     * @param rrcRef RRC reference
     * @param timestamp Timestamp
     */
    public void addReference(String providerId, String rrcRef, String timestamp) {
        this.references.put(rrcRef, new String[]{providerId, timestamp});
    }

    /**
     * Remove RRC reference for selected provider
     *
     * @param reference Reference
     */
    public void removeReference(String reference) {
        this.references.remove(reference);
    }

    /**
     * Get RRC reference for selected provider
     *
     * @param reference Reference
     * @return Array with RRC reference and last edit timestamp
     * @throws NullPointerException If RRC does not exist
     */
    public String[] getReference(String reference) throws NullPointerException {
        return (String[]) this.references.get(reference);
    }

    /**
     * Get RRC reference map
     *
     * @return RRC reference map
     */
    public Map<String, String[]> getReferences() {
        return this.references;
    }
}
