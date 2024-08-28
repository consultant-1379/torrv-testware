/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.rv.taf.test.networkexplorer.operators;

import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.cifwk.taf.tools.http.constants.ContentType;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheRestOperator;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.NetworkExplorerResponse;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NetworkExplorerRestOperator {
    private final static Logger logger = LoggerFactory
            .getLogger(NetworkExplorerRestOperator.class);

    private final static int QUERY_SUCCESS_STATUS_CODE = 200;
    private final static int GET_COLLECTION_SUCCESS_STATUS_CODE = 200;
    private final static int GET_SAVED_SEARCH_SUCCESS_STATUS_CODE = 200;
    private final static int CREATE_COLLECTION_SUCCESS_STATUS_CODE = 201;
    private final static int CREATE_SEARCH_SUCCESS_STATUS_CODE = 201;
    private final static int DELETE_COLLECTION_SUCCESS_STATUS_CODE = 200;
    private final static int DELETE_SAVED_SEARCH_SUCCESS_STATUS_CODE = 200;
    private final static String QUERY_URL = "/managedObjects/query";
    private final static String COLLECTIONS_URL = "/topologyCollections/staticCollections/";
    private final static String SAVED_SEARCH_URL = "/topologyCollections/savedSearches/";

    public NetworkExplorerResponse createCollection(
            final List<String> managedElementId, final String collectionName,
            final String query, final Category category) {
        logger.debug("Create Collection {}", collectionName);
        final NetworkExplorerResponse retResp = new NetworkExplorerResponse();
        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();

        // Query for all managed objects. Fail fast.
        final HttpResponse queryResponse = getManagedObjects(httpTool, query);
        if (queryResponse.getResponseCode().getCode() != QUERY_SUCCESS_STATUS_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "Query '%s' failed with http status code '%s'", query,
                    queryResponse.getResponseCode().getCode()));
            return retResp;
        }

        // Map managedElementId to poId
        Map<String, String> poIds = null;
        try {
            poIds = getPoIdsFromQueryResponse(queryResponse, managedElementId);
            // Fail if some poIds are missing
            if (poIds.size() < managedElementId.size()) {
                retResp.setSuccess(false);
                retResp.setErrorMessage(String
                        .format("Query '%s' did not find all poIds "
                                + "specified in test input. Cannot create collection with missing poIds.",
                                query));
                return retResp;
            }
        } catch (final ParseException e) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "Failed to parse json response for query '%s'.", query));
            return retResp;
        }

        // Generate Json for create call.
        final String jsonStr = generateJsonForCreateCollection(collectionName,
                poIds, category);
        logger.debug("JSON String\n{}", jsonStr);

        // Do call and validate
        final HttpResponse response = httpTool.request()
                .contentType(ContentType.APPLICATION_JSON).body(jsonStr)
                .post(COLLECTIONS_URL);

        logger.debug(
                "Create collection response: code:{}; body:{}; statusLine:{}",
                response.getResponseCode().getCode(), response.getBody(),
                response.getStatusLine());

        if (response.getResponseCode().getCode() != CREATE_COLLECTION_SUCCESS_STATUS_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String
                    .format("Create collection HTTP call returned with status code '%s', error message '%s'.",
                            response.getResponseCode().getCode(),
                            parseServerErrorMessage(response.getBody())));
            return retResp;
        }
        retResp.setSuccess(true);
        return retResp;
    }

    private String parseServerErrorMessage(final String jsonstr) {
        final JSONObject json = (JSONObject) JSONValue.parse(jsonstr);
        final JSONObject message = (JSONObject) json.get("userMessage");
        return (String) message.get("body");
    }

    private String generateJsonForCreateCollection(final String collectionName,
            final Map<String, String> poIds, final Category category) {

        final JSONArray poidArray = new JSONArray();
        for (final String poId : poIds.values()) {
            poidArray.add(poId);
        }

        final JSONObject root = new JSONObject();
        root.put("category", category.toString());
        root.put("name", collectionName);
        root.put("moList", poidArray);
        return root.toJSONString();
    }

    public NetworkExplorerResponse deleteCollection(final String collectionName) {
        logger.debug("Delete Collection {}", collectionName);
        final NetworkExplorerResponse retResp = new NetworkExplorerResponse();
        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();

        // Get all collections and get poId for collectionName
        HttpResponse response = httpTool.get(COLLECTIONS_URL);
        if (response.getResponseCode().getCode() != GET_COLLECTION_SUCCESS_STATUS_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String
                    .format("Failed to get collections from server. HTTP status code '%s'.",
                            response.getResponseCode().getCode()));
            return retResp;
        }
        String poId = "";
        try {
            poId = getCollectionOrSearchPoId(response, collectionName);
        } catch (final ParseException e) {
            retResp.setSuccess(false);
            retResp.setErrorMessage("Failed to parse json response for 'Get Collections'.");
            return retResp;
        }

        if (poId.isEmpty()) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "Collection '%s' does not exist.", collectionName));
            return retResp;
        }

        // Delete collection
        response = httpTool.delete(COLLECTIONS_URL + poId);
        if (response.getResponseCode().getCode() != DELETE_COLLECTION_SUCCESS_STATUS_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String
                    .format("Failed to delete collections '%s'. HTTP status code '%s'.",
                            collectionName, response.getResponseCode()
                                    .getCode()));
            return retResp;
        }
        retResp.setSuccess(true);
        return retResp;
    }

    public List<String> getElementListFromCollectionResponse(
            final NetworkExplorerResponse networkExplorerResponse) {
        logger.info("Getting element list from reponse body.");
        final List<String> toReturn = new ArrayList<String>();
        final String body = networkExplorerResponse.getBody();
        final JSONObject o = (JSONObject) JSONValue.parse(body);
        final JSONArray jsonArray = (JSONArray) o.get("poList");
        final Iterator jsonArrayIterator = jsonArray.iterator();
        while (jsonArrayIterator.hasNext()) {
            toReturn.add((String) ((JSONObject) jsonArrayIterator.next())
                    .get("moName"));
        }
        return toReturn;
    }

    public String getSearchQueryFromSearchResponse(
            final NetworkExplorerResponse networkExplorerResponse) {
        logger.info("Getting search query from reponse body.");
        final String body = networkExplorerResponse.getBody();
        final JSONObject o = (JSONObject) JSONValue.parse(body);
        final String searchQuery = (String) o.get("searchQuery");
        return searchQuery;
    }

    private String getCollectionOrSearchPoId(final HttpResponse response,
            final String collectionName) throws ParseException {

        String poId = "";
        final JSONArray array = (JSONArray) JSONValue.parse(response.getBody());
        if (array == null) {
            logger.warn("Response body is empty. Return empty poid");
            return poId;
        }
        final Iterator it = array.iterator();

        while (it.hasNext()) {
            final JSONObject obj = (JSONObject) it.next();
            final String name = (String) obj.get("name");
            if (name.equals(collectionName)) {
                poId = (String) obj.get("poId");
                break;
            }
        }
        return poId;
    }

    public List<String> stringToList(final String commaSeperatedString) {
        return Arrays.asList(commaSeperatedString.split("\\s*,\\s*"));
    }

    public NetworkExplorerResponse createSearch(final String searchName,
            final String searchQuery, final Category category) {
        logger.debug("Create Search");
        final NetworkExplorerResponse retResp = new NetworkExplorerResponse();
        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", searchName);
        jsonObject.put("searchQuery", searchQuery);
        jsonObject.put("category", category.toString());
        final HttpResponse response = httpTool.request()
                .contentType(ContentType.APPLICATION_JSON)
                .body(jsonObject.toString()).post(SAVED_SEARCH_URL);

        if (response.getResponseCode().getCode() != CREATE_SEARCH_SUCCESS_STATUS_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String
                    .format("Create search HTTP call returned with status code '%s', error message '%s'.",
                            response.getResponseCode().getCode(),
                            parseServerErrorMessage(response.getBody())));
            return retResp;
        }
        retResp.setSuccess(true);
        return retResp;
    }

    public NetworkExplorerResponse deleteSearch(final String searchName) {
        logger.debug("Delete Collection");
        final NetworkExplorerResponse retResp = new NetworkExplorerResponse();
        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();

        HttpResponse response = httpTool.get(SAVED_SEARCH_URL);
        retResp.setBody(response.getBody());
        if (response.getResponseCode().getCode() != GET_SAVED_SEARCH_SUCCESS_STATUS_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "Failed to get search from server. HTTP status code '%s'.",
                    response.getResponseCode().getCode()));
            return retResp;
        }

        String poId = "";
        try {
            poId = getCollectionOrSearchPoId(response, searchName);
        } catch (final ParseException e) {
            retResp.setSuccess(false);
            retResp.setErrorMessage("Failed to parse json response for 'Get Search'.");
            return retResp;
        }

        if (poId.isEmpty()) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "Search '%s' does not exist.", searchName));
            return retResp;
        }

        // Delete collection
        response = httpTool.delete(SAVED_SEARCH_URL + poId);
        retResp.setBody(response.getBody());
        if (response.getResponseCode().getCode() != DELETE_SAVED_SEARCH_SUCCESS_STATUS_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "Failed to delete search '%s'. HTTP status code '%s'.",
                    searchName, response.getResponseCode().getCode()));
            return retResp;
        }
        retResp.setSuccess(true);
        return retResp;
    }

    public NetworkExplorerResponse getSearch(final String searchName) {
        logger.info("Check existance of search:{}", searchName);
        final NetworkExplorerResponse retResp = new NetworkExplorerResponse();

        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();

        // Get all collections
        HttpResponse response = httpTool.get(SAVED_SEARCH_URL);
        retResp.setBody(response.getBody());
        if (response.getResponseCode().getCode() != GET_SAVED_SEARCH_SUCCESS_STATUS_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "Failed to get search from server. HTTP status code '%s'.",
                    response.getResponseCode().getCode()));
            return retResp;
        }

        // Get poId of searchName
        logger.info("Getting poid of the search name '{}'", searchName);
        String poId = "";
        try {
            poId = getCollectionOrSearchPoId(response, searchName);
        } catch (final ParseException e) {
            retResp.setSuccess(false);
            retResp.setErrorMessage("Failed to parse json response for 'Get Collections'.");
            return retResp;
        }
        logger.info("Checking if search name poid is empty.");
        if (poId.isEmpty()) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "search '%s' does not exist.", searchName));
            return retResp;
        }
        logger.info(
                "Checking if response code is a success for search name. Code should be {}.",
                GET_SAVED_SEARCH_SUCCESS_STATUS_CODE);
        response = httpTool.get(SAVED_SEARCH_URL + poId);
        retResp.setBody(response.getBody());
        if (response.getResponseCode().getCode() != GET_SAVED_SEARCH_SUCCESS_STATUS_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "Failed to get search '%s'. HTTP status code '%s'.",
                    searchName, response.getResponseCode().getCode()));
            return retResp;
        }
        retResp.setSuccess(true);
        retResp.setStatusCode(response.getResponseCode().getCode());
        retResp.setBody(response.getBody());
        return retResp;
    }

    private Map<String, String> getPoIdsFromQueryResponse(
            final HttpResponse queryResponse,
            final List<String> managedElementId) throws ParseException {

        final Map<String, String> poIdMap = new HashMap<String, String>(
                managedElementId.size());
        final JSONArray array = (JSONArray) JSONValue.parse(queryResponse
                .getBody());

        final Iterator it = array.iterator();

        while (it.hasNext()) {
            final JSONObject obj = (JSONObject) it.next();
            final String mibRootName = (String) obj.get("mibRootName");
            final String poId = (String) obj.get("poId");
            if (managedElementId.contains(mibRootName)) {
                poIdMap.put(mibRootName, poId);
            }
        }
        return poIdMap;
    }

    private HttpResponse getManagedObjects(final HttpTool httpTool,
            final String query) {
        logger.debug("Executing query: {}", query);

        final HttpResponse response = httpTool.request()
                .header("Accept", ContentType.APPLICATION_JSON)
                .queryParam("searchQuery", query).get(QUERY_URL);

        return response;
    }

    public NetworkExplorerResponse getCollection(final String collectionName,
            final String attributesToReturn) {
        logger.debug("Check existance of collection:{}", collectionName);
        final NetworkExplorerResponse retResp = new NetworkExplorerResponse();

        String attributes = "";
        if (attributesToReturn != null) {
            attributes = attributesToReturn;
        }

        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();

        // Get all collections
        HttpResponse response = httpTool.get(COLLECTIONS_URL);
        if (response.getResponseCode().getCode() != GET_COLLECTION_SUCCESS_STATUS_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String
                    .format("Failed to get collections from server. HTTP status code '%s'.",
                            response.getResponseCode().getCode()));
            return retResp;
        }

        // Get poId of collectionName
        logger.info("Getting poid of the collection name '{}'", collectionName);
        String poId = "";
        try {
            poId = getCollectionOrSearchPoId(response, collectionName);
        } catch (final ParseException e) {
            retResp.setSuccess(false);
            retResp.setErrorMessage("Failed to parse json response for 'Get Collections'.");
            return retResp;
        }
        logger.info("Checking if collection name poid is empty.");
        if (poId.isEmpty()) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "Collection '%s' does not exist.", collectionName));
            return retResp;
        }

        // Get the collection
        logger.info(
                "Checking if response code is a success for collection. Code should be {}.",
                GET_COLLECTION_SUCCESS_STATUS_CODE);
        response = httpTool.get(COLLECTIONS_URL + poId + "/" + attributes);
        retResp.setBody(response.getBody());
        if (response.getResponseCode().getCode() != GET_COLLECTION_SUCCESS_STATUS_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "Failed to get collection '%s'. HTTP status code '%s'.",
                    collectionName, response.getResponseCode().getCode()));
            return retResp;
        }
        retResp.setSuccess(true);
        retResp.setStatusCode(response.getResponseCode().getCode());
        return retResp;
    }

    public NetworkExplorerResponse doQuery(final String query) {
        final NetworkExplorerResponse retResp = new NetworkExplorerResponse();

        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();

        final HttpResponse response = getManagedObjects(httpTool, query);
        retResp.setBody(response.getBody());
        if (response.getResponseCode().getCode() != QUERY_SUCCESS_STATUS_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "Query '%s' failed. HTTP status code '%s'.", query,
                    response.getResponseCode().getCode()));
            return retResp;
        }
        retResp.setSuccess(true);
        retResp.setStatusCode(response.getResponseCode().getCode());
        return retResp;
    }

    public List<String> getCollectionNodeList(final String collectionName) {
        logger.debug("Check existance of collection:{}", collectionName);
        final NetworkExplorerResponse retResp = new NetworkExplorerResponse();
        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();

        // Get all collections
        HttpResponse response = httpTool.get(COLLECTIONS_URL);
        if (response.getResponseCode().getCode() != GET_COLLECTION_SUCCESS_STATUS_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String
                    .format("Failed to get collections from server. HTTP status code '%s'.",
                            response.getResponseCode().getCode()));

        }

        // Get poId of collectionName
        String poId = "";
        try {
            poId = getCollectionOrSearchPoId(response, collectionName);
        } catch (final ParseException e) {
            retResp.setSuccess(false);
            retResp.setErrorMessage("Failed to parse json response for 'Get Collections'.");

        }

        if (poId.isEmpty()) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "Collection '%s' does not exist.", collectionName));

        }

        // Get the collection
        response = httpTool.get(COLLECTIONS_URL + poId + "/poid");
        retResp.setBody(response.getBody());
        final List<String> nodeList = new ArrayList<>();
        if (response.getResponseCode().getCode() == GET_COLLECTION_SUCCESS_STATUS_CODE) {
            final JSONArray array = (JSONArray) JSONValue.parse(response
                    .getBody());
            final Iterator it = array.iterator();

            while (it.hasNext()) {
                final JSONObject obj = (JSONObject) it.next();
                final String fdn = (String) obj.get("fdn");
                final String[] node = fdn.split("=");
                nodeList.add(node[1]);
            }

        }
        return nodeList;
    }
}
