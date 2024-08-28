package com.ericsson.nms.rv.taf.test.networkexplorer.operators;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.WaitTimedOutException;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheUiOperator;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.NetworkExplorerResponse;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.LogviewerViewModel;

public class LogviewerUiOperator {

    private final Logger logger = LoggerFactory
            .getLogger(LogviewerUiOperator.class);
    private static final String LOGVIEWER_URL = "#logviewer";

    private Browser browser = null;
    private BrowserTab currentBrowserTab = null;

    private final ArrayList<String> months = new ArrayList<String>(
            Arrays.asList(new String[] { "JANUARY", "FEBRUARY", "MARCH",
                    "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER",
                    "OCTOBER", "NOVEMBER", "DECEMBER" }));

    public LogviewerUiOperator() {
    }

    public BrowserTab initLogViewer() {
        browser = ApacheUiOperator.getBrowser();
        ApacheUiOperator.skipTestIfUserIsNotLoggedIn(browser);
        currentBrowserTab = browser.getCurrentWindow();

        currentBrowserTab.open("https://"
                + HostConfigurator.getApache().getIp() + LOGVIEWER_URL);
        return currentBrowserTab;
    }

    public NetworkExplorerResponse doSearch(final String query) {
        logger.info("Do search with query: {}", query);
        final LogviewerViewModel logviewerViewModel = currentBrowserTab
                .getView(LogviewerViewModel.class);
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        try {
            final UiComponent searchButton = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            logviewerViewModel.getSearchButton(), 2000);
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    logviewerViewModel.getSearchInput(), 5000)).setText(query);
            searchButton.click();
            response.setSuccess(true);
            logger.info("Search performed correctly.");
            return response;
        } catch (final WaitTimedOutException e) {
            setFailureResponse("Unable to perform search action.", response);
            return response;
        }
    }

    public NetworkExplorerResponse doSearch() {
        logger.info("Clicking the search button.");
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        final LogviewerViewModel logviewerViewModel = currentBrowserTab
                .getView(LogviewerViewModel.class);
        try {
            final UiComponent searchButton = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            logviewerViewModel.getSearchButton(), 2000);
            searchButton.click();
            return response;
        } catch (final WaitTimedOutException e) {
            setFailureResponse("Unable to perform search action.", response);
            return response;
        }
    }

    public NetworkExplorerResponse doRefresh() {
        logger.info("Clicking 'Refresh' component.");
        final LogviewerViewModel logviewerViewModel = currentBrowserTab
                .getView(LogviewerViewModel.class);
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    logviewerViewModel.getRefreshComponent(), 5000).click();
            response.setSuccess(true);
            logger.info("Browser was successfully refreshed.");
            return response;
        } catch (final WaitTimedOutException e) {
            setFailureResponse("Unable refresh browsertab.", response);
            return response;
        }
    }

    public NetworkExplorerResponse selectColumns(final String... columns) {
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        if (columns == null) {
            throw new IllegalArgumentException(
                    "columns for log viewer cannot be null");
        }
        final LogviewerViewModel logviewerViewModel = currentBrowserTab
                .getView(LogviewerViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    logviewerViewModel.getSelectColumnComponent(), 5000)
                    .click();
            ApacheUiOperator.pause(1000);

            final List<UiComponent> columnsCandidates = logviewerViewModel
                    .getColumnsCandidates();
            for (final String column : columns) {
                for (final UiComponent uiComponent : columnsCandidates) {
                    final UiComponent span = uiComponent
                            .getDescendantsBySelector(".ebCheckbox-label").get(
                                    0);
                    final String text = span.getText();
                    if (column.equals(text)) {
                        span.click();
                        break;
                    }
                }
            }
            logger.info("Columns were successfully selected.");
            response.setSuccess(true);
            return response;
        } catch (final WaitTimedOutException e) {
            setFailureResponse("Unable to select columns.", response);
            return response;
        }
    }

    public NetworkExplorerResponse selectColumns(final Set<String> columns) {
        if (columns == null) {
            throw new IllegalArgumentException(
                    "columns for log viewer cannot be null");
        }
        final String[] columnsArray = new String[columns.size()];
        columns.toArray(columnsArray);
        return selectColumns(columnsArray);
    }

    public Set<String> getHeaders() {
        logger.info("Getting headers from search results");
        final LogviewerViewModel logviewerViewModel = currentBrowserTab
                .getView(LogviewerViewModel.class);
        currentBrowserTab.waitUntilComponentIsDisplayed(
                logviewerViewModel.getTableHeader(), 5000);
        final List<UiComponent> headerList = logviewerViewModel
                .getTableHeaders();
        final UiComponent[] headers = new UiComponent[headerList.size()];
        headerList.toArray(headers);
        final Set<String> result = new HashSet<String>();
        for (final UiComponent uiComponent : headers) {
            result.add(uiComponent.getText());
        }
        logger.info("Got headers: {}", result.toString());
        return result;
    }

    public NetworkExplorerResponse setStartTime(final Calendar calendar) {
        logger.info("Setting start time.");
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        final LogviewerViewModel logviewerViewModel = currentBrowserTab
                .getView(LogviewerViewModel.class);
        try {
            final UiComponent startTimeInput = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            logviewerViewModel.getStartTimeInput(), 5000);
            startTimeInput.click();
            setTime(calendar);
            logviewerViewModel.getOkComponent().click();
            response.setSuccess(true);
            logger.info("Start time was successfully set.");
            return response;
        } catch (final WaitTimedOutException e) {
            setFailureResponse("Unable to set start time.", response);
            return response;
        }
    }

    public NetworkExplorerResponse setStartTime(final int year,
            final int month, final int dayOfMonth, final int hourOfDay,
            final int minute, final int sec) {
        final Calendar calendar = new GregorianCalendar(year, month - 1,
                dayOfMonth, hourOfDay, minute, sec);
        return setStartTime(calendar);
    }

    public NetworkExplorerResponse setEndTime(final Calendar calendar) {
        logger.info("Setting end time.");
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        final LogviewerViewModel logviewerViewModel = currentBrowserTab
                .getView(LogviewerViewModel.class);
        try {
            final UiComponent endTimeInput = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            logviewerViewModel.getEndTimeInput(), 5000);
            endTimeInput.click();
            setTime(calendar);
            logviewerViewModel.getOkComponent().click();
            logger.info("End time was successfully set.");
            return response;
        } catch (final WaitTimedOutException e) {
            setFailureResponse("Unable to set end time.", response);
            return response;
        }
    }

    public NetworkExplorerResponse setEndTime(final int year, final int month,
            final int dayOfMonth, final int hourOfDay, final int minute) {
        final Calendar calendar = new GregorianCalendar(year, month,
                dayOfMonth, hourOfDay, minute);
        return setEndTime(calendar);
    }

    public NetworkExplorerResponse setTime(final Calendar calendar) {
        final int newYear = calendar.get(Calendar.YEAR);
        final int newMonth = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int min = calendar.get(Calendar.MINUTE);
        final int sec = calendar.get(Calendar.SECOND);
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        final LogviewerViewModel logviewerViewModel = currentBrowserTab
                .getView(LogviewerViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    logviewerViewModel.getDateInputDialog(), 5000);
            final UiComponent hourInput = logviewerViewModel.getHourInput();
            hourInput.click();
            hourInput.sendKeys(String.valueOf(hour));

            final UiComponent minInput = logviewerViewModel.getMinInput();
            minInput.click();
            minInput.sendKeys(String.valueOf(min));

            final UiComponent secInput = logviewerViewModel.getSecInput();
            secInput.click();
            secInput.sendKeys(String.valueOf(sec));

            final String[] dateMonth = logviewerViewModel.getMonthYear()
                    .getText().split(" ");
            final String currentMonth = dateMonth[0];
            final int currentMonthInt = getMonthInt(currentMonth);
            final String currentYear = dateMonth[1];

            final int differenceYear = Integer.valueOf(currentYear)
                    - Integer.valueOf(newYear);
            if (differenceYear > 0) {
                for (int i = 0; i < differenceYear; i++) {
                    logviewerViewModel.getPrevYear().click();
                }
            }

            final int differenceMonth = currentMonthInt
                    - Integer.valueOf(newMonth);
            if (differenceMonth > 0) {
                for (int i = 0; i < Math.abs(differenceMonth); i++) {
                    logviewerViewModel.getPrevMonth().click();
                }
            } else if (differenceMonth < 0) {
                for (int i = 0; i < Math.abs(differenceMonth); i++) {
                    logviewerViewModel.getNextMonth().click();
                }
            }

            logviewerViewModel.getDayPicker(String.valueOf(day)).click();
            response.setSuccess(true);
            logger.info("Time was successfully set.");
            return response;
        } catch (final WaitTimedOutException e) {
            setFailureResponse("Unable to set time.", response);
            return response;
        }
    }

    public List<Map<String, String>> getResults() {
        final LogviewerViewModel logviewerViewModel = currentBrowserTab
                .getView(LogviewerViewModel.class);

        logger.info("Getting log viewer results table.");
        final List<Map<String, String>> tableData = new ArrayList<>();
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    logviewerViewModel.getTableHeader(), 5000);
            logger.info("Getting table rows");
            final List<UiComponent> tableRowsList = logviewerViewModel
                    .getTableRowsSummary();
            logger.info("Got {} table rows", tableRowsList.size());
            final UiComponent[] tableRows = new UiComponent[tableRowsList
                    .size()];
            tableRowsList.toArray(tableRows);

            logger.info("Getting table headers.");
            final List<UiComponent> tableHeadersList = logviewerViewModel
                    .getTableHeaders();
            logger.info("Got {} table headers", tableHeadersList.size());

            final UiComponent[] tableHeaders = new UiComponent[tableHeadersList
                    .size()];

            tableHeadersList.toArray(tableHeaders);
            final int headerNum = tableHeaders.length;
            final String[] headers = new String[headerNum - 1];

            for (int i = 0; i < headerNum - 1; i++) {
                headers[i] = tableHeaders[i + 1].getText();
            }

            for (final UiComponent tableRow : tableRows) {
                final List<UiComponent> cells = tableRow.getChildren();

                final Map<String, String> rowMap = new HashMap<String, String>();
                for (int i = 0; i < cells.size() - 1; i++) {
                    rowMap.put(headers[i], cells.get(i + 1).getText());
                }
                tableData.add(rowMap);
            }

            logger.info("Result data: {}", tableData.toString());
            if (tableData.isEmpty()) {
                ApacheUiOperator.takeScreenShot(currentBrowserTab,
                        "LogViewer_EmptyResults");
            }
            return tableData;
        } catch (final WaitTimedOutException e) {
            logger.info("Unable to return results.");
            return tableData;
        }
    }

    public NetworkExplorerResponse clickToClearSearchText() {
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        final LogviewerViewModel logviewerViewModel = currentBrowserTab
                .getView(LogviewerViewModel.class);
        try {
            logger.info("Clicking search clear button.");
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    logviewerViewModel.getButtonToClearSearchText(), 1000)
                    .click();
            response.setSuccess(true);
            logger.info("Search was successfully cleared.");
            return response;
        } catch (final WaitTimedOutException e) {
            setFailureResponse("Unable to click search clear button.", response);
            return response;
        }
    }

    private NetworkExplorerResponse setFailureResponse(String failureMessage,
            NetworkExplorerResponse response) {
        response.setErrorMessage(failureMessage);
        response.setSuccess(false);
        return response;
    }

    private int getMonthInt(final String month) {
        int result = 0;

        if (months.contains(month.trim())) {
            result = months.indexOf(month) + 1;
        }
        return result;
    }

}