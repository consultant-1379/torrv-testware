/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 * 
 * created : esaidee
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.rv.taf.test.ui.viewmodels.shm;


import java.util.ArrayList;
import java.util.List;

import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.*;

public class ShmViewModel extends GenericViewModel {

    ///*** SOFTWARE --START
    @UiComponentMapping(selector = "//h1[contains(@class, 'elLayouts-TopSection-title') and contains(., 'Software Administration')]", selectorType = SelectorType.XPATH)
    private UiComponent softwareText;


    @UiComponentMapping(selector = "//a[contains(text(),'Create Upgrade Job')]", selectorType = SelectorType.XPATH)
    private UiComponent crtswactionbar;

    @UiComponentMapping(selector = "//a[contains(text(),'View Software Packages')]", selectorType = SelectorType.XPATH)
    private UiComponent viewswactionbar;


    @UiComponentMapping(selector = "//a[contains(text(),'Import Software Package')]", selectorType = SelectorType.XPATH)
    private UiComponent impswactionbar;

    @UiComponentMapping(selector = "//div[contains(@class, 'elWidgets-TableCheckboxHeaderCell-wrap')]//input[contains(@type, 'checkbox')]", selectorType = SelectorType.XPATH)
    private UiComponent selectnodes;

    @UiComponentMapping(selector = "//*[contains(@class,'ebBtn-caption')][normalize-space(text())='Software Items']", selectorType = SelectorType.XPATH)
    private UiComponent swItemsbutton;

    @UiComponentMapping(selector = "//th[contains(text(),'Node Name')]", selectorType = SelectorType.XPATH)
    private UiComponent nodenameheadercheck;

    @UiComponentMapping(selector = "//th[contains(text(),'Name')]", selectorType = SelectorType.XPATH)
    private UiComponent nameheadercheck;

    @UiComponentMapping(selector = "//th[contains(text(),'Product No.')]", selectorType = SelectorType.XPATH)
    private UiComponent prdnoheadercheck;
    @UiComponentMapping(selector = "//th[contains(text(),'Product Revision')]", selectorType = SelectorType.XPATH)
    private UiComponent prdrevheadercheck;

    @UiComponentMapping(selector = "//th[contains(text(),'Type')]", selectorType = SelectorType.XPATH)
    private UiComponent typeheadercheck;

    @UiComponentMapping(selector = "//th[contains(text(),'Date')]", selectorType = SelectorType.XPATH)
    private UiComponent typedatecheck;

    @UiComponentMapping(selector = "//th[contains(text(),'State')]", selectorType = SelectorType.XPATH)
    private UiComponent typestatecheck;

    @UiComponentMapping(selector = "//th[contains(text(),'Delta UP')]", selectorType = SelectorType.XPATH)
    private UiComponent typedeltaupcheck;

    @UiComponentMapping(selector = "//th[contains(text(),'Executing')]", selectorType = SelectorType.XPATH)
    private UiComponent typeexecutingcheck;

    @UiComponentMapping(selector = "//a[contains(text(),'Delete')]", selectorType = SelectorType.XPATH)
    private UiComponent delswactionbar;

    @UiComponentMapping(selector = "//a[contains(text(),'Export')]", selectorType = SelectorType.XPATH)
    private UiComponent expswactionbar;

    @UiComponentMapping(selector = "//a[contains(text(),'Clear Selection')]", selectorType = SelectorType.XPATH)
    private UiComponent clrselswactionbar;
    ///*** SOFTWARE --END
    //th[contains(text(),'Node Name')]

    //**** SOFTWARE --START

    public UiComponent wereShmSoftwareTextReturnTest() {
        return softwareText;

    }

    public UiComponent wereShmCrtSWActionBarCheckReturnTest() {
        return crtswactionbar;
    }

    public UiComponent wereShmImpSWActionBarCheckReturnTest() {
        return impswactionbar;
    }

    public UiComponent wereViewSWActionBarActionBarCheckReturnTest() {
        return viewswactionbar;
    }

    public UiComponent wereNodeNameHeaderCheckReturnTest() {
        return nodenameheadercheck;
    }

    public UiComponent wereNameHeaderCheckReturnTest() {
        return nameheadercheck;
    }

    public UiComponent werePrdNoHeaderCheckReturnTest() {
        return prdnoheadercheck;
    }

    public UiComponent werePrdRevHeaderCheckReturnTest() {
        return prdrevheadercheck;
    }

    public UiComponent wereTypeHeaderCheckReturnTest() {
        return typeheadercheck;
    }

