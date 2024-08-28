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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.WaitTimedOutException;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.shm.ShmViewModel;

@Operator(context = Context.UI)
public class ShmSoftwareUiOperator extends ShmUiOperator {
    private final Logger logger = LoggerFactory
            .getLogger(ShmSoftwareUiOperator.class);
    private final int SHORT_UI_TIMEOUT_MILLIS = 1000;
    private final int UI_TIMEOUT_MILLIS = 2000;

    public boolean isSoftwareTextExist() throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent softwareTextCheck = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereShmSoftwareTextReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return softwareTextCheck.isDisplayed();
    }

    public boolean isCrtImpActionBarExist() throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent CrtImp = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereShmCrtSWActionBarCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return CrtImp.isDisplayed();
    }

    public boolean isSwImpActionBarExist() throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent SwImp = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereShmImpSWActionBarCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return SwImp.isDisplayed();
    }

    public boolean isViewSWPkgActionBarExist() throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent ViewSWPkg = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereViewSWActionBarActionBarCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return ViewSWPkg.isDisplayed();
    }

    public boolean nodeNameHeaderCheckReturnTest() throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent nodeNameHeader = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereNodeNameHeaderCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return nodeNameHeader.isDisplayed();
    }

    public boolean nameHeaderCheckReturnTest() throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent nodeNameHeader = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereNameHeaderCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return nodeNameHeader.isDisplayed();
    }

    public boolean prdNoHeaderCheckReturnTest() throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent prdNoHeader = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.werePrdNoHeaderCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return prdNoHeader.isDisplayed();
    }

    public boolean prdRevHeaderCheckReturnTest() throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent prdRevHeader = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.werePrdRevHeaderCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return prdRevHeader.isDisplayed();
    }

    public boolean typeHeaderCheckReturnTest() throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent nodeNameHeader = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereTypeHeaderCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return nodeNameHeader.isDisplayed();
    }

    public boolean dateHeaderCheckReturnTest() throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent dateHeader = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereDateHeaderCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return dateHeader.isDisplayed();

    }

    public boolean stateHeaderCheckReturnTest() throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent nodeNameHeader = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereStateHeaderCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return nodeNameHeader.isDisplayed();
    }

    public boolean executingUpHeaderCheckReturnTest()
            throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent executingUpHeaderCheck = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereExecutingUpHeaderCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return executingUpHeaderCheck.isDisplayed();
    }

    public boolean deltaUpHeaderCheckReturnTest() throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent deltaUpHeaderCheck = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereDeltaUpHeaderCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return deltaUpHeaderCheck.isDisplayed();
    }

    public boolean clearSelSWActionBarCheckReturnTest()
            throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent clearSelectionBar = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereClrSelSWActionBarCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return clearSelectionBar.isDisplayed();
    }

    public boolean deleteActionBarCheckReturnTest()
            throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent deleteSelectionBar = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereDeleteSWActionBarCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return deleteSelectionBar.isDisplayed();
    }

    public boolean exportActionBarCheckReturnTest()
            throws WaitTimedOutException {
        final ShmViewModel shmModel = currentBrowserTab
                .getView(ShmViewModel.class);
        final UiComponent exportSelectionBar = currentBrowserTab
                .waitUntilComponentIsDisplayed(
                        shmModel.wereExportSWActionBarCheckReturnTest(),
                        SHORT_UI_TIMEOUT_MILLIS);
        return exportSelectionBar.isDisplayed();
    }

    public ShmResponse selectNodes() {
        final ShmResponse shmResponse = new ShmResponse();
        final ShmViewModel select = currentBrowserTab
                .getView(ShmViewModel.class);
        try {
            final UiComponent firstCheckbox = currentBrowserTab
                    .waitUntilComponentIsDisplayed(select.getCheckBoxes(),
                            UI_TIMEOUT_MILLIS);
            firstCheckbox.click();
            clearSelSWActionBarCheckReturnTest();
            deleteActionBarCheckReturnTest();
            exportActionBarCheckReturnTest();
            final UiComponent softwareItemsButton = currentBrowserTab
                    .waitUntilComponentIsDisplayed(select.swItemsButton(),
                            UI_TIMEOUT_MILLIS);
            softwareItemsButton.click();
        } catch (final WaitTimedOutException e) {
            shmResponse.setSuccess(false);
            shmResponse.setError("Unable to select nodes on the SHM gui.");
            return shmResponse;
        }
        shmResponse.setSuccess(true);
        logger.info("Nodes have successfully been selected from SHM gui.");
        return shmResponse;
    }
}
