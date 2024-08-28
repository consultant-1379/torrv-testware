package com.ericsson.nms.rv.taf.test.fm.operators;

import java.util.Map;

import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Response;

public class FmAlarmResponse implements Response {

    private boolean success;
    private String errorMessage;
    private String authErrorCode;
    private String output;
    private int statusCode;
    private Map<String, String> firstAlarmMap;
    private long timeAlarmReadMillis;
    private UiComponent notificationWidget;
    private UiComponent notificationLabel;

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

    public Map<String, String> getFirstAlarmMap() {
        return firstAlarmMap;
    }

    public void setFirstAlarmMap(final Map<String, String> firstAlarmMap) {
        this.firstAlarmMap = firstAlarmMap;
    }

    public long getTimeAlarmReadMillis() {
        return timeAlarmReadMillis;
    }

    public void setTimeAlarmReadMillis(final long time) {
        this.timeAlarmReadMillis = time;
    }

    public UiComponent getNotificationWidet() {
        return notificationWidget;
    }

    public void setNotificationWidget(final UiComponent notificationWidget) {
        this.notificationWidget = notificationWidget;
    }

    public UiComponent getNotificationLabel() {
        return notificationLabel;
    }

    public void setNotificationLabel(final UiComponent notificationLabel) {
        this.notificationLabel = notificationLabel;
    }
}
