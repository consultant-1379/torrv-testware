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
package com.ericsson.nms.rv.taf.test.ui.viewmodels.pmic;

import java.util.List;

import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.*;

public class StatisticalSubscriptionViewModel extends GenericViewModel {

    public static final String ACTION_SEARCH_FOR_NETWORK_OBJECTS = "Search for Network Objects";
    public static final String ACTION_ADD_COLLECTIONS = "Add Collections";
    public static final String RESOURCES_TAB = "Resources";
    public static final String COUNTERS_TAB = "Counters";
    public static final String SCHEDULER_TAB = "Scheduler";

    // Top buttons
    @UiComponentMapping(selector = "//button[contains(@class,'ebBtn')]//span[normalize-space(text())='Save']", selectorType = SelectorType.XPATH)
    private Button saveButton;

    @UiComponentMapping(selector = "//button[contains(@class,'ebBtn')]//span[normalize-space(text())='Cancel']", selectorType = SelectorType.XPATH)
    private Button cancelButton;

    // Details Section
    @UiComponentMapping("eapmiccommon-rSubscriptionInput-header")
    private UiComponent detailsSubHeading;

    @UiComponentMapping(".eapmiccommon-wSubscriptionName-input")
    private TextBox subscriptionNameTextBox;

    @UiComponentMapping(".eapmiccommon-wSubscriptionDescription-textArea")
    private TextBox descriptionTextBox;

