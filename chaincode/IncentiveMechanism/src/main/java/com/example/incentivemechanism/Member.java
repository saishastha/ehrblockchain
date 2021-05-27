/* SPDX-License-Identifier: Apache-2.0 */
package main.java.com.example.incentivemechanism;

/**
 * Class to represent a member of the blockchain network
 *
 * @author karthik
 */
public class Member {

    private int significance; // significance associated with the member
    private String lastUpdate; // timestamp for last significance update

    /**
     * Constructor for Member object
     */
    public Member() {
    }

    /**
     * Constructor for Member object
     *
     * @param significance Initial significance
     * @param lastUpdate Timestamp for last endorsement
     */
    public Member(int significance, String lastUpdate) {
        this.significance = significance;
        this.lastUpdate = lastUpdate;
    }

    /**
     * Increase significance associated with the member
     *
     * @param significance Significance
     */
    public void increaseSignificance(int significance) {
        this.significance += significance;
    }

    /**
     * Set timestamp for last significance update
     *
     * @param lastUpdate Timestamp for last endorsement
     */
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Get significance
     *
     * @return Significance
     */
    public int getSignificance() {
        return significance;
    }

    /**
     * Get timestamp for last update
     *
     * @return Timestamp for last update
     */
    public String getLastUpdate() {
        return lastUpdate;
    }
}
