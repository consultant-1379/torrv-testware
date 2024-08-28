package com.ericsson.nms.rv.taf.test.shm.operators;

import com.ericsson.cifwk.taf.tools.http.HttpResponse;

/**
 * Created by ewandaf on 09/06/14.
 */
public class ShmResponse {
    private boolean success;
    private String body;
    private String error;
    private HttpResponse httpResponse;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    private int responseCode;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ShmResponse() {

    }

    public ShmResponse(HttpResponse response) {
        this.httpResponse = response;
        final int responseCode = response.getResponseCode().getCode();
        final String body = response.getBody();
        this.responseCode = responseCode;
        this.body = body;
        if (responseCode == 200) {
            this.success = true;
        } else {
            this.success = false;
            this.error = body;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
