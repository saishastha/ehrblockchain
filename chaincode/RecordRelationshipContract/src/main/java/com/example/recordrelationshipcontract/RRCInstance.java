/* SPDX-License-Identifier: Apache-2.0 */
package main.java.com.example.recordrelationshipcontract;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent a Record Relationship Contract instance
 *
 * @author karthik
 */
public class RRCInstance {

    private Map<String, List<Event>> acl; // access control list
    private int significance; // significance associated with the RRC
    private List<LogEntry> log; // logs of all read/write events to the record

    /**
     * Constructor for RRCInstance object
     */
    public RRCInstance() {
    }

    /**
     * Constructor for RRCInstance object
     *
     * @param invoker The client invoking the RRC creation
     * @param mspId MSP (provider) issuing client's certificate
     * @param significance Initial significance value for the record
     *
     */
    public RRCInstance(String invoker, String mspId, int significance) {
        this.significance = significance;
        this.acl = new HashMap<>();
        grant(invoker, Entity.CLIENT, Event.READ); // adding RRC creator to the ACL
        grant(invoker, Entity.CLIENT, Event.WRITE);
        grant(invoker, Entity.CLIENT, Event.CREATE);
        grant(mspId, Entity.MSP, Event.READ); // giving read access to all clients in the MSP
        this.log = new ArrayList<>();
    }

    /**
     * Returns true if the entity is authorized to perform the specified action
     * on the record
     *
     * @param id Entity id
     * @param entity Type of entity
     * @param action Action event
     * @return True if authorized
     */
    public boolean hasAccess(String id, Entity entity, Event action) {
        return this.acl.entrySet().stream().anyMatch((e) -> (e.getKey().equals(id + "," + entity.toString()) && e.getValue().contains(action)));
    }

    /**
     * Adds a new client entity to the list of authorized entities
     *
     * @param entityId Entity id
     * @param type Entity type (client or MSP)
     * @param e Types of access
     */
    public void grant(String entityId, Entity type, Event e) {
        ArrayList<Event> events;
        if (this.acl.containsKey(entityId + "," + type.toString())) {
            events = (ArrayList<Event>) this.acl.get(entityId + "," + type.toString());
        } else {
            events = new ArrayList<>();
        }
        events.add(e);
        this.acl.put(entityId + "," + type.toString(), events);
    }

    /**
     * Removes access type for specified client
     *
     * @param entityId Entity id
     * @param type Entity type (client or MSP)
     * @param e Types of access
     */
    public void revoke(String entityId, Entity type, Event e) {
        this.acl.entrySet().stream().map((entry) -> {
            if (entry.getKey().equals(entityId + "," + type.toString()) && entry.getValue().contains(e)) {
                entry.getValue().remove(e);
            }
            return entry;
        }).filter((entry) -> (entry.getValue().isEmpty())).forEachOrdered((entry) -> {
            this.acl.remove(entry.getKey());
        });
    }

    /**
     * Adds a new log entry to the log
     *
     * @param entry New log entry
     */
    public void addLogEntry(LogEntry entry) {
        this.log.add(entry);
    }

    /**
     * Adds value to the total significance
     *
     * @param significance Significance value
     */
    public void addSignificance(int significance) {
        this.significance += significance;
    }

    /**
     * Returns access control list
     *
     * @return Access Control List
     */
    public Map<String, List<Event>> getACL() {
        return this.acl;
    }

    /**
     * Returns log
     *
     * @return Log
     */
    public List<LogEntry> getLog() {
        return this.log;
    }

    /**
     * Gets significance value
     *
     * @return Significance value
     */
    public int getSignificance() {
        return this.significance;
    }

    /**
     * Gets entity object of the RRC creator
     *
     * @return RRC creator
     */
    public String getCreator() {
        for (Map.Entry<String, List<Event>> entry : this.acl.entrySet()) {
            if (entry.getValue().contains(Event.CREATE)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