    public UiComponent wereStateHeaderCheckReturnTest() {
        return typestatecheck;
    }

    public UiComponent getCheckBoxes() {
        return selectnodes;
    }

    public UiComponent wereExecutingUpHeaderCheckReturnTest() {
        return typeexecutingcheck;
    }

    public UiComponent wereDeltaUpHeaderCheckReturnTest() {
        return typedeltaupcheck;
    }

    public UiComponent wereDeleteSWActionBarCheckReturnTest() {
        return delswactionbar;
    }

    public UiComponent wereExportSWActionBarCheckReturnTest() {
        return expswactionbar;
    }

    public UiComponent wereClrSelSWActionBarCheckReturnTest() {
        return clrselswactionbar;
    }
    //**** SOFTWARE --STOP


    public UiComponent swItemsButton() {
        return swItemsbutton;
    }

    public UiComponent wereDateHeaderCheckReturnTest() {
        return typedatecheck;
    }

    @UiComponentMapping(".ebBreadcrumbs-arrow")
    private UiComponent firstSHMdropDown;

    @UiComponentMapping(".eaShmlibrary-selectionList-enable")
    private UiComponent enableActionsLink;

    @UiComponentMapping(selector = "//li[contains(@class, 'ebComponentList-item') and contains(text(),'Delete Selected')]", selectorType = SelectorType.XPATH)
    private UiComponent deleteSelectedDropDown;

    @UiComponentMapping(selector = "//button[not(@disabled)]//span[contains(text(),'Actions') ]", selectorType = SelectorType.XPATH)
    private Button actionsButton;

    @UiComponentMapping(selector = "//a[contains(@class, 'ebComponentList-link') and contains(., 'License Administration')]", selectorType = SelectorType.XPATH)
    private UiComponent licenseDropdown;

    @UiComponentMapping(".elNetworkExplorerLib-TopologyDropdown-captionText")
    private UiComponent dropDown;

    @UiComponentMapping(selector = "//h1[contains(@class, 'elLayouts-TopSection-title') and contains(., 'Hardware Administration')]", selectorType = SelectorType.XPATH)
    private UiComponent hardwareText;

    @UiComponentMapping(selector = "//h1[contains(@class, 'elLayouts-TopSection-title') and contains(., 'License Administration')]", selectorType = SelectorType.XPATH)
    private UiComponent LicenseText;

    @UiComponentMapping(selector = "//*[contains(@class, 'ebTable elWidgets-Table-body') and contains(., 'Fingerprint')]", selectorType = SelectorType.XPATH)
    private UiComponent LicenseText1;

    @UiComponentMapping(selector = "//*[contains(@class, 'ebTableRow') and contains(., 'SHM01ERBS00002')]", selectorType = SelectorType.XPATH)
    private UiComponent LicenseText2;

    @UiComponentMapping(selector = "//*[contains(@class, 'ebComponentList-item') and contains(., 'Add Collections')]", selectorType = SelectorType.XPATH)
    private UiComponent getCol;

    @UiComponentMapping(selector = "//span[contains(text(),'50')]/ancestor::*[2]//*[contains(@class, 'elCollectionsSavedSearchesWidget-wListItem-checkbox ebCheckbox')]", selectorType = SelectorType.XPATH)
    private UiComponent addCol;

    @UiComponentMapping(selector = "//*[contains(@class,'ebBtn-caption')][normalize-space(text())='Return Selected Collections']", selectorType = SelectorType.XPATH)
    private UiComponent returnCol;

    @UiComponentMapping(".eaNetworkExplorerCollections-wListItem-collectionTitle")
    private List<UiComponent> allTitles;

    @UiComponentMapping(".eaShmlibrary-selectionList-labelText")
    private List<UiComponent> nodesList;

    @UiComponentMapping(id = "filterButtonIconHolder")
    private UiComponent viewInventory;

    @UiComponentMapping(selector = "//*[contains(@class,'ebComponentList-item')][normalize-space(text())='All']", selectorType = SelectorType.XPATH)
    private UiComponent checkInventory;

    @UiComponentMapping(selector = "//*[contains(@class,'elLayouts')][normalize-space(text())='Managed Elements Panel']", selectorType = SelectorType.XPATH)
    private UiComponent managedElementPanel;

    @UiComponentMapping(selector = "//*[contains(@class,'ebBtn-caption')][normalize-space(text())='License Keys']", selectorType = SelectorType.XPATH)
    private UiComponent checkBox;

    @UiComponentMapping(selector = "//*[contains(@class,'ebBtn-caption')][normalize-space(text())='License Keys']", selectorType = SelectorType.XPATH)
    private UiComponent licenseItemsbutton;

