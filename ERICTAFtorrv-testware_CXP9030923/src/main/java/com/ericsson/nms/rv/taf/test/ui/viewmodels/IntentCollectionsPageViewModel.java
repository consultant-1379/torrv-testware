package com.ericsson.nms.rv.taf.test.ui.viewmodels;

import java.util.List;

import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

/*
 Elements that are visible when intent page is present.
 */
public class IntentCollectionsPageViewModel extends GenericViewModel {

    @UiComponentMapping(".elCollectionsSavedSearchesWidget-wListItem")
    private UiComponent firstListItem;

    @UiComponentMapping(".elCollectionsSavedSearchesWidget-wListItem")
    private List<UiComponent> listItems;

    @UiComponentMapping(".ebBtn_color_green")
    private UiComponent returnButton;

    @UiComponentMapping(".eaNetworkExplorerCollections-cancelButton")
    private UiComponent cancelButtonWrapper;

    @UiComponentMapping(".ebCheckbox-label elCollectionsSavedSearchesWidget-wListItem-collectionName")
    private List<UiComponent> savedCollections;

    public List<UiComponent> getCheckboxes() {
        return this.getViewComponents(".ebCheckbox", UiComponent.class);
    }

    public List<UiComponent> getSavedCollections() {
        return savedCollections;
    }

    public UiComponent getFirstListItem() {
        return firstListItem;
    }

    public List<UiComponent> getListItems() {
        return listItems;
    }

    public UiComponent getReturnButton() {
        return returnButton;
    }

    public UiComponent getCancelButtonWrapper() {
        return cancelButtonWrapper;
    }

    public UiComponent getCancelButton() {
        return getCancelButtonWrapper().getChildren().get(0);
    }

    public UiComponent getFavoriteIcon(final String collectionSearchName) {
        return getViewComponent(
                SelectorType.XPATH,
                "//span[contains(@class, 'ebCheckbox-label') and text() = '"
                        + collectionSearchName
                        + "']/..//span[contains(@class, 'elCollectionsSavedSearchesWidget-wListItem-favoriteIcon')]",
                UiComponent.class);
    }

}