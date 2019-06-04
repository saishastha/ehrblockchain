/* SPDX-License-Identifier: Apache-2.0 */
package main.java.com.example.recordrelationshipcontract;

/**
 * Enum class for entity types
 *
 * @author kehm
 */
public enum Entity {
    CLIENT("CLIENT"),
    MSP("MSP");

    private final String entity;

    Entity(String entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return this.entity;
    }
}
