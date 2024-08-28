package com.ericsson.nms.rv.taf.test.ui.viewmodels.monitoringbrowser;

import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.*;
import com.ericsson.cifwk.taf.ui.sdk.Button;

import java.awt.*;
import java.util.List;

/**
 * Created by ejocott on 20/08/2014.
 */
public class DashBoardViewModel extends GenericViewModel {

    @UiComponentMapping(id = "usernameInput")
    private TextBox usernameInput;

    @UiComponentMapping(name = "passwordInput")
    private TextBox passwordInput;

    @UiComponentMapping(name = "currentPassword")
    private TextBox currentPasswordInput;

    @UiComponentMapping(".tab activeTab")
    private Button resourceTab;

    @UiComponentMapping(name = "name")
    private TextBox nameInput;

    @UiComponentMapping(name = "keywords")
    private TextBox keywordInput;

    @UiComponentMapping(name = "description")
    private TextBox alertDescriptionInput;

    @UiComponentMapping(name = "condition[0].metricId")
    private Select selectAlertConditionMetric;

    @UiComponentMapping(name = "condition[0].absoluteValue")
    private TextBox absoluteValueInput;

    @UiComponentMapping(name = "condition[0].absoluteComparator")
    private Select absoluteComparatorInput;

    @UiComponentMapping(name = "firstName")
    private TextBox firstNameInput;

    @UiComponentMapping(name = "lastName")
    private TextBox lastNameInput;

    @UiComponentMapping(name = "newPassword")
    private TextBox newPasswordInput;

    @UiComponentMapping(name = "emailAddress")
    private TextBox emailAddress;

    @UiComponentMapping(name = "okassign")
    private Link okAssignButton;

    @UiComponentMapping(name = "confirmPassword")
    private TextBox passwordConfirmInput;

    @UiComponentMapping(id = "okButton")
    private Link okButton;

    @UiComponentMapping(name = "add")
    private Link addToListArrow;

    @UiComponentMapping(name = "resourceType")
    private Select serverType;

    @UiComponentMapping(name = "installPath")
    private TextBox serverInstallPath;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//a[text() = 'Configuration Properties']")
    private Link configurationPropertiesLink;

    @UiComponentMapping(name = "product.SPA_IP_Address")
    private TextBox spaIpAdressTextBox;

    @UiComponentMapping(name = "product.SPB_IP_Address")
    private TextBox spbIpAddressTextBox;

    @UiComponentMapping(id = "average10830")
    private TextBox availabilityAverage;

    @UiComponentMapping(id = "sysPercCpu")
    private UiComponent systemCpuUsage;

    @UiComponentMapping(id = "agentTable_tableTitle")
    private UiComponent agentHeader;

    public TextBox getUsernameInput() {
        return usernameInput;
    }

    public TextBox getPasswordInput() {
        return passwordInput;
    }

    public TextBox getSearchKeywordInput() {
        return keywordInput;
    }

    public Link getResourceTab() {
        return this.getViewComponent(SelectorType.XPATH, "//a[normalize-space(text())='Resources']", Link.class);
    }

    public Link getAdministrationTab() {
        return this.getViewComponent(SelectorType.XPATH, "//a[normalize-space(text())='Administration']", Link.class);
    }

    public Link getResourceTypeTab(String resourceType) {
        return this.getViewComponent(SelectorType.XPATH, "//td[contains(@class,'ResourceHubBlockTitle')]//a[contains(text(),'" + resourceType + "')]", Link.class);
    }

    public Link getSubmitSearchButton() {
        return this.getViewComponent(SelectorType.XPATH, "//div[contains(@class,'filterBoxFields')]//input[contains(@type,'image')]", Link.class);
    }

    public Link getResourceLink(String keyword) {
        return this.getViewComponent(SelectorType.XPATH, "//td[contains(@class,'tableCell')]//a[normalize-space(text())='" + keyword + "']", Link.class);
    }


    public Link getAlertTab() {
        return this.getViewComponent(SelectorType.XPATH, "//td[contains(@class,'TabCell')]//img[contains(@src,'/images/tab_Alert_')]", Link.class);
    }

