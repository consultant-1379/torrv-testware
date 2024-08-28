package com.ericsson.nms.rv.taf.test.ui.viewmodels;

import java.util.List;

import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.CheckBox;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

public class ResultsViewModel extends GenericViewModel {

    // Actions-DropDown  related mappings!

    @UiComponentMapping(".ebDropdown-header")
    private UiComponent actionBarButton;

    @UiComponentMapping(selector = "//div[contains(@class, 'ebComponentList') and contains(text(), 'Topology Browser')]", selectorType = SelectorType.XPATH)
    private UiComponent topologyBrowserActionBar;

    // Table related mappings!

    public UiComponent getTable() {
        return getViewComponent(".ebTable", UiComponent.class);
    }

    public List<UiComponent> getTableHeaders() {
        //return getViewComponents(".elWidgets-Table-body thead tr th", UiComponent.class);
        return getViewComponents(".ebTable thead tr th", UiComponent.class);
    }

    public UiComponent getFirstTableRow() {
        return getViewComponent(".ebTable .elTablelib-Table-body .ebTableRow",
                UiComponent.class);
    }

    public List<UiComponent> getTableRows() {
        return getViewComponents(".ebTable .elTablelib-Table-body .ebTableRow",
                UiComponent.class);
    }

    public List<CheckBox> getResultRowCheckboxes() {
        return getViewComponents(".ebCheckbox", CheckBox.class);
    }

    public UiComponent getActionBarButton() {
        return actionBarButton;
    }

    public UiComponent getTopologyBrowserActionBar() {
        return topologyBrowserActionBar;
    }

    public UiComponent getInfoMessage() {
        return getViewComponent(".eaNetworkExplorer-rResults-message",
                UiComponent.class);
    }

    public UiComponent getEmptyResultsMessage() {
        return getViewComponent(".eaNetworkExplorer-rResults-noResultsMessage",
                UiComponent.class);
    }

    public UiComponent getErrorMessageHeader() {
        return getViewComponent(
                ".eaNetworkExplorer-rResults-errorMessageHeader",
                UiComponent.class);
    }

    public UiComponent getErrorMessageParagraph() {
        return getViewComponent(
                ".eaNetworkExplorer-rResults-errorMessageParagraph",
                UiComponent.class);
    }

    public UiComponent getPageSizeSelecBoxHolder() {
        return getViewComponent(
                ".eaNetworkExplorer-rResults-actionPanel-pageSizeHolder",
                UiComponent.class);
    }

    public UiComponent getSelectBoxItemByName(final String name) {
        // Use XPATH here for speed. The list can be long, so iterating through it can take a little longer.
        return getViewComponent(SelectorType.XPATH,
                "//*[contains(@class,'ebComponentList-item')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    public UiComponent getNextPageLi() {
        return getViewComponent(".ebPagination-next", UiComponent.class);
    }

    public UiComponent getPreviousPageLi() {
        return getViewComponent(".ebPagination-previous", UiComponent.class);
    }

    public UiComponent getPaginationEntryAnchor() {
        return getViewComponent(".ebPagination-entryAnchor", UiComponent.class);
    }

    public UiComponent getPaginationEntryAnchorWithXpath(final int index) {
        return getViewComponent(SelectorType.XPATH,
                "(//*[contains(@class, 'ebPagination-entryAnchor')])["
                        + (index + 1) + "]", UiComponent.class);
    }

    public List<UiComponent> getPaginationEntryAnchors() {
        return getViewComponents(".ebPagination-entryAnchor", UiComponent.class);
    }

    public UiComponent getNoResultMessage() {
        return getViewComponent(
                SelectorType.XPATH,
                "//*[contains(@class,'eaNetworkExplorer-rResults-message eaNetworkExplorer-rResults-noResultsMessage')]",
                UiComponent.class);
    }
}