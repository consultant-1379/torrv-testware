package com.ericsson.nms.rv.taf.test.monitoring.operators;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.ui.*;
import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.CheckBox;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheUiOperator;
import com.ericsson.nms.rv.taf.test.monitoring.cases.dto.MonitoringResponse;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.monitoringbrowser.DashBoardViewModel;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.monitoringbrowser.LoginLogoutViewModel;

public class MonitoringUiOperator {

    private final Logger logger = LoggerFactory
            .getLogger(MonitoringUiOperator.class);
    protected Browser browser = null;
    protected BrowserTab currentBrowserTab = null;
    private final String SCREENSHOT_FOLDER = System.getProperty("user.dir");
    private static final int HQ_SERVER_TIME_OFFSET = 15;

    public MonitoringUiOperator() {
    }

    /**
     * Login to Monitoring Tool via unsecure http protocol.
     *
     * @param userName
     *            - Login User
     * @param password
     *            - Login password
     * @return - MonitoringResponse with success/error message
     */
    public MonitoringResponse unsecureLogin(final String userName,
            final String password) {
        logger.info(
                "Logging in to Monitoring Tool via unsecure login. User '{}'",
                userName);
        createBrowserInstance();

        final String msIp = HostConfigurator.getMS().getIp();
        MonitoringResponse monitoringResponse = getSuccessResponse();
        final String monitoringSecurePort = HostConfigurator.getMS().getPort()
                .get(Ports.HTTP);
        final String url = "http://" + msIp + ":" + monitoringSecurePort;
        logger.info("Opening browser, URL: {}", url);

        currentBrowserTab = browser.open(url);
        currentBrowserTab.refreshPage();

        final LoginLogoutViewModel loginView = currentBrowserTab
                .getView(LoginLogoutViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    loginView.getMonToolHeadingText(), 3000);
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    loginView.getUsernameInput(), 3000)).setText(userName);
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    loginView.getPasswordInput(), 3000)).setText(password);
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    loginView.getSubmitButton(), 3000).click();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    loginView.getLogoutButton(), 3000);
            monitoringResponse.setSuccess(true);
            logger.info("Logged in successfully.");
        } catch (final UiComponentNotFoundException e) {
            logger.error("Unable to login via unsecure Login");
            ApacheUiOperator.takeScreenShot(currentBrowserTab,
                    "MonitoringUILoginPage");
            UI.pause(3000); // Pause to allow the screenshot to be taken before browser is closed.

            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            monitoringResponse = getFailResponse("Unable to login via secure Login. See screenshot: {}"
                    + "MonitoringUILoginPage");
            closeBrowser();
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Timed out waiting for component on unsecure login page. Message: {}",
                    e.getMessage());
            ApacheUiOperator.takeScreenShot(currentBrowserTab,
                    "MonitoringUILoginPage");
            UI.pause(3000); // Pause to allow the screenshot to be taken before browser is closed.

            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            monitoringResponse = getFailResponse("Timed out waiting for component on unsecure login page. See screenshot: "
                    + "MonitoringUILoginPage");
            closeBrowser();
        }
        return monitoringResponse;
    }

    /**
     * Login to Monitoring Tool via secure https protocol.
     *
     * @param userName
     *            - Login User
     * @param password
     *            - Login password
     * @return - MonitoringResponse with success/error message
     */
    public MonitoringResponse secureLogin(final String userName,
            final String password) {
        logger.info("Logging into Monitoring Tool via secure login. User '{}'",
                userName);
        createBrowserInstance();

        MonitoringResponse monitoringResponse = getSuccessResponse();
        final String msIp = HostConfigurator.getMS().getIp();
        final String monitoringSecurePort = HostConfigurator.getMS().getPort()
                .get(Ports.HTTPS);
        final String url = "https://" + msIp + ":" + monitoringSecurePort;
        logger.info("Opening browser, URL: {}", url);
        browser.open(url);

        currentBrowserTab = browser.getCurrentWindow();
        currentBrowserTab.refreshPage();
        final LoginLogoutViewModel loginView = currentBrowserTab
                .getView(LoginLogoutViewModel.class);
        try {
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    loginView.getUsernameInput(), 3000)).setText(userName);
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    loginView.getPasswordInput(), 3000)).setText(password);
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    loginView.getSubmitButton(), 3000).click();
            logger.info("Logged in successfully.");
        } catch (final WaitTimedOutException e) {
            ApacheUiOperator.takeScreenShot(currentBrowserTab,
                    "MonitoringUILoginPage");
            monitoringResponse = getFailResponse("Timed out waiting for component on secure login page. See screenshot: "
                    + "MonitoringUILoginPage");
            UI.pause(3000); // Pause to allow the screenshot to be taken before browser is closed.
            closeBrowser();
        }
        return monitoringResponse;
    }

    /**
     * Logout of monitoring page
     *
     * @return MonitoringResponse object
     */
    public MonitoringResponse logout() {
        logger.info("Logging out.");

        MonitoringResponse monitoringResponse = getSuccessResponse();
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getLogoutLink(), 10000).click();
        } catch (final WaitTimedOutException e) {
            ApacheUiOperator.takeScreenShot(currentBrowserTab,
                    "MonitoringUILogoutPage");
            monitoringResponse = getFailResponse("Failed to Sign out the hyperic page. Couldn't find Sign out link. See screenshot: "
                    + "MonitoringUILogoutPage");
            UI.pause(3000); // Pause to allow the screenshot to be taken before browser is closed.
        }
        closeBrowser();
        logger.info("Logged out successfully.");
        return monitoringResponse;
    }

    /**
     * @return Browser instance
     */
    private void createBrowserInstance() {
        logger.info("Creating new browser.");
        if (browser != null) {
            logger.info("Browser was not null. Closing old browser.");
            closeBrowser();
        }

        BrowserSetup.Builder setup = BrowserSetup.build().withType(
                BrowserType.FIREFOX);

        // Check if we are running in the cloud and configure proxy settings if so
        if (DataHandler.getAttribute("env_type").toString().equals("cloud")) {
            logger.info("Configured to run in cloud environment - modifying browser to autodetect proxy settings");
            setup = setup.withCapability(CapabilityType.PROXY,
                    new Proxy().setAutodetect(true));
        }

        try {
            browser = UI.newBrowser(setup);
        } catch (final Exception e) {
            closeBrowser();
            logger.error("Failed to create FIREFOX browser.");
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void closeBrowser() {
        if (!browser.isClosed()) {
            logger.info("Closing browser.");
            browser.close();
        }
        browser = null;
    }

    private MonitoringResponse getFailResponse(final String errorMessage) {
        final MonitoringResponse response = new MonitoringResponse();
        response.setErrorMessage(errorMessage);
        response.setSuccess(false);
        return response;
    }

    private MonitoringResponse getSuccessResponse() {
        final MonitoringResponse response = new MonitoringResponse();
        response.setSuccess(true);
        return response;
    }

    /**
     * Open the resources tab on the Dashboard
     */
    private void openResourcesTab() {
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getResourceTab(), 2000).click();
        } catch (final WaitTimedOutException e) {
            logger.error("Failed to open resources tab");
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    private void openAdministrationTab() {
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getAdministrationTab(), 2000).click();
        } catch (final WaitTimedOutException e) {
            logger.error("Failed to open administration tab");
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Open the page of a type of specific resources For example the One of the
     * Platform Servers
     *
     * @param resourceType
     *            (Platforms , Servers , Services , Compatible Groups/Clusters,
     *            Mixed Groups, Applications)
     * @param resourceName
     *            is the Keyword is used to search for the resource in the
     *            'Search' field
     */
    public MonitoringResponse openResourceType(final String resourceType,
            final String resourceName) {
        final MonitoringResponse monitoringResponse = new MonitoringResponse();
        openResourcesTab();
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        UI.pause(2000);
        // User Settings are stored after each session so page may already be
        // selected
        // Check to see if user is already on the correct Resource Tab by
        // searching for the link
        try {
            if (dashBoardView.getResourceTypeTab(resourceType).exists()) {
                logger.info("Opening resource type '{}' page", resourceType);
                currentBrowserTab.waitUntilComponentIsDisplayed(
                        dashBoardView.getResourceTypeTab(resourceType), 2000)
                        .click();
            }
        } catch (final WaitTimedOutException e) {
            monitoringResponse.setErrorMessage(String.format(
                    "Failed to navigate to resource page '%s' ", resourceType));
            monitoringResponse.setSuccess(false);
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            return monitoringResponse;
        }

        try {
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getSearchKeywordInput(), 2000))
                    .setText(resourceName); // to ensure the text box displayed on
            // page.
            logger.info("Opening resource name '{}' page", resourceName);
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getSubmitSearchButton(), 2000).click();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getResourceLink(resourceName), 2000).click();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getResourceHeading(resourceName), 2000);
        } catch (final WaitTimedOutException e) {
            monitoringResponse.setSuccess(false);
            monitoringResponse
                    .setErrorMessage(String
                            .format("Failed to open resource name '%s' under resource type '%s' tab",
                                    resourceName, resourceType));
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            return monitoringResponse;
        }
        monitoringResponse.setSuccess(true);
        return monitoringResponse;
    }

    /**
     * Open the page of a type of specific resources, click tool menu and delete
     * it.
     *
     * @param resourceType
     *            (Platforms , Servers , Services , Compatible Groups/Clusters,
     *            Mixed Groups, Applications)
     * @param resourceName
     *            is the Keyword is used to search for the resource in the
     *            'Search' field
     */
    public MonitoringResponse deleteResourceType(final String resourceType,
            final String resourceName) {
        final MonitoringResponse monitoringResponse = new MonitoringResponse();
        openResourceType(resourceType, resourceName);
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        UI.pause(5000);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getToolMenu(), 5000).click();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getLinkBoxItem("Delete "
                            + resourceType.substring(0,
                                    resourceType.length() - 1)), 5000).click();
            currentBrowserTab.getMessageBox().clickOk();
            monitoringResponse.setSuccess(true);
        } catch (final WaitTimedOutException e) {
            monitoringResponse.setSuccess(false);
            monitoringResponse.setErrorMessage("Failed to delete "
                    + resourceName);
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return monitoringResponse;
    }

    /**
     * Go into specified platform, click "Tools Menu" and click "New Server".
     *
     * @param platform
     *            -
     * @param serverName
     * @param serverType
     * @param installPath
     * @return
     */
    public MonitoringResponse addNewServerInPlatform(final String platform,
            final String serverName, final String serverType,
            final String installPath) {
        final MonitoringResponse monitoringResponse = new MonitoringResponse();
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        this.openResourceType("Platforms", platform);
        UI.pause(5000);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getToolMenu(), 5000).click();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getLinkBoxItem("New Server"), 5000).click();
        } catch (final WaitTimedOutException e) {
            monitoringResponse.setSuccess(false);
            monitoringResponse
                    .setErrorMessage("Failed to click Tool menu and New Server");
            return monitoringResponse;
        }
        this.addNewServer(serverName, serverType, installPath);
        monitoringResponse.setSuccess(true);
        return monitoringResponse;
    }

    /**
     * Configure EMC server after it is created. This operator should be called
     * after addNewServerInPlatform
     *
     * @param spaIp
     * @param spbIp
     * @return MonitoringResponse object
     */
    private MonitoringResponse emcConfigurationProperties(final String spaIp,
            final String spbIp) {
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getConfigurationPropertiesLink(), 2000)
                    .click();
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getSpaIpAdressTextBox(), 2000))
                    .setText(spaIp);
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getSpbIpAddressTextBox(), 2000))
                    .setText(spbIp);
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getOkButton(), 2000).click();
        } catch (final WaitTimedOutException e) {
            logger.error("Failed to configure EMC Properties");
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        final MonitoringResponse monitoringResponse = new MonitoringResponse();
        monitoringResponse.setSuccess(true);
        return monitoringResponse;
    }

    /**
     * Add EMC server in platform and configure its ips
     *
     * @param platform
     * @param serverName
     * @param serverType
     * @param installPath
     * @param spaIp
     * @param spbIp
     * @return
     */
    public MonitoringResponse addEmcVnxServerInPlatform(final String platform,
            final String serverName, final String serverType,
            final String installPath, final String spaIp, final String spbIp) {
        MonitoringResponse monitoringResponse = this.addNewServerInPlatform(
                platform, serverName, serverType, installPath);
        if (monitoringResponse.isSuccess()) {
            monitoringResponse = this.emcConfigurationProperties(spaIp, spbIp);
        }
        return monitoringResponse;
    }

    /**
     * Input Server information on "New Server" page.
     *
     * @param serverName
     * @param serverType
     * @param installPath
     */
    private void addNewServer(final String serverName, final String serverType,
            final String installPath) {
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        try {
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getNameInput(), 2000)).setText(serverName);
            dashBoardView.getServerTypeDropdown().selectByTitle(serverType);
            dashBoardView.getServerInstallPath().setText(installPath);
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getOkButton(), 2000).click();
        } catch (final WaitTimedOutException e) {
            logger.error("Failed to add server {} under server type {}",
                    serverName, serverType);
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Opens the Alert Definition Page for the associated resource
     *
     * @param resourceType
     *            Platforms , Servers , Services , Compatible Groups/Clusters,
     *            Mixed Groups, Applications
     * @param resourceName
     *            is the Keyword is used to search for the resource in the
     *            'Search' field
     */
    public void openResourceAlertDefinition(final String resourceType,
            final String resourceName) {
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        openResourceType(resourceType, resourceName);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getAlertTab(), 2000).click();
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Failed to open Resource Alert Definition for resource name {} under resource type {}",
                    resourceName, resourceType);
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Opens the Alert Configuration Page for the associated resource
     *
     * @param resourceType
     *            Platforms , Servers , Services , Compatible Groups/Clusters,
     *            Mixed Groups, Applications
     * @param resourceName
     *            is the Keyword is used to search for the resource in the
     *            'Search' field
     */
    public void openAlertDefinitionConfiguration(final String resourceType,
            final String resourceName) {
        logger.info(
                "Opening alert definition. Resource type: {}, Resource name: {}",
                resourceType, resourceName);
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        openResourceAlertDefinition(resourceType, resourceName);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getConfigureAlertButton(), 2000).click();
            logger.info("Opened alert definition.");
        } catch (final WaitTimedOutException e) {
            logger.error("Failed to open Alert Definition");
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates an alert once the Alert Configuration Page has been opened
     *
     * @param name
     *            Name of the Alert To be created
     * @param description
     *            A description of what the Alert Does
     * @param metric
     *            Availability, Free Memory, Free Memory (+ buffers/cache), Load
     *            Average 5 Minutes, Swap Used
     * @param absoluteValue
     *            Value between 0-100
     */
    public void setNewAlertConfiguration(final String name,
            final String description, final String metric,
            final int absoluteValue, final String comparator) {
        logger.info("Creating new alert: {}", name);
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getNewAlertButton(), 2000).click();
            UI.pause(1000);
            dashBoardView.getAlertNameInput().setText(name);
            dashBoardView.getAlertDescriptionInput().setText(description);
            dashBoardView.getAlertConditionMetricSelector().selectByTitle(
                    metric);
            dashBoardView.getAlertConditionComparator(comparator);
            dashBoardView.getAlertConditionAbsoluteValueInput().setText(
                    String.valueOf(absoluteValue) + "%");
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getAlertDefinitionOkButton(), 2000).click();
        } catch (final WaitTimedOutException e) {
            logger.error("Failed to create new alert in Monitoring Tool");
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        logger.info("Created new alert.");
    }

    /**
     * Deletes the Alert associated with a resource
     *
     * @param resourceType
     *            Platforms , Servers , Services , Compatible Groups/Clusters,
     *            Mixed Groups, Applications
     * @param resourceName
     *            is the Keyword is used to search for the resource in the
     *            'Search' field
     * @param alertName
     *            Name of the Alert which is to be deleted
     */
    public void deleteAlert(final String resourceType,
            final String resourceName, final String alertName) {
        openAlertDefinitionConfiguration(resourceType, resourceName);
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        try {
            ((CheckBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getAlertCheckBox(alertName), 2000)).select();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getDeleteButton(), 2000).click();
            currentBrowserTab.getMessageBox().clickOk();
        } catch (final WaitTimedOutException e) {
            logger.error("Failed to delete Alert {} from monitoring tool",
                    alertName);
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets a list of Alerts which are associated with a specific resource
     *
     * @param resourceType
     *            Platforms , Servers , Services , Compatible Groups/Clusters,
     *            Mixed Groups, Applications
     * @param resourceName
     *            Keyword is used to search for the resource in the 'Search'
     *            field
     * @return Returns a List of Strings with the names of the Alerts associated
     *         with a specific resource
     */
    public List<String> getListOfAlertNames(final String resourceType,
            final String resourceName) {
        logger.info("Getting list of alert names.");
        openAlertDefinitionConfiguration(resourceType, resourceName);
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        UI.pause(1000);
        final List<UiComponent> alertComponentList = dashBoardView
                .getAlertList();
        final ArrayList<String> alertNamesList = new ArrayList<>();
        for (final UiComponent comp : alertComponentList) {
            alertNamesList.add(comp.getText());
        }
        logger.info("Got list of alert names.");
        return alertNamesList;
    }

    /**
     * Create a user in to access the hyperic GUI and recive Alerts via email
     * and SMS
     *
     * @param username
     *            username to be added
     * @param firstName
     *            first name of user
     * @param lastName
     *            second name of user
     * @param password
     *            At least 6 case-sensitive characters and numbers, no spaces,
     *            or quotation marks.
     * @param email
     *            email address of user
     * @param role
     *            user role of user i.e. Guest or Super User
     */
    public MonitoringResponse addNewUser(final String username,
            final String firstName, final String lastName,
            final String password, final String email, final String role) {
        final MonitoringResponse uiResp = new MonitoringResponse();
        openAdministrationTab();
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        try {
            logger.info("Adding New User '{}'", username);
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getNewUserLink(), 4000).click();
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getNewUsernameInput(), 4000))
                    .setText(username);
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getNewUserFirstNameBox(), 4000))
                    .setText(firstName);
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getNewUserLastNameBox(), 4000))
                    .setText(lastName);
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getNewUserPasswordBox(), 4000))
                    .setText(password);
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getNewUserConfirmPasswordBox(), 4000))
                    .setText(password);
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getNewUserEmailBox(), 4000)).setText(email);
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getNewUserOkButton(), 4000).click();
            ((CheckBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getNewUserRole(role), 4000)).select();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getOkButton(), 4000).click();
            uiResp.setOutput(currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getUserAssignedReturnMessage(), 4000)
                    .getText());
            logger.info("User '{}' Added to Hyperic", username);
            uiResp.setSuccess(true);
        } catch (final WaitTimedOutException e) {
            uiResp.setSuccess(false);
            uiResp.setErrorMessage(String.format(
                    "Failed to add new user %s to monitoring tool", username));
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return uiResp;
    }

    /**
     * Links a given user to an alert.
     *
     * @param userName
     *            user to be linked to alert
     * @param alertName
     *            name of alert which must already exist
     * @param resourceType
     *            Platforms , Servers , Services , Compatible Groups/Clusters,
     *            Mixed Groups, Applications
     * @param resourceName
     *            Keyword is used to search for the resource in the 'Search'
     *            field
     */
    public MonitoringResponse linkUserToAlert(final String userName,
            final String alertName, final String resourceType,
            final String resourceName) {
        final MonitoringResponse uiResp = new MonitoringResponse();
        openAlertDefinitionConfiguration(resourceType, resourceName);
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getAlertDefinitionLink(alertName), 2000)
                    .click();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getNotifyHQUsersTab(), 2000).click();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getAddToListButton(), 2000).click();
            ((CheckBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getUserToAlertCheckBox(userName), 2000))
                    .select();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getAddToNotificationListArrow(), 2000)
                    .click();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getOkButton(), 2000).click();
        } catch (final WaitTimedOutException e) {
            logger.error(
                    "Failed to link user {} to alert {} in resource Name {}",
                    userName, alertName, resourceName);
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        uiResp.setOutput(dashBoardView.getUserAddedToNotificationMessage()
                .getText());
        return uiResp;
    }

    /**
     * Delete a user from the monitoring tool.
     *
     * @param username
     *            user to be deleted
     * @return MonitoringResponse object
     */
    public MonitoringResponse deleteUser(final String username) {
        logger.info("Opening Administration Tab");
        openAdministrationTab();
        MonitoringResponse monitoringResponse = new MonitoringResponse();
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getUserListLink(), 4000).click();
            logger.info("Removing User List");
            ((CheckBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getDeleteUserCheckBox(username), 4000))
                    .select();
            logger.info("Selecting checkbox of user to delete");
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getDeleteButton(), 4000).click();
            logger.info("Clicking OK in pop up message window");
            UI.pause(4000);
            currentBrowserTab.getMessageBox().clickOk();
            monitoringResponse = getSuccessResponse();
        } catch (final WaitTimedOutException e) {
            monitoringResponse.setErrorMessage(String.format(
                    "Could not delete User %s", username));
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return monitoringResponse;
    }

    /**
     * Get auto-discovery item. It searches auto-discovery item based the name
     *
     * @param name
     *            The key name contained in the name
     * @return UiComponent object
     */
    public UiComponent autoDiscoveryResourceContainsName(final String name) {
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        try {
            final UiComponent uiComponent = dashBoardView
                    .getAutoDiscoveryResourceName(name);
            return uiComponent;
        } catch (final UiComponentNotFoundException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Clicks auto-discovery item and clicks ok button to add it into inventory
     *
     * @param uiComponent
     *            link to be added to the inventory
     * @return MonitoringResponse object
     */
    public MonitoringResponse addIntoInventory(final UiComponent uiComponent) {
        final MonitoringResponse monitoringResponse = new MonitoringResponse();
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        uiComponent.click();
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getOkButton(), 10000).click();
        } catch (final WaitTimedOutException e) {
            monitoringResponse.setSuccess(false);
            monitoringResponse.setErrorMessage("Failed to add "
                    + uiComponent.getText()
                    + " into Inventory. Timeout when wait OK button");
            return monitoringResponse;
        }
        monitoringResponse.setSuccess(true);
        return monitoringResponse;
    }

    /**
     * Refresh current browser tab
     */
    public void refresh() {
        currentBrowserTab.refreshPage();
    }

    /**
     * Change the password of a monitoring tool user
     *
     * @param userName
     *            username
     * @param adminUserPassword
     *            Administrator Password
     * @param newPassword
     *            New Password For User being changed
     * @return MonitoringResponse object
     */
    public MonitoringResponse changeUserPassword(final String userName,
            final String adminUserPassword, final String newPassword) {
        MonitoringResponse monitoringResponse = new MonitoringResponse();
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        openAdministrationTab();
        monitoringResponse = openUserProfile(userName, dashBoardView);
        if (!monitoringResponse.isSuccess()) {
            return monitoringResponse;
        }
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getChangePasswordLink(), 2000).click();
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getCurrentPasswordInput(), 2000))
                    .setText(adminUserPassword);
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getNewPasswordInput(), 2000))
                    .setText(newPassword);
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getConfirmPasswordInput(), 2000))
                    .setText(newPassword);
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getOkButton(), 2000).click();
            monitoringResponse = getSuccessResponse();
        } catch (final WaitTimedOutException e) {
            monitoringResponse = getFailResponse("Could not Change User Password");
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return monitoringResponse;
    }

    private MonitoringResponse openUserProfile(final String userName,
            final DashBoardViewModel dashBoardView) {
        MonitoringResponse monitoringResponse = new MonitoringResponse();
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getUserListLink(), 2000).click();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getUserEditLink(userName), 2000).click();
            monitoringResponse = getSuccessResponse();
        } catch (final WaitTimedOutException e) {
            monitoringResponse = getFailResponse(String
                    .format("Could not find user '%s' check to see if user has been created",
                            userName));
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return monitoringResponse;
    }

    /**
     * Checks that the given resource is 100% available Throws an error if
     * resource is 0% available Throws a warning is resource is greater than 0%
     * and less than 100% available
     *
     * @param resourceType
     *            resource type to be opened i.e. Platforms
     * @param resourceName
     *            name of resource to be opened
     * @return MonitoringResponse object
     */
    public MonitoringResponse checkResourceAvailability(
            final String resourceType, final String resourceName) {
        final MonitoringResponse monitoringResponse = new MonitoringResponse();
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        openResourceType(resourceType, resourceName);
        logger.info("Opening Metric Data for resource {}", resourceName);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getMetricDataTab(), 2000).click();
            monitoringResponse.setSuccess(true);
        } catch (final WaitTimedOutException e) {
            logger.error("Unable to open Metric Data for resource {}",
                    resourceName);
            monitoringResponse.setSuccess(false);
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            return monitoringResponse;
        }

        logger.info("Checking availability of resource {}", resourceName);
        try {
            final Boolean isAvailable = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            dashBoardView.getLastAvailabilityStatus(), 2000)
                    .isDisplayed();
            monitoringResponse.setSuccess(isAvailable);
            final String averageAvailability = currentBrowserTab
                    .waitUntilComponentIsDisplayed(
                            dashBoardView.getAvailabilityAverage(), 2000)
                    .getText();
            if (!averageAvailability.equals("100.0%")) {
                logger.warn("Resource {} is not 100% Available : Status {}",
                        resourceName, averageAvailability);
            }
        } catch (final WaitTimedOutException e) {
            logger.error("Cannot get availability average for {}", resourceName);
            monitoringResponse.setSuccess(false);
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            return monitoringResponse;
        }

        return monitoringResponse;
    }

    private MonitoringResponse setResponseAndTakeScreenshot(
            final BrowserTab tab, final String error,
            final String screenshotName) {
        final String screenshotPath = takeScreenShot(tab, screenshotName);
        return getFailResponse(error + ": Screen Shot taken. Path : "
                + screenshotPath);
    }

    private String takeScreenShot(final BrowserTab tab,
            final String screenshotName) {
        final File directory = new File(SCREENSHOT_FOLDER);
        if (!directory.exists()) {
            directory.mkdir();
        }
        final String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS")
                .format(new Date());
        final String screenshotPath = SCREENSHOT_FOLDER + File.separator
                + screenshotName + "_" + date + ".png";
        logger.debug("Taking screenshot: {}", screenshotPath);
        tab.takeScreenshot(screenshotPath);
        return screenshotPath;
    }

    /**
     * Checks CPU usage on hyperic server If value exceeds 95% test fails If
     * value is between 80-95% a warning is given but testcase passes If value
     * is between 0-80% testcase passes
     *
     * @return MonitoringResponse with success value and error message if
     *         applicable
     */

    public MonitoringResponse checkHQHealthCPU() {
        final MonitoringResponse monitoringResponse = new MonitoringResponse();
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        String cpuUsageText = "";
        int cpuUsageValue;
        openAdministrationTab();
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getHQHealthLink(), 3000).click();
            cpuUsageText = currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getSystemCPUPercentage(), 3000).getText();
        } catch (final WaitTimedOutException e) {
            monitoringResponse.setErrorMessage("Failed to open HQ Health page");
            monitoringResponse.setSuccess(false);
        }
        try {
            cpuUsageValue = Integer.parseInt(cpuUsageText);
        } catch (final NumberFormatException e) {
            monitoringResponse.setErrorMessage(String.format(
                    "Could not parse value '%s' to a double", cpuUsageText));
            monitoringResponse.setSuccess(false);
            return monitoringResponse;
        }
        if (cpuUsageValue >= 95) {
            monitoringResponse.setErrorMessage(String.format(
                    "HQ Health CPU usage is too high : Value %s %",
                    cpuUsageValue));
            monitoringResponse.setSuccess(false);
            return monitoringResponse;
        } else if (cpuUsageValue >= 80) {
            logger.warn("HQ Health CPU usage is high. Currently at {}%. Execute 'top' command on the MS and check hyperic process consumption");
            monitoringResponse.setSuccess(true);
        } else if (cpuUsageValue >= 0 && cpuUsageValue <= 80) {
            logger.info("HQ Health CPU usage is currently at {}%",
                    cpuUsageValue);
            monitoringResponse.setSuccess(true);
        } else {
            monitoringResponse
                    .setErrorMessage("Unable to get HQ CPU usage value");
            monitoringResponse.setSuccess(false);
        }
        return monitoringResponse;
    }

    /**
     * Verifies the time difference between the hyperic server and the hyperic
     * agents is below 15ms
     *
     * @return MonitoringResponse with success value and error message if
     *         applicable
     */

    public MonitoringResponse checkHQTimeOffset() {
        MonitoringResponse monitoringResponse = new MonitoringResponse();
        final DashBoardViewModel dashBoardView = currentBrowserTab
                .getView(DashBoardViewModel.class);
        openAdministrationTab();

        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getHQHealthLink(), 3000).click();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getAgentsTab(), 3000).click();
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    dashBoardView.getAgentsHeader(), 3000);
        } catch (final WaitTimedOutException e) {
            monitoringResponse
                    .setErrorMessage("Failed to open Agents Tab on HQ Health page");
            monitoringResponse.setSuccess(false);
        }
        final List<UiComponent> serverTimeOffsetValues = dashBoardView
                .getServerTimeOffsets();
        monitoringResponse = verifyOffsetValues(serverTimeOffsetValues);
        return monitoringResponse;
    }

    private MonitoringResponse verifyOffsetValues(
            final List<UiComponent> offsetValues) {
        final MonitoringResponse monitoringResponse = new MonitoringResponse();
        double serverTimeOffset = 0;
        for (final UiComponent offsetValue : offsetValues) {
            final String stringValue = offsetValue.getText();
            try {
                serverTimeOffset = Double.parseDouble(stringValue);
            } catch (final NumberFormatException e) {
                monitoringResponse
                        .setErrorMessage(String
                                .format("Could not parse value '%s' to an double. "
                                        + "Check HQ Health offset values are correct in monitoring tool",
                                        stringValue));
                monitoringResponse.setSuccess(false);
                return monitoringResponse;
            }
            if (serverTimeOffset >= HQ_SERVER_TIME_OFFSET) {
                monitoringResponse.setErrorMessage(String.format(
                        "Hyperic Agent %s ms out of sync with Hyperic Server. "
                                + "Server time offset exceeds %s ms",
                        serverTimeOffset, HQ_SERVER_TIME_OFFSET));
                monitoringResponse.setSuccess(false);
                return monitoringResponse;
            }
        }
        monitoringResponse.setSuccess(true);
        return monitoringResponse;
    }
}