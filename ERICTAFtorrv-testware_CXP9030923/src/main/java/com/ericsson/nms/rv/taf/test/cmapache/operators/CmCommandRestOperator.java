package com.ericsson.nms.rv.taf.test.cmapache.operators;

import java.io.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.tools.http.*;
import com.ericsson.cifwk.taf.tools.http.constants.ContentType;
import com.ericsson.cifwk.taf.tools.http.constants.HttpStatus;
import com.ericsson.cifwk.taf.utils.FileFinder;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheRestOperator;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.CmResponse;

public class CmCommandRestOperator {
    private final Logger logger = LoggerFactory
            .getLogger(CmCommandRestOperator.class);

    private final String CLI_COMMAND_URL_POST = "/script-engine/services/command";
    private final String CLI_COMMAND_URL_HEAD = "/script-engine/services/command/status";
    private final String CLI_COMMAND_URL_GET_TEMPLATE = "/script-engine/services/command/output/0/%s";
    private final String SCRIPT_ENGINE_CONTENT_TYPE = "application/json";
    private final long POLL_HEAD_TIMEOUT_MILLIS = (Long
            .parseLong(DataHandler
                    .getAttribute(
                            CmApachePropertyKeys.DO_CLI_COMMANDS_POLL_HEAD_TIMEOUT_SECONDS)
                    .toString())) * 1000;

    public CmResponse doCliCommand(final String command,
            final String expectedBodyContains) {

        logger.info("Executing Command: {}, Expected Result: {}", command,
                expectedBodyContains);

        final CmResponse retResp = new CmResponse();
        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();
        HttpResponse response = null;

        response = executeCommand(httpTool, command);
        retResp.setBody(response.getBody());

        if (!response.getBody().contains(expectedBodyContains)) {
            return setResponse(
                    retResp,
                    false,
                    String.format(
                            "Failed on command: '%s'. Body does not contain expected response: '%s'.",
                            command, expectedBodyContains));
        }

        retResp.setSuccess(true);
        return retResp;
    }

    public CmResponse doCliCommandWithoutChecking(final String command) {

        logger.info("Executing Command: {}", command);

        final CmResponse retResp = new CmResponse();
        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();
        final HttpResponse response = executeCommand(httpTool, command);
        retResp.setBody(response.getBody());
        retResp.setStatusCode(response.getResponseCode().getCode());
        retResp.setSuccess(true);
        return retResp;
    }

    /**
     * @param command
     *            Full auto provisioning 'import' or 'update' command, including
     *            filename. This method does not validate the correctness of the
     *            command.
     * @param fileName
     *            Name of the file to be uploaded.
     * @param expectedResponseContains
     *            Compared against the response body of the get command.
     * @return
     */
    public CmResponse doCliCommandWithDragAndDrop(final String command,
            final String fileName, final String expectedResponseContains) {

        logger.info(
                "Executing Command with Drag and Drop. Command: {}, Filename: {}, Expected response contains: {}",
                command, fileName, expectedResponseContains);

        final CmResponse retResp = new CmResponse();
        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();

        final List<String> filePaths = FileFinder.findFile(fileName);
        File projectFile = null;
        for (final String singleFilePath : filePaths) {
            final File file = new File(singleFilePath);
            if (file.getName().equals(fileName)) {
                projectFile = file;
                break;
            }
        }

        if (projectFile == null) {
            return setResponse(retResp, false, String.format(
                    "Project file %s was not found in the system.", fileName));
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(projectFile);
        } catch (final FileNotFoundException fnfe) {
            return setResponse(retResp, false,
                    String.format("Failed to read file %s", fileName));
        }

        final HttpResponse postResponse = doPostCommandWithDragAndDrop(
                httpTool, fis, fileName, command);
        checkForSessonDeletion(postResponse);
        checkResponseCode(postResponse, HttpStatus.CREATED);

        final HttpResponse pollResponse = pollUntilCommandStatusIsComplete(httpTool);

        final HttpResponse getResponse = doGetCommand(httpTool, pollResponse);
        checkResponseCode(getResponse, HttpStatus.OK);
        retResp.setBody(getResponse.getBody());

        if (getResponse.getBody().contains(expectedResponseContains)) {
            return setResponse(retResp, true, "");
        }
        return setResponse(
                retResp,
                false,
                String.format(
                        "Response does not contain expected result '%s'. Actual result: %s",
                        expectedResponseContains, getResponse.getBody()));
    }

    public CmResponse pollCliCommand(final String command,
            final String expectedResponseContains,
            final long pollIntervalMillis, final long pollTimeoutMillis) {

        logger.info(
                "Polling CLI with Command: {}. Expected Response: {}, Poll interval: {}, timout in milliseconds: {}",
                command, expectedResponseContains, pollIntervalMillis,
                pollTimeoutMillis);

        final CmResponse retResp = new CmResponse();
        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();
        HttpResponse httpResponse = null;

        final long startTimeMillis = System.currentTimeMillis();
        long elapsedTimeMillis = 0L;
        boolean commandCompleted = false;

        while (!commandCompleted && (elapsedTimeMillis < pollTimeoutMillis)) {
            httpResponse = executeCommand(httpTool, command);

            if (httpResponse.getBody().contains(expectedResponseContains)) {
                commandCompleted = true;
            }

            if (!commandCompleted) {
                try {
                    Thread.sleep(pollIntervalMillis);
                } catch (final InterruptedException e) {
                    logger.warn("Thread sleep interrupted. Message: {}",
                            e.getMessage());
                }
            }
            elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        }

        retResp.setBody(httpResponse.getBody());
        if (commandCompleted) {
            return setResponse(retResp, true, "");
        } else {
            return setResponse(retResp, false,
                    "Timed out while polling CLI app.");
        }
    }

