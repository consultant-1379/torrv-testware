package com.ericsson.nms.rv.taf.test.apache.operators.dto;

/**
 * Created by ewandaf on 17/11/14.
 */
public interface Response {

    String getErrorMessage();

    void setErrorMessage(String errorMessage);

    boolean isSuccess();

    void setSuccess(boolean success);

}
