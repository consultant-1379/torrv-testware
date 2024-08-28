package com.ericsson.nms.rv.taf.test.ui.viewmodels;

import java.util.List;

import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.sdk.*;

public class QueryBuilderViewModel extends GenericViewModel {

    public UiComponent getSplashMessage() {
        return getViewComponent(
                ".eaNetworkExplorer-rQueryBuilder-main-splashMessage",
                UiComponent.class);
    }

    public UiComponent getQueryBuilderSearch() {
        return getViewComponent(".eaNetworkExplorer-rQueryBuilder-main-search",
                UiComponent.class);
    }

    public UiComponent getSwitchToSearchLink() {
        return getViewComponent(
                ".eaNetworkExplorer-rQueryBuilder-bottomControls-switchToSearch-link",
                UiComponent.class);
    }

    public UiComponent getAddNodeButton() {
        return getViewComponent(
                ".eaNetworkExplorer-rQueryBuilder-controls-findNode",
                UiComponent.class);
    }

    public UiComponent getAddMOButton() {
        return getViewComponent(
                ".eaNetworkExplorer-rQueryBuilder-controls-findMO",
                UiComponent.class);
    }

    public List<UiComponent> getModelDrivenSelectBoxes() {
        return getViewComponents(".eaNetworkExplorer-wModelDrivenSelectBox",
                UiComponent.class);
    }

    public UiComponent getFirstSelectItem() {
        return getViewComponent(".ebComponentList-item", UiComponent.class);
    }

    public List<UiComponent> getSelectItems() {
        return getViewComponents(".ebComponentList-item", UiComponent.class);
    }

    public UiComponent getClearButton() {
        return getViewComponent(
                ".eaNetworkExplorer-rQueryBuilder-main-search-clear-link",
                UiComponent.class);
    }

    public UiComponent getSearchButton() {
        return getViewComponent(
                ".eaNetworkExplorer-rQueryBuilder-bottomControls-searchBtnIcon",
                UiComponent.class);
    }

    public UiComponent getFirstAddEditCriteriaLink() {
        return getViewComponent(".eaNetworkExplorer-wCriteria-addEditLink",
                UiComponent.class);
    }

    public UiComponent getAddEditCriteriaLinkAtIndex(final int index) {
        // Use XPATH here because when we target a specific element in the DOM
        // rather than a list of elements, we can we can use waitForComponent
        // with it later on.
        return getViewComponent(SelectorType.XPATH,
                "(//*[contains(@class, 'eaNetworkExplorer-wCriteria-addEditLink')])["
                        + (index + 1) + "]", UiComponent.class);
    }

    public List<UiComponent> getAddMoreLinks() {
        return getViewComponents(
                ".eaNetworkExplorer-wCriteriaAttribute-addMoreLink",
                UiComponent.class);
    }

    public UiComponent getAttributeSelectBox() {
        return getViewComponent(
                ".eaNetworkExplorer-wCriteriaAttribute-attributeSelectBox",
                UiComponent.class);
    }

    public UiComponent getAttributeSelectBoxAtIndex(final int index) {
        // Use XPATH here because when we target a specific element in the DOM
        // rather than a list of elements, we can we can use waitForComponent
        // with it later on.
        return getViewComponent(
                SelectorType.XPATH,
                "(//*[contains(@class, 'eaNetworkExplorer-wCriteriaAttribute-attributeSelectBox')])["
                        + (index + 1) + "]", UiComponent.class);
    }

    public List<UiComponent> getOperatorSelectBoxes() {
        return getViewComponents(
                ".eaNetworkExplorer-wCriteriaAttribute-operatorSelectBox",
                UiComponent.class);
    }

    public List<UiComponent> getAttributeValueInputs() {
        return getViewComponents(
                ".eaNetworkExplorer-wCriteriaAttribute-valueInput",
                UiComponent.class);
    }

    public List<UiComponent> getAttributeValueSelectBoxes() {
        return getViewComponents(
                ".eaNetworkExplorer-wCriteriaAttribute-valueSelectBox",
                UiComponent.class);
    }

    public UiComponent getDeleteIcon() {
        return getViewComponent(
                ".eaNetworkExplorer-wCriteriaAttribute-deleteButton",
                UiComponent.class);
    }

    public UiComponent getDeleteIconByIndex(final int index) {
        // Use XPATH here because when we target a specific element in the DOM
        // rather than a list of elements, we can we can use waitForComponent
        // with it later on.
        return getViewComponent(SelectorType.XPATH,
                "(//*[contains(@class, 'eaNetworkExplorer-wCriteriaAttribute-deleteButton')])["
                        + (index + 1) + "]", UiComponent.class);
    }