    private HttpResponse executeCommand(final HttpTool httpTool,
            final String command) {

        final HttpResponse postResponse = doPostCommand(httpTool, command);
        checkForSessonDeletion(postResponse);
        checkResponseCode(postResponse, HttpStatus.CREATED);

        final HttpResponse pollResponse = pollUntilCommandStatusIsComplete(httpTool);

        final HttpResponse getResponse = doGetCommand(httpTool, pollResponse);
        checkForSessonDeletion(getResponse);
        checkResponseCode(getResponse, HttpStatus.OK);

        return getResponse;
    }

    private HttpResponse pollUntilCommandStatusIsComplete(
            final HttpTool httpTool) {

        final long startTime = System.currentTimeMillis();
        boolean timedOut = false;
        boolean statusComplete = false;
        String status = "";
        HttpResponse pollResponse = null;

        while (!timedOut && !statusComplete) {
            pollResponse = doHeadCommand(httpTool);
            checkHeadResponse(pollResponse);

            status = pollResponse.getHeaders().get("CommandStatus");

            if (status == null) {
                logger.debug("HEAD response headers: {}", pollResponse
                        .getHeaders().toString());
                logger.error("The HEAD response did not contains header 'CommandStatus', retrying.");
            } else {
                if (!status.equals("COMPLETE")) {
                    try {
                        Thread.sleep(1000L);
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    statusComplete = true;
                }
            }

            final long currentTime = System.currentTimeMillis();
            if (currentTime - startTime > POLL_HEAD_TIMEOUT_MILLIS) {
                timedOut = true;
            }
        }

        if (statusComplete) {
            return pollResponse;
        } else {
            throw new OperatorException(
                    logger,
                    "OperatorException: CLI command timed out while checking for command status using the HEAD request. Timeout time (ms): {}.",
                    POLL_HEAD_TIMEOUT_MILLIS);
        }
    }

    private HttpResponse doPostCommand(final HttpTool httpTool,
            final String command) {

        return httpTool.request().contentType(ContentType.MULTIPART_FORM_DATA)
                .header("Accept", SCRIPT_ENGINE_CONTENT_TYPE)
                .body("command", command).post(CLI_COMMAND_URL_POST);

    }

    private HttpResponse doPostCommandWithDragAndDrop(final HttpTool httpTool,
            final FileInputStream fis, final String fileName,
            final String command) {

        return httpTool.request().body("command", command.trim())
                .body("fileName", fileName).body("file:" + fileName, fis)
                .header("Accept", "application/json")
                .header("X-TOR-userid", "administrator")
                .post(CLI_COMMAND_URL_POST);
    }

    private HttpResponse doHeadCommand(final HttpTool httpTool) {
        return httpTool.request().head(CLI_COMMAND_URL_HEAD);
    }

    private HttpResponse doGetCommand(final HttpTool httpTool,
            final HttpResponse pollResponse) {
        final String responseSize = pollResponse.getHeaders().get(
                "ResponseSize");
        final String getUrl = String.format(CLI_COMMAND_URL_GET_TEMPLATE,
                responseSize);
        final RequestBuilder getRequsetBuilder = httpTool.request().header(
                "Accept", SCRIPT_ENGINE_CONTENT_TYPE);
        final HttpResponse response = getRequsetBuilder.get(getUrl);
        return response;
    }

    private void checkForSessonDeletion(final HttpResponse response) {
        if (userSessionHasBeenDeleted(response)) {
            throw new OperatorException(
                    logger,
                    "OperatorException: User session has been terminated. The user has been redirected to the 'ENM Login' page.");
        }
    }

    private boolean userSessionHasBeenDeleted(final HttpResponse response) {
        return response.getBody().contains("<title>ENM Login</title>");
    }

    private void checkResponseCode(final HttpResponse response,
            final HttpStatus expectedStatus) {
        if (response.getResponseCode() != expectedStatus) {

            logger.debug("---- Response Headers: {}", response.getHeaders()
                    .toString());
            logger.debug("---- Response Body: {}", response.getBody());

            throw new OperatorException(
                    logger,
                    "OperatorException: Unexpected HTTP response. Expected {}, found {}",
                    expectedStatus.getCode(), response.getResponseCode()
                            .getCode());
        }
    }

    private void checkHeadResponse(final HttpResponse response) {
        final boolean incorrectResponse = response.getResponseCode() != HttpStatus.OK;
        final boolean userLoggedOut = userIsLoggedOut(response);

        if (incorrectResponse || userLoggedOut) {
            logger.debug("---- HEAD response headers: {}", response
                    .getHeaders().toString());
        }
        if (incorrectResponse) {
            logger.error(
                    "The HEAD response did not return HTTP status code 200. Actual status: {}",
                    response.getResponseCode().getCode());
        }

        if (userLoggedOut) {
            throw new OperatorException(logger,
                    "OperatorException: The user has been logged out.");
        }
    }

    private boolean userIsLoggedOut(final HttpResponse response) {
        //If the user is logged out the Location header will contain the redirect url
        final String location = response.getHeaders().get("Location");
        if (!(location == null || location.equals(""))) {
            if (location.contains("/login/")) {
                return true;
            }
        }
        return false;
    }

    private CmResponse setResponse(final CmResponse response,
            final boolean success, final String error) {
        response.setSuccess(success);
        response.setErrorMessage(error);
        return response;
    }
}