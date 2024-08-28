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
package com.ericsson.nms.rv.taf.test.ui.viewmodels.fm;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.*;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.LoaderViewModel;

/*
 * Copied from scenarios FmViewModel.java
 */
public class FmViewModel extends GenericViewModel implements LoaderViewModel {
    public static final String CRITICAL_FILTER = "Critical";
    public static final String MAJOR_FILTER = "Major";
    public static final String MINOR_FILTER = "Minor";
    public static final String WARNING_FILTER = "Warning";
    public static final String INDETERMINATE_FILTER = "Indeterminate";
    public static final String CLEARED_FILTER = "Cleared";
    public static final String ACKNOWLEDGE_BUTTON = "Acknowledge";
    public static final String UNACKNOWLEDGE_BUTTON = "Unacknowledge";
    public static final String SPECIFIC_PROBLEM_HEADER = "Specific Problem";
    public static final String ALARM_STATE_HEADER = "Alarm State";
    public static final String OBJECT_OF_REFERENCE_HEADER = "Object Of Reference";
    public static final String CLEARED_ALARM_STATE = "Cleared";
    public static final String ACTIVE_ALARM_STATE = "Active";
    public static final String HEARTBEAT_FAILURE_ALARM = "Heartbeat Failure";
    private static final String ADD_COMMENT_BUTTON = "Add Comment";
    private static final String CLEAR_BUTTON = "Clear";

    @UiComponentMapping(".elLayouts-PanelActionBar-button_filter")
    private UiComponent advancedFilter;

