package com.ericsson.nms.rv.taf.test.monitoring.cases.dto;

/**
 * Created by ejocott on 03/09/2014.
 */
public class MonitoringResponse {

    private boolean success;
    private String errorMessage;
    private String authErrorCode;
    private String output;
    private int statusCode;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getAuthErrorCode() {
        return authErrorCode;
    }

    public void setAuthErrorCode(String authErrorCode) {
        this.authErrorCode = authErrorCode;
    }

    public void setOutput(String output) { this.output = output; }

    public String getOutput(){return output; }
}