    public Link getConfigureAlertButton() {
        return this.getViewComponent(SelectorType.XPATH, "//td[contains(@class,'SubTab')]//img[contains(@src,'/images/SubMonitor_Configure_')]", Link.class);
    }

    public Link getNewAlertButton() {
        return this.getViewComponent(SelectorType.XPATH, "//table[contains(@class,'ToolbarContent')]//img[contains(@src,'/images/tbb_new')]", Link.class);
    }

    public TextBox getAlertNameInput() {
        return nameInput;
    }

    public TextBox getAlertDescriptionInput() {
        return alertDescriptionInput;
    }

    public Select getAlertConditionMetricSelector() {
        return selectAlertConditionMetric;
    }

    public TextBox getAlertConditionAbsoluteValueInput() {
        return absoluteValueInput;
    }

    public Link getAlertDefinitionOkButton() {
        return this.getViewComponent(SelectorType.XPATH, "//input[contains(@value,'Ok')]", Link.class);
    }

    public CheckBox getAlertCheckBox(String alertName) {
        return this.getViewComponent(SelectorType.XPATH, "//tr[contains(@class, 'tableRow')]/td[@class = 'tableCell']/a[contains" +
                "(text(),'" + alertName + "')]/ancestor::*[1]/preceding-sibling::*[2]/label/input", CheckBox.class);
    }

    public Link getDeleteButton() {
        return this.getViewComponent(SelectorType.XPATH, "//input[contains(@name,'delete')]", Link.class);
    }

    public List<UiComponent> getAlertList() {
        return this.getViewComponents(".table tr td a ", UiComponent.class);
    }

    public Link getNewUserLink() {
        return this.getViewComponent(SelectorType.XPATH, "//a[normalize-space(text())='New User...']", Link.class);
    }

    public TextBox getNewUserFirstNameBox() {
        return firstNameInput;
    }

    public TextBox getNewUserLastNameBox() {
        return lastNameInput;
    }

    public TextBox getNewUserPasswordBox() {
        return newPasswordInput;
    }

    public TextBox getNewUserEmailBox() {
        return emailAddress;
    }

    public Link getNewUserOkButton() {
        return okAssignButton;
    }

    public TextBox getNewUsernameInput() {
        return nameInput;
    }

    public TextBox getNameInput() {
        return nameInput;
    }

    public TextBox getNewUserConfirmPasswordBox() {
        return passwordConfirmInput;
    }

    public CheckBox getNewUserRole(String role) {
        return this.getViewComponent(SelectorType.XPATH, "//td[contains(text(), '" + role + "')]/ancestor::*[1]/td/label/input", CheckBox.class);
    }

    public Link getOkButton() {
        return okButton;
    }

    public Link getAlertDefinitionLink(String alertName) {
        return this.getViewComponent(SelectorType.XPATH, "//a[contains(text(), '" + alertName + "')]", Link.class);
    }

    public Link getNotifyHQUsersTab() {
        return this.getViewComponent(SelectorType.XPATH, "//a[contains(text(), 'Notify HQ Users')]", Link.class);
    }

    public UiComponent getAddToListButton() {
        return this.getViewComponent(SelectorType.XPATH, "//form[contains(@name, 'RemoveNotificationsForm')]/table/tbody//tr/td/a/img", Link.class);
    }

    public CheckBox getUserToAlertCheckBox(String username) {
        return this.getViewComponent(SelectorType.XPATH, "//td[normalize-space(text())='" + username + "']/ancestor::*[1]//input", CheckBox.class);
    }

    public Link getUserListLink() {
        return this.getViewComponent(SelectorType.XPATH, "//a[contains(text(), 'List Users')]", Link.class);
    }

    public CheckBox getDeleteUserCheckBox(String username) {
        return this.getViewComponent(SelectorType.XPATH, "//td/a[normalize-space(text())='" + username + "']/ancestor::*[2]//input", CheckBox.class);
    }

    public Link getAddToNotificationListArrow() {
        return addToListArrow;
    }

