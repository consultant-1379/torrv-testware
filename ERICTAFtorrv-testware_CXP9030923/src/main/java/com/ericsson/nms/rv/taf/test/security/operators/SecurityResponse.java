package com.ericsson.nms.rv.taf.test.security.operators;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.ericsson.cifwk.taf.tools.http.HttpResponse;

/**
 * Created by ewandaf on 20/06/14.
 */
public class SecurityResponse {

    public SecurityResponse() {

    }

    public SecurityResponse(HttpResponse httpResponse) {
        this(httpResponse, 200);
    }

    public SecurityResponse(HttpResponse httpResponse, int expectedCode) {
        this.httpResponse = httpResponse;
        final int responseCode = httpResponse.getResponseCode().getCode();
        final String responseBody = httpResponse.getBody();

        if (responseCode == expectedCode) {
            this.success = true;
            if (responseBody != null) {
                final Object respsonseObject = JSONValue.parse(responseBody);
                if (respsonseObject instanceof JSONObject) {
                    final JSONObject responseJSONObject = (JSONObject) respsonseObject;
                    this._id = this.getValue(responseJSONObject, "_id");
                }
            }
        } else {
            this.success = false;
            this.error = responseBody;
        }
    }

    public SecurityResponse(JSONObject jsonObject) {
        if (jsonObject != null && jsonObject.get("error") == null) {
            this.success = true;
        } else {
            this.success = false;
            this.error = jsonObject.toString();
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public HttpResponse getHttpResponse() {
        return this.httpResponse;
    }

    private String getValue(JSONObject jsonObject, String key) {
        String result = null;
        if (jsonObject != null) {
            final Object valueObject = jsonObject.get(key);
            if (valueObject != null) {
                if (valueObject instanceof String) {
                    result = (String) valueObject;
                } else {
                    result = String.valueOf(valueObject);
                }
            }

        }
        return result;
    }

    private String _id;
    private HttpResponse httpResponse;
    private boolean success;
    private String error;

}
