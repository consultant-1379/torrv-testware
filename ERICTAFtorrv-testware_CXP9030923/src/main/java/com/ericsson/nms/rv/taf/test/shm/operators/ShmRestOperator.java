package com.ericsson.nms.rv.taf.test.shm.operators;

import org.apache.log4j.Logger;
import org.json.simple.*;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.cifwk.taf.tools.http.constants.ContentType;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheRestOperator;
import com.ericsson.nms.rv.taf.test.cmapache.operators.CmCommandRestOperator;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.CmResponse;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.NetworkExplorerRestOperator;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.NetworkExplorerResponse;

/**
 * Created by ewandaf on 09/06/14.
 */
@Operator(context = Context.REST)
public class ShmRestOperator implements ShmOperator {

    private static Logger LOGGER = Logger.getLogger(ShmRestOperator.class);

    @Override
    public ShmResponse getHWInventoryFromCLI(final String node) {
        final CmCommandRestOperator cmCommandRestOperator = new CmCommandRestOperator();
        final CmResponse cmResponse = cmCommandRestOperator.doCliCommand(
                "cmedit get Inventory=" + node + ",HWInventory=1", "a");
        final ShmResponse shmResponse = new ShmResponse();
        if (cmResponse != null && cmResponse.isSuccess()) {
            shmResponse.setSuccess(true);
            shmResponse.setBody(cmResponse.getBody());
        } else {
            shmResponse.setSuccess(false);
        }
        return shmResponse;
    }

    @Override
    public ShmResponse getHWInventory(final String collectionName) {
        HttpTool httpTool = null;
        JSONArray fdns = null;
        try {
            final JSONArray columns = new JSONArray();
            fdns = getFDNsJsonArray(collectionName);
            final JSONObject jsonObject = buildRequestBody(columns, fdns, 10,
                    1, "asc", "fdn");
            LOGGER.debug("Sending request to get HW inventory");
            httpTool = getHttpTool();
            final HttpResponse response = httpTool.request()
                    .contentType(ContentType.APPLICATION_JSON)
                    .body(jsonObject.toString()).post(HW_INVENTORY_LINK);
            final ShmResponse shmResponse = new ShmResponse(response);
            LOGGER.debug("Get response: " + response.getBody());
            return shmResponse;
        } catch (final Exception e) {
            e.printStackTrace();
            final String error = e.getMessage();
            final ShmResponse shmResponse = new ShmResponse();
            shmResponse.setSuccess(false);
            shmResponse.setError(error);
            return shmResponse;
        }
    }

    private HttpTool getHttpTool() {
        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();
        return httpTool;
    }

    @Override
    public ShmResponse getSWInventory(final String collectionName) {
        HttpTool httpTool = null;
        JSONArray fdns = null;
        try {
            final JSONArray columns = new JSONArray();
            fdns = getFDNsJsonArray(collectionName);
            final JSONObject jsonObject = buildRequestBody(columns, fdns, 10,
                    1, "asc", "fdn");
            LOGGER.debug("Sending request to get SW inventory");
            httpTool = getHttpTool();
            final HttpResponse response = httpTool.request()
                    .contentType(ContentType.APPLICATION_JSON)
                    .header("Accept", "application/json")
                    .body(jsonObject.toString()).post(SW_INVENTORY_LINK);
            final ShmResponse shmResponse = new ShmResponse(response);
            LOGGER.debug("Get response: " + response.getBody());
            return shmResponse;
        } catch (final Exception e) {
            e.printStackTrace();
            final String error = e.getMessage();
            final ShmResponse shmResponse = new ShmResponse();
            shmResponse.setSuccess(false);
            shmResponse.setError(error);
            return shmResponse;
        }
    }

    @Override
    public ShmResponse getLicenseInventory(final String collectionName) {
        HttpTool httpTool = null;
        JSONArray fdns = null;
        try {
            final JSONArray columns = new JSONArray();
            fdns = getFDNsJsonArray(collectionName);
            final JSONObject jsonObject = buildRequestBody(columns, fdns, 10,
                    1, "asc", "fdn");
            LOGGER.debug("Sending request to get License inventory");
            httpTool = getHttpTool();
            final HttpResponse response = httpTool.request()
                    .contentType(ContentType.APPLICATION_JSON)
                    .header("Accept", "application/json")
                    .body(jsonObject.toString()).post(LICENSE_INVENTORY_LINK);
            final ShmResponse shmResponse = new ShmResponse(response);
            LOGGER.debug("Get response: " + response.getBody());
            return shmResponse;
        } catch (final Exception e) {
            e.printStackTrace();
            final String error = e.getMessage();
            final ShmResponse shmResponse = new ShmResponse();
            shmResponse.setSuccess(false);
            shmResponse.setError(error);
            return shmResponse;
        }
    }

    private JSONObject buildRequestBody(final JSONArray columns,
            final JSONArray fdns, final int limit, final int offset,
            final String orderBy, final String sortBy) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("columns", columns);
        jsonObject.put("fdns", fdns);
        jsonObject.put("limit", limit);
        jsonObject.put("offset", offset);
        jsonObject.put("orderBy", orderBy);
        jsonObject.put("sortBy", sortBy);
        return jsonObject;
    }

    private JSONArray getFDNsJsonArray(final String collectionName)
            throws Exception {
        LOGGER.debug("Sending request to get collection " + collectionName);
        final JSONArray fdns = new JSONArray();
        final NetworkExplorerRestOperator networkExplorerRestOperator = new NetworkExplorerRestOperator();
        final NetworkExplorerResponse networkExplorerResponse = networkExplorerRestOperator
                .getCollection(collectionName, "fdnName");
        final String body = networkExplorerResponse.getBody();
        if (!networkExplorerResponse.isSuccess()) {
            throw new RuntimeException("Failed to get Collection "
                    + collectionName + " " + body);
        }
        final JSONArray jsonArray = (JSONArray) JSONValue.parse(body);

        for (final Object entry : jsonArray) {
            if (entry instanceof JSONObject) {
                final JSONObject jsonObject = (JSONObject) entry;
                final String fdn = (String) jsonObject.get("fdn");
                fdns.add(fdn);
            }
        }
        return fdns;
    }

}
