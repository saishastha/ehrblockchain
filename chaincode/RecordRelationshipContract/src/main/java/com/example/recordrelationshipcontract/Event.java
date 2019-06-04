/* SPDX-License-Identifier: Apache-2.0 */
package main.java.com.example.recordrelationshipcontract;

/**
 * Enum class for events
 *
 * @author kehm
 */
public enum Event {
    READ("READ"),
    WRITE("WRITE"),
    CREATE("CREATE"),
    GRANT("GRANT"),
    REVOKE("REVOKE"),
    OVERRIDE("OVERRIDE");

    private final String event;

    Event(String event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return this.event;
    }
}
