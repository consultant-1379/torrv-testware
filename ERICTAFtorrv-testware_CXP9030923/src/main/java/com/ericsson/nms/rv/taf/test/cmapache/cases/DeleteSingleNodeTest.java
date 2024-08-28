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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.*;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.cmapache.operators.CmCommandRestOperator;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.*;

public class DeleteSingleNodeTest extends TorTestCaseHelper implements TestCase {
    @Inject
    private CmCommandRestOperator cmOperator;

    @Inject
    private TestContext deleteNodeContext;

    private static final Logger logger = LoggerFactory
            .getLogger(DeleteSingleNodeTest.class);

    @BeforeTest
    @Parameters({ XML_COMMANDS_TEMPLATE, TEST_ID_ATTRIBUTE,
            TEST_TITLE_ATTRIBUTE })
    public void setup(String templateFileName, String testId, String testTitle) {
        logger.info("templateFileName: {}; testId:{}, testTitle: {}",
                templateFileName, testId, testTitle);
        DataHandler.setAttribute(DO_CLI_COMMANDS_TEMPLATE_FILE_KEY,
                templateFileName);
        DataHandler.setAttribute(TEST_ID_ATTRIBUTE, testId);
        DataHandler.setAttribute(TEST_TITLE_ATTRIBUTE, testTitle);
    }

    @DataDriven(name = "cm_do_cli_commands")
    @Test
    public void deleteSingleNodeAndDontReturnToNodePool(
            @Input(HEADER_FOR_COMMAND_GROUP) CommandGroup commandGroup) {
        setTestcase(DataHandler.getAttribute(TEST_ID_ATTRIBUTE).toString(),
                DataHandler.getAttribute(TEST_TITLE_ATTRIBUTE).toString());

        final long startTime = System.currentTimeMillis();
        boolean isAllSuccess = true;
        final List<Command> commands = commandGroup.getCommands();

        final String commandString = commands.get(1).getCommand();

        final String nodeName = StringUtils.substringBetween(commandString,
                "NetworkElement=", ",");
        deleteNodeContext.setAttribute(nodeName.trim(), true);

        if (cmNodeHeartbeatSupervisionExists(commands.get(0))) {
            turnOffCmNodeHeartbeatSupervision(commands.get(1));
        } else {
            logger.info("CmNodeHeartbeatSupervision MO not found. Skipping set command for MO.");
        }

        for (final Command command : commands.subList(2, 4)) {
            final CmResponse response = doCommand(command);

            saveAssertTrue(
                    String.format(
                            "Failed to execute command '%s'. Message: '%s'. Body: '%s'",
                            command.get(COMMAND_HEADER),
                            response.getErrorMessage(), response.getBody()),
                    response.isSuccess());

            if (!response.isSuccess()) {
                logger.debug("Command response body: {}", response.getBody());
            }
            isAllSuccess = isAllSuccess && response.isSuccess();
        }
        final long totalTime = System.currentTimeMillis() - startTime;
        logger.info("Total time in ms to delete node: {}", totalTime);
    }

    public boolean checkIfNodeHasBeenDeleted(String nodeName) {
        if (deleteNodeContext.getAttribute(nodeName) != null) {
            return true;
        }
        return false;
    }

    private CmResponse doCommand(Command commandObject) {
        final String command = (String) commandObject.get(COMMAND_HEADER);
        final String expectedBodyContains = (String) commandObject
                .get(EXPECTED_BODY_RESPONSE_CONTAINS_HEADER);

        return cmOperator.doCliCommand(command, expectedBodyContains);
    }

    private boolean cmNodeHeartbeatSupervisionExists(Command commandObject) {
        final CmResponse resp = doCommand(commandObject);
        if (!resp.isSuccess()) {
            logger.warn("Failed to find CmNodeHeartbeatSupervision MO. Skipping set command");
        }
        return resp.isSuccess();
    }

    private void turnOffCmNodeHeartbeatSupervision(Command command) {
        final CmResponse resp = doCommand(command);
        if (!resp.isSuccess()) {
            logger.warn(
                    "Failed to turn off CmNodeHeartbeatSupervision MO. Message: {}, Body: {}",
                    resp.getErrorMessage(), resp.getBody());
        }
    }
}