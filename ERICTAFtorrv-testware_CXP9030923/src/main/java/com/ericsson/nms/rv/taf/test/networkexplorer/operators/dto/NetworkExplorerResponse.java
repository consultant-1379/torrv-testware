package com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto;

import com.ericsson.nms.rv.taf.test.apache.operators.dto.Response;

/**
 * Created by epaulki.
 */
public class NetworkExplorerResponse implements Response{

    private boolean success;
    private String errorMessage;
    private int statusCode;
    private String body;

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
}
