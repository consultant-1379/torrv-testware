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
import com.ericsson.nms.rv.taf.test.pmic.operators.*;

public class VerifySymbolicLinkDeletionTest extends TorTestCaseHelper implements
        TestCase {
    private final Logger logger = LoggerFactory
            .getLogger(VerifySymbolicLinkDeletionTest.class);
    @Inject
    private PmicCliOperator pmicCliOperator;

    @Inject
    private PmicTestCaseContext context;

    @BeforeTest
    @Parameters({ "pmic.symbolic.links.deletion", "testId", "testTitle" })
    public void setup(final String fileName, final String testId,
            final String testTitle) {
        logger.info("testId:{}, testTitle: {}, dataFile: {}", testId,
                testTitle, fileName);
        DataHandler.setAttribute(
                "dataprovider.symbolic_link_deletion.location", fileName);
        DataHandler.setAttribute("dataprovider.symbolic_link_deletion.type",
                "csv");
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "symbolic_link_deletion")
    @Test
    public void testSymbolicLinkOnAnActiveSubscription(
            @Input("subName") final String subName,
            @Input("description") final String description,
            @Input("symbolicDirectory") final String symbolicDirectory,
            @Input("symbolicLinkDeletionCheck") final String symbolicLinkDeletionCheck) {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
        logger.debug(
                "Test to verify symbolic link deletion from subscription: {}, Desc: {}",
                subName, description);
        context.checkIfNodesHaveBeenAdded();
        context.verifySubscriptionIsActive();
        PmicResponse pmicResponse = assertStepIsTrue(pmicCliOperator
                .executeCliCommandsOnPmServ(symbolicLinkDeletionCheck));

        final String symbolicLinkDeleteInitialisation = pmicResponse
                .getOutput();
        pmicResponse = assertStepIsTrue(pmicCliOperator
                .calculateWaitTimeAndWait(symbolicLinkDeleteInitialisation));

        pmicResponse = assertStepIsTrue(pmicCliOperator
                .executeCliCommandsOnPmServ(symbolicDirectory));
        final String[] oldestXmlFileInDirectory = pmicResponse.getOutput()
                .split("\r\n");

        assertStepIsTrue(pmicCliOperator
                .verifySymbolicLinkDeletion(oldestXmlFileInDirectory[0]));
    }

    private PmicResponse assertStepIsTrue(final PmicResponse pmicResponse) {
        assertTrue(String.format("%s", pmicResponse.getErrorMessage()),
                pmicResponse.isSuccess());
        return pmicResponse;
    }
}
