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

import static com.ericsson.nms.rv.taf.test.cmapache.operators.CmApachePropertyKeys.DATA_CSV_TEMPLATE_FILE_KEY;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.pmic.operators.*;
import com.google.inject.Inject;

public class FileCollectionActiveSubscriptionDisable extends TorTestCaseHelper
        implements TestCase {
    private final Logger logger = LoggerFactory
            .getLogger(FileCollectionActiveSubscriptionDisable.class);

    @Inject
    private PmicCliOperator pmicCliOperator;
    @Inject
    private PmicTestCaseContext context;

    private static List<String> filesInDirectoryBeforeRop;
    private static List<String> filesInDirectoryAfterRop;

    @BeforeTest
    @Parameters({ "data.csv.template", "testId", "testTitle" })
    public void setup(final String fileName, final String testId,
            final String testTitle) {
        logger.info("testId:{}, testTitle: {}, dataFile: {}", testId,
                testTitle, fileName);
        DataHandler.setAttribute(DATA_CSV_TEMPLATE_FILE_KEY, fileName);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "data_csv_template")
    @Test
    public void getFilesInDirectoryBeforeRop(
            @Input("subName") final String subName,
            @Input("directory") final String directory,
            @Input("deletedNode") final String deletedNodeName) {
        logger.debug(
                "Getting list of files in directory before Rop in subscription: {}",
                subName);
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        final List<String> deletedNodes = new ArrayList<>();
        deletedNodes.add(deletedNodeName);
        logger.info(
                "Checking file collection disabled for following nodes: {}.",
                deletedNodeName);
        final PmicResponse filesCollectedResponse = assertStepIsTrue(pmicCliOperator
                .executeCommandAndCreateListOfFilesCollected(deletedNodes,
                        directory));
        filesInDirectoryBeforeRop = filesCollectedResponse
                .getListOfFilesInDirectory();

    }

    @DataDriven(name = "data_csv_template")
    @Test
    public void getFilesInSubscriptionAfterRopAndVerifyCollection(
            @Input("subName") final String subName,
            @Input("directory") final String directory,
            @Input("deletedNode") final String deletedNodeName) {
        logger.debug(
                "Getting list of files in directory after Rop and verify they have been updated in subscription: {}",
                subName);
        context.checkIfNodesHaveBeenAdded();
        context.verifySubscriptionIsActive();

        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        final List<String> deletedNodes = new ArrayList<>();
        deletedNodes.add(deletedNodeName);

        logger.info("Checking file collection after ROP on nodes: {}",
                deletedNodeName);
        final PmicResponse filesCollectedResponse = assertStepIsTrue(pmicCliOperator
                .executeCommandAndCreateListOfFilesCollected(deletedNodes,
                        directory));
        filesInDirectoryAfterRop = filesCollectedResponse
                .getListOfFilesInDirectory();

        // Verify file collection
        assertStepIsFalse(pmicCliOperator.checkFileCollectionSuccessful(
                deletedNodes, filesInDirectoryBeforeRop,
                filesInDirectoryAfterRop));
    }

    private PmicResponse assertStepIsTrue(final PmicResponse pmicResponse) {
        assertTrue(String.format("%s", pmicResponse.getErrorMessage()),
                pmicResponse.isSuccess());
        return pmicResponse;
    }

    private PmicResponse assertStepIsFalse(final PmicResponse pmicResponse) {
        assertFalse(String.format("%s", pmicResponse.getErrorMessage()),
                pmicResponse.isSuccess());
        return pmicResponse;
    }

}
