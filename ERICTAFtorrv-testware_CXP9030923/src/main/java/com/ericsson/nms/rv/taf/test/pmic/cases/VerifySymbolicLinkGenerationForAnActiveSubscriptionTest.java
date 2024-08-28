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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.nms.rv.taf.test.pmic.operators.*;
import com.google.inject.Inject;

public class VerifySymbolicLinkGenerationForAnActiveSubscriptionTest extends
        TorTestCaseHelper implements TestCase {
    private final Logger logger = LoggerFactory
            .getLogger(VerifySymbolicLinkGenerationForAnActiveSubscriptionTest.class);
    @Inject
    private PmicUiOperator pmicOperator;

    @Inject
    private PmicCliOperator pmicCliOperator;

    private static List<String> nodeNamesInSubscription;
    private static Map<String, String> nodesXmlFilesBefore;
    private static Map<String, String> nodesXmlFilesAfter;

    @BeforeTest
    @Parameters({ "pmic.active.symbolic.links", "testId", "testTitle" })
    public void setup(final String fileName, final String testId,
            final String testTitle) {
        logger.info("testId:{}, testTitle: {}, dataFile: {}", testId,
                testTitle, fileName);
        DataHandler.setAttribute("dataprovider.symbolic_links.location",
                fileName);
        DataHandler.setAttribute("dataprovider.symbolic_links.type", "csv");
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "symbolic_links")
    @Test
    public void getListOfNodesInSubscription(
            @Input("subName") final String subName,
            @Input("description") final String description) {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
        logger.debug(
                "Testing symbolic links on nodes in an active subscription: {}, Desc: {}",
                subName, description);

        PmicResponse response = assertStepIsTrue(pmicOperator
                .initialiseCurrentBrowserTab());
        final BrowserTab currentBrowserTab = response.getCurrentBrowserTab();
        // Get list of nodes in the subscription
        response = pmicOperator.getListOfNodesFromTheSubscription(
                currentBrowserTab, subName);
        assertTrue(
                String.format(
                        "Unable to retrieve the list of nodes in the subscription: '%s'",
                        response.getErrorMessage()), response.isSuccess());
        nodeNamesInSubscription = response.getNodeNamesListInSubscription();
    }

    @DataDriven(name = "symbolic_links")
    @Test
    public void getXmlsInDirectoryBeforeRop(
            @Input("symbolicDirectory") final String symbolicDirectory) {
        logger.debug("Getting files in directory before ROP.");
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        logger.info("Checking directory for symbolic link creation.");
        final PmicResponse response = pmicCliOperator
                .executeCliCommandsOnPmServ(symbolicDirectory);
        assertTrue(String.format(
                "Unable to execute command successfully. Message: %s",
                response.getErrorMessage()), response.isSuccess());

        // Create map between nodes,lastXmlFileCollected
        checkIfNodeNamesInSubscriptionIsNull();
        nodesXmlFilesBefore = pmicCliOperator.mapNodesToXmlFilesInDirectory(
                response.getOutput().split("\r\n"), nodeNamesInSubscription);

    }

    @DataDriven(name = "symbolic_links")
    @Test
    public void getXmlsInDirectoryAfterRopAndVerifyCollection(
            @Input("symbolicDirectory") final String symbolicDirectory) {
        logger.debug("Getting xml files in directory after ROP. Verifying successful collection.");
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        logger.info("Checking directory to see if symbolic links have been updated");
        PmicResponse response = pmicCliOperator
                .executeCliCommandsOnPmServ(symbolicDirectory);
        assertTrue(String.format(
                "Unable to execute command successfully. Message: %s",
                response.getErrorMessage()), response.isSuccess());
        checkIfNodeNamesInSubscriptionIsNull();
        nodesXmlFilesAfter = pmicCliOperator.mapNodesToXmlFilesInDirectory(
                response.getOutput().split("\r\n"), nodeNamesInSubscription);
        response = pmicCliOperator.verifyXmlFilesUpdated(nodesXmlFilesBefore,
                nodesXmlFilesAfter);
        assertTrue(String.format(
                "Symbolic link collection on the following nodes failed: '%s'",
                response.getErrorMessage()), response.isSuccess());

    }

    private PmicResponse assertStepIsTrue(final PmicResponse pmicResponse) {
        assertTrue(String.format("%s", pmicResponse.getErrorMessage()),
                pmicResponse.isSuccess());
        return pmicResponse;
    }

    private void checkIfNodeNamesInSubscriptionIsNull() {
        if (nodeNamesInSubscription == null) {
            fail("Test failed as nodeNamesInSubscription is null. Cause: 'getListOfNodesFromTheSubscription' step failed.");
        }
    }
}
