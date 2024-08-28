package com.ericsson.nms.rv.taf.test.netsim;

import com.ericsson.nms.rv.taf.test.apache.operators.dto.Response;

public class NetsimResponse implements Response {

    private boolean success;
    private String errorMessage;
    private String authErrorCode;
    private String output;
    private int statusCode;
    private String timeAlarmSent;
    private long timeAlarmSentMillis;

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }

    public String getAuthErrorCode() {
        return authErrorCode;
    }

    public void setAuthErrorCode(final String authErrorCode) {
        this.authErrorCode = authErrorCode;
    }

    public void setOutput(final String output) {
        this.output = output;
    }

    public String getOutput() {
        return output;
    }

    public void setTimeAlarmSent(final String timeAlarmSent) {
        this.timeAlarmSent = timeAlarmSent;
    }

    public String getTimeAlarmSent() {
        return timeAlarmSent;
    }

    public long getTimeAlarmSentMillis() {
        return timeAlarmSentMillis;
    }

    public void setTimeAlarmSentMillis(final long timeAlarmSentMillis) {
        this.timeAlarmSentMillis = timeAlarmSentMillis;
    }
}