    // Tabs Section
    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class,'ebTabs-tabItem')][normalize-space(text())='Resources']")
    private UiComponent resourcesTab;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class,'ebTabs-tabItem')][normalize-space(text())='Counters']")
    private UiComponent countersTab;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class,'ebTabs-tabItem')][normalize-space(text())='Scheduler']")
    private UiComponent schedulerTab;

    // Resources Tab
    @UiComponentMapping(".elNetworkExplorerLib-TopologyDropdown-button")
    private UiComponent addTopologyDataDropdown;

    @UiComponentMapping(".ebTableRow")
    private UiComponent firstRowOfNodesInTable;

    @UiComponentMapping(".ebTableRow")
    private List<UiComponent> rowsOfNodesInTable;

    @UiComponentMapping(".ebTable")
    private UiComponent nodeTable;

    @UiComponentMapping(".ebPagination-nextAnchor")
    private UiComponent paginationNextButton;

    @UiComponentMapping(".ebPagination-nextAnchor_disabled")
    private UiComponent paginationNextButtonDisabled;

    @UiComponentMapping(".ebPagination-entryAnchor_current")
    private UiComponent currentNodeTablePage;

    @UiComponentMapping(".ebLoader-Dots")
    private UiComponent tableLoadingBar;

    @UiComponentMapping(".eapmiccommon-wNetworkElementsSelection-header-nodes")
    private UiComponent numberOfNodes;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//a[contains(@title, 'PM Node Processes')]")
    private UiComponent pmNodeProcessesLink;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//button[contains(@class,'elLayouts-ActionBarButtonFlat')]//span[normalize-space(text())='Remove']")
    private UiComponent removeButton;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//a[@class='ebPagination-nextAnchor']")
    private Link nextPageLinkEnabled;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//a[contains(@class,'ebPagination-nextAnchor_disabled')]")
    private Link nextPageLinkDisabled;

    // Counters tab
    @UiComponentMapping(".eapmicsubscription-wCounterSelection-header")
    private UiComponent countersTabHeaderText;

    @UiComponentMapping(".elTablelib-Table-body")
    private UiComponent countersTableRow;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//*[contains(@class,'eapmiccommon-FilterHeaderCell')][3]//input[contains(@class,'eapmiccommon-FilterHeaderCell-input')]")
    private UiComponent sourceObjectSearchBox;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//*[contains(@class,'eapmiccommon-FilterHeaderCell')][1]//input[contains(@class,'eapmiccommon-FilterHeaderCell-input')]")
    private UiComponent counterNameSearchBox;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class,'elTablelib-CheckboxHeaderCell-wrap')]")
    private UiComponent selectAllCounters;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//tbody[contains(@class,'elTablelib-Table-body')]//tr[contains(@class,'ebTableRow')]")
    private UiComponent countersTableBodyFirstRow;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//button[contains(@class,'ebBtn')]//span[normalize-space(text())='Subscribe']")
    private Button subscribeButton;

    // Scheduler tab
    @UiComponentMapping(".eapmiccommon-wSchedulerSelection-fileCollectionInput-content-ropInput-selectionBox")
    private UiComponent fileCollectionRopIntervalButton;

    @UiComponentMapping(".eapmiccommon-wSchedulerSelection-schedulerDateTimeInput")
    private UiComponent schedulerTimeInputArea;

    @UiComponentMapping(".ebLayout-SectionSubheading")
    private UiComponent subHeadingScheduler;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class,'eapmiccommon-wSchedulerSelection-schedulerDateTimeInput-content-dateTimePicker-startTime-selector')]//button[contains(@class,'ebSelect-header')]")
    private Button fromDropDownBox;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class,'eapmiccommon-wSchedulerSelection-schedulerDateTimeInput-content-dateTimePicker-endTime-selector')]//button[contains(@class,'ebSelect-header')]")
    private Button untilDropDownBox;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class,'eapmiccommon-wSchedulerSelection-schedulerDateTimeInput-content-dateTimePicker-startTime-datePicker')]//input[contains(@class,'eapmiccommon-PopupDatePicker-inputArea-input')]")
    private UiComponent startTimeDatePicker;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class,'eapmiccommon-wSchedulerSelection-schedulerDateTimeInput-content-dateTimePicker-endTime-datePicker')]//input[contains(@class,'eapmiccommon-PopupDatePicker-inputArea-input')]")
    private UiComponent endTimeDatePicker;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class,'eapmiccommon-wSchedulerSelection-schedulerDateTimeInput-content-dateTimePicker-startTime-datePicker')]//div[contains(@class,'eapmiccommon-PopupDatePicker-popup')]")
    private UiComponent startDateTimePickerPopup;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class,'eapmiccommon-wSchedulerSelection-schedulerDateTimeInput-content-dateTimePicker-endTime-datePicker')]//div[contains(@class,'eapmiccommon-PopupDatePicker-popup')]")
    private UiComponent endDateTimePickerPopup;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class,'eapmiccommon-wSchedulerSelection-fileCollectionInput-content-ropInput-selectionBox')]//button[contains(@class,'ebSelect-header')]")
    private UiComponent ropSelectionBox;

    // CounterList
    @UiComponentMapping(".eapmicsubscription-wMoClass-container")
    private UiComponent counterRow;

    @UiComponentMapping(".ebCheckbox-label")
    private List<UiComponent> counterNameList;

    @UiComponentMapping(".ebCheckbox")
    private List<UiComponent> countersCheckBox;

    @UiComponentMapping(".eapmicsubscription-wCounterSelection-count")
    private UiComponent totalCountersSelected;

    @UiComponentMapping(".ebAccordion-button")
    private List<UiComponent> expandCounterRows;

    @UiComponentMapping(".ebCheckbox-label-counter-name")
    private List<UiComponent> subCountersNameList;

    // Save confirmation
    @UiComponentMapping(".ebIcon_tick")
    private UiComponent saveConfirmation;

    // Dialog box
    @UiComponentMapping(".ebDialogBox")
    private UiComponent dialogBox;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//button[contains(@class,'ebBtn_color_darkBlue')]//span[normalize-space(text())='Confirm']")
    private UiComponent confirmNodeRemovalButton;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//button[contains(@class,'ebBtn')]//span[normalize-space(text())='Cancel']")
    private UiComponent cancelNodeRemovalButton;

    // Subheadings on tab pages
    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//h3")
    private UiComponent tabSubHeading;

    /* ********** METHODS ************** */

    // Action items by name
    public UiComponent getActionItemByName(final String name) {
        // Use XPATH here for speed. The list can be long, so iterating through
        // it can take a little longer.
        return getViewComponent(SelectorType.XPATH,
                "//*[contains(@class,'ebComponentList-item')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    public UiComponent getSubscriptionName(final String name) {
        return getViewComponent(SelectorType.XPATH,
                "//*[contains(@class,'ebTableRow')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    public UiComponent getTabByName(final String name) {
        return getViewComponent(SelectorType.XPATH,
                "//div[contains(@class,'ebTabs-tabItem')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    public UiComponent getDropDownByName(final String name) {
        return getViewComponent(SelectorType.XPATH,
                "//div[contains(@class,'ebSelect-value')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    public UiComponent getDropDownValueByName(final String name) {
        return getViewComponent(SelectorType.XPATH,
                "//span[contains(@class,'ebSelect-value')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    // pm initiation breadcrumbLinkByName
    public UiComponent getBreadCrumbLinkByName(final String name) {
        // Use XPATH here for speed. The list can be long, so iterating through
        // it can take a little longer.
        return getViewComponent(SelectorType.XPATH,
                "//*[contains(@class,'ebBreadcrumbs-link')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    // Counter list checkboxByName Parent
    public UiComponent getParentCheckboxByName(final String name) {
        // Use XPATH here for speed. The list can be long, so iterating through
        // it can take a little longer.
        return getViewComponent(SelectorType.XPATH,
                "//*[contains(@class,'ebCheckbox-label')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    // Subcounter checkboxByName
    public UiComponent getCheckboxByName(final String name) {
        // Use XPATH here for speed. The list can be long, so iterating through
        // it can take a little longer.
        return getViewComponent(SelectorType.XPATH, "//input[contains(@name,'"
                + name + "')]", UiComponent.class);
    }

    public Link getNextPageLinkEnabled() {
        return nextPageLinkEnabled;
    }

    public Link getNextPageLinkDisabled() {
        return nextPageLinkDisabled;
    }

    public UiComponent getRemoveButton() {
        return removeButton;
    }

    public UiComponent getPmNodeProcessesLink() {
        return pmNodeProcessesLink;
    }

    public UiComponent getStartDateTimePickerPopup() {
        return startDateTimePickerPopup;
    }

    public UiComponent getEndDateTimePickerPopup() {
        return endDateTimePickerPopup;
    }

    public UiComponent getRowByTitle(final String title) {
        // Use XPATH here for speed. The list can be long, so iterating through
        // it can take a little longer.
        return getViewComponent(SelectorType.XPATH, "//td[contains(@title,'"
                + title + "')]", UiComponent.class);
    }

    public UiComponent getCounterExpansionByName(final String name) {
        // Use XPATH here for speed. The list can be long, so iterating through
        // it can take a little longer.
        return getViewComponent(SelectorType.XPATH,
                "//div[contains(@class,'ebAccordion-button eapmiccommon-wMoClass-"
                        + name + "-drop-down')]", UiComponent.class);
    }

    public TextBox getSubscriptionNameTextBox() {
        return subscriptionNameTextBox;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public UiComponent getResourcesTab() {
        return resourcesTab;
    }

    public UiComponent getCountersTab() {
        return countersTab;
    }

    public UiComponent getSchedulerTab() {
        return schedulerTab;
    }

    public TextBox getDescriptionTextBox() {
        return descriptionTextBox;
    }

    public UiComponent getAddTopologyDataDropdown() {
        return addTopologyDataDropdown;
    }

    public UiComponent getCountersTabHeaderText() {
        return countersTabHeaderText;
    }

    public UiComponent getFileCollectionRopIntervalButton() {
        return fileCollectionRopIntervalButton;
    }

    public UiComponent getSchedulerTimeInputArea() {
        return schedulerTimeInputArea;
    }

    public List<UiComponent> getCountersNameList() {
        return counterNameList;
    }

    public List<UiComponent> getCountersCheckBox() {
        return countersCheckBox;
    }

    public UiComponent getNumberOfCountersSelected() {
        return totalCountersSelected;
    }

    public UiComponent getSaveConfirmationForSubscription() {
        return saveConfirmation;
    }

    public UiComponent getCounterRow() {
        return counterRow;
    }

    public UiComponent getSubHeadingScheduler() {
        return subHeadingScheduler;
    }

    public UiComponent getFirstRowFromNodesTable() {
        return firstRowOfNodesInTable;
    }

    public List<UiComponent> getCounterExpand() {
        return expandCounterRows;
    }

    public List<UiComponent> getSubCountersNameList() {
        return subCountersNameList;
    }

    public UiComponent getNodesTable() {
        return nodeTable;
    }

    public UiComponent getStartTimeDatePicker() {
        return startTimeDatePicker;
    }

    public UiComponent getEndTimeDatePicker() {
        return endTimeDatePicker;
    }

    public UiComponent getRopSelectionBox() {
        return ropSelectionBox;
    }

    public Button getFromDropdownBox() {
        return fromDropDownBox;
    }

    public Button getUntilDropdownBox() {
        return untilDropDownBox;
    }

    public List<UiComponent> getRowsOfNodesFromTable() {
        return rowsOfNodesInTable;
    }

    public UiComponent getPaginationNextButton() {
        return paginationNextButton;
    }

    public UiComponent getPaginationNextButtonDisabled() {
        return paginationNextButtonDisabled;
    }

    public UiComponent getCurrentNodeTablePage() {
        return currentNodeTablePage;
    }

    public UiComponent getLoadingBar() {
        return tableLoadingBar;
    }

    public UiComponent getNumberOfNodesFromTable() {
        return numberOfNodes;
    }

    public UiComponent getDialogBox() {
        return dialogBox;
    }

    public UiComponent getConfirmNodeRemovalButton() {
        return confirmNodeRemovalButton;
    }

    public UiComponent waitForSubheadingToAppearUnderTab() {
        return tabSubHeading;
    }

    public UiComponent getCountersTabTableHeadings() {
        return countersTableRow;
    }

    public UiComponent getCounterNameSearchTextBox() {
        return counterNameSearchBox;
    }

    public UiComponent getCounterTableBodyFirstRow() {
        return countersTableBodyFirstRow;
    }

    public UiComponent getSourceObjectSearchTextBox() {
        return sourceObjectSearchBox;
    }

    public UiComponent getSubscribeButton() {
        return subscribeButton;
    }

    public UiComponent selectAllFilteredCountersCheckBox() {
        return selectAllCounters;
    }

    public UiComponent selectNodeByName(final String nodeName) {
        return getViewComponent(SelectorType.XPATH,
                String.format("//td[contains(@title, '%s')]", nodeName),
                UiComponent.class);

    }
}
