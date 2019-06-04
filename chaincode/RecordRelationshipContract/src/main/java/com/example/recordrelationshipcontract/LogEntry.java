/* SPDX-License-Identifier: Apache-2.0 */
package main.java.com.example.recordrelationshipcontract;

/**
 * Class to represent an entry in the log
 *
 * @author kehm
 */
public class LogEntry {

    private final String event; // action executed on the record
    private final String entry; // new entry written to the record
    private final String invokerId; // id of the entity who invoked the event
    private final String timestamp; // time of event

    /**
     * Constructor for LogEntry object
     *
     * @param event Action
     * @param entry New entry written to the record
     * @param invokerId Invoker id
     * @param timestamp Time of action
     */
    public LogEntry(String event, String entry, String invokerId, String timestamp) {
        this.event = event;
        this.entry = entry;
        this.invokerId = invokerId;
        this.timestamp = timestamp;
    }

    /**
     * Returns action
     *
     * @return Action
     */
    public String getEvent() {
        return this.event;
    }

    /**
     * Return entry
     *
     * @return Entry made to the record
     */
    public String getEntry() {
        return this.entry;
    }

    /**
     * Returns invoker id
     *
     * @return Invoker id
     */
    public String getInvokerId() {
        return this.invokerId;
    }

    /**
     * Returns timestamp
     *
     * @return timestamp
     */
    public String getTimestamp() {
        return this.timestamp;
    }
}
