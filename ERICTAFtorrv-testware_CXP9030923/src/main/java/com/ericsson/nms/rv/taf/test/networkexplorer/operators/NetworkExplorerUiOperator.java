package com.ericsson.nms.rv.taf.test.networkexplorer.operators;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.WaitTimedOutException;
import com.ericsson.cifwk.taf.ui.sdk.*;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheUiOperator;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.Criteria;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.NetworkExplorerResponse;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.*;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.topologybrowser.TopologyBrowserModel;
import com.google.common.base.Predicate;

@Operator(context = Context.UI)
public class NetworkExplorerUiOperator {

    private final Logger logger = LoggerFactory
            .getLogger(NetworkExplorerUiOperator.class);
    private static final int COMPONENT_TIMEOUT_MILLIS = 5000;
    private static final int PAGE_TIMEOUT_MILLIS = 5000 * 5;
    private static final int WIDGET_TIMEOUT_MILLIS = 2000 * 5;
    private static final int CRITERIA_BUILDER_TIMEOUT_MILLIS = 15000 * 5;
    private static final int TABLE_LOADING_TIMEOUT_MILLIS = 5000;
    protected static final String NETWORK_EXPLORER = "/#networkexplorer";
    private static final int ANIMATION_FLYOUT_TIME = 2000;

    protected Browser browser;
    protected BrowserTab currentBrowserTab;

    public NetworkExplorerUiOperator() {
    }

    public BrowserTab initNetworkExplorer() {
        logger.info("Open network explorer and check if user is logged in.");
        browser = ApacheUiOperator.getBrowser();
        ApacheUiOperator.skipTestIfUserIsNotLoggedIn(browser);

        final String url = "https://" + HostConfigurator.getApache().getIp()
                + NETWORK_EXPLORER;
        logger.info("Opening Network Explorer page: {}", url);
        try {
            currentBrowserTab = browser.getCurrentWindow();
            if (currentBrowserTab == null) {
                logger.info("Current browser tab is null. Opening a new browser tab.");
                browser.open(url);
                currentBrowserTab = browser.getCurrentWindow();
            } else {
                logger.info("Current browser tab is not null. Opening network explorer page in current tab.");
                currentBrowserTab.open(url);
            }
        } catch (final RuntimeException e) {
            logger.error("Error while opening browser.");
            ApacheUiOperator.takeScreenShot(browser.getCurrentWindow(),
                    "Failed_to_open_networkexplorer");
            e.printStackTrace();
            throw e;
        }

        return currentBrowserTab;
    }

    public void switchToCriteriaBuilder() {
        logger.info("Switch to critera builder");
        final SearchViewModel nEVieModel = currentBrowserTab
                .getView(SearchViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    nEVieModel.getSwitchToQueryBuilderLink(),
                    PAGE_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'Switch to Criteria Builder'. Timout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }
        nEVieModel.getSwitchToQueryBuilderLink().click();
    }

