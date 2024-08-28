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
package com.ericsson.nms.rv.taf.test.pmic.cases;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.pmic.operators.PmicPibOperator;
import com.ericsson.nms.rv.taf.test.pmic.operators.PmicResponse;

public class PibSetupStep extends TorTestCaseHelper implements TestCase {
    private final Logger logger = LoggerFactory.getLogger(PibSetupStep.class);

    @Inject
    private PmicPibOperator pmicPibOperator;

    @BeforeTest
    @Parameters({ "setup.symbolic.links", "testId", "testTitle" })
    public void setup(final String symbolicFileName, final String testId,
            final String testTitle) {
        DataHandler.setAttribute("dataprovider.symbolic_links.location",
                symbolicFileName);
        DataHandler.setAttribute("dataprovider.symbolic_links.type", "csv");
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "symbolic_links")
    @Test
    public void setupStepSetMaxSymbolicLinkDirectoriesAndEnableCreation(
            @Input("command") final String command,
            @Input("expectedResult") final String expectedResult) {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
        logger.info("Executing Platform Integration Bridge Command: {}",
                command);
        final PmicResponse pmicResponse = pmicPibOperator.pibCommand(command,
                expectedResult);
        assertTrue(
                String.format(
                        "Response from command was not as expected. Command: '%s'. Error message: '%s'",
                        command, pmicResponse.getErrorMessage()),
                pmicResponse.isSuccess());
    }
}
