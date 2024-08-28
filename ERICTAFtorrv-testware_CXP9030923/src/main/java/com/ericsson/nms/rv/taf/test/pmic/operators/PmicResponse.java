package com.ericsson.nms.rv.taf.test.pmic.operators;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.ui.BrowserTab;

/**
 * Created by ewandaf on 09/05/14.
 */
public class PmicResponse {
    private boolean success;
    private String errorMessage;
    private String ropInfo;
    private HttpResponse httpResponse;
    private String subscriptionName;
    private String id;
    private String description;
    private String schedulerInfo;
    private int totalNodes;
    private int totalCounters;
    private String output;
    private List<String> nodeNamesListInSubscription;
    private String lastXmlFileName;
    private List<String> listOfFilesInDirectory;
    private BrowserTab currentBrowserTab;

    public PmicResponse(final HttpResponse response) {
        this.httpResponse = response;
        final int responseCode = response.getResponseCode().getCode();
        final String responseBody = response.getBody();

        if (responseCode == 200) {
            this.success = true;
            if (responseBody != null) {
                final Object respsonseObject = JSONValue.parse(responseBody);
                if (respsonseObject instanceof JSONObject) {
                    final JSONObject responseJSONObject = (JSONObject) respsonseObject;
                    this.id = this.getValue(responseJSONObject, "id");
                    this.subscriptionName = this.getValue(responseJSONObject,
                            "name");
                    this.description = this.getValue(responseJSONObject,
                            "description");
                    final JSONObject scheduleInfoJSONObject = this
                            .getJSONObject(responseJSONObject, "scheduleInfo");
                    this.ropInfo = this.getValue(scheduleInfoJSONObject,
                            "ropInfo");
                }
            }
        } else {
            this.success = false;
            this.errorMessage = responseBody;
        }
    }

    public PmicResponse() {

    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(final HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(final String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getDescription() {
        return description;
    }

    public String getRopInfo() {
        return ropInfo;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setRopInfo(final String ropInfo) {
        this.ropInfo = ropInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(final boolean isSuccess) {
        this.success = isSuccess;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private String getValue(final JSONObject jsonObject, final String key) {
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

    private JSONObject getJSONObject(final JSONObject jsonObject,
            final String key) {
        JSONObject result = null;
        if (jsonObject != null) {
            final Object valueObject = jsonObject.get(key);
            if (valueObject != null) {
                if (valueObject instanceof JSONObject) {
                    result = (JSONObject) valueObject;
                }
            }
        }
        return result;
    }

    public int getTotalNodes() {
        return totalNodes;
    }

    public void setTotalNodes(final int totalNodes) {
        this.totalNodes = totalNodes;
    }

    public int getTotalCounters() {
        return totalCounters;
    }

    public void setTotalCounters(final int totalCounters) {
        this.totalCounters = totalCounters;
    }

    public List<String> getNodeNamesListInSubscription() {
        return nodeNamesListInSubscription;
    }

    public void setNodeNamesListInSubscription(
            final List<String> nodeNamesListInSubscription) {
        this.nodeNamesListInSubscription = nodeNamesListInSubscription;
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
    public void setOutput(final String output) {
        this.output = output;
    }

    /**
     * @return the lastXmlFileName
     */
    public String getLastXmlFileName() {
        return lastXmlFileName;
    }

    /**
     * @param lastXmlFileName
     *            the lastXmlFileName to set
     */
    public void setLastXmlFileName(final String lastXmlFileName) {
        this.lastXmlFileName = lastXmlFileName;
    }

    /**
     * @return the listOfFilesInDirectory
     */
    public List<String> getListOfFilesInDirectory() {
        return listOfFilesInDirectory;
    }

    /**
     * @param listOfFilesInDirectory
     *            the listOfFilesInDirectory to set
     */
    public void setListOfFilesInDirectory(
            final List<String> listOfFilesInDirectory) {
        this.listOfFilesInDirectory = listOfFilesInDirectory;
    }

    /**
     * @return the currentBrowserTab
     */
    public BrowserTab getCurrentBrowserTab() {
        return currentBrowserTab;
    }

    /**
     * @param currentBrowserTab
     *            the currentBrowserTab to set
     */
    public void setCurrentBrowserTab(final BrowserTab currentBrowserTab) {
        this.currentBrowserTab = currentBrowserTab;
    }
}
