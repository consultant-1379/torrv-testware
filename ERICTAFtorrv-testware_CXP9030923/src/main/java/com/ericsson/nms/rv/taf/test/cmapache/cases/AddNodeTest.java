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

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.cmapache.operators.CmCommandRestOperator;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.*;

public class AddNodeTest extends TorTestCaseHelper implements TestCase {
    @Inject
    private CmCommandRestOperator cmOperator;

    private static final Logger logger = LoggerFactory
            .getLogger(AddNodeTest.class);

    private static final long POLL_INTERVAL_MILLIS = 1000;
    private static final long POLL_TIMEOUT_MILLIS = 20000;
    private final List<String> moList = Arrays.asList("FmAlarmSupervision",
            "CmFunction", "SHMFunction", "SecurityFunction");

    @BeforeTest
    @Parameters({ XML_COMMANDS_TEMPLATE, TEST_ID_ATTRIBUTE,
            TEST_TITLE_ATTRIBUTE })
    public void setup(final String templateFileName, final String testId,
            final String testTitle) {
        logger.info("templateFileName: {}; testId:{}, testTitle: {}",
                templateFileName, testId, testTitle);
        DataHandler.setAttribute(DO_CLI_COMMANDS_TEMPLATE_FILE_KEY,
                templateFileName);
        DataHandler.setAttribute(TEST_ID_ATTRIBUTE, testId);
        DataHandler.setAttribute(TEST_TITLE_ATTRIBUTE, testTitle);
    }

    @DataDriven(name = "cm_do_cli_commands")
    @Test
    public void nodeAddAndVerifyAndSetSecurity(
            @Input(HEADER_FOR_COMMAND_GROUP) final CommandGroup commandGroup) {
        setTestcase(DataHandler.getAttribute(TEST_ID_ATTRIBUTE).toString(),
                DataHandler.getAttribute(TEST_TITLE_ATTRIBUTE).toString());

        final List<Command> commandList = commandGroup.getCommands();

        // The first three commands create the MeContext, NetworkElement and CppConnectivityInfo
        for (final Command commandObject : commandList.subList(0, 3)) {
            final String command = (String) commandObject.get(COMMAND_HEADER);
            final String expectedBodyContains = (String) commandObject
                    .get(EXPECTED_BODY_RESPONSE_CONTAINS_HEADER);
            final CmResponse response = cmOperator.doCliCommand(command,
                    expectedBodyContains);
            if (!response.isSuccess()) {
                logger.warn(
                        "Failed to execute command '{}'. Message: '{}'. Body: '{}'",
                        command, response.getErrorMessage(), response.getBody());
            }
            assertTrue(response.isSuccess());
        }

        // Verfiy creation of child MOs by polling using command at index 3.
        final String verifyCommand = (String) (commandList.get(3)
                .get(COMMAND_HEADER));

        for (final String moName : moList) {
            final CmResponse pollResp = cmOperator.pollCliCommand(
                    verifyCommand, moName, POLL_INTERVAL_MILLIS,
                    POLL_TIMEOUT_MILLIS);
            if (!pollResp.isSuccess()) {
                logger.warn(
                        "Failed to find MO '{}'. Message: '{}'. Body: '{}'",
                        moName, pollResp.getErrorMessage(), pollResp.getBody());
            }
            assertTrue(pollResp.isSuccess());
        }

        // Set Node Security MO using command at index 4
        final String secCommand = (String) (commandList.get(4)
                .get(COMMAND_HEADER));
        final String secExpected = (String) (commandList.get(4)
                .get(EXPECTED_BODY_RESPONSE_CONTAINS_HEADER));

        final CmResponse secResponse = cmOperator.doCliCommand(secCommand,
                secExpected);
        if (!secResponse.isSuccess()) {
            logger.warn(
                    "Failed to execute command '{}'. Message: '{}'. Body: '{}'",
                    secCommand, secResponse.getErrorMessage(),
                    secResponse.getBody());
        }
        assertTrue(secResponse.isSuccess());
    }
}