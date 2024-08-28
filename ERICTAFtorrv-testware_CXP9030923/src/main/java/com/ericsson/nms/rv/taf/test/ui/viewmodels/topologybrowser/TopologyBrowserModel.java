package com.ericsson.nms.rv.taf.test.ui.viewmodels.topologybrowser;

import java.util.List;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.*;

public class TopologyBrowserModel extends GenericViewModel {

    private static final Logger logger = Logger
            .getLogger(TopologyBrowserModel.class);

    @UiComponentMapping(".eaTopologyBrowser")
    private Label applicationContainer;

    @UiComponentMapping(".eaTopologyBrowser-rSelectedNodeProperties-RegionContainer")
    private Label seletcedNodeDetailsContainer;

    @UiComponentMapping(".elLayouts-QuickActionBar-right>.elLayouts-PanelButton>.elLayouts-PanelButton-menu>.elLayouts-PanelButton-button")
    private Button attributesButton;

    @UiComponentMapping(".elLayouts-QuickActionBar-left>.elLayouts-PanelButton>.elLayouts-PanelButton-menu>.elLayouts-PanelButton-button")
    private Button mainButton;

    @UiComponentMapping(".elLayouts-QuickActionBar-right>div.elLayouts-PanelButton>div.elLayouts-PanelButton-menu>button.elLayouts-PanelButton-button")
    private Link viewDetailsLink;

    @UiComponentMapping(".eaTopologyBrowser-rActionBar-hideDetailsLink")
    private Link viewDetailsClose;

    @UiComponentMapping(".elLayouts-QuickActionBar-left>.elLayouts-PanelButton>.elLayouts-PanelButton-menu>.elLayouts-PanelButton-button>.ebIcon_leftArrowLarge")
    private Label mainLeftArrowIcon;

    @UiComponentMapping(".elLayouts-QuickActionBar-right>.elLayouts-PanelButton>.elLayouts-PanelButton-menu>.elLayouts-PanelButton-button>.ebIcon_leftArrowLarge")
    private Label detailsLeftArrowIcon;

    @UiComponentMapping(".elLayouts-QuickActionBar-right>.elLayouts-PanelButton>.elLayouts-PanelButton-menu>.elLayouts-PanelButton-button>.ebIcon_rightArrowLarge")
    private Label detailsRightArrowIcon;

    @UiComponentMapping(".eaTopologyBrowser-rTopologyTree-legend")
    private Label legendContainer;

    public UiComponent getAttributeButton() {
        return attributesButton;
    }

    @Override
    public boolean isCurrentView() {
        return hasComponent(".eaTopologyBrowser");
    }

    public UiComponent getTitleHeader() {
        return getViewComponent(
                SelectorType.XPATH,
                "//*[contains(@class,'elLayouts-TopSection-title')][normalize-space(text())='Topology Browser']",
                UiComponent.class);
    }

    /*
     * Includes margins
     */
    public int getApplicationHolderWidth() {
        final UiComponentSize containerSize = applicationContainer.getSize();
        logger.info("CONTAINER WIDTH: " + containerSize.getWidth());
        return containerSize.getWidth();
    }

    public int getLayoutOffestWidth(final BrowserTab browserTab) {
        final Object applicationLayoutOffsetWidth = browserTab
                .evaluate("return document.getElementsByClassName('ebLayout')[0].offsetWidth;");
        return Integer.valueOf(applicationLayoutOffsetWidth.toString());
    }

    public boolean checkLegendInStraightLine() {

        final int legendHeightExpected = Integer.valueOf((String) DataHandler
                .getAttribute("topologybrowser.legend.heigth"));
        final int legendWidthExpected = Integer.valueOf((String) DataHandler
                .getAttribute("topologybrowser.legend.width"));

        final int legendHeight = legendContainer.getSize().getHeight();
        final int legendWidth = legendContainer.getSize().getWidth();
        logger.info("Height:" + legendContainer.getSize().getHeight());
        logger.info("Width" + +legendContainer.getSize().getWidth());

        return (legendHeight > (legendHeightExpected - 3))
                && (legendHeight < (legendHeightExpected + 3))
                && legendWidth < legendWidthExpected;

    }

    public boolean isAttributesButtonDisplayed() {
        return attributesButton.isDisplayed();
    }

    public boolean isMainButtonDisplayed() {
        return mainButton.isDisplayed();
    }

    public boolean isMainLeftArrowIconDisplayed() {
        return mainLeftArrowIcon.isDisplayed();
    }