    @UiComponentMapping(".elNetworkExplorerLib-TopologyDropdown-button.ebDropdown-header")
    private Button topologyDropDownButton;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//ul[contains(@class,'eaAlarmviewer-FiltersRegion-list')]/li[@class = 'ebList-item']")
    private List<UiComponent> filters;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class,'eaAlarmviewer-rMain-table')]//table//tbody//tr")
    private List<UiComponent> mainTableRows;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class,'eaAlarmviewer-rMain-table')]//table//thead//th")
    private List<UiComponent> mainTableHeaders;

    @UiComponentMapping(".eaAlarmviewer-Side-filter-specificProblem")
    private TextBox specificProblemSearch;

    @UiComponentMapping(".eaAlarmviewer-Side-filter-apply")
    private Button applySearchButton;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class, 'eaAlarmviewer-content-body')]//textarea[contains(@class,'eaAlarmviewer-NewComment-text')]")
    private TextBox commentTextArea;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class, 'eaAlarmviewer-content-body')]//button[contains(@class,'eaAlarmviewer-NewComment-button')]")
    private Button addCommentButton;

    @UiComponentMapping(".elTablelib-Table-body")
    private UiComponent alarmTable;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class, 'ebContextMenu-expandBtn')]//i[contains(@class, 'ebIcon_interactive')]")
    private UiComponent enableActions;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class, 'elWidgets-ComponentList')]")
    private UiComponent actionButtonArea;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//input[@class = 'ebInput']")
    private TextBox filterTextBox;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//button[contains(@class, 'eaAlarmviewer-Network-nodeFiltersButton')]")
    private Button filterButton;

    @UiComponentMapping(".ebIcon_close_green")
    private UiComponent successNotificationIcon;

    @UiComponentMapping(selector = ".eaAlarmsearch-wCriteria-addEditLink", selectorType = SelectorType.CSS)
    private UiComponent addCriteriaLink;

    @UiComponentMapping(selector = ".ebSelect .ebSelect-header ", selectorType = SelectorType.CSS)
    private Button selectAttributeButton;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//input[@class = 'eaAlarmsearch-wCriteriaAttribute-valueInput ebInput']")
    private TextBox specificProblemValueTextBox;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//button[contains(@class, 'eaAlarmsearch-wCriteria-done ebBtn ebBtn_color_orange')]")
    private Button selectAttributeDoneButton;

    @UiComponentMapping(selector = ".eaAlarmsearch-AlarmSearchRegion .eaAlarmsearch-AlarmSearchRegion-playButton", selectorType = SelectorType.CSS)
    private Button alarmHistorySearchButton;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//i[contains(@class, 'eaAlarmviewer-content-close ebIcon ebIcon_close eaAlarmviewer-content-close_show_true')]")
    private UiComponent closeAdvancedFilters;

    @UiComponentMapping(selector = ".eaAlarmsearch-AlarmSearchRegion-searchType .eaAlarmsearch-AlarmSearchRegion-historyType .ebRadioBtn-label", selectorType = SelectorType.CSS)
    private UiComponent selectHistoryAlarmsOption;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//tbody[contains(@class, 'elTablelib-Table-body')]//tr[contains(@class, 'ebTableRow')][1]")
    private UiComponent firstRowInAlarmTable;

    @UiComponentMapping(".ebNotification")
    private UiComponent notificatonWidget;

    @UiComponentMapping(".ebNotification-label")
    private UiComponent notificatonLabel;

    @UiComponentMapping(".eaContainer-LoaderWidget_show")
    private UiComponent containerLoading;

    @UiComponentMapping(".elTablelib-VirtualScrolling-loader-overlay_show")
    private UiComponent nodeListLoading;

    @UiComponentMapping(".eaAlarmviewer-loader-overlay_show")
    private UiComponent tableLoadingWidget;

    public TextBox getFilterTextBox() {
        return filterTextBox;
    }

    public Button getFilterButton() {
        return filterButton;
    }

    public UiComponent getSelectHistoryAlarmsOption() {
        return selectHistoryAlarmsOption;
    }

    public Button getTopologyDropDownButton() {
        return topologyDropDownButton;
    }

    public UiComponent getDropDownItemByName(final String name) {
        return getViewComponent(
                SelectorType.XPATH,
                String.format(
                        "//*[contains(@class,'ebComponentList-item')][normalize-space(text())='%s']",
                        name), UiComponent.class);
    }

    public UiComponent getAdvancedFilter() {
        return advancedFilter;
    }

    public List<UiComponent> getFiltersCheckBoxes() {
        return filters;
    }

    public List<UiComponent> getMainTableRows() {
        return mainTableRows;
    }

    public List<UiComponent> getMainTableHeaders() {
        return mainTableHeaders;
    }

    public TextBox getProblemSearchInput() {
        return specificProblemSearch;
    }

    public Button getApplySearchButton() {
        return applySearchButton;
    }

    public Button getAddCommentButton() {
        return this.getQuickActionButton(ADD_COMMENT_BUTTON);
    }

    public Button getAcknowledgeButton() {
        return this.getQuickActionButton(ACKNOWLEDGE_BUTTON);
    }

    public Button getClearButton() {
        return this.getQuickActionButton(CLEAR_BUTTON);
    }

    public TextBox getCommentTextArea() {
        return commentTextArea;
    }

    public Button getAddCommentButtonOnCommentSlideout() {
        return addCommentButton;
    }

    public Link getQuickActionLink(final String action) {
        return getViewComponent(
                SelectorType.XPATH,
                String.format(
                        "//a[contains(@class,'elLayouts-ActionBarItem')][normalize-space(text())='%s']",
                        action), Link.class);
    }

    public Button getQuickActionButton(final String action) {
        return getViewComponent(
                SelectorType.XPATH,
                String.format(
                        "//button[contains(@class,'elLayouts-ActionBarButton')]//span[normalize-space(text())='%s']",
                        action), Button.class);
    }

    public UiComponent getAlarmTable() {
        return alarmTable;
    }

    public UiComponent getEnableActionsLink() {
        return enableActions;
    }

    public UiComponent getActionsButtonArea() {
        return actionButtonArea;
    }

    public UiComponent getNetworkElement(final String nodeName) {
        return getViewComponent(
                SelectorType.XPATH,
                String.format(
                        "//div[contains(@class,'eaAlarmviewer-nodeSelection-table')]//tr[contains(@class,'ebTableRow')]//td[normalize-space(text())='%s']",
                        nodeName), UiComponent.class);
    }

    public UiComponent getNetworkElementHighlighted(final String nodeName) {
        return getViewComponent(
                SelectorType.XPATH,
                String.format(
                        "//div[contains(@class,'eaAlarmviewer-nodeSelection-table')]//tr[contains(@class,'ebTableRow_highlighted')]//td[normalize-space(text())='%s']",
                        nodeName), UiComponent.class);
    }

    public UiComponent getEnableSupervison() {
        return getNodeActionsSelection("Enable Supervision");
    }

    public UiComponent getDisableSupervision() {
        return getNodeActionsSelection("Disable Supervision");
    }

    public UiComponent getInitiateAlarmSync() {
        return getNodeActionsSelection("Initiate Alarm Synchronization");
    }

    public UiComponent getRemoveFromList() {
        return getNodeActionsSelection("Remove From List");
    }

    public UiComponent getCancelActions() {
        return getNodeActionsSelection("Cancel Actions");
    }

    public UiComponent getNodeActionsSelection(final String name) {
        return getViewComponent(
                SelectorType.XPATH,
                String.format(
                        "//*[contains(@class,'ebComponentList-item')][normalize-space(text())='%s']",
                        name), UiComponent.class);
    }

    public UiComponent getSuccessNotification() {
        return successNotificationIcon;
    }

    public UiComponent getAddCriteriaLink() {
        return addCriteriaLink;
    }

    public Button getSelectAttributeButton() {
        return selectAttributeButton;
    }

    public TextBox getSpecificProblemValueTextBox() {
        return specificProblemValueTextBox;
    }

    public Button getSelectAttributeDoneButton() {
        return selectAttributeDoneButton;
    }

    public Button getAlarmHistorySearchButton() {
        return alarmHistorySearchButton;
    }

    public UiComponent getCloseAdvancedFilters() {
        return closeAdvancedFilters;
    }

    public UiComponent getFirstRowInAlarmTable() {
        return firstRowInAlarmTable;
    }

    @Override
    public List<UiComponent> getLoadingWidgets() {

        final ArrayList<UiComponent> list = new ArrayList<UiComponent>();
        list.add(0, containerLoading);
        list.add(1, tableLoadingWidget);
        list.add(2, nodeListLoading);
        return list;
    }

    public UiComponent getNotificatonWidget() {
        return notificatonWidget;
    }

    public UiComponent getNotificatonLabel() {
        return notificatonLabel;
    }
}