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
import com.ericsson.cifwk.taf.ui.sdk.Button;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

public class PMInitiationViewModel extends GenericViewModel {

    public static final String ACTION_CREATE_CELL_TRACE_SUBSCRIPTION = "Cell Trace Subscription";
    public static final String ACTION_CREATE_STATISTICAL_SUBSCRIPTION = "Statistical Subscription";
    public static final String ACTIVE_STATUS = "Active";
    public static final String INACTIVE_STATUS = "Inactive";
    public static final String UPDATING_STATUS = "Updating";
    public static final String DEACTIVATING_STATUS = "Deactivating";
    public static final String ACTIVATING_STATUS = "Activating";
    public static final String VIEW_SUBSCRIPTION_ = "View Subscription";
    public static final String SCHEDULE_CONTINUOUS_STATUS = "Continuous";
    public static final String CONTINUE_ACTIVATING_BUTTON = "Continue Activating";

    @UiComponentMapping(".ebBtn")
    private UiComponent actionButton;

    @UiComponentMapping(".ebComponentList")
    private UiComponent actionList;

    @UiComponentMapping(selector = "//*[contains(@class,'elLayouts-PanelButton-text')][normalize-space(text())='Activate']", selectorType = SelectorType.XPATH)
    private Button activateSubscriptionButton;

    @UiComponentMapping(selector = "//*[contains(@class,'elLayouts-PanelButton-text')][normalize-space(text())='Deactivate']", selectorType = SelectorType.XPATH)
    private Button deactivateSubscriptionButton;

    @UiComponentMapping(selector = "//*[contains(@class,'elLayouts-PanelButton-text')][normalize-space(text())='Delete']", selectorType = SelectorType.XPATH)
    private Button deleteSubscriptionButton;

    @UiComponentMapping("button.ebBtn_color_darkBlue")
    private Button deleteSubscriptionConfirmationButton;

    @UiComponentMapping(selector = "//*[contains(@class,'elLayouts-PanelButton-text')][normalize-space(text())='Edit Subscription']", selectorType = SelectorType.XPATH)
    private Button editSubscriptionButton;

    @UiComponentMapping(".elLayouts-TopSection-title")
    private UiComponent pmInitiationHeader;

    @UiComponentMapping(".ebTableRow")
    private List<UiComponent> subscriptionTableRows;

    @UiComponentMapping(".eapmiclistsubscription-wSubscriptionTable")
    private UiComponent pmSubscriptionTable;

    @UiComponentMapping(".eapmiclistsubscription-wStatusCell")
    private List<UiComponent> statusList;

    @UiComponentMapping(".eapmiclistsubscription-wNameCell-subName")
    private List<UiComponent> subscriptionNames;

    @UiComponentMapping(".ebNotification-label")
    private UiComponent activateDeactivateNotificationLabel;

    @UiComponentMapping(".ebIcon_tick")
    private UiComponent activateDeactivateNotificationSuccessIcon;

    @UiComponentMapping(".ebDialogBox")
    private UiComponent dialogBox;

    @UiComponentMapping(".ebPagination-nextAnchor_disabled")
    private UiComponent nextPageButtonDisabled;

    public UiComponent getActionButton() {
        return actionButton;
    }

    @UiComponentMapping(".ebPagination-nextAnchor")
    private UiComponent paginationNextButtton;

    // @UiComponentMapping(selector =
    // "//a[contains(@class,'ebPagination-entryAnchor ebPagination-entryAnchor_current')]",
    // selectorType = SelectorType.XPATH)
    @UiComponentMapping(".ebPagination-entryAnchor_current")
    private UiComponent currentPage;

    @UiComponentMapping("body")
    private UiComponent pibResponseBody;

    @UiComponentMapping(".ebTableRow_highlighted")
    public UiComponent tableRowHighlighted;

    public UiComponent getActionList() {
        return actionList;
    }

    public UiComponent getCreateSubscriptionDropdownButtonActionByName(
            final String name) {
        return getViewComponent(
                SelectorType.XPATH,
                String.format(
                        "//div[contains(@class, 'ebComponentList-item') and normalize-space(text()) = '%s']",
                        name), UiComponent.class);
    }

    public UiComponent getActionItemByName(final String name) {
        // Use XPATH here for speed. The list can be long, so iterating through
        // it can take a little longer.
        return getViewComponent(SelectorType.XPATH,
                "//*[contains(@class,'elLayouts-ActionBarItem')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    public UiComponent getSubscriptionByName(final String name) {
        // Use XPATH here for speed. The list can be long, so iterating through
        // it can take a little longer.
        return getViewComponent(
                SelectorType.XPATH,
                "//*[contains(@class,'eapmiclistsubscription-wNameCell-subName')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    public UiComponent getActionBarItem(final String name) {
        // Use XPATH here for speed. The list can be long, so iterating through
        // it can take a little longer.
        return getViewComponent(SelectorType.XPATH,
                "//*[contains(@class,'elLayouts-ActionBarItem')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    public UiComponent getDialogBoxButtonsByName(final String name) {
        // Use XPATH here for speed. The list can be long, so iterating through
        // it can take a little longer.
        return getViewComponent(SelectorType.XPATH,
                "//*[contains(@class,'ebBtn-caption')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    public Button getActivateSubscriptionButton() {
        return activateSubscriptionButton;
    }

    public Button getDeactivateSubscriptionButton() {
        return deactivateSubscriptionButton;
    }

    public Button getDeleteSubscriptionButton() {
        return deleteSubscriptionButton;
    }

    public Button getDeleteSubscriptionConfirmationButton() {
        return deleteSubscriptionConfirmationButton;
    }

    public Button getEditSubscriptionButton() {
        return editSubscriptionButton;
    }

    public UiComponent getPMInitiationHeader() {
        return pmInitiationHeader;
    }

    public List<UiComponent> getTableRows() {
        return subscriptionTableRows;
    }

    public List<UiComponent> getStatusList() {
        return statusList;
    }

    public List<UiComponent> getSubscriptionNameList() {
        return subscriptionNames;
    }

    public UiComponent getActivateDeactivateNotficationLabel() {
        return activateDeactivateNotificationLabel;
    }

    public UiComponent getActivateDeactivateSuccessNotificationIcon() {
        return activateDeactivateNotificationSuccessIcon;
    }

    public UiComponent getPaginationNextButton() {
        return paginationNextButtton;
    }

    public UiComponent getCurrentPage() {
        return currentPage;
    }

    public UiComponent getPmSubscriptionTable() {
        return pmSubscriptionTable;
    }

    public UiComponent getDialogBox() {
        return dialogBox;
    }

    public UiComponent getPaginationNextButttonDisabled() {
        return nextPageButtonDisabled;
    }

    public UiComponent getBody() {
        return pibResponseBody;
    }

    public UiComponent getTableRowHighlighted() {
        return tableRowHighlighted;
    }

}
