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

import javax.inject.Inject;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.ConfigCopyCommandResponseParser;
import com.ericsson.nms.rv.taf.test.cmapache.operators.CmCommandRestOperator;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.CmResponse;

public class CliConfigCommandsTest extends TorTestCaseHelper implements
        TestCase {

    private static final Logger logger = LoggerFactory
            .getLogger(CliConfigCommandsTest.class);
    private static final String CONFIG_STATUS_COMMAND_TEMPLATE = "config copy -st -j %s";
    private static final String JOB_STATUS_COMPLETED = "COMPLETED";

    @Inject
    private CmCommandRestOperator cmOperator;

    @BeforeTest
    @Parameters({ TEST_ID_ATTRIBUTE, TEST_TITLE_ATTRIBUTE,
            DATA_CSV_TEMPLATE_FILE_KEY })
    public void setup(final String testId, final String testTitle,
            final String fileName) {
        DataHandler.setAttribute(DATA_CSV_TEMPLATE_FILE_KEY, fileName);
        DataHandler.setAttribute(TEST_ID_ATTRIBUTE, testId);
        DataHandler.setAttribute(TEST_TITLE_ATTRIBUTE, testTitle);
    }

    @DataDriven(name = "data_csv_template")
    @Test
    public void doAsynchronousCommands(
            //command,expectedBodyContains,pollIntervalMillis,pollTimeoutMillis,expectedNodesCopied
            @Input("command") final String command,
            @Input("expectedBodyContains") final String expectedBodyContains,
            @Input("pollIntervalMillis") final String pollIntervalMillis,
            @Input("pollTimeoutMillis") final String pollTimeoutMillis,
            @Input("expectedNodesCopied") final String expectedNodesCopied) {

        setTestcase(DataHandler.getAttribute(TEST_ID_ATTRIBUTE).toString(),
                DataHandler.getAttribute(TEST_TITLE_ATTRIBUTE).toString());

        // Step 1: do the config command and get the job number.
        CmResponse response = cmOperator.doCliCommand(command,
                expectedBodyContains);
        if (!response.isSuccess()) {
            logger.warn(
                    "Command failed. Command '{}'. Message: '{}'. Body: '{}'",
                    command, response.getErrorMessage(), response.getBody());
        }
        assertTrue(response.isSuccess());

        final String jobNumber = ConfigCopyCommandResponseParser
                .getJobNumber(response.getBody());
        assertNotSame("Job Number not found for config command.", jobNumber, "");
        assertNotSame(
                String.format(
                        "Job Number of -1 for command indicates an error. Response body: {}",
                        response.getBody()), jobNumber, "-1");

        // Step 2: poll cli until the job is complete
        response = cmOperator.pollCliCommand(
                String.format(CONFIG_STATUS_COMMAND_TEMPLATE, jobNumber),
                JOB_STATUS_COMPLETED, Long.parseLong(pollIntervalMillis),
                Long.parseLong(pollTimeoutMillis));
        if (!response.isSuccess()) {
            logger.warn(
                    "Failed to get job status for job number '{}'. Message: {}",
                    jobNumber, response.getErrorMessage());
        }
        assertTrue(response.isSuccess());

        // Step 3: check the number or copied nodes.
        int actualNodesCopied = -1;
        try {
            actualNodesCopied = ConfigCopyCommandResponseParser
                    .getNumberNodesCopied(response);
        } catch (final ParseException e) {
            logger.error("Failed to parse JSON response to config copy status command.");
            logger.error("Error message: {}", e.getMessage());
        }
        assertNotSame(
                String.format(
                        "Number of nodes copied is -1. This indicates an error in config command. Response body: {}",
                        response.getBody()), actualNodesCopied, -1);
        assertEquals(
                String.format(
                        "Expected number of nodes to be copied '%s' does not match actual value '%s'.",
                        expectedNodesCopied, actualNodesCopied),
                Integer.parseInt(expectedNodesCopied), actualNodesCopied);
    }
}
