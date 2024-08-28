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
package com.ericsson.nms.rv.taf.test.shm.cases;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.ui.core.WaitTimedOutException;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.DataSourceBuilder;
import com.ericsson.nms.rv.taf.test.shm.operators.*;
import com.google.inject.Inject;

public class ShmUiLaunchTest extends TorTestCaseHelper implements TestCase {

    @Inject
    private ShmUiOperator shmHardwareUI;
    @Inject
    private ShmSoftwareUiOperator shmSoftwareUI;

    private final Logger logger = LoggerFactory
            .getLogger(ShmUiLaunchTest.class);

    @BeforeTest
    @Parameters({ "testId", "testTitle", "collection.name" })
    public void setup(final String testId, final String testTitle,
            final String fileName) {
        logger.info("testId:{}, testTitle: {}, data file: {}", testId,
                testTitle, fileName);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
        DataHandler.setAttribute("dataprovider.collection_name.location",
                fileName);
        DataHandler.setAttribute("dataprovider.collection_name.type", "csv");
    }

    @Test
    @DataDriven(name = "collection_name")
    public void shmHwUILauncher(
            @Input("collectionName") final String collectionName) {
        setTestIdAndTitle();
        shmHardwareUI.init();
        assertStepIsTrue(shmHardwareUI.isSwHwHeadingDisplayed());

        logger.info("Checking Hardware Inventory");
        assertStepIsTrue(shmHardwareUI
                .addCollection("Hardware", collectionName));
        assertStepIsTrue(shmHardwareUI.isTopologyDropdownDisplayed());

        final String nodeID = DataSourceBuilder.getRandomNodeInPool()
                .getManagedElementId();
        logger.info(
                "Checking if Random Node '{}' has been added to the Hardware Inventory",
                nodeID);

        ShmResponse shmResponse = shmHardwareUI
                .isCollectionAddedToInventory(nodeID);
        assertTrue("Collection Not Added to Hardware Inventory: "
                + "Verify that the collection '" + collectionName
                + "' exists in the Network Explorer and can be imported "
                + "to the Hardware Inventory", shmResponse.isSuccess());

        final ArrayList<String> expectedTableHeadings = new ArrayList<>(
                Arrays.asList("Name", "Node Name", "Hardware Type",
                        "No Of Slots", "Position", "Revision", "Serial No"));

        shmResponse = shmHardwareUI
                .verifyTableHeadingsExist(expectedTableHeadings);
        assertTrue(String.format(
                "Table headings for Hardware Inventory Are Incorrect: %s",
                shmResponse.getError()), shmResponse.isSuccess());

        assertStepIsTrue(shmHardwareUI.verifyNodeInfoPopulated(nodeID));
        assertStepIsTrue(shmHardwareUI.removeCollection());
    }

