package com.ericsson.nms.rv.taf.test.ui.viewmodels;

import java.util.List;

import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.Link;

public class CollectionsPageViewModel extends GenericViewModel {

    // TODO: export breadcrumbs to ContainerModelView ?
    @UiComponentMapping(".elLayouts-TopSection-title")
    private UiComponent topLevelHeader;

    @UiComponentMapping(".ebBreadcrumbs-link")
    private List<UiComponent> breadCrumbLinks;

    @UiComponentMapping(".ebLayout-SectionHeading h2")
    private UiComponent sectionHeading;

    @UiComponentMapping(".eaNetworkExplorerCollections-wListItem-viewLink")
    private UiComponent firstViewLink;

    @UiComponentMapping(".elCollectionsSavedSearchesWidget-wListItem")
    private UiComponent firstListItem;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//span[contains(@class,'ebBtn-caption')][normalize-space(text())='Return Selected Collections']")
    private UiComponent returnSelectedCollectionsButton;

    @UiComponentMapping(".ebLoader-Dots")
    private UiComponent tableLoadingBar;

    @UiComponentMapping(".ebCheckbox-label")
    private List<UiComponent> collectionNameList;

    @UiComponentMapping(".elCollectionsSavedSearchesWidget-rList")
    private UiComponent collectionTable;

    @UiComponentMapping(".elCollectionsSavedSearchesWidget-wListItem-infoDetail")
    private UiComponent infoDetailFirstRow;

    @UiComponentMapping(".eapmicsubscription-wNetworkElementsSelection-header-nodes-pageInfo")
    private UiComponent nodesTotal;

    public UiComponent getCollectionByName(String name) {
        // Use XPATH here for speed. The list can be long, so iterating through
        // it can take a little longer.
        return getViewComponent(
                SelectorType.XPATH,
                "//*[contains(@class,'ebCheckbox-label elCollectionsSavedSearchesWidget-wListItem-collectionName')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    public Link getViewLinkByName(String collectionName) {
        return getViewComponent(
                SelectorType.XPATH,
                "//*[contains(@class,'ebCheckbox-label elCollectionsSavedSearchesWidget-wListItem-collectionName')][normalize-space(text())='"
                        + collectionName
                        + "']/..//a[contains(@class, 'elCollectionsSavedSearchesWidget-wListItem-viewLink')]",
                Link.class);
    }

    public UiComponent getDeleteUiComponentByName(String collectionName) {
        return getViewComponent(
                SelectorType.XPATH,
                "//*[contains(@class,'ebCheckbox-label elCollectionsSavedSearchesWidget-wListItem-collectionName')][normalize-space(text())='"
                        + collectionName
                        + "']/..//*[contains(@class, 'elCollectionsSavedSearchesWidget-wListItem-deleteIcon')]",
                UiComponent.class);
    }

    public UiComponent getTableLoadingBar() {
        return tableLoadingBar;
    }

    public UiComponent getTopLevelHeader() {
        return topLevelHeader;
    }

    public UiComponent getSectionHeading() {
        return sectionHeading;
    }

    public List<UiComponent> getBreadCrumbLinks() {
        return breadCrumbLinks;
    }

    public UiComponent getLastBreadCrumbLink() {
        return breadCrumbLinks.get(breadCrumbLinks.size() - 1);
    }

    public UiComponent getFirstViewLink() {
        return firstViewLink;
    }

    public List<UiComponent> getNames() {
        return this.getViewComponents(
                ".eaNetworkExplorerCollections-wListItem-collectionName",
                UiComponent.class);
    }

    public List<UiComponent> getDeleteButtons() {
        return this.getViewComponents(
                ".eaNetworkExplorerCollections-wListItem-deleteIcon",
                UiComponent.class);
    }

    public List<UiComponent> getCheckboxes() {
        return this.getViewComponents(
                ".eaNetworkExplorerCollections-wListItem-checkbox",
                UiComponent.class);
    }

    public UiComponent getFirstListItem() {
        return firstListItem;
    }

    public UiComponent getSelectedCollectionsButton() {
        return returnSelectedCollectionsButton;
    }

    public List<UiComponent> getCollectionNameList() {
        return collectionNameList;
    }

    public UiComponent getCollectionTable() {
        return collectionTable;
    }

    /**
     * @return
     */
    public UiComponent getFirstRowInfoDetail() {
        return infoDetailFirstRow;
    }

    /**
     * @return
     */
    public UiComponent getNodesTotal() {
        return nodesTotal;
    }
}