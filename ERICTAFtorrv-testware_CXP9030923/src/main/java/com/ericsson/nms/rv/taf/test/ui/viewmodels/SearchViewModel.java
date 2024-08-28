package com.ericsson.nms.rv.taf.test.ui.viewmodels;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.*;

public class SearchViewModel extends GenericViewModel implements
        LoaderViewModel {
    public static final String NETWORK_ELEMENT = "NetworkElement";

    @UiComponentMapping(".elLayouts-TopSection-title")
    private UiComponent searchTabHeaderText;

    @UiComponentMapping(".ebTableRow")
    private List<UiComponent> tableRowsList;

    @UiComponentMapping(selector = "//input[contains(@class,'ebCheckbox')]", selectorType = SelectorType.XPATH)
    private List<UiComponent> nodeCheckBoxList;

    @UiComponentMapping(selector = "//*[contains(@class,'ebBtn-caption')][normalize-space(text())='Return Selected Objects']", selectorType = SelectorType.XPATH)
    private UiComponent returnSelectedObjectsButton;

    @UiComponentMapping(".ebTable")
    private UiComponent nodeTable;

    // next page button
    @UiComponentMapping(".ebPagination-nextAnchor")
    private UiComponent nextPageButton;

    // Next page button disabled
    @UiComponentMapping(".ebPagination-nextAnchor_disabled")
    private UiComponent nextPageButtonDisabled;

    // current page number
    @UiComponentMapping(".ebPagination-entryAnchor_current")
    private UiComponent pageNumber;

    @UiComponentMapping(selector = "//button[contains(@class, 'elLayouts-ActionBarButton') and span[text() = 'Add Objects to a New Collection']]", selectorType = SelectorType.XPATH)
    private UiComponent addObjectToNewCollectionButton;

    @UiComponentMapping(selector = "//button[contains(@class, 'elLayouts-ActionBarButton') and span[text() = 'Save Search']]", selectorType = SelectorType.XPATH)
    private UiComponent saveSearchButton;

    @UiComponentMapping(selector = "//button[contains(@class, 'elLayouts-ActionBarButtonFlat') and span[text() = 'Open with Topology Browser']]", selectorType = SelectorType.XPATH)
    private UiComponent openWithTopologyBrowserButton;

    // checkbox
    @UiComponentMapping(".ebCheckbox")
    private UiComponent checkBox;

    @UiComponentMapping(".eaNetworkExplorer-rResults-loadingAnimation")
    private UiComponent resultsTableLoader;

    //We don't use these loaders. Keeping names for future reference.
    //eaNetworkExplorer-rSlidingMenu-collectionList-loadingAnimation
    //eaNetworkExplorer-rSlidingMenu-savedSearchesList-loadingAnimation
    //eaNetworkExplorer-rSlidingMenu-favoritesLists-loadingAnimation

    public UiComponent getRootElement() {
        return getViewComponent(".eaNetworkExplorer-rSearch", UiComponent.class);
    }

    public UiComponent getSaveSearchButton() {
        return saveSearchButton;
    }

    public UiComponent getAddObjectToNewCollectionButton() {
        return addObjectToNewCollectionButton;
    }

    public TextBox getSearchInput() {
        return getViewComponent(".eaNetworkExplorer-wSearchInput-searchInput",
                TextBox.class);
    }

    public Button getSearchButton() {
        return getViewComponent(".eaNetworkExplorer-rSearch-form-searchBtn",
                Button.class);
    }

    public UiComponent getSwitchToQueryBuilderLink() {
        return getViewComponent(
                ".eaNetworkExplorer-rSearch-form-switchToBuilder-link",
                UiComponent.class);
    }

    public UiComponent getOpenWithTopologyBrowserButton() {
        return openWithTopologyBrowserButton;
    }

    public UiComponent getSearchTabHeaderText() {
        return searchTabHeaderText;
    }

    public UiComponent getReturnObjectsButton() {
        return returnSelectedObjectsButton;
    }

    public UiComponent getNodeTable() {
        return nodeTable;
    }

    public List<UiComponent> getNodeCheckBoxes() {
        return nodeCheckBoxList;
    }

    public List<UiComponent> getTableRows() {
        return tableRowsList;
    }

    @Override
    public List<UiComponent> getLoadingWidgets() {
        final ArrayList<UiComponent> list = new ArrayList<UiComponent>();
        list.add(0, resultsTableLoader);
        return list;
    }

    public UiComponent getPaginationNextButton() {
        return nextPageButton;
    }

    public UiComponent getPaginationNextButtonDisabled() {
        return nextPageButtonDisabled;
    }

    public UiComponent getCurrentSearchTablePage() {
        return pageNumber;
    }

    public UiComponent getCheckBox() {
        return checkBox;
    }

    public CheckBox selectNodeByName(final String nodeName) {
        return getViewComponent(
                SelectorType.XPATH,
                String.format(
                        "//tr[contains(@class, 'ebTableRow')]//td[contains(@title, '%s')]/preceding-sibling::td[@class='elTablelib-CheckboxCell']",
                        nodeName), CheckBox.class);
    }
}