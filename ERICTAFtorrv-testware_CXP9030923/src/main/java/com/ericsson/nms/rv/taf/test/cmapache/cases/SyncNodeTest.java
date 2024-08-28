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
package com.ericsson.nms.rv.taf.test.cmapache.cases;

import static com.ericsson.nms.rv.taf.test.cmapache.operators.CmApachePropertyKeys.*;

import java.util.List;

import javax.inject.Inject;

import org.json.simple.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.cmapache.operators.CmApachePropertyKeys;
import com.ericsson.nms.rv.taf.test.cmapache.operators.CmCommandRestOperator;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.*;

public class SyncNodeTest extends TorTestCaseHelper implements TestCase {
    @Inject
    private CmCommandRestOperator cmOperator;

    private static final Logger logger = LoggerFactory
            .getLogger(SyncNodeTest.class);

    @BeforeTest
    @Parameters({ XML_COMMANDS_TEMPLATE, TEST_ID_ATTRIBUTE,
            TEST_TITLE_ATTRIBUTE })
    public void setup(final String templateFileName, final String testId,
            final String testTitle) {
        DataHandler.setAttribute(DO_CLI_COMMANDS_TEMPLATE_FILE_KEY,
                templateFileName);
        DataHandler.setAttribute(TEST_ID_ATTRIBUTE, testId);
        DataHandler.setAttribute(TEST_TITLE_ATTRIBUTE, testTitle);
    }

    @DataDriven(name = "cm_do_cli_commands")
    @Test
    public void doSyncNode(
            @Input(HEADER_FOR_COMMAND_GROUP) final CommandGroup commandGroup) {
        setTestcase(DataHandler.getAttribute(TEST_ID_ATTRIBUTE).toString(),
                DataHandler.getAttribute(TEST_TITLE_ATTRIBUTE).toString());

        final List<Command> commands = commandGroup.getCommands();
        verifyMoExists(commands.get(0), "CmNodeHeartbeatSupervision");
        verifyMoExists(commands.get(1), "InventorySupervision");
        manageNode(commands.subList(2, 5));
        verifyNodeSynchronized(commands.get(5));
    }

    private void verifyMoExists(final Command command, final String moName) {
        logger.info("Verifying that MO '{}' exists. Executing command: {}",
                moName, command.get(COMMAND_HEADER));
        final CmResponse response = doCommand(command);
        if (!response.isSuccess()) {
            logger.warn("{} does not exist. Message: '{}'. Body: '{}'", moName,
                    response.getErrorMessage(), response.getBody());
        }
    }

    private void manageNode(final List<Command> commands) {
        logger.info("Executing the Sync commands.");
        for (final Command command : commands) {
            final CmResponse response = doCommand(command);
            if (!response.isSuccess()) {
                logger.warn(
                        "Failed to execute manage command '{}'. Message: '{}'. Body: '{}'",
                        command, response.getErrorMessage(), response.getBody());
            }
        }
    }

    private CmResponse doCommand(final Command commandObject) {
        final String command = (String) commandObject.get(COMMAND_HEADER);
        final String expectedBodyContains = (String) commandObject
                .get(EXPECTED_BODY_RESPONSE_CONTAINS_HEADER);

        return cmOperator.doCliCommand(command, expectedBodyContains);
    }

    public void verifyNodeSynchronized(final Command commandLine) {
        logger.info("Verifying that node has synced.");
        boolean isSynchronized = false;

        int timeout = 30000;
        try {
            timeout = Integer
                    .parseInt((String) DataHandler
                            .getAttribute(CmApachePropertyKeys.SYNC_NODE_TIMEOUT_SECONDS_KEY)) * 1000;
        } catch (final NumberFormatException e) {
            logger.warn(
                    "Failed to get property {} from file. Using default time 30 seconds.",
                    CmApachePropertyKeys.SYNC_NODE_TIMEOUT_SECONDS_KEY);
        }

        int sleepTime = 5000;
        try {
            sleepTime = Integer
                    .parseInt((String) DataHandler
                            .getAttribute(CmApachePropertyKeys.SYNC_NODE_SLEEP_TIME_SECONDS_KEY)) * 1000;
        } catch (final NumberFormatException e) {
            logger.warn(
                    "Failed to get property {} from file. Using default time 5 seconds.",
                    CmApachePropertyKeys.SYNC_NODE_SLEEP_TIME_SECONDS_KEY);
        }

        final Long startTime = System.currentTimeMillis();
        int elapsed = 0;
        int generationCounter = 0;
        String syncStatus = "";
        CmResponse response = null;
        final String command = (String) commandLine.get(COMMAND_HEADER);
        final String expectedResult = (String) commandLine
                .get(EXPECTED_BODY_RESPONSE_CONTAINS_HEADER);

        while (!isSynchronized && elapsed < timeout) {

            try {
                Thread.sleep(sleepTime);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

            response = cmOperator.doCliCommand(command, expectedResult);

            if (!response.isSuccess()) {
                logger.warn(
                        "Failed to verify synchronization of object, command: '{}'. Message: '{}'",
                        command, response.getErrorMessage());
            }
            assertTrue(response.isSuccess());

            final Long currentTime = System.currentTimeMillis();
            elapsed = (int) (currentTime - startTime);

            final JSONObject json = (JSONObject) JSONValue.parse(response
                    .getBody());
            final JSONObject responseDto = (JSONObject) json.get("responseDto");
            final JSONArray array = (JSONArray) responseDto.get("elements");

            for (final Object obj : array) {
                final JSONObject innerObj = (JSONObject) obj;
                final String value = (String) innerObj.get("value");
                if (value != null) {
                    final String[] valueInfo = value.split(":");
                    if (valueInfo[0].trim().equals("syncStatus")) {
                        syncStatus = valueInfo[1].trim();
                    } else if (valueInfo[0].trim().equals("generationCounter")) {
                        generationCounter = Integer.parseInt(valueInfo[1]
                                .trim());
                    }
                }
            }
            logger.info("GenerationCounter: {}, syncStatus: {}",
                    generationCounter, syncStatus);
            isSynchronized = (generationCounter > 0)
                    && ("SYNCHRONIZED".equals(syncStatus));

        }
        logger.info("Time taken (MS): {}", elapsed);
        if (!isSynchronized) {
            logger.warn(
                    "Sync failed. generationCounter = '{}'. syncStatus = '{}'.",
                    generationCounter, syncStatus);
        }
        assertTrue(isSynchronized);

    }
}
