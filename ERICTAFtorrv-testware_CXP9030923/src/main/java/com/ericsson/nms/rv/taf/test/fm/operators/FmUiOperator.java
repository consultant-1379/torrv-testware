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
package com.ericsson.nms.rv.taf.test.fm.operators;

import java.util.*;

import org.openqa.selenium.StaleElementReferenceException;
import org.slf4j.Logger;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.WaitTimedOutException;
import com.ericsson.cifwk.taf.ui.sdk.*;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheUiOperator;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.ResultsViewModel;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.SearchViewModel;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.fm.FmViewModel;

@Operator(context = Context.UI)
public class FmUiOperator {

    private static final String FM_ALARM_MONITOR = "/#alarmviewer";
    private static final String FM_ALARM_HISTORY = "/#alarmhistory";
    private static final int STALE_ELEMENT_TIMEOUT = 2000;
    private static final int COMPONENT_TIMEOUT = 5000;
    private static final int UPDATE_TIMEOUT = 12000;
    private static final long LOADING_WIDGET_TIMEOUT_MILLIS = 30000;

    private final Logger logger = org.slf4j.LoggerFactory
            .getLogger(FmUiOperator.class);

    protected Browser browser = null;
    protected BrowserTab currentBrowserTab = null;

    public BrowserTab getAlarmMonitoringPage() {
        logger.info("Opening FM alarm monitoring page");
        browser = ApacheUiOperator.getBrowser();
        ApacheUiOperator.skipTestIfUserIsNotLoggedIn(browser);

        currentBrowserTab = browser.getCurrentWindow();
        currentBrowserTab.open("https://"
                + HostConfigurator.getApache().getIp() + FM_ALARM_MONITOR);
        logger.info("Opened FM alarm monitoring page. Current url: {}",
                currentBrowserTab.getCurrentUrl());

        final FmViewModel view = currentBrowserTab.getView(FmViewModel.class);
        ApacheUiOperator.waitUntilLoaderIsHidden(currentBrowserTab, view,
                LOADING_WIDGET_TIMEOUT_MILLIS);

        return currentBrowserTab;
    }

    public BrowserTab getAlarmHistoryPage() {

        logger.info("Opening FM alarm history page");
        browser = ApacheUiOperator.getBrowser();
        ApacheUiOperator.skipTestIfUserIsNotLoggedIn(browser);

        currentBrowserTab = browser.getCurrentWindow();
        currentBrowserTab.open("https://"
                + HostConfigurator.getApache().getIp() + FM_ALARM_HISTORY);
        return currentBrowserTab;
    }

    public FmAlarmResponse addNodeBySearchNetworkObject(
            final String nodeManagedElementId) {
        logger.info("Adding node by searching by NetworkElement.");
        final FmViewModel viewModel = currentBrowserTab
                .getView(FmViewModel.class);

        ApacheUiOperator.waitUntilLoaderIsHidden(currentBrowserTab, viewModel,
                LOADING_WIDGET_TIMEOUT_MILLIS);

        FmAlarmResponse response = clickUiComponent(
                viewModel.getTopologyDropDownButton(),
                "topology drop down button");
        if (!response.isSuccess()) {
            return response;
        }
        response = clickUiComponent(
                viewModel.getDropDownItemByName("Search for Network Objects"),
                "'Search for Network Objects' drop down item");
        if (!response.isSuccess()) {
            return response;
        }
        doSearch("NetworkElement name=" + nodeManagedElementId);
        response = selectSpecificRowsFromResultsTable(1);
        if (!response.isSuccess()) {
            return response;
        }
        clickReturnedSelectedObjectsButton();
        return response;
    }

    public FmAlarmResponse removeNodeFromList(final String nodeManagedElementId) {
        logger.info("Removing node from list: {}", nodeManagedElementId);
        FmAlarmResponse fmAlarmResponse;
        fmAlarmResponse = clickNetworkElements(nodeManagedElementId);
        if (!fmAlarmResponse.isSuccess()) {
            return fmAlarmResponse;
        } else {
            fmAlarmResponse = clickNetworkElementsContextMenuButton();
            if (!fmAlarmResponse.isSuccess()) {
                return fmAlarmResponse;
            }
            fmAlarmResponse = clickRemoveFromList();
        }
        return fmAlarmResponse;
    }