    public UiComponent getHideEyeIconByIndex(final int index) {
        // Use XPATH here because when we target a specific element in the DOM
        // rather than a list of elements, we can we can use waitForComponent
        // with it later on.
        return getViewComponent(SelectorType.XPATH,
                "(//*[contains(@class, 'eaNetworkExplorer-wQueryItem-shownEyeIcon')])["
                        + (index + 1) + "]", UiComponent.class);
    }

    public UiComponent getShowEyeIconByIndex(final int index) {
        // Use XPATH here because when we target a specific element in the DOM
        // rather than a list of elements, we can we can use waitForComponent
        // with it later on.
        return getViewComponent(SelectorType.XPATH,
                "(//*[contains(@class, 'eaNetworkExplorer-wQueryItem-hiddenEyeIcon')])["
                        + (index + 1) + "]", UiComponent.class);
    }

    public Button getLastDoneEditingButton() {
        return getViewComponent(SelectorType.XPATH,
                "(//button[text() = 'Done Editing'])[last()]", Button.class);
    }

    public List<UiComponent> getFirstAtributesTemplates() {
        return getViewComponents(
                ".eaNetworkExplorer-wCriteria-currentListTemplate",
                UiComponent.class);
    }

    public UiComponent getDropDownList() {
        return getViewComponent(".ebComponentList eb_scrollbar",
                UiComponent.class);
    }

    public UiComponent getHasChild() {
        // Use XPATH here because when we target a specific element in the DOM
        // rather than a list of elements, we can we can use waitForComponent
        // with it later on.
        return getViewComponent(
                SelectorType.XPATH,
                "(//span[contains(@class, 'eaNetworkExplorer-wQueryItem-addChild')])[last()]",
                UiComponent.class);
    }

    public Button getQueryBuilderMoiInputFieldDropdownButtonByIndex(
            final int criteriaBuilderQueryItemNumber) {
        return getViewComponent(
                SelectorType.XPATH,
                "(//div[contains(@class, 'eaNetworkExplorer-wQueryItem-wrapper')])["
                        + criteriaBuilderQueryItemNumber
                        + "]//div[@class = 'eaNetworkExplorer-wQueryItem-selectBoxesHolder']//button",
                Button.class);
    }

    public Button getLastAddCriteria(final int index) {
        return getViewComponent(
                SelectorType.XPATH,
                "(//div[contains(@class, 'eaNetworkExplorer-wQueryItem-wrapper')])["
                        + index
                        + "]//a[@class = 'eaNetworkExplorer-wCriteria-addEditLink'][last()]",
                Button.class);
    }

    public Button getLastAddMoreCriteria(final int index) {
        return getViewComponent(
                SelectorType.XPATH,
                "(//div[contains(@class, 'eaNetworkExplorer-wQueryItem-wrapper')])["
                        + index
                        + "]//a[@class = 'eaNetworkExplorer-wCriteriaAttribute-addMoreLink'][last()]",
                Button.class);
    }

    public Button getAttributeValueButtonByIndex(final int index) {
        return getViewComponent(SelectorType.XPATH,
                "(//button[contains(@class, 'ebSelect-header')])["
                        + (index + 1) + "]", Button.class);
    }

    public UiComponent getSelectBoxItemByName(final String name) {
        // Use XPATH here for speed. The list can be long, so iterating through
        // it can take a little longer.
        return getViewComponent(SelectorType.XPATH,
                "//*[contains(@class,'ebComponentList-item')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    public Button getCriteriaAttributeKeyButtonByIndex(final int index) {
        return getViewComponent(
                SelectorType.XPATH,
                "(//div[@class = 'eaNetworkExplorer-wCriteriaAttribute'])["
                        + index
                        + "]//div[@class = 'eaNetworkExplorer-wCriteriaAttribute-attributeInputWrapper']//button",
                Button.class);
    }

    public Button getCriteriaAttriuteOperatorButtonByIndex(final int index) {
        return getViewComponent(
                SelectorType.XPATH,
                "(//div[@class = 'eaNetworkExplorer-wCriteriaAttribute'])["
                        + index
                        + "]//span[@class = 'eaNetworkExplorer-wCriteriaAttribute-operatorSelectBox']//button",
                Button.class);
    }

    public TextBox getCriteriaAttributeValueInputByIndex(final int index) {
        return getViewComponent(
                SelectorType.XPATH,
                "(//div[@class = 'eaNetworkExplorer-wCriteriaAttribute'])["
                        + index
                        + "]//div[@class = 'eaNetworkExplorer-wCriteriaAttribute-valueInputWrapper']//input",
                TextBox.class);
    }

    public Button getCriteriaAttributeValueSelectBoxByIndex(final int index) {
        return getViewComponent(
                SelectorType.XPATH,
                "(//div[@class = 'eaNetworkExplorer-wCriteriaAttribute'])["
                        + index
                        + "]//span[@class = 'eaNetworkExplorer-wCriteriaAttribute-valueSelectBox']//button",
                Button.class);
    }

}