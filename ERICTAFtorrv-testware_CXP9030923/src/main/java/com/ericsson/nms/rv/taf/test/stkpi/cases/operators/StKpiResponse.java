/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.rv.taf.test.stkpi.cases.operators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StKpiResponse implements Serializable {

    /**
     * In this case success is true when the command has been called and gives
     * back a valid response. It does not meant the test has been successful.
     */
    private boolean success;
    private final List<String> errorMessages;
    private String output;
    private long networkSyncTimeSecs;
    private int totalNodes;
    private int createdNodes;
    private int syncedNodes;
    private float minSyncTime;
    private float avgSyncTime;
    private float maxSyncTime;
    private String timeAlarmSent;

    public StKpiResponse() {
        errorMessages = new ArrayList<String>();
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success
     *            the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the errorMessage
     */
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    /**
     * @param errorMessage
     *            the errorMessage to set
     */
    public void addErrorMessage(String errorMessage) {
        this.errorMessages.add(errorMessage);
    }

    /**
     * @return the output
     */
    public String getOutput() {
        return output;
    }

    /**
     * @param output
     *            the output to set
     */
    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * @return the networkSyncTimeMillis
     */
    public long getNetworkSyncTimeSecs() {
        return networkSyncTimeSecs;
    }

    /**
     * @param networkSyncTimeMillis
     *            the networkSyncTimeMillis to set
     */
    public void setNetworkSyncTimeSecs(long networkSyncTimeMillis) {
        this.networkSyncTimeSecs = networkSyncTimeMillis;
    }

    /**
     * @return the totalNodes
     */
    public int getTotalNodes() {
        return totalNodes;
    }

    /**
     * @param totalNodes
     *            the totalNodes to set
     */
    public void setTotalNodes(int totalNodes) {
        this.totalNodes = totalNodes;
    }

    /**
     * @return the createdNodes
     */
    public int getCreatedNodes() {
        return createdNodes;
    }

    /**
     * @param createdNodes
     *            the createdNodes to set
     */
    public void setCreatedNodes(int createdNodes) {
        this.createdNodes = createdNodes;
    }

    /**
     * @return the syncedNodes
     */
    public int getSyncedNodes() {
        return syncedNodes;
    }

    /**
     * @param syncedNodes
     *            the syncedNodes to set
     */
    public void setSyncedNodes(int syncedNodes) {
        this.syncedNodes = syncedNodes;
    }

    /**
     * @return the minSyncTime
     */
    public float getMinSyncTime() {
        return minSyncTime;
    }

    /**
     * @param minSyncTime
     *            the minSyncTime to set
     */
    public void setMinSyncTime(float minSyncTime) {
        this.minSyncTime = minSyncTime;
    }

    /**
     * @return the avgSyncTime
     */
    public float getAvgSyncTime() {
        return avgSyncTime;
    }

    /**
     * @param avgSyncTime
     *            the avgSyncTime to set
     */
    public void setAvgSyncTime(float avgSyncTime) {
        this.avgSyncTime = avgSyncTime;
    }

    /**
     * @return the maxSyncTime
     */
    public float getMaxSyncTime() {
        return maxSyncTime;
    }

    /**
     * @param maxSyncTime
     *            the maxSyncTime to set
     */
    public void setMaxSyncTime(float maxSyncTime) {
        this.maxSyncTime = maxSyncTime;
    }

    /**
     * @param timeAlarmSent
     * @return the time alarm was sent from netsim at
     */
    public String getTimeAlarmSent() {
        return timeAlarmSent;
    }

    /**
     * @param the
     *            timeAlarmSent
     */
    public void setTimeAlarmSent(String timeAlarmSent) {
        this.timeAlarmSent = timeAlarmSent;
    }
}
