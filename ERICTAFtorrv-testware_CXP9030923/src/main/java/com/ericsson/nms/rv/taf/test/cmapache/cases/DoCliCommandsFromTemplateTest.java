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

public class DoCliCommandsFromTemplateTest extends TorTestCaseHelper implements
        TestCase {
    @Inject
    private CmCommandRestOperator cmOperator;

    private static final Logger logger = LoggerFactory
            .getLogger(DoCliCommandsFromTemplateTest.class);

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
    public void doCliCommandsFromTemplate(
            @Input(HEADER_FOR_COMMAND_GROUP) final CommandGroup commandGroup) {
        setTestcase(DataHandler.getAttribute(TEST_ID_ATTRIBUTE).toString(),
                DataHandler.getAttribute(TEST_TITLE_ATTRIBUTE).toString());
        final List<Command> commandList = commandGroup.getCommands();
        for (final Command commandObject : commandList) {
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
    }
}