    public FmAlarmResponse clearAlarm(final String nodeName) {
        logger.info("Clearing Alarm for node: {}", nodeName);
        FmAlarmResponse fmResp = clickAlarmOnTable(0);
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
            return fmResp;
        } else {

            fmResp = alarmAcknowledge();
            if (!fmResp.isSuccess()) {
                logger.warn(fmResp.getErrorMessage());
            }

            fmResp = clickClearLink();
            if (!fmResp.isSuccess()) {
                logger.warn(fmResp.getErrorMessage());
            }
        }
        logger.info("Alarm has been cleared.");
        return fmResp;
    }

    private void doSearch(final String query) {
        logger.info("Performing search with query: {}", query);
        final SearchViewModel searchViewModel = currentBrowserTab
                .getView(SearchViewModel.class);
        try {
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    searchViewModel.getSearchInput(), COMPONENT_TIMEOUT))
                    .setText(query);
        } catch (final WaitTimedOutException e) {
            logger.error("Timed out while waiting for " + "search box"
                    + " to display. Waited " + COMPONENT_TIMEOUT
                    + " milliseconds");
        }
        UiComponent searchButton = null;
        try {
            searchButton = currentBrowserTab.waitUntilComponentIsDisplayed(
                    searchViewModel.getSearchButton(), STALE_ELEMENT_TIMEOUT);
            searchButton.click();
        } catch (final WaitTimedOutException e) {
            logger.error("Timed out while waiting for " + "search button"
                    + " to display. Waited " + STALE_ELEMENT_TIMEOUT
                    + " milliseconds");
        }

        ApacheUiOperator.waitUntilLoaderIsHidden(currentBrowserTab,
                searchViewModel, LOADING_WIDGET_TIMEOUT_MILLIS);
    }

    // will click "n" number of rows from results table (counting from the top).
    // accepts arguments greater than 0!
    private FmAlarmResponse selectSpecificRowsFromResultsTable(final int n) {
        logger.info("Selecting results from table at row: {}", n);
        final FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();
        if (n > 0) {
            final ResultsViewModel resultsViewModel = currentBrowserTab
                    .getView(ResultsViewModel.class);
            // wait for rows (bug in TAF, can't rely on checkBoxes being
            // reported visible)
            try {
                currentBrowserTab.waitUntilComponentIsDisplayed(
                        resultsViewModel.getFirstTableRow(), UPDATE_TIMEOUT);
            } catch (final WaitTimedOutException e) {
                return setResponse(fmAlarmResponse, false,
                        "Timed out while waiting for " + "First Table Row"
                                + " to display. Waited " + UPDATE_TIMEOUT
                                + " milliseconds");
            }

            // first row is selecting all, so starting loop from 2nd!
            for (int i = 1; i <= n; i++) {
                resultsViewModel.getResultRowCheckboxes().get(i).click();
            }
        }
        fmAlarmResponse.setSuccess(true);
        return fmAlarmResponse;
    }

    private void clickReturnedSelectedObjectsButton() {
        final SearchViewModel searchViewModel = currentBrowserTab
                .getView(SearchViewModel.class);
        searchViewModel.getReturnObjectsButton().click();
    }

    private FmAlarmResponse clickUiComponent(final UiComponent uiComponent,
            final String description) {
        logger.info("Clicking component: {}", description);
        final FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();
        try {
            final UiComponent component = currentBrowserTab
                    .waitUntilComponentIsDisplayed(uiComponent,
                            COMPONENT_TIMEOUT);
            // Doesn't add Node without pause
            ApacheUiOperator.pause(500);
            component.click();
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    fmAlarmResponse,
                    false,
                    String.format("Timed out while waiting for " + "%s"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds", description));
        }
        fmAlarmResponse.setSuccess(true);
        return fmAlarmResponse;
    }

    private FmAlarmResponse clickAdvancedFiltersButton() {
        final FmViewModel viewModel = currentBrowserTab
                .getView(FmViewModel.class);
        final FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();
        try {
            final UiComponent button = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            viewModel.getAdvancedFilter(), COMPONENT_TIMEOUT);
            button.click();
        } catch (final WaitTimedOutException e) {
            return setResponse(fmAlarmResponse, false,
                    "Timed out while waiting for " + "Advanced Filters"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
        fmAlarmResponse.setSuccess(true);
        return fmAlarmResponse;
    }

    public UiComponent getAlarmTable() {
        final FmViewModel viewModel = currentBrowserTab
                .getView(FmViewModel.class);
        final UiComponent table = currentBrowserTab
                .waitUntilComponentIsDisplayed(viewModel.getAlarmTable(),
                        UPDATE_TIMEOUT);
        return table;
    }

    /**
     * Click alarms on alarm table.
     *
     * @param id
     *            start from 0
     */
    public FmAlarmResponse clickAlarmOnTable(final int id) {
        logger.info("Clicking alarm on table with id: {}", id);
        FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();
        final FmViewModel resultsViewModel = currentBrowserTab
                .getView(FmViewModel.class);
        List<UiComponent> rows = new ArrayList<>();

        if (!verifyAlarmTablesDisplay()) {
            return setResponse(fmAlarmResponse, false,
                    "Alarm table not displayed.");
        }
        logger.info("Alarm table is displayed.");

        final Long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < STALE_ELEMENT_TIMEOUT) {
            try {
                rows = resultsViewModel.getMainTableRows();
                if (rows.isEmpty()) {
                    return setResponse(fmAlarmResponse, false,
                            "The row list is empty. Please make sure XPath is correct and there is result.");
                }
                logger.info("Got table rows. Size: {}", rows.size());
                logger.debug("Row data: {}", rows.get(id).getText());

                final UiComponent row = rows.get(id);
                logger.info("Table row class name: {}",
                        row.getProperty("class"));
                final boolean rowIsSelected = row.getProperty("class")
                        .contains("highlighted");
                if (rowIsSelected) {
                    logger.info("Row is already selected. Skipping click operation.");
                } else {
                    logger.info("Clicking on row with id: {}", id);
                    row.click();
                    logger.info("Clicked on row with id: {}", id);
                    ApacheUiOperator.pause(1000); //Wait for buttons to appear
                }

                fmAlarmResponse.setSuccess(true);
                break;
            } catch (final StaleElementReferenceException e) {
                logger.info("Click alarm on table: StaleElementReferenceException thrown. Retrying try/catch block");
                fmAlarmResponse = setResponse(fmAlarmResponse, false,
                        "Click alarm on table failed with StaleElementReferenceException");
                continue;
            } catch (final WaitTimedOutException e) {
                return setResponse(
                        fmAlarmResponse,
                        false,
                        String.format(
                                "WaitTimedOutException thrown  after %dms while trying to click alarm on row number %d in table.",
                                COMPONENT_TIMEOUT, id));
            }
        }
        return fmAlarmResponse;
    }

    public FmAlarmResponse clickAddCommentLink() {
        final FmViewModel viewModel = currentBrowserTab
                .getView(FmViewModel.class);
        final FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();
        Link addComment = null;
        try {
            addComment = (Link) currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            viewModel.getAddCommentButton(), COMPONENT_TIMEOUT);
        } catch (final WaitTimedOutException e) {
            return setResponse(fmAlarmResponse, false,
                    "Add comment link not found");
        }
        addComment.click();
        fmAlarmResponse.setSuccess(true);
        return fmAlarmResponse;
    }

    public FmAlarmResponse alarmAcknowledge() {
        logger.info("Acknowledge alarm");
        final FmViewModel viewModel = currentBrowserTab
                .getView(FmViewModel.class);
        FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();

        ApacheUiOperator.waitUntilLoaderIsHidden(currentBrowserTab, viewModel,
                LOADING_WIDGET_TIMEOUT_MILLIS);

        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    viewModel.getAcknowledgeButton(), COMPONENT_TIMEOUT)
                    .click();
            fmAlarmResponse.setSuccess(true);
        } catch (final WaitTimedOutException e) {
            fmAlarmResponse = setResponse(fmAlarmResponse, false,
                    "Timed out while waiting for " + "Acknowledge Link"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
        return fmAlarmResponse;
    }

    public FmAlarmResponse alarmUnacknowledge() {
        final FmViewModel viewModel = currentBrowserTab
                .getView(FmViewModel.class);
        FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();

        try {
            currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            viewModel
                                    .getQuickActionLink(FmViewModel.UNACKNOWLEDGE_BUTTON),
                            COMPONENT_TIMEOUT).click();
            fmAlarmResponse.setSuccess(true);
        } catch (final WaitTimedOutException e) {
            fmAlarmResponse = setResponse(fmAlarmResponse, false,
                    "Timed out while waiting for " + "UnAcknowledge Link"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
        return fmAlarmResponse;
    }

    public FmAlarmResponse clickClearLink() {
        logger.info("Click 'clear' link.");
        final FmViewModel viewModel = currentBrowserTab
                .getView(FmViewModel.class);
        final FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();
        try {
            final Button clear = (Button) currentBrowserTab
                    .waitUntilComponentIsDisplayed(viewModel.getClearButton(),
                            COMPONENT_TIMEOUT);
            clear.click();
            fmAlarmResponse.setSuccess(true);
            return fmAlarmResponse;
        } catch (final WaitTimedOutException e) {
            return setResponse(fmAlarmResponse, false,
                    "Timed out while waiting for " + "Clear alarm link"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
    }

    public FmAlarmResponse inputComment(final String comment) {
        final FmViewModel viewModel = currentBrowserTab
                .getView(FmViewModel.class);
        FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();
        try {
            final TextBox commentTextArea = (TextBox) currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            viewModel.getCommentTextArea(), COMPONENT_TIMEOUT);
            commentTextArea.setText(comment);
            fmAlarmResponse.setSuccess(true);
        } catch (final WaitTimedOutException e) {
            fmAlarmResponse = setResponse(fmAlarmResponse, false,
                    "Timed out while waiting for " + "comment text area"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
        return fmAlarmResponse;
    }

    public FmAlarmResponse enterProblemSearchText(final String specificProblem) {
        logger.info("Enter problem search text: {}", specificProblem);
        final FmViewModel viewModel = currentBrowserTab
                .getView(FmViewModel.class);
        final FmAlarmResponse fmAlarmResponse = clickAdvancedFiltersButton();
        if (!fmAlarmResponse.isSuccess()) {
            return fmAlarmResponse;
        }
        TextBox searchBox = null;
        try {
            searchBox = (TextBox) currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            viewModel.getProblemSearchInput(),
                            COMPONENT_TIMEOUT);
        } catch (final WaitTimedOutException e) {
            return setResponse(fmAlarmResponse, false,
                    "Timed out while waiting for "
                            + "Specific Problem search input"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
        searchBox.setText(specificProblem);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    viewModel.getApplySearchButton(), COMPONENT_TIMEOUT)
                    .click();
        } catch (final WaitTimedOutException e) {
            return setResponse(fmAlarmResponse, false,
                    "Timed out while waiting for "
                            + "Alarm Search Apply button"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
        fmAlarmResponse.setSuccess(true);
        return fmAlarmResponse;
    }

    public FmAlarmResponse selectFilters(final String... severities) {
        FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();

        final Long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < STALE_ELEMENT_TIMEOUT) {
            try {
                final List<String> severitiesList = new ArrayList<String>(
                        Arrays.asList(severities));
                final FmViewModel fmAlarmViewModel = currentBrowserTab
                        .getView(FmViewModel.class);
                currentBrowserTab.waitUntilComponentIsDisplayed(
                        fmAlarmViewModel.getAdvancedFilter(),
                        STALE_ELEMENT_TIMEOUT).click();
                currentBrowserTab.waitUntilComponentIsDisplayed(
                        fmAlarmViewModel.getFiltersCheckBoxes().get(0), 1000);
                final List<UiComponent> checkBoxes = fmAlarmViewModel
                        .getFiltersCheckBoxes();

                for (int i = 0; i < checkBoxes.size(); i++) {
                    final UiComponent checkBox = checkBoxes.get(i);
                    final String value = checkBox.getProperty("data-id");
                    final UiComponent box = checkBox.getDescendantsBySelector(
                            ".ebCheckbox").get(0);
                    if (severitiesList.contains(value)) {
                        if (!box.isSelected()) {
                            box.click();
                        }
                        severitiesList.remove(value);
                    } else {
                        if (box.isSelected()) {
                            box.click();
                        }
                    }
                }
                fmAlarmResponse.setSuccess(true);
                break;
            } catch (final StaleElementReferenceException e) {
                logger.info("Select filters: StaleElementReferenceException thrown. Retrying try/catch block");
                fmAlarmResponse = setResponse(fmAlarmResponse, false,
                        "StaleElementReferenceException is thrown when selecting filter.");
                continue;
            } catch (final WaitTimedOutException e) {
                return setResponse(fmAlarmResponse, false,
                        "WaitTimedOutException is thrown while waiting for filters.");
            }
        }
        return fmAlarmResponse;
    }

    public FmAlarmResponse getFirstAlarm() {
        logger.info("Getting first alarm in FM Gui");
        FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();
        final FmViewModel resultsViewModel = currentBrowserTab
                .getView(FmViewModel.class);
        final Map<String, String> rowMap = new HashMap<String, String>();

        if (!verifyAlarmTablesDisplay()) {
            fmAlarmResponse.setFirstAlarmMap(rowMap);
            fmAlarmResponse.setSuccess(false);
            fmAlarmResponse.setErrorMessage("Alarm table not displayed.");
            return fmAlarmResponse;
        }
        logger.info("Alarm table is displayed");

        final List<UiComponent> tableHeadersList = resultsViewModel
                .getMainTableHeaders();
        final int headerNum = tableHeadersList.size();
        final String[] headers = new String[headerNum - 1];
        for (int i = 0; i < headerNum - 1; i++) {
            headers[i] = tableHeadersList.get(i + 1).getText();
        }

        final Long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < STALE_ELEMENT_TIMEOUT) {
            logger.info("Checking for alarm.");
            final List<UiComponent> tableRowsList = resultsViewModel
                    .getMainTableRows();
            final List<UiComponent> cells = tableRowsList.get(0).getChildren();
            try {
                for (int i = 0; i < cells.size() - 1; i++) {
                    // skip the first empty column called Repeated
                    rowMap.put(headers[i], cells.get(i + 1).getText());
                }
                fmAlarmResponse.setFirstAlarmMap(rowMap);
                fmAlarmResponse.setSuccess(true);
                logger.info("Got first alarm in FM Gui");
                break;
            } catch (final StaleElementReferenceException e) {
                logger.info("Get first alarm: StaleElementReferenceException thrown. Retrying try/catch block");
                fmAlarmResponse = setResponse(fmAlarmResponse, false,
                        "Get first alarm failed: StaleElementReferenceException");
                continue;
            }
        }
        return fmAlarmResponse;
    }

    /**
     * This method is used for ST KPIs. It assumes that the alarm page is open
     * and ready to receive an alarm. The intention is to get the result as
     * quick as possible so there is less confirmation.
     *
     * @param alarmProblem
     * @param timeoutMillis
     * @return
     */
    public FmAlarmResponse pollForAlarm(final String alarmProblem,
            final long timeoutMillis) {
        logger.info("Polling for alarm '{}' in FM Gui. Timeout: {}ms.",
                alarmProblem, timeoutMillis);
        final FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();
        final FmViewModel resultsViewModel = currentBrowserTab
                .getView(FmViewModel.class);

        final long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            final List<UiComponent> tableRowsList = resultsViewModel
                    .getMainTableRows();
            logger.info("Polling: Got {} table rows", tableRowsList.size());
            if (tableRowsList.size() > 0) {
                final long endTime = System.currentTimeMillis();
                for (final UiComponent comp : tableRowsList) {
                    logger.debug("Component text: {}", comp.getText());
                    if (comp.getText().contains(alarmProblem)) {
                        logger.info("Found alarm '{}'", alarmProblem);
                        fmAlarmResponse.setSuccess(true);
                        fmAlarmResponse.setTimeAlarmReadMillis(endTime);
                        return fmAlarmResponse;
                    }
                }
            }
        }
        logger.info("Did not find alarm '{}' in given time '{}' milliseconds",
                alarmProblem, timeoutMillis);
        fmAlarmResponse.setSuccess(false);
        fmAlarmResponse.setErrorMessage(String.format(
                "Did not find alarm '%s' in given time '%d' milliseconds",
                alarmProblem, timeoutMillis));
        return fmAlarmResponse;
    }

    private boolean verifyAlarmTablesDisplay() {
        logger.info("Verifing alarm table is displayed");
        final FmViewModel resultsViewModel = currentBrowserTab
                .getView(FmViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    resultsViewModel.getAlarmTable(), UPDATE_TIMEOUT);
            return true;
        } catch (final WaitTimedOutException e) {
            return false;
        }
    }

    public FmAlarmResponse clickNetworkElementsContextMenuButton() {
        logger.info("Clicking 'Network Elements Context Menu Expand Button'");
        final FmViewModel resultsViewModel = currentBrowserTab
                .getView(FmViewModel.class);
        final FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();

        ApacheUiOperator.waitUntilLoaderIsHidden(currentBrowserTab,
                resultsViewModel, LOADING_WIDGET_TIMEOUT_MILLIS);

        try {
            logger.info("Waiting for 'context menu expand button' to display.");
            final UiComponent button = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            resultsViewModel.getEnableActionsLink(),
                            /*COMPONENT_TIMEOUT*/60000);
            logger.info("'context menu expand button' has appeared. Clicking.");
            button.click();
            //ApacheUiOperator.pause(1000); //Don't remove
            logger.info("Clicked context menu button");
            ApacheUiOperator.pause(1000); //Don't remove

        } catch (final WaitTimedOutException e) {
            return setResponse(
                    fmAlarmResponse,
                    false,
                    String.format(
                            "Timed out while waiting for Context Menu Expand Button to display. Waited %d milliseconds.",
                            COMPONENT_TIMEOUT));
        }

        try {
            logger.info("Waiting for 'context menu dropdown buttons' to appear.");
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    resultsViewModel.getActionsButtonArea(), COMPONENT_TIMEOUT);
            logger.info("'context menu dropdown buttons' have appeared.");
        } catch (final WaitTimedOutException e) {
            logger.warn(
                    "Timed out while waiting for Context Menu Dropdown buttons to appear. Waited {} milliseconds.",
                    COMPONENT_TIMEOUT);
            return setResponse(
                    fmAlarmResponse,
                    false,
                    String.format(
                            "Timed out while waiting for Context Menu Dropdown buttons to appear. Waited %d milliseconds.",
                            COMPONENT_TIMEOUT));
        }
        fmAlarmResponse.setSuccess(true);
        return fmAlarmResponse;
    }

    public FmAlarmResponse clickNetworkElements(final String... nodeNames) {
        logger.info(
                "Selecting network elements in 'Network Elements' table: {}",
                Arrays.toString(nodeNames));
        final FmViewModel fmViewModel = currentBrowserTab
                .getView(FmViewModel.class);

        ApacheUiOperator.waitUntilLoaderIsHidden(currentBrowserTab,
                fmViewModel, LOADING_WIDGET_TIMEOUT_MILLIS);

        final FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();
        if (nodeNames.length == 0) {
            return setResponse(fmAlarmResponse, false, "No node passed in.");
        }
        for (final String nodeName : nodeNames) {
            try {
                long elapsedTimeInMillis = 0L;
                final long startTimeInMillis = System.currentTimeMillis();
                boolean isClicked = false;
                while (elapsedTimeInMillis < 10000) {
                    if (fmViewModel.getNetworkElementHighlighted(nodeName)
                            .exists()) {
                        isClicked = true;
                        break;
                    }
                    elapsedTimeInMillis = System.currentTimeMillis()
                            - startTimeInMillis;
                }
                if (isClicked) {
                    logger.info("Node is already selected");
                } else {
                    currentBrowserTab.waitUntilComponentIsDisplayed(
                            fmViewModel.getNetworkElement(nodeName),
                            UPDATE_TIMEOUT).click();
                    logger.info("Clicked network element: {}", nodeName);
                }
            } catch (final WaitTimedOutException e) {
                return setResponse(fmAlarmResponse, false,
                        String.format(
                                "Failed to click network Element called: %s.",
                                nodeName));
            }
        }
        fmAlarmResponse.setSuccess(true);
        return fmAlarmResponse;
    }

    public FmAlarmResponse enableSupervision(final String nodeName) {
        logger.info("Enabeling supervision");

        FmAlarmResponse fmResp = clickNetworkElements(nodeName);
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
            return fmResp;
        }

        fmResp = clickNetworkElementsContextMenuButton();
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
            return fmResp;
        }

        final FmViewModel fmViewModel = currentBrowserTab
                .getView(FmViewModel.class);

        final FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();

        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    fmViewModel.getEnableSupervison(), COMPONENT_TIMEOUT)
                    .click();
        } catch (final WaitTimedOutException e) {
            return setResponse(fmAlarmResponse, false,
                    "Timed out while waiting for " + "Enable Supervision"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }

        //Return the response message from the GUI
        final UiComponent notificationLabel = fmViewModel.getNotificatonLabel();
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(notificationLabel,
                    COMPONENT_TIMEOUT);
            fmAlarmResponse.setNotificationLabel(notificationLabel);
            logger.info("Enable Supervision message: {}",
                    notificationLabel.getText());
        } catch (final WaitTimedOutException e) {
            logger.warn("Timed out waiting for the notification widget to appear.");
        }

        fmAlarmResponse.setSuccess(true);
        return fmAlarmResponse;
    }

    public FmAlarmResponse disableSupervision(final String nodeName) {
        logger.info("Disabling supervision for node: {}", nodeName);

        FmAlarmResponse fmResp = clickNetworkElements(nodeName);
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
            return fmResp;
        }

        fmResp = clickNetworkElementsContextMenuButton();
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
            return fmResp;
        }

        try {
            final FmViewModel fmViewModel = currentBrowserTab
                    .getView(FmViewModel.class);
            final UiComponent disableSupervision = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            fmViewModel.getDisableSupervision(),
                            COMPONENT_TIMEOUT);
            disableSupervision.click();
        } catch (final WaitTimedOutException e) {
            return setResponse(fmResp, false, "Timed out while waiting for "
                    + "Disable Supervision" + " to display. Waited "
                    + COMPONENT_TIMEOUT + " milliseconds");
        }
        fmResp.setSuccess(true);
        return fmResp;
    }

    public FmAlarmResponse clickInitiateAlarmSync(final String nodeName) {
        logger.info("Initiating alarm sync");

        FmAlarmResponse fmResp = clickNetworkElements(nodeName);
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
            return fmResp;
        }

        fmResp = clickNetworkElementsContextMenuButton();
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
            return fmResp;
        }

        final FmViewModel fmViewModel = currentBrowserTab
                .getView(FmViewModel.class);

        try {
            final UiComponent InitiateAlarmSync = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            fmViewModel.getInitiateAlarmSync(),
                            COMPONENT_TIMEOUT);
            InitiateAlarmSync.click();
            fmResp.setSuccess(true);
            return fmResp;
        } catch (final WaitTimedOutException e) {
            return setResponse(fmResp, false, "Timed out while waiting for "
                    + "Initiate Alarm Synchronization" + " to display. Waited "
                    + COMPONENT_TIMEOUT + " milliseconds");
        }
    }

    public FmAlarmResponse clickRemoveFromList() {
        final FmViewModel fmViewModel = currentBrowserTab
                .getView(FmViewModel.class);
        final FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    fmViewModel.getRemoveFromList(), COMPONENT_TIMEOUT).click();
            fmAlarmResponse.setSuccess(true);
            logger.info("Remove from list button has been clicked.");
            return fmAlarmResponse;
        } catch (final WaitTimedOutException e) {
            return setResponse(fmAlarmResponse, false,
                    "Timed out while waiting for " + "Remove from list button"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
    }

    public FmAlarmResponse verifySuccessIconShown() {
        logger.info("Verify success icon is shown");
        final FmViewModel fmViewModel = currentBrowserTab
                .getView(FmViewModel.class);
        final FmAlarmResponse response = new FmAlarmResponse();
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    fmViewModel.getSuccessNotification(), COMPONENT_TIMEOUT)
                    .click();
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Failed to recieve success notification.");
        }
        response.setSuccess(true);
        logger.info("Success notification recieved.");
        return response;
    }

    public FmAlarmResponse clickSaveComment() {
        final FmViewModel viewModel = currentBrowserTab
                .getView(FmViewModel.class);
        final FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    viewModel.getAddCommentButtonOnCommentSlideout(),
                    COMPONENT_TIMEOUT).click();
            fmAlarmResponse.setSuccess(true);
        } catch (final WaitTimedOutException e) {
            return setResponse(fmAlarmResponse, false,
                    "Timed out while waiting for " + "Add Comment button"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
        return fmAlarmResponse;
    }

    public FmAlarmResponse clearAllFiltersAndSearches() {
        FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();
        final Long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < STALE_ELEMENT_TIMEOUT) {
            try {
                final FmViewModel fmAlarmViewModel = currentBrowserTab
                        .getView(FmViewModel.class);
                currentBrowserTab.waitUntilComponentIsDisplayed(
                        fmAlarmViewModel.getAdvancedFilter(),
                        STALE_ELEMENT_TIMEOUT).click();
                currentBrowserTab.waitUntilComponentIsDisplayed(
                        fmAlarmViewModel.getFiltersCheckBoxes().get(0), 1000);
                final List<UiComponent> checkBoxes = fmAlarmViewModel
                        .getFiltersCheckBoxes();
                deselectCheckboxes(checkBoxes);
                fmAlarmResponse.setSuccess(true);
                break;
            } catch (final StaleElementReferenceException e) {
                logger.info("Clear all filters: StaleElementReferenceException thrown. Retrying try/catch block");
                fmAlarmResponse = setResponse(fmAlarmResponse, false,
                        "StaleElementReferenceException is thrown when selecting filter");
                continue;
            } catch (final WaitTimedOutException e) {
                return setResponse(fmAlarmResponse, false,
                        "WaitTimedOutException is thrown while waiting for filters");
            }
        }
        if (!fmAlarmResponse.isSuccess()) {
            return fmAlarmResponse;
        }
        fmAlarmResponse = enterProblemSearchText("");
        if (!fmAlarmResponse.isSuccess()) {
            return fmAlarmResponse;
        }
        fmAlarmResponse.setSuccess(true);
        return fmAlarmResponse;
    }

    private void deselectCheckboxes(final List<UiComponent> checkBoxes) {
        for (int i = 0; i < checkBoxes.size(); i++) {
            final UiComponent checkBox = checkBoxes.get(i)
                    .getDescendantsBySelector(".ebCheckbox").get(0);
            if (checkBox.isSelected()) {
                checkBox.click();
                final UiComponent filterName = checkBoxes.get(i)
                        .getDescendantsBySelector(".ebCheckbox-label-text")
                        .get(0);
                logger.info("{} filter has been deselected.",
                        filterName.getText());
            }
        }
    }

    public FmAlarmResponse searchHistoryAlarms(final String specificProblem) {
        logger.info("Search for history alarm with specific problem: {}",
                specificProblem);
        final FmViewModel viewModel = currentBrowserTab
                .getView(FmViewModel.class);

        final FmAlarmResponse fmAlarmResponse = new FmAlarmResponse();

        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    viewModel.getAddCriteriaLink(), COMPONENT_TIMEOUT).click();
            fmAlarmResponse.setSuccess(true);
        } catch (final WaitTimedOutException e) {
            return setResponse(fmAlarmResponse, false,
                    "Timed out while waiting for "
                            + "'Add Criteria' button for alarm search"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }

        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    viewModel.getSelectAttributeButton(), COMPONENT_TIMEOUT)
                    .click();
            fmAlarmResponse.setSuccess(true);
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    fmAlarmResponse,
                    false,
                    "Timed out while waiting for "
                            + "'Select Attribute' button for history alarm search"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    viewModel.getDropDownItemByName("specificProblem"),
                    COMPONENT_TIMEOUT).click();
            fmAlarmResponse.setSuccess(true);
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    fmAlarmResponse,
                    false,
                    "Timed out while waiting for "
                            + "'Select specificProblem Option' for history alarm search"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
        TextBox searchBox = null;
        try {
            searchBox = (TextBox) currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            viewModel.getSpecificProblemValueTextBox(),
                            COMPONENT_TIMEOUT);
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    fmAlarmResponse,
                    false,
                    "Timed out while waiting for "
                            + "'Specific Problem Value ' Text Box for history alarm search"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
        searchBox.setText(specificProblem);
        try {
            currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            viewModel.getSelectAttributeDoneButton(),
                            COMPONENT_TIMEOUT).click();
        } catch (final WaitTimedOutException e) {
            return setResponse(fmAlarmResponse, false,
                    "Timed out while waiting for "
                            + "'Apply Select Attribute' search button"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    viewModel.getAlarmHistorySearchButton(), COMPONENT_TIMEOUT)
                    .click();
        } catch (final WaitTimedOutException e) {
            return setResponse(fmAlarmResponse, false,
                    "Timed out while waiting for "
                            + "'Search Button' for history alarms"
                            + " to display. Waited " + COMPONENT_TIMEOUT
                            + " milliseconds");
        }
        fmAlarmResponse.setSuccess(true);
        return fmAlarmResponse;
    }

    private FmAlarmResponse setResponse(final FmAlarmResponse fmAlarmResponse,
            final boolean success, final String errorMessage) {
        fmAlarmResponse.setSuccess(success);
        fmAlarmResponse.setErrorMessage(errorMessage);
        return fmAlarmResponse;
    }
}