    @Test
    @DataDriven(name = "collection_name")
    public void shmSwUILauncher(
            @Input("collectionName") final String collectionName) {
        setTestIdAndTitle();
        shmSoftwareUI.init();
        assertStepIsTrue(shmSoftwareUI.isSwHwHeadingDisplayed());

        logger.info("Checking Software Inventory");
        assertStepIsTrue(shmSoftwareUI
                .addCollection("Hardware", collectionName));
        assertStepIsTrue(shmSoftwareUI.isTopologyDropdownDisplayed());

        final String nodeID = DataSourceBuilder.getRandomNodeInPool()
                .getManagedElementId();
        logger.info(
                "Checking if Random Node '{}' has been added to the Software Inventory",
                nodeID);

        ShmResponse shmResponse = shmSoftwareUI
                .isCollectionAddedToInventory(nodeID);
        assertTrue("Collection Not Added to Software Inventory: "
                + "Verify that the collection '" + collectionName
                + "' exists in the Network Explorer and can be imported "
                + "to the Hardware Inventory", shmResponse.isSuccess());

        try {
            saveAssertTrue("Software Text not found",
                    shmSoftwareUI.isSoftwareTextExist());
            saveAssertTrue("Create import action bar not found",
                    shmSoftwareUI.isCrtImpActionBarExist());
            saveAssertTrue("Software import action bar not found",
                    shmSoftwareUI.isSwImpActionBarExist());
            saveAssertTrue("View  import action bar not found",
                    shmSoftwareUI.isViewSWPkgActionBarExist());
            saveAssertTrue("Delete Header not found",
                    shmSoftwareUI.deltaUpHeaderCheckReturnTest());
            saveAssertTrue("Node Name heading  not found",
                    shmSoftwareUI.nodeNameHeaderCheckReturnTest());
            saveAssertTrue("Name heading not found",
                    shmSoftwareUI.nameHeaderCheckReturnTest());
            saveAssertTrue("Product number not found",
                    shmSoftwareUI.prdNoHeaderCheckReturnTest());
            saveAssertTrue("Type not found",
                    shmSoftwareUI.typeHeaderCheckReturnTest());
            saveAssertTrue("Date not found",
                    shmSoftwareUI.dateHeaderCheckReturnTest());
            saveAssertTrue("State not found",
                    shmSoftwareUI.stateHeaderCheckReturnTest());
            saveAssertTrue("Executing Package not found",
                    shmSoftwareUI.executingUpHeaderCheckReturnTest());
            saveAssertTrue("Product revision not found",
                    shmSoftwareUI.prdRevHeaderCheckReturnTest());
        } catch (final WaitTimedOutException e) {
            logger.error("WaitTimedOutException was thrown while checking software inventory.");
        }
        shmResponse = shmSoftwareUI.verifyNodeInfoPopulated(nodeID);
        assertTrue(
                String.format(
                        "Information for Node '%s' is not populated in the Software Inventory",
                        nodeID), shmResponse.isSuccess());

        assertStepIsTrue(shmSoftwareUI.selectNodes());
        assertStepIsTrue(shmSoftwareUI.removeCollection());
    }

    @Test
    @DataDriven(name = "collection_name")
    public void shmLicenseUILauncher(
            @Input("collectionName") final String collectionName) {
        setTestIdAndTitle();
        shmHardwareUI.init();
        assertStepIsTrue(shmHardwareUI.isSwHwHeadingDisplayed());

        logger.info("Checking License Inventory");
        assertStepIsTrue(shmHardwareUI
                .addCollection("Hardware", collectionName));
        assertStepIsTrue(shmHardwareUI.isTopologyDropdownDisplayed());

        final String nodeID = DataSourceBuilder.getRandomNodeInPool()
                .getManagedElementId();
        logger.info(
                "Checking if Random Node '{}' has been added to the License Inventory",
                nodeID);

        ShmResponse shmResponse = shmHardwareUI
                .isCollectionAddedToInventory(nodeID);
        assertTrue("Collection Not Added to License Inventory: "
                + "Verify that the collection '" + collectionName + "' "
                + "exists in the Network Explorer and can be imported "
                + "to the License Inventory", shmResponse.isSuccess());

        final ArrayList<String> expectedTableHeadings = new ArrayList<>(
                Arrays.asList("Node Name", "Fingerprint", "HW Resource Name",
                        "Installation Date"));

        shmResponse = shmHardwareUI
                .verifyTableHeadingsExist(expectedTableHeadings);
        assertTrue(String.format(
                "Table headings for License Inventory Are Incorrect: %s",
                shmResponse.getError()), shmResponse.isSuccess());

        assertStepIsTrue(shmHardwareUI.verifyNodeInfoPopulated(nodeID));
        assertStepIsTrue(shmHardwareUI.removeCollection());
    }

    private void assertStepIsTrue(ShmResponse shmResponse) {
        assertTrue(shmResponse.getError(), shmResponse.isSuccess());
    }

    private void setTestIdAndTitle() {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
    }
}
