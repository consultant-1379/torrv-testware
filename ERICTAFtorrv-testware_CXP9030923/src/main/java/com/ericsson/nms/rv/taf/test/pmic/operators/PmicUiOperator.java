/*------------------------------------------------------------------------------
 *******************************************************************************
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.rv.taf.test.pmic.operators;

import java.text.DateFormatSymbols;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.ElementNotVisibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.WaitTimedOutException;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheUiOperator;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.NetworkExplorerUiOperator;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.NetworkExplorerResponse;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.DateTimePickerViewModel;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.SearchViewModel;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.pmic.PMInitiationViewModel;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.pmic.StatisticalSubscriptionViewModel;
import com.google.inject.Inject;

public class PmicUiOperator {
    private final Logger logger = LoggerFactory.getLogger(PmicUiOperator.class);
    private static final String PM_INITIATION = "/#pmiclistsubscription";
    private static final int LOOP_WAIT_TIMEOUT_MILLIS = 200;
    private static final int ACTIVATE_WAIT_TIMEOUT_MILLIS = 500;
    private static final int SHORT_UI_WAIT_TIMEOUT_MILLIS = 1000;
    private static final int ANIMATION_LOAD_TIMEOUT_MILLIS = 2000;
    private static final int NEXT_PAGE_WAIT_TIMEOUT_MILLIS = 2000;
    private static final int ELEMENT_LOADING_PAUSE = 3000;
    private static final int ELEMENT_NOT_VISIBLE_TIMEOUT = 5000;
    private static final int ACTION_TIMEOUT_MILLIS = 5000;
    private static final int PAGE_TIMEOUT_MILLIS = 5000;
    private static final int UPDATE_NODES_TIMEOUT_MILLIS = 15000;
    private static final int SAVE_TIMEOUT_MILLIS = 10000;
    private static final int REFRESH_TIMEOUT_MILLIS = 15000;
    private static final int LONG_TIMEOUT_MILLIS = 60000;

    @Inject
    NetworkExplorerUiOperator networkExplorer;

    public PmicResponse initialiseCurrentBrowserTab() {
        logger.info("Step: Initializing browser to 'PMIC initiation' page");
        final PmicResponse response = new PmicResponse();
        final Browser browser = ApacheUiOperator.getBrowser();
        ApacheUiOperator.skipTestIfUserIsNotLoggedIn(browser);

        final String url = "https://" + HostConfigurator.getApache().getIp()
                + PM_INITIATION;

        final BrowserTab currentBrowserTab = browser.getCurrentWindow();
        currentBrowserTab.open(url);
        response.setCurrentBrowserTab(currentBrowserTab);
        response.setSuccess(true);
        logger.info("Browser successfully initialised to 'PMIC initiation' page");
        return response;
    }

    public PmicResponse createStatisticalSubscription(
            final BrowserTab currentBrowserTab, final String subName,
            final String description) {

        logger.info(
                "Step: create statistical subscription. Name: {}, description: {}",
                subName, description);

        final PmicResponse response = new PmicResponse();

        if (!selectCreateStaticticalSubscriptionAction(currentBrowserTab,
                response).isSuccess()) {
            return response;
        }
        if (!setSubscriptionName(currentBrowserTab, response, subName)
                .isSuccess()) {
            return response;
        }
        if (!setSubscriptionDescription(currentBrowserTab, response,
                description).isSuccess()) {
            return response;
        }
        if (!saveSubscriptionAndGetSaveConfirmation(currentBrowserTab, response)
                .isSuccess()) {
            return response;
        }
        return response;
    }

    public PmicResponse addNodesToSubscriptionViaCollection(
            final BrowserTab currentBrowserTab, final String subName,
            final String collection, final List<String> nodesInCollection) {

        logger.info(
                "Step: Adding nodes to subscription via a collection. Subscription name: {}, collection name: {}, nodes: {}",
                subName, collection, nodesInCollection);

        PmicResponse response = new PmicResponse();
        final NetworkExplorerResponse networkExplorerResponse = new NetworkExplorerResponse();
        if (!openPmicPage(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subName).isSuccess()) {
            return response;
        }
        if (!clickEditSubscription(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionTabByName(currentBrowserTab, response,
                StatisticalSubscriptionViewModel.RESOURCES_TAB).isSuccess()) {
            return response;
        }
        if (!getNumberOfNodesInSubscription(currentBrowserTab, response)
                .isSuccess()) {
            return response;
        }
        final int numberOfNodesBefore = response.getTotalNodes();
        if (!selectTopologyAction(currentBrowserTab, response,
                StatisticalSubscriptionViewModel.ACTION_ADD_COLLECTIONS)
                .isSuccess()) {
            return response;
        }
        if (!networkExplorer.selectCollectionByNameAndReturnObjects(
                currentBrowserTab, networkExplorerResponse, collection)
                .isSuccess()) {
            response = getInformationFromNetworkExplorerResponse(response,
                    networkExplorerResponse);
            return response;
        }
        if (!getNumberOfNodesInSubscription(currentBrowserTab, response)
                .isSuccess()) {
            return response;
        }
        final int numberOfNodesAfter = response.getTotalNodes();
        if (!waitForTableToUpdate(currentBrowserTab, response,
                numberOfNodesBefore, numberOfNodesAfter).isSuccess()) {
            return response;
        }
        if (!saveSubscriptionAndGetSaveConfirmation(currentBrowserTab, response)
                .isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subName).isSuccess()) {
            return response;
        }
        if (!clickEditSubscription(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!verfiyNodesAddedToSubscription(currentBrowserTab, response,
                nodesInCollection).isSuccess()) {
            return response;
        }
        return response;
    }

    public PmicResponse addNodesToSubscriptionViaNetworkExplorer(
            final BrowserTab currentBrowserTab, final String subName,
            final List<String> nodeList) {

        logger.info(
                "Step: Adding nodes to subscription via Network Explorer. Subscription name: {}, nodes: {}",
                subName, nodeList);

        PmicResponse response = new PmicResponse();
        final NetworkExplorerResponse networkExplorerResponse = new NetworkExplorerResponse();
        if (!openPmicPage(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subName).isSuccess()) {
            return response;
        }
        if (!clickEditSubscription(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionTabByName(currentBrowserTab, response,
                StatisticalSubscriptionViewModel.RESOURCES_TAB).isSuccess()) {
            return response;
        }
        if (!getNumberOfNodesInSubscription(currentBrowserTab, response)
                .isSuccess()) {
            return response;
        }
        final int numberOfNodesBeforeAddNet = response.getTotalNodes();
        if (!selectTopologyAction(
                currentBrowserTab,
                response,
                StatisticalSubscriptionViewModel.ACTION_SEARCH_FOR_NETWORK_OBJECTS)
                .isSuccess()) {
            return response;
        }
        if (!networkExplorer.enterSearchCriteriaAndWaitForResultsToLoad(
                currentBrowserTab, networkExplorerResponse,
                SearchViewModel.NETWORK_ELEMENT).isSuccess()) {
            response = getInformationFromNetworkExplorerResponse(response,
                    networkExplorerResponse);
            return response;
        }
        if (!networkExplorer.selectNodesByNameAndReturnSelectedValues(
                currentBrowserTab, networkExplorerResponse, nodeList)
                .isSuccess()) {
            response = getInformationFromNetworkExplorerResponse(response,
                    networkExplorerResponse);
            return response;
        }
        if (!getNumberOfNodesInSubscription(currentBrowserTab, response)
                .isSuccess()) {
            return response;
        }
        final int numberOfNodesAfterAddNet = response.getTotalNodes();
        if (!waitForTableToUpdate(currentBrowserTab, response,
                numberOfNodesBeforeAddNet, numberOfNodesAfterAddNet)
                .isSuccess()) {
            return response;
        }
        if (!saveSubscriptionAndGetSaveConfirmation(currentBrowserTab, response)
                .isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subName).isSuccess()) {
            return response;
        }
        if (!clickEditSubscription(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!verfiyNodesAddedToSubscription(currentBrowserTab, response,
                nodeList).isSuccess()) {
            return response;
        }
        return response;
    }

    public PmicResponse addCountersToStatisticalSubscription(
            final BrowserTab currentBrowserTab, final String subName,
            final List<String> sourceObjectsNameList,
            final List<String> counterNameList) {

        logger.info(
                "Step: Adding counters to statistical subscription. Subscription name: {}, counters: {}, source objects: {}",
                subName, counterNameList, sourceObjectsNameList);

        final PmicResponse response = new PmicResponse();
        if (!openPmicPage(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subName).isSuccess()) {
            return response;
        }
        if (!clickEditSubscription(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionTabByName(currentBrowserTab, response,
                StatisticalSubscriptionViewModel.COUNTERS_TAB).isSuccess()) {
            return response;
        }
        if (!selectSourceObjectsByName(currentBrowserTab, response,
                sourceObjectsNameList).isSuccess()) {
            return response;
        }
        if (!selectCountersByName(currentBrowserTab, response, counterNameList)
                .isSuccess()) {
            return response;
        }
        if (!saveSubscriptionAndGetSaveConfirmation(currentBrowserTab, response)
                .isSuccess()) {
            return response;
        }
        return response;
    }

    public PmicResponse modifySchedulerDetails(
            final BrowserTab currentBrowserTab, final String subName,
            final String ropInterval) {

        logger.info("Step: Modifying scheduler details");

        final PmicResponse response = new PmicResponse();

        if (!openPmicPage(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subName).isSuccess()) {
            return response;
        }
        if (!clickEditSubscription(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionTabByName(currentBrowserTab, response,
                StatisticalSubscriptionViewModel.SCHEDULER_TAB).isSuccess()) {
            return response;
        }

        final Calendar startDateTime = new GregorianCalendar();
        startDateTime.add(Calendar.HOUR, +1);

        final Calendar endDateTime = new GregorianCalendar();
        endDateTime.add(Calendar.HOUR, +2);

        if (!setSchedulerStartDate(currentBrowserTab, response, startDateTime)
                .isSuccess()) {
            return response;
        }
        if (!setSchedulerEndDate(currentBrowserTab, response, endDateTime)
                .isSuccess()) {
            return response;
        }
        if (!setSchedulerRopInterval(currentBrowserTab, response, ropInterval)
                .isSuccess()) {
            return response;
        }
        if (!saveSubscriptionAndGetSaveConfirmation(currentBrowserTab, response)
                .isSuccess()) {
            return response;
        }
        return response;
    }

    public PmicResponse modifyRopSchedulerDetails(
            final BrowserTab currentBrowserTab, final String subName,
            final String ropInterval) {

        logger.info(
                "Step: Modifying ROP scheduler details. Subscription name: {}, rop interval: {}",
                subName, ropInterval);

        final PmicResponse response = new PmicResponse();
        if (!openPmicPage(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subName).isSuccess()) {
            return response;
        }
        if (!clickEditSubscription(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionTabByName(currentBrowserTab, response,
                StatisticalSubscriptionViewModel.SCHEDULER_TAB).isSuccess()) {
            return response;
        }
        if (!setSchedulerRopInterval(currentBrowserTab, response, ropInterval)
                .isSuccess()) {
            return response;
        }
        if (!saveSubscriptionAndGetSaveConfirmation(currentBrowserTab, response)
                .isSuccess()) {
            return response;
        }
        return response;
    }

    public PmicResponse activateStatisticalSubscription(
            final BrowserTab currentBrowserTab, final String subName) {

        logger.info("Step: Activate Statistical Subscription");

        final PmicResponse response = new PmicResponse();
        int numberOfNodesInSubscription = 0;
        if (!openPmicPage(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subName).isSuccess()) {
            return response;
        }
        if (!clickEditSubscription(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionTabByName(currentBrowserTab, response,
                StatisticalSubscriptionViewModel.RESOURCES_TAB).isSuccess()) {
            return response;
        }
        if (!getListOfNodesInTheSubscription(currentBrowserTab, response)
                .isSuccess()) {
            logger.error("{}", response.getErrorMessage());
        } else {
            numberOfNodesInSubscription = response
                    .getNodeNamesListInSubscription().size();
        }
        if (!openPmicPage(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subName).isSuccess()) {
            return response;
        }
        if (!clickActivateOnSubscriptionAndWaitForSuccessNotification(
                currentBrowserTab, response).isSuccess()) {
            return response;
        }
        logger.info("There are {} nodes in the subscription",
                numberOfNodesInSubscription);
        if (!verifySubscriptionHasBeenActivated(currentBrowserTab, response,
                subName, numberOfNodesInSubscription).isSuccess()) {
            return response;
        }

        return response;
    }

    public PmicResponse updateSubscriptionDescription(
            final BrowserTab currentBrowserTab, final String subName,
            final String description) {

        logger.info("Step: Updating subscription Description");

        final PmicResponse response = new PmicResponse();
        if (!openPmicPage(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subName).isSuccess()) {
            return response;
        }
        if (!clickEditSubscription(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!setSubscriptionDescription(currentBrowserTab, response,
                description).isSuccess()) {
            return response;
        }
        if (!saveSubscriptionAndGetSaveConfirmation(currentBrowserTab, response)
                .isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subName).isSuccess()) {
            return response;
        }
        if (!clickEditSubscription(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!verifyChangeInSubscriptionDescription(currentBrowserTab, response,
                description).isSuccess()) {
            return response;
        }

        return response;
    }

    public PmicResponse deleteStatisticalSubscription(
            final BrowserTab currentBrowserTab, final String subName) {

        logger.info("Step: Deleting Statistical Subscription.");

        final PmicResponse response = new PmicResponse();
        int numberOfNodesInSubscription = 0;
        if (!openPmicPage(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subName).isSuccess()) {
            return response;
        }
        if (!clickEditSubscription(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionTabByName(currentBrowserTab, response,
                StatisticalSubscriptionViewModel.RESOURCES_TAB).isSuccess()) {
            return response;
        }
        if (!getListOfNodesInTheSubscription(currentBrowserTab, response)
                .isSuccess()) {
            logger.error("{}", response.getErrorMessage());
        } else {
            numberOfNodesInSubscription = response
                    .getNodeNamesListInSubscription().size();
        }
        if (!openPmicPage(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subName).isSuccess()) {
            return response;
        }
        if (!clickDeleteOnSubscriptionAndWaitForSuccessNotification(
                currentBrowserTab, response, numberOfNodesInSubscription,
                subName).isSuccess()) {
            return response;
        }
        if (!verifySubscriptionHasBeenDeleted(currentBrowserTab, response,
                subName).isSuccess()) {
            return response;
        }
        return response;
    }

    public PmicResponse deleteNodesFromSubscription(
            final BrowserTab currentBrowserTab, final String subscriptionName,
            final List<String> nodesList) {

        logger.info(
                "Step: Delete nodes from subscription. Subscription name: {}, nodes: {}",
                subscriptionName, nodesList);

        final PmicResponse response = new PmicResponse();
        if (!openPmicPage(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subscriptionName).isSuccess()) {
            return response;
        }
        if (!clickEditSubscription(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionTabByName(currentBrowserTab, response,
                StatisticalSubscriptionViewModel.RESOURCES_TAB).isSuccess()) {
            return response;
        }
        if (!removeNodesFromSubscription(currentBrowserTab, response, nodesList)
                .isSuccess()) {
            return response;
        }
        if (!saveSubscriptionAndGetSaveConfirmation(currentBrowserTab, response)
                .isSuccess()) {
            return response;
        }
        if (!verifyNodesDeletedFromSubscription(currentBrowserTab, response,
                nodesList).isSuccess()) {
            return response;
        }
        return response;
    }

    public PmicResponse verifySubscriptionStatus(
            final BrowserTab currentBrowserTab, final String subscriptionName,
            final String statusToCheck) {

        logger.info("Step: Verify subscription status.");

        final PmicResponse response = new PmicResponse();
        if (!openPmicPage(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!getSubscriptionStatusAndVerify(currentBrowserTab, response,
                subscriptionName, statusToCheck).isSuccess()) {
            return response;
        }
        return response;
    }

    public PmicResponse getListOfNodesFromTheSubscription(
            final BrowserTab currentBrowserTab, final String subscriptionName) {

        logger.info("Step: Get list of nodes from the subscription.");

        final PmicResponse response = new PmicResponse();
        if (!openPmicPage(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subscriptionName).isSuccess()) {
            return response;
        }
        if (!clickEditSubscription(currentBrowserTab, response).isSuccess()) {
            return response;
        }
        if (!selectSubscriptionTabByName(currentBrowserTab, response,
                StatisticalSubscriptionViewModel.RESOURCES_TAB).isSuccess()) {
            return response;
        }
        if (!getListOfNodesInTheSubscription(currentBrowserTab, response)
                .isSuccess()) {
            return response;
        }
        return response;
    }

    /**
     * @param BrowserTab
     * @param StatisticalSubscriptionViewModel
     * @return List<String> - Returns a list of nodes in the subscription
     */
    private PmicResponse getListOfNodesInTheSubscription(final BrowserTab tab,
            final PmicResponse response) {
        final StatisticalSubscriptionViewModel view = tab
                .getView(StatisticalSubscriptionViewModel.class);
        final List<String> nodesList = new ArrayList<>();
        boolean check = true;
        while (check) {
            tab.waitUntilComponentIsHidden(view.getLoadingBar(),
                    PAGE_TIMEOUT_MILLIS);
            final List<UiComponent> nodesInSubscription = view
                    .getRowsOfNodesFromTable();
            for (final UiComponent node : nodesInSubscription) {
                final String[] nodesArray = node.getText().split("\\s+");
                nodesList.add(nodesArray[1]);
            }

            if (view.getPaginationNextButtonDisabled().exists()) {
                check = false;
            } else {
                response.setSuccess(waitForUiComponentAndClick(tab,
                        view.getPaginationNextButton(), ACTION_TIMEOUT_MILLIS));
                if (!response.isSuccess()) {
                    return setResponse(response, false,
                            "Timed out waiting for the nextpage button to be clicked");
                }
            }
        }
        response.setNodeNamesListInSubscription(nodesList);
        return response;
    }

    private PmicResponse getSubscriptionStatusAndVerify(
            final BrowserTab currentBrowserTab, final PmicResponse response,
            final String subscriptionName, final String statusToCheck) {
        final PMInitiationViewModel pmicInitView = currentBrowserTab
                .getView(PMInitiationViewModel.class);

        // Get status of subscription
        String status = "";
        try {
            status = getStatusBySubscriptionName(currentBrowserTab,
                    pmicInitView, subscriptionName);
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Timed out looking for subscription: " + subscriptionName);
        }
        // Check that status is not in intermediary step
        try {
            final Long startTime = System.currentTimeMillis();
            final int timeout = 60000;
            Long elapsed = 0L;
            while (status.equals(PMInitiationViewModel.UPDATING_STATUS)
                    || status.equals(PMInitiationViewModel.DEACTIVATING_STATUS)
                    || status.equals(PMInitiationViewModel.ACTIVATING_STATUS)) {
                currentBrowserTab.refreshPage();
                ApacheUiOperator.pause(LOOP_WAIT_TIMEOUT_MILLIS);
                status = getStatusBySubscriptionName(currentBrowserTab,
                        pmicInitView, subscriptionName);
                final Long currentTime = System.currentTimeMillis();
                elapsed = currentTime - startTime;
                if (elapsed > timeout) {
                    break;
                }
            }
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for subscription to be %s. Actual status: %s",
                            statusToCheck, status));

        }
        // Verify status is as expected
        if (status.equals(statusToCheck)) {
            response.setSuccess(true);
            logger.info("Status is as expected: " + statusToCheck);
        } else {
            setResponse(response, false, String.format(
                    "Expected Status: %s. Actual Status: %s", statusToCheck,
                    status));
        }
        return response;

    }

    private PmicResponse verifySubscriptionHasBeenDeleted(
            final BrowserTab currentBrowserTab, PmicResponse response,
            final String subscriptionName) {

        response = selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                currentBrowserTab, response, subscriptionName);
        // Success if the subscription does not exist.
        if (!response.isSuccess()) {
            return setResponse(response, true, String.format(
                    "Subscription %s deleted successfully.", subscriptionName));
        }
        return setResponse(
                response,
                false,
                String.format(
                        "Failed to delete subscription %s. Subscription still exists after delete command issued.",
                        subscriptionName));
    }

    private PmicResponse clickDeleteOnSubscriptionAndWaitForSuccessNotification(
            final BrowserTab currentBrowserTab, final PmicResponse response,
            final int numberOfNodesInSubscription, final String subscriptionName) {
        final PMInitiationViewModel view = currentBrowserTab
                .getView(PMInitiationViewModel.class);
        final String subscriptionStatus = getStatusBySubscriptionName(
                currentBrowserTab, view, subscriptionName);
        logger.info("Subscription status is {},", subscriptionStatus);

        if (subscriptionStatus.equals(PMInitiationViewModel.ACTIVE_STATUS)) {
            logger.info("Subscription {} is active, deactivating.",
                    subscriptionName);
            // Deactivate subscription and then select subscription again as it
            // looses focus.
            view.getDeactivateSubscriptionButton().click();

            try {
                currentBrowserTab.waitUntilComponentIsHidden(
                        view.getActivateDeactivateSuccessNotificationIcon(),
                        ACTION_TIMEOUT_MILLIS);
            } catch (final WaitTimedOutException e) {
                return setResponse(response, false, String.format(
                        "Failed to deactivate subscription %s.",
                        subscriptionName));
            }

            String status = "";
            try {
                final Long startTime = System.currentTimeMillis();
                final int timeOut = numberOfNodesInSubscription
                        * PAGE_TIMEOUT_MILLIS;
                Long elapsedTime = 0L;
                currentBrowserTab.refreshPage();
                status = getStatusBySubscriptionName(currentBrowserTab, view,
                        subscriptionName);
                while (PMInitiationViewModel.DEACTIVATING_STATUS.equals(status)) {
                    currentBrowserTab.refreshPage();
                    currentBrowserTab.waitUntilComponentIsDisplayed(
                            view.getPmSubscriptionTable(), PAGE_TIMEOUT_MILLIS);
                    status = getStatusBySubscriptionName(currentBrowserTab,
                            view, subscriptionName);
                    final Long currentTime = System.currentTimeMillis();
                    elapsedTime = currentTime - startTime;
                    if (elapsedTime > timeOut) {
                        return setResponse(response, false, String.format(
                                "Failed to deactivate subscription %s.",
                                subscriptionName));
                    }
                }
            } catch (final WaitTimedOutException e) {
                return setResponse(
                        response,
                        false,
                        String.format(
                                "Failed while deactivating subscription %s. Status expected: Inactive. Actual Status: %s",
                                subscriptionName, status));
            }

            try {
                selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
                        currentBrowserTab, response, subscriptionName);
            } catch (final WaitTimedOutException e) {
                return setResponse(response, false, String.format(
                        "Subscription %s does not exist.", subscriptionName));
            }
        }

        // Click the delete button
        response.setSuccess(waitForUiComponentAndClick(currentBrowserTab,
                view.getDeleteSubscriptionButton(), PAGE_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out while waiting for delete subscription button. Subscription %s.",
                            subscriptionName));
        }
        logger.info("Clicked delete button");

        // Wait for deletion confirmation to appear
        response.setSuccess(waitForUiComponentAndClick(currentBrowserTab,
                view.getDeleteSubscriptionConfirmationButton(),
                PAGE_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out while waiting for delete subscription confirmation. Subscription %s.",
                            subscriptionName));
        }

        try {
            final UiComponent successIcon = view
                    .getActivateDeactivateSuccessNotificationIcon();
            currentBrowserTab.waitUntilComponentIsDisplayed(successIcon,
                    ACTION_TIMEOUT_MILLIS);
            logger.info("Got success icon");
            currentBrowserTab.waitUntilComponentIsHidden(successIcon,
                    ACTION_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Failed to delete subscription %s while waiting for notification.",
                            subscriptionName));
        }
        response.setSuccess(true);
        logger.info("Delete action confirmed");
        return response;
    }

    private PmicResponse verifySubscriptionHasBeenActivated(
            final BrowserTab currentBrowserTab, final PmicResponse response,
            final String subscriptionName, final int numberOfNodesInSubscription) {
        final PMInitiationViewModel view = currentBrowserTab
                .getView(PMInitiationViewModel.class);
        String status = "";

        try {
            final Long activationStartTime = System.currentTimeMillis();
            final int activationTimeout = numberOfNodesInSubscription
                    * PAGE_TIMEOUT_MILLIS;
            Long elapsedTime = 0L;
            status = getStatusBySubscriptionName(currentBrowserTab, view,
                    subscriptionName);
            while (PMInitiationViewModel.ACTIVATING_STATUS.equals(status)
                    || PMInitiationViewModel.INACTIVE_STATUS.equals(status)) {
                currentBrowserTab.refreshPage();
                ApacheUiOperator.pause(ACTIVATE_WAIT_TIMEOUT_MILLIS);
                logger.info("Subscription is in 'Activating' phase.");
                currentBrowserTab.waitUntilComponentIsDisplayed(
                        view.getPmSubscriptionTable(), PAGE_TIMEOUT_MILLIS);
                status = getStatusBySubscriptionName(currentBrowserTab, view,
                        subscriptionName);
                final Long currentTime = System.currentTimeMillis();
                elapsedTime = currentTime - activationStartTime;
                if (elapsedTime > activationTimeout) {
                    throw new WaitTimedOutException(
                            "Timed out waiting for subscription to activate");
                }
            }
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false, String.format(
                    "Stuck in activating state for subscription %s.",
                    subscriptionName));
        }

        if (PMInitiationViewModel.ACTIVE_STATUS.equals(status)) {
            response.setSuccess(true);
            logger.info("Subscription '{}' has been activated",
                    subscriptionName);
        } else {
            return setResponse(response, false, String.format(
                    "Subscription '%s' has not been activated. Status is '%s'",
                    subscriptionName, status));

        }
        return response;
    }

    private PmicResponse clickActivateOnSubscriptionAndWaitForSuccessNotification(
            final BrowserTab currentBrowserTab, final PmicResponse response) {
        final PMInitiationViewModel view = currentBrowserTab
                .getView(PMInitiationViewModel.class);
        // Short pause to allow animation to load
        ApacheUiOperator.pause(ANIMATION_LOAD_TIMEOUT_MILLIS);
        response.setSuccess(waitForUiComponentAndClick(currentBrowserTab,
                view.getActivateSubscriptionButton(), PAGE_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(response, false,
                    "Timed out waiting for 'Activate' button to appear.");
        }

        // Give dialog box chance to show
        final Long startTime = System.currentTimeMillis();
        final int timeout = 1000;
        Long elapsed = 0L;
        while (!view.getDialogBox().exists()) {
            ApacheUiOperator.pause(LOOP_WAIT_TIMEOUT_MILLIS);
            final Long currentTime = System.currentTimeMillis();
            elapsed = currentTime - startTime;
            if (elapsed > timeout) {
                break;
            }
        }
        // Check if dialog box exists
        if (view.getDialogBox().exists()) {
            response.setSuccess(waitForUiComponentAndClick(
                    currentBrowserTab,
                    view.getDialogBoxButtonsByName(PMInitiationViewModel.CONTINUE_ACTIVATING_BUTTON),
                    PAGE_TIMEOUT_MILLIS));
            if (!response.isSuccess()) {
                return setResponse(response, false,
                        "Timed out waiting for 'Continue Acitivation' button to appear.");
            }
            // Wait for dialog box to disappear
            try {
                currentBrowserTab.waitUntilComponentIsHidden(
                        view.getDialogBox(), PAGE_TIMEOUT_MILLIS);
            } catch (final WaitTimedOutException e) {
                return setResponse(response, false,
                        "Timed out waiting for dialog box to disappear.");
            }
        }
        try {
            currentBrowserTab.waitUntilComponentIsHidden(
                    view.getActivateDeactivateSuccessNotificationIcon(),
                    ACTION_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Timed out waiting for activate notification to appear.");
        }
        response.setSuccess(true);
        logger.info("Successfully clicked activate on subscription");
        return response;
    }

    private PmicResponse getInformationFromNetworkExplorerResponse(
            final PmicResponse pmicResponse,
            final NetworkExplorerResponse networkExplorerResponse) {
        pmicResponse.setSuccess(networkExplorerResponse.isSuccess());
        pmicResponse.setErrorMessage(networkExplorerResponse.getErrorMessage());
        return pmicResponse;
    }

    private PmicResponse openPmicPage(final BrowserTab tab,
            final PmicResponse response) {

        logger.info("Opening PMIC page.");
        final PMInitiationViewModel view = tab
                .getView(PMInitiationViewModel.class);
        final String url = "https://" + HostConfigurator.getApache().getIp()
                + PM_INITIATION;
        tab.open(url);

        response.setSuccess(waitForUiComponentAndClick(tab,
                view.getPmSubscriptionTable(), PAGE_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(response, false,
                    "Unable to open PMIC initiation page");
        }

        response.setSuccess(true);
        logger.info("Browser successfully opened on PMIC initiation page");
        return response;
    }

    private PmicResponse selectSubscriptionByNameAndWaitForEditSubscriptionButtonToAppear(
            final BrowserTab tab, final PmicResponse response,
            final String subscriptionName) {

        logger.info("Selecting subscription by name and waiting for 'Edit Subscription' button to appear");

        final PMInitiationViewModel view = tab
                .getView(PMInitiationViewModel.class);
        // Wait on PMIC subscription table to load
        try {
            tab.waitUntilComponentIsDisplayed(view.getPmSubscriptionTable(),
                    PAGE_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Timed out waiting for 'PM Initiaion' table to load.");
        }
        boolean check = true;
        try {
            while (check) {
                final UiComponent subscription = tab
                        .waitUntilComponentIsDisplayed(
                                view.getSubscriptionByName(subscriptionName),
                                REFRESH_TIMEOUT_MILLIS);
                if (subscription.exists()) {
                    subscription.click();
                    if (!view.getTableRowHighlighted().exists()) {
                        logger.warn("Row is not highlighted");
                        subscription.click();
                        logger.warn("Clicked subscription");
                        tab.waitUntilComponentIsDisplayed(
                                view.getEditSubscriptionButton(),
                                PAGE_TIMEOUT_MILLIS);
                        check = false;
                    }
                    check = false;
                } else if (view.getPaginationNextButttonDisabled().exists()) {
                    return setResponse(response, false, String.format(
                            "Subscription '{}' was not found in the list",
                            subscriptionName));
                } else {
                    tab.waitUntilComponentIsDisplayed(
                            view.getPaginationNextButton(), PAGE_TIMEOUT_MILLIS)
                            .click();
                }

            }
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timeout exception thrown while trying to find subscription '{}' on table.",
                            subscriptionName));
        }
        response.setSuccess(true);
        logger.info("Subscription {} successfully selected", subscriptionName);
        return response;
    }

    private PmicResponse selectCreateStaticticalSubscriptionAction(
            final BrowserTab tab, final PmicResponse response) {
        final PMInitiationViewModel view = tab
                .getView(PMInitiationViewModel.class);

        logger.info("Attempting to select 'Create Subscription' button.");
        response.setSuccess(waitForUiComponentAndClick(tab,
                view.getActionButton(), PAGE_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(response, false,
                    "Timed out waiting for 'Create Subscription' button to appear.");
        }

        logger.info("Attempting to select 'Statistical Subscription' action in the dropdown.");
        ApacheUiOperator.pause(3000);

        response.setSuccess(waitForUiComponentAndClick(
                tab,
                view.getCreateSubscriptionDropdownButtonActionByName(PMInitiationViewModel.ACTION_CREATE_STATISTICAL_SUBSCRIPTION),
                PAGE_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(
                    response,
                    false,
                    "Timed out waiting for 'Statistical Subscription' action to appear in dropdown.");
        }
        logger.info("'Statistical Subscription' action selected.");
        return response;
    }

    private PmicResponse setSubscriptionName(
            final BrowserTab currentBrowserTab, final PmicResponse response,
            final String subscriptionName) {
        final StatisticalSubscriptionViewModel subsView = currentBrowserTab
                .getView(StatisticalSubscriptionViewModel.class);
        try {
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    subsView.getSubscriptionNameTextBox(), PAGE_TIMEOUT_MILLIS))
                    .setText(subscriptionName);
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Timed out waiting for subscription name textBox to appear.");
        }
        response.setSuccess(true);
        logger.info("Subscription name set to: '{}'", subscriptionName);
        return response;
    }

    private PmicResponse setSubscriptionDescription(
            final BrowserTab currentBrowserTab, final PmicResponse response,
            final String description) {
        logger.info("Attempting to set description to {}.", description);
        final StatisticalSubscriptionViewModel subsView = currentBrowserTab
                .getView(StatisticalSubscriptionViewModel.class);
        try {
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    subsView.getDescriptionTextBox(), PAGE_TIMEOUT_MILLIS))
                    .setText(description);
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Timed out waiting for description textbox to appear.");
        }
        response.setSuccess(true);
        logger.info("Subscription description set to: '{}'", description);
        return response;
    }

    private PmicResponse clickEditSubscription(final BrowserTab tab,
            final PmicResponse response) {

        logger.info("Selecting 'Edit subscription' button");
        final PMInitiationViewModel view = tab
                .getView(PMInitiationViewModel.class);
        // Short pause to allow animation to load
        ApacheUiOperator.pause(ANIMATION_LOAD_TIMEOUT_MILLIS);
        response.setSuccess(waitForUiComponentAndClick(tab,
                view.getEditSubscriptionButton(), PAGE_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(response, false,
                    "Timed out waiting for 'Edit Subscription' button to appear");
        }
        response.setSuccess(true);
        logger.info("'Edit subscription' button selected");
        return response;
    }

    private PmicResponse saveSubscriptionAndGetSaveConfirmation(
            final BrowserTab currentBrowserTab, final PmicResponse response) {

        logger.info("Attempting to save subscription");
        final StatisticalSubscriptionViewModel subsView = currentBrowserTab
                .getView(StatisticalSubscriptionViewModel.class);

        logger.info("Clicking save button");
        response.setSuccess(waitForUiComponentAndClick(currentBrowserTab,
                subsView.getSaveButton(), PAGE_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(response, false,
                    "Timed out waiting for 'Save' button to appear.");
        }
        logger.info("Clicked save button");

        logger.info("Wait for save confirmation to appear");
        response.setSuccess(waitForUiComponent(currentBrowserTab,
                subsView.getSaveConfirmationForSubscription(),
                SAVE_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(response, false,
                    "Timed out waiting for 'Save' confirmation to appear.");
        }
        response.setSuccess(true);
        logger.info("Subscription saved and confirmation recieved");
        return response;
    }

    private PmicResponse selectSubscriptionTabByName(
            final BrowserTab currentBrowserTab, final PmicResponse response,
            final String tabName) {

        logger.info("Selecting subscripton tab by name: {}", tabName);

        final StatisticalSubscriptionViewModel subsView = currentBrowserTab
                .getView(StatisticalSubscriptionViewModel.class);

        // Give tab name time to load
        ApacheUiOperator.pause(SHORT_UI_WAIT_TIMEOUT_MILLIS);
        response.setSuccess(waitForUiComponentAndClick(currentBrowserTab,
                subsView.getTabByName(tabName), REFRESH_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(response, false, String.format(
                    "Timed out waiting for %s tab to appear.", tabName));
        }

        response.setSuccess(waitForUiComponentAndClick(currentBrowserTab,
                subsView.waitForSubheadingToAppearUnderTab(),
                REFRESH_TIMEOUT_MILLIS));

        if (!response.isSuccess()) {
            return setResponse(response, false, String.format(
                    "Timed out waiting for %s tab to load.", tabName));
        }

        logger.info("'{}' tab selected", tabName);
        return response;
    }

    private PmicResponse selectTopologyAction(final BrowserTab tab,
            final PmicResponse response, final String action)
            throws WaitTimedOutException {

        logger.info("Selecting topology action: {}", action);

        final StatisticalSubscriptionViewModel view = tab
                .getView(StatisticalSubscriptionViewModel.class);
        response.setSuccess(waitForUiComponentAndClick(tab,
                view.getAddTopologyDataDropdown(), PAGE_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(response, false,
                    "Timed out waiting for %s topology action dropdown to appear");
        }

        response.setSuccess(waitForUiComponentAndClick(tab,
                view.getActionItemByName(action), PAGE_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for %s to appear in 'Add Topolgy Data' dropdown.",
                            action));
        }
        logger.info("Topology action '{}' selected", action);
        return response;
    }

    private PmicResponse verfiyNodesAddedToSubscription(final BrowserTab tab,
            final PmicResponse response, final List<String> nodesList) {
        final StatisticalSubscriptionViewModel view = tab
                .getView(StatisticalSubscriptionViewModel.class);
        final List<String> nodesToFind = new ArrayList<>(nodesList);

        tab.waitUntilComponentIsHidden(view.getLoadingBar(),
                PAGE_TIMEOUT_MILLIS);
        String nodeName;
        for (int i = 0; i < nodesList.size(); i++) {
            nodeName = nodesList.get(i);
            try {
                tab.waitUntilComponentIsDisplayed(
                        view.selectNodeByName(nodeName), ACTION_TIMEOUT_MILLIS)
                        .click();
                nodesToFind.remove(nodeName);
                logger.info("Node '{}' has been selected.", nodeName);
            } catch (final WaitTimedOutException e) {
                logger.info("Node '{}' was not found in the current table.",
                        nodeName);
                continue;
            }
        }

        if (nodesToFind.isEmpty()) {
            logger.info("All nodes were added successfully: {}",
                    nodesList.toString());
            response.setSuccess(true);
        } else if (view.getPaginationNextButtonDisabled().exists()) {
            return setResponse(response, false, String.format(
                    "Some nodes were not found on the list: %s",
                    nodesToFind.toString()));
        } else {
            tab.waitUntilComponentIsDisplayed(view.getPaginationNextButton(),
                    ACTION_TIMEOUT_MILLIS).click();
            verfiyNodesAddedToSubscription(tab, response, nodesToFind);
        }
        return response;
    }

    private PmicResponse waitForTableToUpdate(final BrowserTab tab,
            final PmicResponse response, final int numberOfNodesBefore,
            int numberOfNodesAfter) {
        final StatisticalSubscriptionViewModel view = tab
                .getView(StatisticalSubscriptionViewModel.class);
        final Long startTime = System.currentTimeMillis();
        int timeout = LONG_TIMEOUT_MILLIS;

        logger.info("Waiting for first row in node table to appear.");
        try {
            tab.waitUntilComponentIsDisplayed(view.getFirstRowFromNodesTable(),
                    LONG_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Table rows failed to appear on GUI.");
        }
        // Wait for list to appear
        ApacheUiOperator.pause(ANIMATION_LOAD_TIMEOUT_MILLIS);
        if (numberOfNodesBefore != numberOfNodesAfter) {
            timeout = Math.abs(numberOfNodesBefore - numberOfNodesAfter)
                    * UPDATE_NODES_TIMEOUT_MILLIS;
        } else if (numberOfNodesAfter != view.getRowsOfNodesFromTable().size()) {
            timeout = Math.abs(numberOfNodesAfter
                    - view.getRowsOfNodesFromTable().size())
                    * UPDATE_NODES_TIMEOUT_MILLIS;
        }

        logger.info("Timeout for table wait has been set to {}ms.", timeout);
        Long elapsed = 0L;
        Long currentTime = 0L;
        while (numberOfNodesBefore == numberOfNodesAfter
                || numberOfNodesAfter != view.getRowsOfNodesFromTable().size()) {
            currentTime = System.currentTimeMillis();
            ApacheUiOperator.pause(LOOP_WAIT_TIMEOUT_MILLIS);
            numberOfNodesAfter = numberOfNodesInSubscription(tab, view);
            elapsed = currentTime - startTime;
            if (elapsed > timeout) {
                return setResponse(response, false,
                        "Failed to update the nodes table in the subscription.");
            }
        }
        logger.info(
                "Time taken for table rows to appear after returning from Network Explorer: {} seconds",
                (System.currentTimeMillis() - startTime) / 1000);
        logger.info("Number of nodes in subscription before adding: {}",
                numberOfNodesBefore);
        logger.info("Number of nodes in list after adding: {}", view
                .getRowsOfNodesFromTable().size());
        response.setSuccess(true);
        return response;
    }

    private PmicResponse getNumberOfNodesInSubscription(final BrowserTab tab,
            final PmicResponse response) {

        logger.info("Getting number of nodes in subscription");

        final StatisticalSubscriptionViewModel view = tab
                .getView(StatisticalSubscriptionViewModel.class);
        final Long startTime = System.currentTimeMillis();

        logger.info(
                "- waiting for sub heading to appear on subscription page. Timeout: {}",
                ELEMENT_NOT_VISIBLE_TIMEOUT);

        while (System.currentTimeMillis() - startTime < ELEMENT_NOT_VISIBLE_TIMEOUT) {
            try {
                logger.info("Waiting for subheading to appear.");
                ApacheUiOperator.pause(LOOP_WAIT_TIMEOUT_MILLIS);
                waitForUiComponent(tab,
                        view.waitForSubheadingToAppearUnderTab(),
                        PAGE_TIMEOUT_MILLIS);
                response.setSuccess(true);
                break;
            } catch (final ElementNotVisibleException e) {
                logger.info("Load header on tab: ElementNotVisibleException thrown. Retrying try/catch block");
                setResponse(
                        response,
                        false,
                        String.format("Timed out waiting for header element to appear."));
                continue;
            } catch (final WaitTimedOutException e) {
                logger.info("Timed out waiting for tab header to appear.");
                return setResponse(
                        response,
                        false,
                        String.format("Timed out waiting for header element to appear."));
            }
        }
        if (!response.isSuccess()) {
            return response;
        }

        logger.info("- getting the number of nodes in the subscription");
        int numberOfNodes = 0;
        // Give node info a chance to display
        ApacheUiOperator.pause(SHORT_UI_WAIT_TIMEOUT_MILLIS);
        if (view.getNumberOfNodesFromTable().isDisplayed()) {
            try {
                numberOfNodes = numberOfNodesInSubscription(tab, view);
            } catch (final WaitTimedOutException e) {
                logger.error("- Timed out waiting for number of nodes info to appear in subscription page.");
            }
        } else {
            response.setTotalNodes(0);
            response.setSuccess(true);
            return response;
        }

        response.setTotalNodes(numberOfNodes);
        response.setSuccess(true);
        logger.info("Got number of nodes in subscription.");
        return response;
    }

    private PmicResponse selectSourceObjectsByName(final BrowserTab tab,
            final PmicResponse response, final List<String> sourceObjectNames) {
        final StatisticalSubscriptionViewModel view = tab
                .getView(StatisticalSubscriptionViewModel.class);
        logger.info("Waiting for counters table to load.");
        try {
            tab.waitUntilComponentIsDisplayed(
                    view.getCountersTabTableHeadings(), ACTION_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Unable to load table headers for Counters table.");
        }

        try {
            for (final String objectName : sourceObjectNames) {
                logger.info("Searching for Counter Names.");
                tab.waitUntilComponentIsDisplayed(
                        view.getSourceObjectSearchTextBox(),
                        ACTION_TIMEOUT_MILLIS).sendKeys(objectName);
                logger.info("Clicking on select all counters under source object.");
                tab.waitUntilComponentIsDisplayed(
                        view.selectAllFilteredCountersCheckBox(),
                        ACTION_TIMEOUT_MILLIS).click();
                logger.info("Click subscribe button.");
                // Allow subscribe button to appear
                ApacheUiOperator.pause(SHORT_UI_WAIT_TIMEOUT_MILLIS);
                tab.waitUntilComponentIsDisplayed(view.getSubscribeButton(),
                        ACTION_TIMEOUT_MILLIS).click();
                logger.info("Clearing search input for next search.");
                tab.waitUntilComponentIsDisplayed(
                        view.getSourceObjectSearchTextBox(),
                        ACTION_TIMEOUT_MILLIS).sendKeys(
                        "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
            }
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Search failed to load a component during search of Counters.");
        }

        response.setSuccess(true);
        logger.info("Source objects '{}' added to subscription",
                sourceObjectNames);
        return response;
    }

    private PmicResponse selectCountersByName(final BrowserTab tab,
            final PmicResponse response, final List<String> counterNames) {
        final StatisticalSubscriptionViewModel view = tab
                .getView(StatisticalSubscriptionViewModel.class);
        logger.info("Waiting for counters table to load.");
        try {
            tab.waitUntilComponentIsDisplayed(
                    view.getCountersTabTableHeadings(), ACTION_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Unable to return headings from Counters table.");
        }

        try {
            for (final String counterName : counterNames) {
                logger.info("Searching for Counter Names.");
                tab.waitUntilComponentIsDisplayed(
                        view.getCounterNameSearchTextBox(),
                        ACTION_TIMEOUT_MILLIS).sendKeys(counterName);
                logger.info("Clicking on first row returned from search.");
                tab.waitUntilComponentIsDisplayed(
                        view.getCounterTableBodyFirstRow(),
                        ACTION_TIMEOUT_MILLIS).click();
                logger.info("Click subscribe button.");
                // Allow subscribe button to appear
                ApacheUiOperator.pause(SHORT_UI_WAIT_TIMEOUT_MILLIS);
                tab.waitUntilComponentIsDisplayed(view.getSubscribeButton(),
                        ACTION_TIMEOUT_MILLIS).click();
                logger.info("Clearing search input for next search.");
                tab.waitUntilComponentIsDisplayed(
                        view.getCounterNameSearchTextBox(),
                        ACTION_TIMEOUT_MILLIS).sendKeys(
                        "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
            }
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Search failed to load a component during search of Counters.");
        }

        response.setSuccess(true);
        logger.info("Counters {} successfully added to the subscription",
                counterNames);
        return response;
    }

    private PmicResponse setSchedulerRopInterval(final BrowserTab tab,
            final PmicResponse response, final String ropInterval) {
        final StatisticalSubscriptionViewModel view = tab
                .getView(StatisticalSubscriptionViewModel.class);

        response.setSuccess(waitForUiComponentAndClick(tab,
                view.getRopSelectionBox(), ACTION_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(response, false,
                    "Timed out waiting for 'ROP Intevals' dropdown to appear.");
        }

        response.setSuccess(waitForUiComponentAndClick(tab,
                view.getDropDownValueByName(ropInterval), ACTION_TIMEOUT_MILLIS));
        if (!response.isSuccess()) {
            return setResponse(response, false,
                    "Timed out waiting for 'ROP Intevals' value to be selected.");
        }

        response.setSuccess(true);
        response.setRopInfo(ropInterval);
        logger.info("Rop Interval has been set to {} minutes", ropInterval);
        return response;
    }

    private PmicResponse setSchedulerStartDate(final BrowserTab tab,
            final PmicResponse response, final Calendar startDateTime) {
        final StatisticalSubscriptionViewModel view = tab
                .getView(StatisticalSubscriptionViewModel.class);
        try {
            fillStartDateTime(tab, view, startDateTime);
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Timed out during fill scheduler start DateTime.");
        }
        response.setSuccess(true);
        logger.info("Start date has been set to {}", startDateTime.getTime());
        return response;
    }

    private PmicResponse setSchedulerEndDate(final BrowserTab tab,
            final PmicResponse response, final Calendar endDateTime) {
        final StatisticalSubscriptionViewModel view = tab
                .getView(StatisticalSubscriptionViewModel.class);
        try {
            fillEndDateTime(tab, view, endDateTime);
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Timed out during fill scheduler end DateTime.");
        }
        response.setSuccess(true);
        logger.info("End date has been set to {}", endDateTime.getTime());
        return response;
    }

    private boolean waitForUiComponent(final BrowserTab tab,
            final UiComponent component, final int timeout) {
        try {
            tab.waitUntilComponentIsDisplayed(component, timeout);
            return true;
        } catch (final WaitTimedOutException e) {
            logger.warn(
                    "WaitTimedOutException occurred after {}ms on component {}",
                    timeout, component);
            return false;
        }
    }

    private boolean waitForUiComponentAndClick(final BrowserTab tab,
            final UiComponent component, final int timeout) {
        try {
            tab.waitUntilComponentIsDisplayed(component, timeout).click();
            return true;
        } catch (final WaitTimedOutException e) {
            logger.warn(
                    "WaitTimedOutException occurred after {}ms on component {}",
                    timeout, component);
            return false;
        }
    }

    private int numberOfNodesInSubscription(final BrowserTab tab,
            final StatisticalSubscriptionViewModel view)
            throws WaitTimedOutException {
        final UiComponent nodesInSubscription = tab
                .waitUntilComponentIsDisplayed(
                        view.getNumberOfNodesFromTable(), PAGE_TIMEOUT_MILLIS);
        final String numberOfNodes = nodesInSubscription.getText();
        final Matcher m = Pattern.compile("(\\d+)").matcher(numberOfNodes);
        final List<Integer> nodes = new ArrayList<>();
        while (m.find()) {
            nodes.add(Integer.parseInt(m.group()));
        }
        return nodes.get(nodes.size() - 1);
    }

    private PmicResponse setResponse(final PmicResponse response,
            final boolean success, final String error) {
        response.setSuccess(success);
        response.setErrorMessage(error);
        return response;
    }

    private PmicResponse verifyChangeInSubscriptionDescription(
            final BrowserTab tab, final PmicResponse response,
            final String description) {
        final StatisticalSubscriptionViewModel view = tab
                .getView(StatisticalSubscriptionViewModel.class);
        String newDescription = "";
        try {
            newDescription = tab.waitUntilComponentIsDisplayed(
                    view.getDescriptionTextBox(), PAGE_TIMEOUT_MILLIS)
                    .getText();
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Timed out waiting for description textbox to appear");
        }

        if (newDescription.equals(description)) {
            logger.info("Description of PMIC subscription updated.");
            response.setDescription(description);
            response.setSuccess(true);
        } else {
            return setResponse(response, false,
                    "Description has not been updated");
        }
        return response;
    }

    private PmicResponse removeNodesFromSubscription(final BrowserTab tab,
            final PmicResponse response, final List<String> nodeList) {
        logger.info("Removing nodes from subscription. Nodes: {}", nodeList);
        final StatisticalSubscriptionViewModel view = tab
                .getView(StatisticalSubscriptionViewModel.class);

        final List<String> nodesToRemove = new ArrayList<>(nodeList);

        try {
            tab.waitUntilComponentIsDisplayed(view.getNumberOfNodesFromTable(),
                    PAGE_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            logger.warn("No table of nodes loaded in the PMIC subscription.");
            return setResponse(response, false,
                    "There are no nodes in the subscription.");
        }

        final boolean morePage = true;
        while (morePage) {
            String nodeName = null;
            for (int i = 0; i < nodeList.size(); i++) {
                try {
                    nodeName = nodeList.get(i);
                    tab.waitUntilComponentIsDisplayed(
                            view.selectNodeByName(nodeName),
                            ACTION_TIMEOUT_MILLIS).click();
                    logger.info("Node '{}' has been selected.", nodeName);
                    nodesToRemove.remove(nodeName);
                    // Allow buttons pane to load fully.
                    ApacheUiOperator.pause(ANIMATION_LOAD_TIMEOUT_MILLIS);
                    tab.waitUntilComponentIsDisplayed(view.getRemoveButton(),
                            ACTION_TIMEOUT_MILLIS).click();
                    logger.info("Checking if 'Confirm Node Removal' dialog box exists.");
                    // Giving dialog box chance to appear
                    ApacheUiOperator.pause(ELEMENT_LOADING_PAUSE);
                    if (view.getDialogBox().exists()) {
                        tab.waitUntilComponentIsDisplayed(
                                view.getConfirmNodeRemovalButton(),
                                ACTION_TIMEOUT_MILLIS).click();
                    }
                    logger.info(
                            "Node '{}' has been removed from the subscription.",
                            nodeName);
                } catch (final WaitTimedOutException e) {
                    logger.info("Node '{}' was not found on this page.",
                            nodeName);
                    continue;
                }
            }
            logger.info("-- finished matching nodes to current table");
            if (nodesToRemove.isEmpty()) {
                logger.info("No more nodes to remove.");
                response.setSuccess(true);
                logger.info("Finished removing nodes from subscription.");
                return response;
            } else if (view.getNextPageLinkDisabled().exists()) {
                logger.warn("There are more nodes to remove but there are no more table pages to check.");
                ApacheUiOperator.takeScreenShot(tab,
                        "RemoveNodes_moreNodesButNextPageLinkDisabled");
                return setResponse(
                        response,
                        false,
                        String.format(
                                "No Next page available and node has not been found %s",
                                nodesToRemove));
            } else if (view.getNextPageLinkEnabled().exists()) {
                logger.info("Node list table: click to next page.");
                view.getNextPageLinkEnabled().click();
            }
        }
    }

    private PmicResponse verifyNodesDeletedFromSubscription(
            final BrowserTab tab, final PmicResponse response,
            final List<String> nodesList) {
        final StatisticalSubscriptionViewModel view = tab
                .getView(StatisticalSubscriptionViewModel.class);
        final List<String> nodesRemoved = new ArrayList<>(nodesList);
        boolean morePage = true;
        while (morePage) {
            final List<UiComponent> nodeRows = view.getRowsOfNodesFromTable();
            for (final String node : nodesList) {
                for (final UiComponent row : nodeRows) {
                    if (row.getText().contains(node)) {
                        return setResponse(
                                response,
                                false,
                                String.format(
                                        "Node %s found in subscription. Should have been deleted",
                                        node));
                    } else {
                        nodesRemoved.remove(node);
                        break;
                    }
                }
            }
            if (nodesRemoved.isEmpty()) {
                morePage = false;
                response.setSuccess(true);
                logger.info("All nodes removed from the subscription: {}",
                        nodesList);
                break;
            } else if (view.getNextPageLinkDisabled().exists()) {
                return setResponse(response, false,
                        String.format("No Next page avaliable"));
            } else {
                logger.info("Node list table: click to next page.");
                view.getNextPageLinkEnabled().click();
                morePage = true;
            }
        }
        return response;
    }

    /**
     * Assumes you are in the PM Initiation view and have selected the
     * subscription.
     *
     * @param tab
     * @param view
     * @param subscriptionName
     * @return statusInfo
     */
    private String getStatusBySubscriptionName(final BrowserTab tab,
            final PMInitiationViewModel view, final String subscriptionName) {
        // Nothing to wait on here. Dom doesn't change between pages
        // Wait to allow lists to update
        ApacheUiOperator.pause(NEXT_PAGE_WAIT_TIMEOUT_MILLIS);
        final List<UiComponent> nameList = view.getSubscriptionNameList();
        final List<UiComponent> statusList = view.getStatusList();

        String statusInfo = "";
        int i = 0;
        for (final UiComponent name : nameList) {
            if (name.getText().equals(subscriptionName)) {
                statusInfo = statusList.get(i).getText();
                return statusInfo;
            }
            i++;
        }
        if (!view.getPaginationNextButttonDisabled().exists()) {
            tab.waitUntilComponentIsDisplayed(view.getPaginationNextButton(),
                    PAGE_TIMEOUT_MILLIS).click();
            statusInfo = this.getStatusBySubscriptionName(tab, view,
                    subscriptionName);
        }
        return statusInfo;
    }

    // Fill scheduler start date time details
    private void fillStartDateTime(final BrowserTab tab,
            final StatisticalSubscriptionViewModel subsView,
            final Calendar startDateTime) throws WaitTimedOutException {
        final DateTimePickerViewModel pickerView = tab
                .getView(DateTimePickerViewModel.class);
        // click start date drop down button
        tab.waitUntilComponentIsDisplayed(subsView.getFromDropdownBox(),
                PAGE_TIMEOUT_MILLIS).click();
        // select "Date" item
        tab.waitUntilComponentIsDisplayed(subsView.getActionItemByName("Date"),
                PAGE_TIMEOUT_MILLIS).click();
        // click TimeDate picker input
        tab.waitUntilComponentIsDisplayed(subsView.getStartTimeDatePicker(),
                PAGE_TIMEOUT_MILLIS).click();
        // waiting for timeDate picker popup
        tab.waitUntilComponentIsDisplayed(
                subsView.getStartDateTimePickerPopup(), PAGE_TIMEOUT_MILLIS);
        // fill date time
        setTime(tab, pickerView, startDateTime);
    }

    // Fill scheduler end date time details
    private void fillEndDateTime(final BrowserTab tab,
            final StatisticalSubscriptionViewModel subsView,
            final Calendar endDateTime) throws WaitTimedOutException {
        final DateTimePickerViewModel pickerView = tab
                .getView(DateTimePickerViewModel.class);
        // click start date drop down button
        tab.waitUntilComponentIsDisplayed(subsView.getUntilDropdownBox(),
                PAGE_TIMEOUT_MILLIS).click();
        // select "Date" item
        tab.waitUntilComponentIsDisplayed(subsView.getActionItemByName("Date"),
                PAGE_TIMEOUT_MILLIS).click();
        // click TimeDate picker input
        tab.waitUntilComponentIsDisplayed(subsView.getEndTimeDatePicker(),
                PAGE_TIMEOUT_MILLIS).click();
        // waiting for timeDate picker popup
        tab.waitUntilComponentIsDisplayed(subsView.getEndDateTimePickerPopup(),
                PAGE_TIMEOUT_MILLIS);
        // fill date time
        setTime(tab, pickerView, endDateTime);
    }

    // set date time in DateTime Picker
    private void setTime(final BrowserTab tab,
            final DateTimePickerViewModel pickerView, final Calendar calendar)
            throws WaitTimedOutException {
        final int newYear = calendar.get(Calendar.YEAR);
        final int newMonth = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int min = calendar.get(Calendar.MINUTE);
        final int sec = calendar.get(Calendar.SECOND);
        final TextBox hourInput = pickerView.getHourInput();
        hourInput.click();
        hourInput.setText(String.valueOf(hour));

        final TextBox minInput = pickerView.getMinInput();
        minInput.click();
        minInput.setText(String.valueOf(min));

        final TextBox secInput = pickerView.getSecInput();
        secInput.click();
        secInput.setText(String.valueOf(sec));

        final String[] dateMonth = pickerView.getMonthYear().getText()
                .split(" ");
        final String currentMonth = dateMonth[0];
        final int currentMonthInt = getMonthInt(currentMonth);
        final String currentYear = dateMonth[1];

        final int differenceYear = Integer.valueOf(currentYear)
                - Integer.valueOf(newYear);
        if (differenceYear > 0) {
            for (int i = 0; i < differenceYear; i++) {
                pickerView.getPrevYear().click();
            }
        }

        final int differenceMonth = currentMonthInt - Integer.valueOf(newMonth);
        if (differenceMonth > 0) {
            for (int i = 0; i < Math.abs(differenceMonth); i++) {
                pickerView.getPrevMonth().click();
            }
        } else if (differenceMonth < 0) {
            for (int i = 0; i < Math.abs(differenceMonth); i++) {
                pickerView.getNextMonth().click();
            }
        }
        tab.waitUntilComponentIsDisplayed(
                pickerView.getDayPicker(String.valueOf(day)),
                PAGE_TIMEOUT_MILLIS).click();
        tab.waitUntilComponentIsDisplayed(pickerView.getOkComponent(),
                PAGE_TIMEOUT_MILLIS).click();
    }

    private int getMonthInt(final String month) {
        final List<String> monthsOfTheYear = new ArrayList<>(
                Arrays.asList(new DateFormatSymbols().getMonths()));
        int numberOfMonth = 0;
        if (monthsOfTheYear.contains(month)) {
            numberOfMonth = monthsOfTheYear.indexOf(month) + 1;
        }
        return numberOfMonth;
    }
}