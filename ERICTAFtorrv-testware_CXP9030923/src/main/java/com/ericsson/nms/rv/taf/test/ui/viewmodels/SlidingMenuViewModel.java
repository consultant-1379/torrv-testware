package com.ericsson.nms.rv.taf.test.ui.viewmodels;

import java.util.List;

import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

public class SlidingMenuViewModel extends GenericViewModel {

    @UiComponentMapping(selector = ".eaNetworkExplorer-rSlidingMenu-collectionList .eaNetworkExplorer-wLimitedList-topList-listItem", selectorType = SelectorType.CSS)
    private UiComponent firstCollectionListItem;

    @UiComponentMapping(selector = ".eaNetworkExplorer-rSlidingMenu-savedSearchesList .eaNetworkExplorer-wLimitedList-topList-listItem", selectorType = SelectorType.CSS)
    private UiComponent firstSavedSearchListItem;

    @UiComponentMapping(selector = ".eaNetworkExplorer-rSlidingMenu-savedSearchesList .eaNetworkExplorer-wLimitedList-showMore", selectorType = SelectorType.CSS)
    private UiComponent viewAllSavedSearchesLink;

    @UiComponentMapping(selector = ".eaNetworkExplorer-rSlidingMenu-collectionList .eaNetworkExplorer-wLimitedList-showMore", selectorType = SelectorType.CSS)
    private UiComponent viewAllCollectionsLink;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[@class='eaNetworkExplorer-rSlidingMenu-favoritesLists-collections']//li")
    private List<UiComponent> favoriteCollections;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[@class='eaNetworkExplorer-rSlidingMenu-favoritesLists-collections']//li")
    private UiComponent favoriteCollection;

    public UiComponent getFirstCollectionListItem() {
        return firstCollectionListItem;
    }

    public UiComponent getFirstSavedSearchListItem() {
        return firstSavedSearchListItem;
    }

    public List<UiComponent> getCollectionListItems() {
        return getViewComponents(
                ".eaNetworkExplorer-rSlidingMenu-collectionList .eaNetworkExplorer-wLimitedListItem",
                UiComponent.class);
    }

    public UiComponent getFavoritesCollection() {
        return favoriteCollection;
    }

    public List<UiComponent> getFavoritesCollections() {
        return favoriteCollections;
    }

    public UiComponent getFavoritesSearch() {
        return getViewComponent(
                ".eaNetworkExplorer-rSlidingMenu-favoritesLists .eaNetworkExplorer-rSlidingMenu-favoritesLists-savedSearches .eaNetworkExplorer-wLimitedListItem",
                UiComponent.class);
    }

    public List<UiComponent> getFavoritesSearches() {
        return getViewComponents(
                ".eaNetworkExplorer-rSlidingMenu-favoritesLists .eaNetworkExplorer-rSlidingMenu-favoritesLists-savedSearches .eaNetworkExplorer-wLimitedListItem",
                UiComponent.class);
    }

    public List<UiComponent> getSavedSearchListItems() {
        return getViewComponents(
                ".eaNetworkExplorer-rSlidingMenu-savedSearchesList .eaNetworkExplorer-wLimitedList-topList-listItem",
                UiComponent.class);
    }

    public List<UiComponent> getSavedSearch(String searchName) {
        return getViewComponents(
                ".eaNetworkExplorer-rSlidingMenu-savedSearchesList .eaNetworkExplorer-wLimitedList-topList-listItem text()="
                        + searchName, UiComponent.class);
    }

    public UiComponent getViewAllSavedSearchesLink() {
        return viewAllSavedSearchesLink;
    }

    public UiComponent getViewAllCollectionsLink() {
        return viewAllCollectionsLink;
    }
}