    public Link getUserAssignedReturnMessage() {
        return this.getViewComponent(SelectorType.XPATH, "//td[contains(text(),'The requested roles have been assigned to the user')]", Link.class);
    }

    public Link getUserAddedToNotificationMessage() {
        return this.getViewComponent(SelectorType.XPATH, "//td[contains(@class,'ConfirmationBlock')][contains(text(),'Successfully added users to notifications for this alert')]", Link.class);
    }

    public UiComponent getAutoDiscoveryResourceName(String name) {
        //return this.getViewComponent(SelectorType.XPATH, "//table[contains(@class, 'portletLRBorder')]//a[contains(text(), '" + name + "')]", UiComponent.class);
        return this.getViewComponent(SelectorType.XPATH, "//div[contains(text(), 'Auto-Discovery')]/ancestor::*[5]//a[contains(text(),'" + name + "')]", CheckBox.class);
    }

    public UiComponent getToolMenu() {
        return this.getViewComponent(SelectorType.XPATH, "//span[@id='toolMenuSpan']", UiComponent.class);
    }

    public Link getLinkBoxItem(String keyword) {
        return this.getViewComponent(SelectorType.XPATH, "//td[@class = 'LinkBox']//a[contains(text(), '" + keyword + "')]", Link.class);
    }

    public Link getLogoutLink() {
        return this.getViewComponent(SelectorType.XPATH, "//a[text()='Sign Out']", Link.class);
    }

    public Select getServerTypeDropdown() {
        return serverType;
    }

    public TextBox getServerInstallPath() {
        return serverInstallPath;
    }

    public Link getConfigurationPropertiesLink() {
        return configurationPropertiesLink;
    }

    public TextBox getSpaIpAdressTextBox() {
        return spaIpAdressTextBox;
    }

    public TextBox getSpbIpAddressTextBox() {
        return spbIpAddressTextBox;
    }

    public void getAlertConditionComparator(String comparator) {
        absoluteComparatorInput.selectByValue(comparator);

    }

    public UiComponent getUserEditLink(String userName) {
        return this.getViewComponent(SelectorType.XPATH, "//a[contains(text(), '" + userName + "')]", Link.class);
    }

    public UiComponent getChangePasswordLink() {
        return this.getViewComponent(SelectorType.XPATH, "//a[contains(text(), 'Change...')]", Link.class);
    }

    public TextBox getCurrentPasswordInput() {
        return currentPasswordInput;
    }

    public TextBox getNewPasswordInput() {
        return newPasswordInput;
    }

    public TextBox getConfirmPasswordInput() {
        return passwordConfirmInput;
    }

    public UiComponent getResourceHeading(String resourceName) {
        return this.getViewComponent(SelectorType.XPATH, "//li[contains(text(),'" + resourceName + "')]", Link.class);
    }

    public Link getMetricDataTab() {
        return this.getViewComponent(SelectorType.XPATH, "//a//img[contains(@src,'/images/MiniTab_MetricData_off.png')]", Link.class);
    }

    public UiComponent getLastAvailabilityStatus() {
        return this.getViewComponent(SelectorType.XPATH, "//span[@id='avail10830']//img[contains(@src,'/images/icon_available_green.gif')]", UiComponent.class);

    }

    public UiComponent getAvailabilityAverage() {
        return this.getViewComponent(SelectorType.XPATH, "//span[@id='average10830']", UiComponent.class);

    }

    public UiComponent getHQHealthLink() {
        return this.getViewComponent(SelectorType.XPATH, "//a[contains(text(),'HQ Health')]", Link.class);
    }

    public UiComponent getSystemCPUPercentage() {
        return systemCpuUsage;
    }

    public UiComponent getAgentsTab() {
        return this.getViewComponent(SelectorType.XPATH, "//span[contains(text(),'Agents')]", Link.class);
    }

    public List<UiComponent> getServerTimeOffsets() {
        return this.getViewComponents(SelectorType.XPATH, "//td[contains(@idx,'8')]/a", UiComponent.class);
    }

    public UiComponent getAgentsHeader() {
        return agentHeader;
    }
}