    public boolean isDetailsLeftArrowIconDisplayed() {
        return detailsLeftArrowIcon.isDisplayed();
    }

    public boolean isDetailsRightArrowIconDisplayed() {
        return detailsRightArrowIcon.isDisplayed();
    }

    public UiComponent getRightArrowIcon() {
        return detailsRightArrowIcon;
    }

    public void clickAttributesButton() {
        attributesButton.click();
    }

    public void clickMainButton() {
        mainButton.click();
    }

    public boolean isSelectedNodeDetailsDisplayed() {
        return seletcedNodeDetailsContainer.isDisplayed();
    }

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//*[contains(@class, 'elLayouts-PanelButton')]//span[contains(@class, 'leftArrow')]")
    private Label detailsButtonLeftArrow;

    public void openDetailsPanel() {
        //check if left arrow is visible
        //If yes, click on the button

        if (detailsButtonLeftArrow.exists()) {
            attributesButton.click();
        }

    }

    public Link getCloseNodeDetailsButton() {
        return viewDetailsClose;
    }

    /*
     * Filter Details
     */

    @UiComponentMapping(".eaTopologyBrowser-wNodeDetailsForm-filter-form-filterInput")
    private TextBox propertyDetailsFilter;

    @UiComponentMapping(".eaTopologyBrowser-wNodeDetailsForm-filter-form-attributeValuesFiltered")
    private Label filterAttributeCount;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class, 'SlidingPanelsLayout-rightContents')]//*[contains(@class, 'eaTopologyBrowser-wNodeDetailsForm-form-container')]/div[@style='display: inherit;']//*[contains(@class, 'ebComponentList-item')][1]")
    private Label setNewPropertyDropdownValue;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class, 'SlidingPanelsLayout-rightContents')]//*[contains(@class, 'eaTopologyBrowser-wNodeDetailsForm-form-container')]/div[@style='display: inherit;']//*[contains(@class, 'ebSelect-header')]")
    private Button getPropertyDropdown;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class, 'SlidingPanelsLayout-rightContents')]//*[contains(@class, 'eaTopologyBrowser-wNodePropertyList-keyStyle')]")
    private List<UiComponent> propertyAttributesCountInReadOnly;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class, 'SlidingPanels-rightContents')]//div[contains(@class, 'eaTopologyBrowser-wNodeDetailsForm-form-container eb_scrollbar')]/div[contains(@class, 'eaTopologyBrowser-wNodeDetailsForm-form')][@style='display: inherit;']")
    private List<UiComponent> propertyAttributesInEditMode;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class, 'SlidingPanelsLayout-rightContents')]//*[contains(@class, 'eaTopologyBrowser-wNodeDetailsForm-form-container')]/div[@style='display: inherit;']//input[contains(@class, 'form-numberInput-input')]")
    private TextBox getPropertyInputField;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class, 'SlidingPanelsLayout-rightContents')]//*[contains(@class, 'eaTopologyBrowser-wNodeDetailsForm-form-container')]/div[@style='display: inherit;']//input[contains(@class, 'form-stringInput')]")
    private TextBox getPropertyStringInputField;

    //@UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class, 'SlidingPanelsLayout-rightContents')]//*[contains(@class, 'eaTopologyBrowser-wNodeDetailsForm-form-container')]/div[@style='display: inherit;']//div[contains(@class, 'form-enum-dropdownContainer')]")
    //private Select getPropertyDropdown;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[contains(@class, 'SlidingPanelsLayout-rightContents')]//*[contains(@class, 'eaTopologyBrowser-wNodeDetailsForm-form-container')]/div[@style='display: inherit;'][1]//*[2]")
    private Label getPropertyValue;

    @UiComponentMapping(".eaTopologyBrowser-rSelectedNodeProperties-saveButton")
    private UiComponent saveButton;

    @UiComponentMapping(".eaTopologyBrowser-rSelectedNodeProperties-editPropertiesLink")
    private Link editAttributesLink;

    @UiComponentMapping(".eaTopologyBrowser-wNodeDetailsForm-form-cancelLink")
    private Button editPropertiesCancelButton;

    @UiComponentMapping(".eaTopologyBrowser-wNodeDetailsForm-form-saveButton")
    private Button editPropertiesSaveButton;

    @UiComponentMapping(".ebDialogBox-actionBlock>.ebBtn_color_darkBlue")
    private Button dialogSaveButton;

    @UiComponentMapping(".eaTopologyBrowser-wNodeDetailsForm-filter-form-clearFilterIcon")
    private Link clearFilterIcon;

    @UiComponentMapping(".eaTopologyBrowser-wNodeDetailsForm-filter-form-searchFilterIcon")
    private Link searchFilterIcon;

    @UiComponentMapping(".eaTopologyBrowser-rTopologyTree-leftArrowIconContainer")
    private Link treeNavBackArrow;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//a[contains(@class, 'elLayouts-ActionBarItem')][normalize-space(text())='Search for an Object']")
    private Link searchForAnObjectLink;

    public Link getSearchForAnObjectLink() {
        return searchForAnObjectLink;
    }

    public UiComponent getSaveButton() {
        return saveButton;
    }

    public void setFilterInputText(final String filterValue) {
        propertyDetailsFilter.setText(filterValue);
    }

    public String getFilterInputText() {
        return propertyDetailsFilter.getText();
    }

    public String getFilterAttributeText() {
        return filterAttributeCount.getText();
    }

    public TextBox getFilterInputBox() {
        return propertyDetailsFilter;
    }

    public String getFilterTextAttributeCount() {
        final String filterAttributesText = filterAttributeCount.getText();
        final String[] getFilterCountNumber = filterAttributesText
                .split("\\s+");
        final String filterCount = getFilterCountNumber[1];

        return filterCount;
    }

    public String getTotalTextAttributeCount() {
        final String filterAttributesText = filterAttributeCount.getText();
        final String[] getFilterCountNumber = filterAttributesText
                .split("\\s+");
        final String totalCount = getFilterCountNumber[3];
        return totalCount;
    }

    public int getpropertyAttributesCountInReadOnly() {
        return propertyAttributesCountInReadOnly.size();
    }

    public List<UiComponent> getPropertyAttributesInEditMode() {
        return propertyAttributesInEditMode;
    }

    public int getpropertyAttributesCountInEditMode() {
        return propertyAttributesInEditMode.size();
    }

    public String updatePropertyAttribute() {

        final String getTitle = getPropertyValue.getProperty("title");

        //If loop to change property value depending if field is numeric, string or dropdown
        if (getTitle.contains("LONG")) {
            //It is Input field
            final String inputValue = getPropertyInputField.getText();
            final int inputValueNum = Integer.parseInt(inputValue);

            final int newInputValue = inputValueNum - 10;
            final String newInputValueNum = Integer.toString(newInputValue);

            getPropertyInputField.setText(newInputValueNum);

        } else if (getTitle.contains("String")) {
            //It is String Input field
            final String newStringInputValue = "newString";
            getPropertyStringInputField.setText(newStringInputValue);

        } else if (getTitle.contains("ENUM_REF")) {
            logger.info("enter ENUM_REF");

            //It is dropdown field
            getPropertyDropdown.click();

            try {
                Thread.sleep(2000);
            } catch (final InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //select new dropdown value
            setNewPropertyDropdownValue.click();

            try {
                Thread.sleep(2000);
            } catch (final InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return null;
    }

    public void clickEditAttributesLink() {
        editAttributesLink.click();
    }

    public void clickClearFilterXIcon() {
        clearFilterIcon.click();
    }

    public void treeNavBackArrowLink() {
        treeNavBackArrow.click();
    }

    public void propertyDetailsCancelButton() {
        editPropertiesCancelButton.click();
    }

    public void propertyDetailsSaveButton() {
        editPropertiesSaveButton.click();
    }

    public void propertyDetailsDialogSaveButton() {
        dialogSaveButton.click();
    }

    public boolean clearFilterIconIsDisplayed() {
        return clearFilterIcon.isDisplayed();
    }

    public boolean searchFilterIconIsDisplayed() {
        return searchFilterIcon.isDisplayed();
    }

    public void clickLinkToNE() {
        searchForAnObjectLink.click();

    }

    public Link getEditAttributesLink() {
        return editAttributesLink;
    }

    public UiComponent getSelectBoxItemByName(final String name) {
        // Use XPATH here for speed. The list can be long, so iterating through it can take a little longer.
        return getViewComponent(SelectorType.XPATH,
                "//*[contains(@class,'ebComponentList-item')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }

    public UiComponent getTreeElementByAttributeName(final String name) {
        return getViewComponent(
                SelectorType.XPATH,
                "//*[contains(@class,'eaTopologyBrowser-rTopologyTree-treeNodeLabel-textSpanMoType ebText_alternative')][normalize-space(text())='"
                        + name + "']", UiComponent.class);
    }
}
