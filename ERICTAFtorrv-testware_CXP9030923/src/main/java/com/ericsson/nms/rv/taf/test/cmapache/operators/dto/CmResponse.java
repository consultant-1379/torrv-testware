package com.ericsson.nms.rv.taf.test.cmapache.operators.dto;


public class CmResponse {
    private boolean success;
    private String errorMessage;
    private String body;
    private int statusCode;
    private int count;

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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getCount() {return count;}

    public void setCount(int count) {this.count = count;}
}
