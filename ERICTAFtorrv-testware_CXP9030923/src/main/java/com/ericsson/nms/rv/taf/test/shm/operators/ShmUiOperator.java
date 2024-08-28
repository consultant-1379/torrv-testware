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
package com.ericsson.nms.rv.taf.test.shm.operators;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.ui.*;
import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.CheckBox;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheUiOperator;
import com.ericsson.nms.rv.taf.test.shm.cases.ShmUiLaunchTest;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.shm.ShmViewModel;

public class ShmUiOperator {
    private static final String SHM_LAUNCHER = "/#shm";

    protected static Browser browser = null;
    protected static BrowserTab currentBrowserTab = null;
    private final Logger logger = LoggerFactory
            .getLogger(ShmUiLaunchTest.class);

    private final int ACTION_TIMEOUT_MILLIS = 2000;

    public void init() {
        browser = ApacheUiOperator.getBrowser();
        ApacheUiOperator.skipTestIfUserIsNotLoggedIn(browser);

        currentBrowserTab = browser.getCurrentWindow();
        currentBrowserTab.open("https://"
                + HostConfigurator.getApache().getIp() + SHM_LAUNCHER);
    }

    /**
     * Open Administration page
     *
     * @param administrationType
     *            - Hardware, Software, License
     */
    private void openShmAdministrationPage(final String administrationType)
            throws WaitTimedOutException {
        final ShmViewModel shmViewModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent dropDown = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmViewModel.getfirtSHMDropDown(), 5000);
        dropDown.click();
        final UiComponent inventoryDropDown = currentBrowserTab
                .waitUntilComponentIsDisplayed(shmViewModel
                        .selectAdminPagefromDropDown(administrationType), 5000);
        inventoryDropDown.click();
    }

    /**
     * Add a collection to the inventory of the selected admin page
     *
     * @param adminPage
     *            - Hardware, Software, License
     * @param collectionName
     *            - Name of collection to be added
     * @return ShmResponse - Error message and Success parameters returned
     */
    public ShmResponse addCollection(final String adminPage,
            final String collectionName) {
        final ShmResponse shmResponse = new ShmResponse();
        try {
            openShmAdministrationPage(adminPage);
            clickDropDown();
            selectAddCollections();
            selectCollection(collectionName);
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    shmResponse,
                    false,
                    String.format(
                            "Could not add Collection '%s': Verify collection has been created.",
                            collectionName));
        }
        logger.info("Successfully added collection to SHM.");
        shmResponse.setSuccess(true);
        return shmResponse;
    }

    /**
     * Verify if the 'Add Topology Data' Button is displayed on the current
     * admin page (hardware,software or license)
     *
     * @return ShmResponse - Error message and Success parameters returned
     */
    public ShmResponse isTopologyDropdownDisplayed() {
        final ShmViewModel shmViewModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final ShmResponse shmResponse = new ShmResponse();
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    shmViewModel.getSHMCollectionDropDown(), 5000);
        } catch (final WaitTimedOutException e) {
            return setResponse(shmResponse, false,
                    "Cannot View License Administration Page");
        }
        shmResponse.setSuccess(true);
        return shmResponse;
    }

    /**
     *
     * Click if the 'Add Topology Data' dropdown button on the current admin
     * page (hardware,software or license)
     */
    private void clickDropDown() throws WaitTimedOutException {
        final ShmViewModel shmViewModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent dropDown = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmViewModel.getSHMCollectionDropDown(),
                        ACTION_TIMEOUT_MILLIS);
        dropDown.click();
    }

    /**
     * Select add collections from dropdown list
     */
    private void selectAddCollections() throws WaitTimedOutException {
        final ShmViewModel getCollection = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent getCol = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        getCollection.selectAddCollections(),
                        ACTION_TIMEOUT_MILLIS);
        getCol.click();
    }

    /**
     * Select the collection to be added to the admin page from the network
     * explorer
     *
     * @param collectionName
     */
    private void selectCollection(final String collectionName)
            throws WaitTimedOutException {
        final ShmViewModel shmViewModel = currentBrowserTab
                .getView(ShmViewModel.class);

        currentBrowserTab.waitUntilComponentIsDisplayed(
                shmViewModel.isCollectionDisplayed(collectionName),
                ACTION_TIMEOUT_MILLIS);

        final CheckBox collectionCheckBox = shmViewModel
                .selectCollection(collectionName);
        UI.pause(1000);
        collectionCheckBox.select();

        final UiComponent addSelectedCollectionButton = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmViewModel.getAddSelectedCollection(),
                        ACTION_TIMEOUT_MILLIS);
        addSelectedCollectionButton.click();
    }

    /**
     * Remove the collection from the current admin page
     *
     * @return ShmResponse - Error message and Success parameters returned
     */
    public ShmResponse removeCollection() {
        final ShmResponse shmResponse = new ShmResponse();
        final ShmViewModel shmViewModel = currentBrowserTab
                .getView(ShmViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    shmViewModel.enableActions(), ACTION_TIMEOUT_MILLIS)
                    .click();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    shmViewModel.actionsButton(), ACTION_TIMEOUT_MILLIS)
                    .click();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    shmViewModel.deleteSelectedCollection(),
                    ACTION_TIMEOUT_MILLIS).click();
            shmResponse.setSuccess(true);
            return shmResponse;
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    shmResponse,
                    false,
                    "Could not remove collection from the inventory : Verify collection was created");
        }
    }

    /**
     * Verify that the collection has been added to the inventory of the page by
     * checking if the random node passed has been added to the inventory.
     *
     * @param randomNode
     * @return ShmResponse - Error message and Success parameters returned
     */
    public ShmResponse isCollectionAddedToInventory(final String randomNode) {
        final ShmResponse shmResponse = new ShmResponse();
        final ShmViewModel shmViewModel = currentBrowserTab
                .getView(ShmViewModel.class);

        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    shmViewModel.isNodeDisplayed(randomNode),
                    ACTION_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            return setResponse(shmResponse, false,
                    String.format("Could Not Find Node '%s'", randomNode));
        }

        final List<UiComponent> networkElementList = shmViewModel
                .getNetworkElementList();

        for (final UiComponent component : networkElementList) {
            if (component.getText().equals(randomNode)) {
                shmResponse.setSuccess(true);
                return shmResponse;
            }
        }
        return setResponse(shmResponse, false,
                String.format("Could Not Find Node '%s'", randomNode));
    }

    /**
     * Check that the 'Software Hardware Manager' heading is displayed
     *
     * @return ShmResponse - Error message and Success parameters returned
     */
    public ShmResponse isSwHwHeadingDisplayed() {
        final ShmViewModel shmViewModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final ShmResponse shmResponse = new ShmResponse();
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    shmViewModel.getSwHwHeading(), ACTION_TIMEOUT_MILLIS);
            shmResponse.setSuccess(true);
            return shmResponse;
        } catch (final UiComponentNotFoundException e) {
            return setResponse(shmResponse, false,
                    "Cannot Login to Software Hardware Manager");
        }
    }

    /**
     *
     * @param expectedTableHeadings
     * @return ShmResponse - Error message and Success parameters returned
     */
    public ShmResponse verifyTableHeadingsExist(
            final List<String> expectedTableHeadings) {
        final ShmResponse shmResponse = new ShmResponse();
        final ShmViewModel shmViewModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final List<String> tableHeadings = shmViewModel.getTableHeadings();

        if (tableHeadings.containsAll(expectedTableHeadings)) {
            shmResponse.setSuccess(true);
            return shmResponse;
        } else {
            return setResponse(shmResponse, false, String.format(
                    "Expected Headings to be '%s' but found : '%s'",
                    expectedTableHeadings.toString(), tableHeadings.toString()));
        }
    }

    /**
     * Check to see that the information from the node which passed in has been
     * populated correctly in the Inventory. Warning is trigger if there are
     * blank fields returned.
     *
     * @param nodeID
     * @return ShmResponse - Error message and Success parameters returned
     */

    public ShmResponse verifyNodeInfoPopulated(final String nodeID) {
        final ShmResponse shmResponse = new ShmResponse();
        final ShmViewModel shmViewModel = currentBrowserTab
                .getView(ShmViewModel.class);

        // Check that Node is displayed in the Hardware Inventory Table
        Boolean nodeDisplayed = true;
        try {
            nodeDisplayed = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            shmViewModel.isNodeDisplayed(nodeID),
                            ACTION_TIMEOUT_MILLIS).isDisplayed();
        } catch (final WaitTimedOutException e) {
            return setResponse(shmResponse, false, String.format(
                    "Timeout: No information provided "
                            + "for node %s in the inventory", nodeID));
        }
        // Get a list of unpopulated fields in the Hardware Inventory Table
        final List<UiComponent> emptyCells = shmViewModel
                .getListEmptyTableCells(nodeID);
        if (emptyCells.size() <= 0 && nodeDisplayed) {
            shmResponse.setSuccess(true);
            logger.info("All fields associated with Node {} are populated "
                    + "in the inventory", nodeID);
        } else if (nodeDisplayed) {
            // Set response to true but give a warning to populate Information
            shmResponse.setSuccess(true);
            logger.warn(
                    "Node '{}' is displayed but there are unpopulated fields in the "
                            + " inventory", nodeID);
        }
        return shmResponse;
    }

    private ShmResponse setResponse(final ShmResponse shmResponse,
            final boolean success, final String error) {
        shmResponse.setSuccess(success);
        shmResponse.setError(error);
        return shmResponse;
    }
}
