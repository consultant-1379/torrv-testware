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

public class FileCollectionActiveSubscription extends TorTestCaseHelper
        implements TestCase {
    private final Logger logger = LoggerFactory
            .getLogger(FileCollectionActiveSubscription.class);

    @Inject
    private PmicUiOperator pmicOperator;
    @Inject
    private PmicCliOperator pmicCliOperator;

    private static List<String> nodeNamesInSubscription;
    private static List<String> filesInDirectoryBeforeRop;
    private static List<String> filesInDirectoryAfterRop;

    @BeforeTest
    @Parameters({ "pmic.active.file.collection", "testId", "testTitle" })
    public void setup(final String fileName, final String testId,
            final String testTitle) {
        logger.info("testId:{}, testTitle: {}, dataFile: {}", testId,
                testTitle, fileName);
        DataHandler.setAttribute(
                "dataprovider.active_file_collection.location", fileName);
        DataHandler.setAttribute("dataprovider.active_file_collection.type",
                "csv");
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "active_file_collection")
    @Test
    public void getNodesInSubscriptionAndFilesInDirectoryBeforeRop(
            @Input("subName") final String subName,
            @Input("directory") final String directory) {
        logger.debug("Getting files in directory before ROP");
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        final PmicResponse response = assertStepIsTrue(pmicOperator
                .initialiseCurrentBrowserTab());
        final BrowserTab currentBrowserTab = response.getCurrentBrowserTab();
        // Get list of nodes in the subscription

        PmicResponse filesCollectedResponse = assertStepIsTrue(pmicOperator
                .getListOfNodesFromTheSubscription(currentBrowserTab, subName));
        nodeNamesInSubscription = filesCollectedResponse
                .getNodeNamesListInSubscription();
        checkIfNodeNamesInSubscriptionIsNull();
        filesCollectedResponse = assertStepIsTrue(pmicCliOperator
                .executeCommandAndCreateListOfFilesCollected(
                        nodeNamesInSubscription, directory));
        filesInDirectoryBeforeRop = filesCollectedResponse
                .getListOfFilesInDirectory();
    }

    @DataDriven(name = "active_file_collection")
    @Test
    public void testFileCollectionDirectoryAfterRop(
            @Input("directory") final String directory) {
        logger.debug("Getting files in directory after ROP. Verifying they have been updated.");
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        logger.info("Checking file collection after ROP");
        checkIfNodeNamesInSubscriptionIsNull();
        final PmicResponse filesCollectedResponse = assertStepIsTrue(pmicCliOperator
                .executeCommandAndCreateListOfFilesCollected(
                        nodeNamesInSubscription, directory));
        filesInDirectoryAfterRop = filesCollectedResponse
                .getListOfFilesInDirectory();

        // Verify file collection
        assertStepIsTrue(pmicCliOperator.checkFileCollectionSuccessful(
                nodeNamesInSubscription, filesInDirectoryBeforeRop,
                filesInDirectoryAfterRop));
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