    @UiComponentMapping(selector = "//*[contains(@class, 'ebTableRow') and contains(., 'SHM01ERBS00002')]", selectorType = SelectorType.XPATH)
    private UiComponent nodeName;

    @UiComponentMapping(selector = "//*[contains(@class, 'ebTableRow') and contains(., 'Load Module')]", selectorType = SelectorType.XPATH)
    private UiComponent Loadmodule;

    @UiComponentMapping(selector = "//h1[contains(text(),'Software Hardware Manager')]", selectorType = SelectorType.XPATH)
    private UiComponent swHwHeading;

    @UiComponentMapping(selector = "//span[contains(text(), 'Return Selected Collections')]/ancestor::*[1]", selectorType = SelectorType.XPATH)
    private Button selectCollectionButton;

    public UiComponent getfirtSHMDropDown() {
        return firstSHMdropDown;
    }

    public UiComponent selectAdminPagefromDropDown(String administrationType) {
        return this.getViewComponent(SelectorType.XPATH, "//a[contains(@class, 'ebComponentList-link') and contains(., '" + administrationType + "')]", Link.class);
    }

    public Boolean wereShmLicenseCheckReturntest() {
        return LicenseText.isDisplayed();
    }

    public Boolean wereShmLicenseCheckReturntest1() {
        return LicenseText1.isDisplayed();
    }

    public Boolean wereShmLicenseCheckReturntest2() {
        return LicenseText2.isDisplayed();
    }

    public Boolean wereShmLicenseCheckReturntest3() {
        return LicenseText.isDisplayed();
    }

    public Boolean wereShmNodeCheckReturntest() {
        return nodeName.isDisplayed();
    }

    public List<UiComponent> getResultRowCheckboxes() {
        return getViewComponents(".ebCheckbox", UiComponent.class);
    }

    public UiComponent getSHMCollectionDropDown() {
        return dropDown;
    }

    public UiComponent selectAddCollections() {
        return getCol;
    }

    public UiComponent addCollections() {
        return addCol;
    }

    public UiComponent returnCollections() {
        return returnCol;
    }

    public UiComponent checkInventory() {
        return viewInventory;
    }

    public UiComponent selectAllNodesforInvCheck() {

        return checkInventory;
    }

    public UiComponent managedPanel() {
        return managedElementPanel;
    }

    public List<UiComponent> getAllTitles() {
        return allTitles;
    }

    public UiComponent licenseItemsbutton() {
        return licenseItemsbutton;
    }

    public UiComponent nodeName() {
        return nodeName;
    }

    public Boolean wereShmLoadmoduleReturntest() {
        return Loadmodule.isDisplayed();
    }

    public UiComponent isCollectionDisplayed(String collectionName) {
        return this.getViewComponent(SelectorType.XPATH, "//span[contains(text(),'" + collectionName + "')]"
                , UiComponent.class);
    }

    public CheckBox selectCollection(String collectionName) {
        return this.getViewComponent(SelectorType.XPATH, "//span[contains(text(),'" + collectionName + "')]" +
                "/ancestor::*[2]//*[contains(@class, 'elCollectionsSavedSearchesWidget-wListItem-checkbox ebCheckbox')]"
                , CheckBox.class);
    }

    public UiComponent getAddSelectedCollection() {
        return selectCollectionButton;
    }

    public List<UiComponent> getNetworkElementList() {
        return nodesList;
    }

    public UiComponent enableActions() {
        return enableActionsLink;
    }

    public Button actionsButton() {
        return actionsButton;
    }

    public UiComponent deleteSelectedCollection() {
        return deleteSelectedDropDown;
    }

    public UiComponent getSwHwHeading() {
        return swHwHeading;
    }

    public List<UiComponent> getListEmptyTableCells(String nodeID) {
        return this.getViewComponents(SelectorType.XPATH, "//td[text()=''][..//td/text() = '" + nodeID + "']", UiComponent.class);
    }

    public List<String> getTableHeadings() {
        List<UiComponent> tableHeadingComponents = this.getViewComponents(SelectorType.XPATH, "//th[not(@class)]", UiComponent.class);
        List<String> tableHeadings = new ArrayList<String>();
        for (UiComponent component : tableHeadingComponents) {
            tableHeadings.add(component.getText());
        }
        return tableHeadings;
    }

    public UiComponent isNodeDisplayed(String nodeID) {
        return this.getViewComponent(SelectorType.XPATH, "//td[text() = '" + nodeID + "']", UiComponent.class);
    }


}