    public void clickManagedObjectAndWaitForDropdownButtonToAppear() {
        logger.info("Click 'Managed Object' button and wait for first dropdown list to appear.");
        final QueryBuilderViewModel view = currentBrowserTab
                .getView(QueryBuilderViewModel.class);
        view.getAddMOButton().click();
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    view.getQueryBuilderMoiInputFieldDropdownButtonByIndex(1),
                    PAGE_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for drop down button to appear in Query Builder. Timeout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }
    }

    /**
     * @param criteriaBuilderQueryItemNumber
     *            - starts at 0!
     * @param moiName
     *            - value of the item in select Box identified by number, will
     *            be selected if found!
     */
    public void selectMoiNameFromQueryBuilderSection(
            final int criteriaBuilderQueryItemNumber, final String moiName) {
        logger.info(
                "Selecting MOI '{}' from query builder section number '{}'",
                moiName, criteriaBuilderQueryItemNumber);

        final QueryBuilderViewModel view = currentBrowserTab
                .getView(QueryBuilderViewModel.class);

        try {
            final Button drowDownButton = (Button) currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            view.getQueryBuilderMoiInputFieldDropdownButtonByIndex(criteriaBuilderQueryItemNumber),
                            PAGE_TIMEOUT_MILLIS);
            this.selectValueFromDropList(drowDownButton, moiName);
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for drop down button to appear in Query Builder. Timeout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }
    }

    public void clickAddChildButton() {
        logger.info("Adding child object to query using '+ Has Child' button in query builder");
        final QueryBuilderViewModel view = currentBrowserTab
                .getView(QueryBuilderViewModel.class);
        logger.info("Adding child to object at index.");
        try {
            final UiComponent addChildButton = currentBrowserTab
                    .waitUntilComponentIsDisplayed(view.getHasChild(),
                            PAGE_TIMEOUT_MILLIS);
            addChildButton.click();
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for Query Builder 'Add Child to Object' button. Timeout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }
    }

    public void executeSearch() {
        logger.info("Clicking Search button.");
        final QueryBuilderViewModel view = currentBrowserTab
                .getView(QueryBuilderViewModel.class);
        currentBrowserTab.waitUntil(view.getSearchButton(),
                new Predicate<UiComponent>() {
                    @Override
                    public boolean apply(final UiComponent uiComponent) {
                        return uiComponent.isEnabled();
                    }
                }, 2000);
        view.getSearchButton().click();
    }

    public void addCriteria(final int criteriaBuilderQueryItemNumber,
            final int criteriaInputAreaNumber, final List<Criteria> criterias) {

        logger.info("Adding '{}' critera from criteria list.", criterias.size());

        if (criterias.size() != 0) {
            final QueryBuilderViewModel view = currentBrowserTab
                    .getView(QueryBuilderViewModel.class);

            try {
                logger.info("Getting last '+ Add Criteria' link at index {}",
                        criteriaBuilderQueryItemNumber);
                final UiComponent addEditCriteriaLink = currentBrowserTab
                        .waitUntilComponentIsDisplayed(
                                view.getLastAddCriteria(criteriaBuilderQueryItemNumber),
                                CRITERIA_BUILDER_TIMEOUT_MILLIS);
                addEditCriteriaLink.click();
            } catch (final WaitTimedOutException e) {
                logger.error(
                        "Timed out waiting for Criteria Builder, last criteria added component. Timeout (ms): {}",
                        CRITERIA_BUILDER_TIMEOUT_MILLIS);
            }

            this.inputCriteriaAtCriteriaUiIndex(criterias.get(0),
                    criteriaInputAreaNumber);

            for (int criteriaIndex = 1; criteriaIndex < criterias.size(); criteriaIndex++) {
                view.getLastAddMoreCriteria(criteriaBuilderQueryItemNumber)
                        .click();
                this.inputCriteriaAtCriteriaUiIndex(
                        criterias.get(criteriaIndex), criteriaInputAreaNumber
                                + criteriaIndex);
            }
            view.getLastDoneEditingButton().click();
            ApacheUiOperator.pause(1000);
        } else {
            logger.warn("There were no criteria.");
        }
    }

    public void hideObject(final int index) {
        logger.info("Clicking 'Hide Eye' object at index {}", index);
        final QueryBuilderViewModel view = currentBrowserTab
                .getView(QueryBuilderViewModel.class);

        try {
            final UiComponent hideEyeIcon = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            view.getHideEyeIconByIndex(index),
                            PAGE_TIMEOUT_MILLIS);
            hideEyeIcon.click();
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for Hide Eye Icon to appear in Query Builder. Timeout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }

    }

    public void switchToSearch() {
        logger.info("Clicking 'Switch to Search Box'");
        final QueryBuilderViewModel view = currentBrowserTab
                .getView(QueryBuilderViewModel.class);
        view.getSwitchToSearchLink().click();
    }

    public void saveSavedSearch(final String savedSearchName) {
        logger.info("Saving search '{}'", savedSearchName);

        final SearchViewModel searchView = currentBrowserTab
                .getView(SearchViewModel.class);
        logger.info("Clicking 'Save Search' button.");
        try {
            logger.info("Clicking 'Save Search' button");
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    searchView.getSaveSearchButton(), PAGE_TIMEOUT_MILLIS)
                    .click();
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'save search button' to appear in Query Builder. Timeout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }

        final SaveDialogViewModel dialogViewModel = currentBrowserTab
                .getView(SaveDialogViewModel.class);
        logger.info("Waiting for name text box to appear.");
        try {
            logger.info("Waiting for 'Name' text box to appear.");
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dialogViewModel.getNameTextBox(), PAGE_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'get name text box' to appear in save dialog. Timeout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }

        logger.info("Setting search name");
        dialogViewModel.getNameTextBox().sendKeys(savedSearchName);
        logger.info("Clicking 'Private' radio button.");
        dialogViewModel.getPrivatePermissionRadio().click();
        logger.info("Clicking 'Save' button");
        dialogViewModel.getSaveButton().click();
        // if any error pop up, throws it.
        final UiComponent notification = dialogViewModel.getNotificationLabel();
        //TODO: can this be removed?
        ApacheUiOperator.pause(2000);
        if (notification.isDisplayed()) {
            throw new RuntimeException(
                    "A popup notification was displayed with text: "
                            + notification.getText());
        }
    }

    public NetworkExplorerResponse openWithTopologyBrowser(final int rowNumber) {
        final SearchViewModel searchViewModel = currentBrowserTab
                .getView(SearchViewModel.class);
        final TopologyBrowserModel topologyViewModel = currentBrowserTab
                .getView(TopologyBrowserModel.class);

        NetworkExplorerResponse response = new NetworkExplorerResponse();
        logger.info(
                "Clicking specific table row '{}' which will be opened in 'Topology Browser'.",
                rowNumber);
        response = selectNRowsFromTopOfResultsTable(rowNumber);
        if (!response.isSuccess()) {
            return response;
        }

        logger.info(
                "Pausing for {} milliseconds to allow the button animation to slide out to full width.",
                ANIMATION_FLYOUT_TIME);
        ApacheUiOperator.pause(ANIMATION_FLYOUT_TIME);

        try {
            logger.info("Clicking 'Open with Topology Browser' link.");
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    searchViewModel.getOpenWithTopologyBrowserButton(),
                    PAGE_TIMEOUT_MILLIS).click();
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for 'Open with Topology Browser' button to appear in search view. Timeout (ms): %d",
                            PAGE_TIMEOUT_MILLIS));
        }

        try {
            logger.info("Waiting for 'Topology Browser' page to open.");
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    topologyViewModel.getTitleHeader(), PAGE_TIMEOUT_MILLIS);
            logger.info("Found Topology Browser page header");
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for Topology Browser header text to appear. Timeout (ms): %d",
                            PAGE_TIMEOUT_MILLIS));
        }
        response.setSuccess(true);
        return response;
    }

    // Will select N number of rows from results table (counting from top) and
    // save them as collection.
    public void saveCollectionForNRows(final String collectionName,
            final int numberOfRows) {
        logger.info("Select {} rows from results table to add to collection.",
                numberOfRows);
        final SearchViewModel searchViewModel = currentBrowserTab
                .getView(SearchViewModel.class);

        selectNRowsFromTopOfResultsTable(numberOfRows);

        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    searchViewModel.getAddObjectToNewCollectionButton(),
                    PAGE_TIMEOUT_MILLIS).click();
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'add object to new collection button' to appear. Timeout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }
        final SaveDialogViewModel dialogViewModel = currentBrowserTab
                .getView(SaveDialogViewModel.class);
        logger.info("Waiting for collection name textbox to appear.");
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dialogViewModel.getNameTextBox(), PAGE_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'name text box' to appear. Timeout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }
        logger.info("Sending keys to collection textbox: {}.", collectionName);
        dialogViewModel.getNameTextBox().sendKeys(collectionName);
        logger.info("Clicking 'Private' permission radio button.");
        dialogViewModel.getPrivatePermissionRadio().click();
        logger.info("Clicking save button.");
        dialogViewModel.getSaveButton().click();
        // if any error pop up, throws it.
        ApacheUiOperator.pause(2000);
        final UiComponent notification = dialogViewModel.getNotificationLabel();
        if (notification.isDisplayed()) {
            throw new RuntimeException(notification.getText());
        }
        logger.info("Saved collection '{}'.", collectionName);
    }

    // will click "n" number of rows from results table (counting from the top).
    // accepts arguments greater than 0!
    public NetworkExplorerResponse selectNRowsFromTopOfResultsTable(
            final int numberOfRowsToSelect) {
        final NetworkExplorerResponse networkExplorerResponse = new NetworkExplorerResponse();
        if (numberOfRowsToSelect > 0) {
            final ResultsViewModel resultsViewModel = currentBrowserTab
                    .getView(ResultsViewModel.class);
            logger.info("Waiting for the table to be displayed.");
            try {
                currentBrowserTab.waitUntilComponentIsDisplayed(
                        resultsViewModel.getFirstTableRow(),
                        TABLE_LOADING_TIMEOUT_MILLIS);
            } catch (final WaitTimedOutException e) {
                logger.error(
                        "Timed out waiting for table rows to appear. Timout (ms): {}",
                        TABLE_LOADING_TIMEOUT_MILLIS);
            }

            final List<UiComponent> rows = resultsViewModel.getTableRows();
            logger.debug("Number of rows available: {}. Nunber to check: {}",
                    rows.size(), numberOfRowsToSelect);

            if (rows.size() <= numberOfRowsToSelect) {
                for (int i = 0; i < numberOfRowsToSelect; i++) {
                    logger.info("Clicking table row index {}.", i);
                    rows.get(i).click();
                }
            } else {
                ApacheUiOperator.takeScreenShot(currentBrowserTab,
                        "Attempting_to_select_more_rows_than_are_available");
                networkExplorerResponse.setSuccess(false);
                networkExplorerResponse
                        .setErrorMessage(String
                                .format("Attempting to save {} rows when only {} rows are available.",
                                        numberOfRowsToSelect, rows.size()));
            }
            networkExplorerResponse.setSuccess(true);

        } else {
            networkExplorerResponse.setSuccess(false);
            networkExplorerResponse
                    .setErrorMessage(String
                            .format("Failed to click row '%d' in table. Argument should be greater than 0",
                                    numberOfRowsToSelect));
        }
        return networkExplorerResponse;
    }

    public void clickViewAllSavedSearches() {
        final SlidingMenuViewModel slidingMenuViewModel = currentBrowserTab
                .getView(SlidingMenuViewModel.class);
        logger.info("Click view all saved changes.");
        try {
            final UiComponent viewAllSearchesLink = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            slidingMenuViewModel.getViewAllSavedSearchesLink(),
                            PAGE_TIMEOUT_MILLIS);
            viewAllSearchesLink.click();
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'View all saved searches' link to appear. Timout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }
    }

    public void clickViewAllCollections() {
        logger.info("Clicking 'Collections/View All' link.");
        final SlidingMenuViewModel slidingMenuViewModel = currentBrowserTab
                .getView(SlidingMenuViewModel.class);
        try {
            final UiComponent viewAllCollectionsLink = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            slidingMenuViewModel.getViewAllCollectionsLink(),
                            PAGE_TIMEOUT_MILLIS);
            viewAllCollectionsLink.click();
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'View all collections' link to appear. Timout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }
    }

    public void clickSearch(final String searchName) {
        logger.info("Clicking 'Viev...' link for collection '{}'", searchName);
        final CollectionsPageViewModel colPageVM = currentBrowserTab
                .getView(CollectionsPageViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    colPageVM.getViewLinkByName(searchName),
                    PAGE_TIMEOUT_MILLIS).click();
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'View...' link to appear for collection '{}' in Collections page. Timeout (ms): {}",
                    searchName, PAGE_TIMEOUT_MILLIS);
        }
    }

    private void inputCriteriaAtCriteriaUiIndex(final Criteria criteria,
            final int criteriaIndex) {
        final QueryBuilderViewModel view = currentBrowserTab
                .getView(QueryBuilderViewModel.class);
        logger.info("Inputting criteria '{}' at index {}", criteria,
                criteriaIndex);
        try {
            final UiComponent attributeSelectBox = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            view.getCriteriaAttributeKeyButtonByIndex(criteriaIndex),
                            PAGE_TIMEOUT_MILLIS);
            this.selectValueFromDropList(attributeSelectBox,
                    criteria.getAttribute());

            final UiComponent comparatorSelectBox = view
                    .getCriteriaAttriuteOperatorButtonByIndex(criteriaIndex);
            this.selectValueFromDropList(comparatorSelectBox,
                    criteria.getComparator());
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'critera attribute key button' to appear in query builder page. Timeout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }

        UiComponent valueField = null;
        logger.info("Get criteria attribute value by index.");
        try {
            valueField = currentBrowserTab.waitUntilComponentIsDisplayed(
                    view.getCriteriaAttributeValueInputByIndex(criteriaIndex),
                    WIDGET_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'critera attribute value input' to appear at index '{}' in query builder page. Timeout (ms): {}",
                    criteriaIndex, WIDGET_TIMEOUT_MILLIS);
        }
        logger.info("Check if value field is displayed.");
        if (valueField.isDisplayed()) {
            valueField
                    .sendKeys("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
            valueField.sendKeys(criteria.getValue());
        } else {
            try {
                valueField = currentBrowserTab
                        .waitUntilComponentIsDisplayed(
                                view.getCriteriaAttributeValueSelectBoxByIndex(criteriaIndex),
                                WIDGET_TIMEOUT_MILLIS);
            } catch (final WaitTimedOutException e) {
                logger.error(
                        "Timed out waiting for 'critera attribute value selection box' to appear in query builder page. Timeout (ms): {}",
                        WIDGET_TIMEOUT_MILLIS);
            }
            this.selectValueFromDropList(valueField, criteria.getValue());
        }

    }

    private void selectValueFromDropList(final UiComponent uiComponent,
            final String value) {
        final QueryBuilderViewModel view = currentBrowserTab
                .getView(QueryBuilderViewModel.class);

        uiComponent.click();

        logger.info("Selecting value '{}' from drop down list. {}, {}", value,
                uiComponent.getComponentName(),
                uiComponent.getComponentSelector());
        try {
            final UiComponent valueComponent = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            view.getSelectBoxItemByName(value),
                            WIDGET_TIMEOUT_MILLIS);

            valueComponent.click();
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for select box item '{}' in drop down list to appear in query builder page. Timeout (ms): {}",
                    value, WIDGET_TIMEOUT_MILLIS);
        }
    }

    public NetworkExplorerResponse doSearch(final String query) {
        final SearchViewModel searchViewModel = currentBrowserTab
                .getView(SearchViewModel.class);
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        logger.info("Find search text box and enter search query: '{}'", query);
        try {
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    searchViewModel.getSearchInput(), PAGE_TIMEOUT_MILLIS))
                    .setText(query);
        } catch (final WaitTimedOutException e) {
            logger.info("WaitTimedOutException. See screenshot.");
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for 'search input' to appear in search page. Timeout (ms): %d",
                            PAGE_TIMEOUT_MILLIS));
        }
        logger.info("Click on 'Search' button.");
        try {
            final UiComponent searchButton = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            searchViewModel.getSearchButton(),
                            WIDGET_TIMEOUT_MILLIS);
            searchButton.click();
        } catch (final WaitTimedOutException e) {
            logger.info("WaitTimedOutException. See screenshot.");
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for 'search button' to appear in search page. Timeout (ms): %d",
                            WIDGET_TIMEOUT_MILLIS));
        }
        logger.info("Search for '{}' carried out successfully.", query);
        response.setSuccess(true);
        return response;
    }

    public NetworkExplorerResponse selectCollectionByNameAndReturnObjects(
            final BrowserTab tab, final NetworkExplorerResponse response,
            final String collectionName) throws WaitTimedOutException {

        logger.info(
                "Selecting collection by name and returing objects. Collection name: {}",
                collectionName);

        final CollectionsPageViewModel neCollectionView = tab
                .getView(CollectionsPageViewModel.class);

        logger.info("Waiting for the collection table to load.");
        try {
            tab.waitUntilComponentIsDisplayed(
                    neCollectionView.getCollectionTable(), PAGE_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for collections list to appear in Collections page. Timeout (ms): %d",
                            PAGE_TIMEOUT_MILLIS));
        }

        // Do not remove pause. Waiting for table to load. There is nothing to
        // wait on.
        ApacheUiOperator.pause(TABLE_LOADING_TIMEOUT_MILLIS);
        logger.info("Selecting collection: {}.", collectionName);
        try {
            tab.waitUntilComponentIsDisplayed(
                    neCollectionView.getCollectionByName(collectionName),
                    PAGE_TIMEOUT_MILLIS).click();
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for collection name to appear in table on Collections page. Timeout (ms): %d",
                            PAGE_TIMEOUT_MILLIS));
        }

        logger.info("Click the selected collections button.");
        try {
            tab.waitUntilComponentIsDisplayed(
                    neCollectionView.getSelectedCollectionsButton(),
                    PAGE_TIMEOUT_MILLIS).click();
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for 'Return selected Objects' button to appear on Collections page. Timeout (ms): %d",
                            PAGE_TIMEOUT_MILLIS));
        }
        response.setSuccess(true);
        logger.info("Collection '{}' selected and returned", collectionName);
        return response;
    }

    protected NetworkExplorerResponse setResponse(
            final NetworkExplorerResponse response, final boolean success,
            final String error) {
        response.setSuccess(success);
        response.setErrorMessage(error);
        return response;
    }

    public List<Map<String, String>> getResults() {
        logger.info("Getting list of results from network explorer node table.");
        final ResultsViewModel resultsViewModel = currentBrowserTab
                .getView(ResultsViewModel.class);
        //TODO: can this be removed?
        ApacheUiOperator.pause(2000);
        logger.info("Getting results of query.");

        final UiComponent errorMessage = resultsViewModel.getNoResultMessage();

        if (errorMessage.isDisplayed()) {
            logger.info("No results error message was displayed. Returning empty list.");
            return new ArrayList<Map<String, String>>();
        }
        logger.info("Get the first table row.");
        try {
            logger.info("Getting first table row in results table.");
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    resultsViewModel.getFirstTableRow(), PAGE_TIMEOUT_MILLIS);
        } catch (final Exception e) {
            logger.warn(
                    "Timed out waiting for first table row to appear in results view. Timeout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
            return new ArrayList<Map<String, String>>();
        }

        final List<Map<String, String>> tableData = new ArrayList<>();
        final List<UiComponent> tableRowsList = resultsViewModel.getTableRows();
        final UiComponent[] tableRows = new UiComponent[tableRowsList.size()];
        tableRowsList.toArray(tableRows);
        logger.info("Getting table headers.");
        final List<UiComponent> tableHeadersList = resultsViewModel
                .getTableHeaders();
        final UiComponent[] tableHeaders = new UiComponent[tableHeadersList
                .size()];

        if (tableHeaders.length == 0) {
            logger.warn("No table headers found. Returning an empty result");
            return tableData;
        }

        tableHeadersList.toArray(tableHeaders);
        final int headerNum = tableHeaders.length;
        final String[] headers = new String[headerNum - 1];

        for (int i = 0; i < headerNum - 1; i++) {
            headers[i] = tableHeaders[i + 1].getProperty("title");
        }

        logger.info("Parsing table data.");
        for (final UiComponent tableRow : tableRows) {
            final List<UiComponent> cells = tableRow.getChildren();
            final Map<String, String> rowMap = new HashMap<String, String>();
            for (int i = 0; i < cells.size() - 1; i++) {
                rowMap.put(headers[i], cells.get(i + 1).getText());
            }
            tableData.add(rowMap);
        }
        logger.debug("Result tableData: {}", tableData);
        return tableData;
    }

    public void saveCollectionAsFavorite(final String collectionName) {
        final IntentCollectionsPageViewModel intentCollectionsPageViewModel = currentBrowserTab
                .getView(IntentCollectionsPageViewModel.class);
        logger.info("Clicking view all collections button.");
        this.clickViewAllCollections();
        logger.info("Get favourite icon for collection '{}' and click it.",
                collectionName);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    intentCollectionsPageViewModel
                            .getFavoriteIcon(collectionName),
                    PAGE_TIMEOUT_MILLIS);
            intentCollectionsPageViewModel.getFavoriteIcon(collectionName)
                    .click();
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'Favorite Icon' for collection {}. Timeout (ms): {}",
                    collectionName, PAGE_TIMEOUT_MILLIS);
        }
    }

    public void saveSearchAsFavorite(final String searchName) {
        final IntentCollectionsPageViewModel intentCollectionsPageViewModel = currentBrowserTab
                .getView(IntentCollectionsPageViewModel.class);
        logger.info("Clicking view all saved searches button.");
        this.clickViewAllSavedSearches();
        logger.info("Get favourite icon for saved search '{}' and click it.",
                searchName);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    intentCollectionsPageViewModel.getFavoriteIcon(searchName),
                    PAGE_TIMEOUT_MILLIS);
            intentCollectionsPageViewModel.getFavoriteIcon(searchName).click();
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'Favorite Icon' for search {}. Timeout (ms): {}",
                    searchName, PAGE_TIMEOUT_MILLIS);
        }
    }

    public List<String> getFavoriteCollections() {
        final SlidingMenuViewModel resultsViewModel = currentBrowserTab
                .getView(SlidingMenuViewModel.class);
        final List<String> toReturn = new ArrayList<String>();
        logger.info("Get favourite collections list.");
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    resultsViewModel.getFavoritesCollection(),
                    PAGE_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'Favourites Collection' to appear in Sliding Menu. See screenshot. Timeout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }

        final List<UiComponent> favoritesCollections = resultsViewModel
                .getFavoritesCollections();
        final UiComponent[] uiComponents = new UiComponent[favoritesCollections
                .size()];
        favoritesCollections.toArray(uiComponents);

        for (final UiComponent uiComponent : uiComponents) {
            final String text = uiComponent.getText();
            toReturn.add(text);
        }
        return toReturn;
    }

    public List<String> getFavoriteSearches() {
        final SlidingMenuViewModel resultsViewModel = currentBrowserTab
                .getView(SlidingMenuViewModel.class);
        final List<String> toReturn = new ArrayList<String>();
        logger.info("Get favourite searches list.");
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    resultsViewModel.getFavoritesSearch(), PAGE_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for 'Favourites Search' to appear in Sliding Menu. Timeout (ms): {}",
                    PAGE_TIMEOUT_MILLIS);
        }

        final List<UiComponent> favoritesSearches = resultsViewModel
                .getFavoritesSearches();
        final UiComponent[] uiComponents = new UiComponent[favoritesSearches
                .size()];
        favoritesSearches.toArray(uiComponents);
        for (final UiComponent uiComponent : uiComponents) {
            final String text = uiComponent.getText();
            toReturn.add(text);
        }
        return toReturn;
    }

    private void selectNodesFromSearchTable(final BrowserTab tab,
            final SearchViewModel view, final List<String> selectedNodes)
            throws WaitTimedOutException {
        // Wait on table to load. Otherwise list may throw StaleElementReferenceException
        ApacheUiOperator.pause(TABLE_LOADING_TIMEOUT_MILLIS);
        logger.info("Selecting {} nodes from node table.", selectedNodes);
        final List<String> nodesToCheck = new ArrayList<>(selectedNodes);

        String nodeName;
        for (int i = 0; i < selectedNodes.size(); i++) {
            try {
                nodeName = selectedNodes.get(i);
                logger.info("Finding '{}' in networkExplorer table.", nodeName);
                final CheckBox checkBox = view.selectNodeByName(nodeName);
                if (!checkBox.isSelected()) {
                    logger.info("Selecting the checkbox for the node '{}'.",
                            nodeName);
                    checkBox.click();
                    logger.info("Checkbox has been selected.");
                } else {
                    logger.info("Checkbox was already selected.");
                }
                nodesToCheck.remove(nodeName);
                logger.info("Removed from nodesToCheck {}", nodeName);
            } catch (final WaitTimedOutException e) {
                logger.info("Timed out while trying to find node on this page. Node may be on next page.");
                continue;
            }
        }

        if (nodesToCheck.isEmpty()) {
            logger.info("All nodes were found");
        } else if (!view.getPaginationNextButtonDisabled().exists()) {
            view.getPaginationNextButton().click();
            this.selectNodesFromSearchTable(tab, view, nodesToCheck);
        } else {
            throw new IllegalStateException(String.format(
                    "The following nodes were not found on the list: %s",
                    nodesToCheck.toString()));
        }

    }

    public NetworkExplorerResponse enterSearchCriteriaAndWaitForResultsToLoad(
            final BrowserTab tab, final NetworkExplorerResponse response,
            final String searchParameter) {
        final SearchViewModel neSearchView = tab.getView(SearchViewModel.class);
        // In NE. Wait for header text to load
        logger.info("Checking that network explorer page has loaded by checking header.");
        try {
            tab.waitUntilComponentIsDisplayed(
                    neSearchView.getSearchTabHeaderText(), PAGE_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            logger.info("WaitTimedOutException. See screenshot.");
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for 'Search' page to appear. Timeout (ms): %d",
                            PAGE_TIMEOUT_MILLIS));
        }

        logger.info("Enter search query {} in search textbox.", searchParameter);
        try {
            ((TextBox) tab.waitUntilComponentIsDisplayed(
                    neSearchView.getSearchInput(), PAGE_TIMEOUT_MILLIS))
                    .setText(searchParameter);
        } catch (final WaitTimedOutException e) {
            logger.info("WaitTimedOutException. See screenshot.");
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for search box to appear in NE search window. Timeout (ms): %d",
                            PAGE_TIMEOUT_MILLIS));
        }

        logger.info("Clicking search button to submit search.");
        try {
            tab.waitUntilComponentIsDisplayed(neSearchView.getSearchButton(),
                    PAGE_TIMEOUT_MILLIS).click();
        } catch (final WaitTimedOutException e) {
            logger.info("WaitTimedOutException. See screenshot.");
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for 'Search' button to appear in NE search window.",
                            PAGE_TIMEOUT_MILLIS));
        }
        logger.info("Waiting for table to load.");
        try {
            ApacheUiOperator.waitUntilLoaderIsHidden(tab, neSearchView,
                    PAGE_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            logger.info("WaitTimedOutException. See screenshot.");
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for nodes table to be displayed in NE search window. Timeout (ms): %d",
                            PAGE_TIMEOUT_MILLIS));
        }

        response.setSuccess(true);
        logger.info("Search for '{}' completed", searchParameter);
        return response;
    }

    public NetworkExplorerResponse selectNodesByNameAndReturnSelectedValues(
            final BrowserTab tab, final NetworkExplorerResponse response,
            final List<String> nodesToBeAdded) {

        logger.info(
                "Selecting nodes by name and returing  selected values. Nodes: {}",
                nodesToBeAdded);

        final SearchViewModel neSearchView = tab.getView(SearchViewModel.class);

        try {
            selectNodesFromSearchTable(currentBrowserTab, neSearchView,
                    nodesToBeAdded);
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Timed out waiting for nodes to be selected.");
        } catch (final IllegalStateException e) {
            return setResponse(response, false, e.getMessage());
        }

        logger.info("Click return objects button.");
        try {
            tab.waitUntilComponentIsDisplayed(
                    neSearchView.getReturnObjectsButton(), PAGE_TIMEOUT_MILLIS)
                    .click();
        } catch (final WaitTimedOutException e) {
            return setResponse(
                    response,
                    false,
                    String.format(
                            "Timed out waiting for 'Return selected Objects' button to appear in NE search window. Timeout (ms): %d",
                            PAGE_TIMEOUT_MILLIS));
        }

        response.setSuccess(true);
        logger.info("The following nodes have been selected: {}",
                nodesToBeAdded);
        return response;
    }
}
