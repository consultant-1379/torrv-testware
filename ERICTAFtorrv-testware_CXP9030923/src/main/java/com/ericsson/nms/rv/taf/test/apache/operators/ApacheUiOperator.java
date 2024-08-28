/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.rv.taf.test.apache.operators;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.testng.SkipException;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.ui.*;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.WaitTimedOutException;
import com.ericsson.cifwk.taf.ui.sdk.Link;
import com.ericsson.cifwk.taf.ui.sdk.MessageBox;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.ApacheResponse;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.LoaderViewModel;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.LoginLogoutViewModel;

/**
 * @author ekieobr
 *
 */
public class ApacheUiOperator implements ApacheOperator {

    private static ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private final static Logger logger = org.slf4j.LoggerFactory
            .getLogger(ApacheUiOperator.class);

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ericsson.nms.rv.taf.test.apache.operators.ApacheOperator#login(java
     * .lang.String, java.lang.String)
     */
    @Override
    public ApacheResponse login(final String userId, final String password) {
        boolean success = false;
        final ApacheResponse retResp = new ApacheResponse();

        logger.info("Logging in to ENM via UI with user/password: {}/{}",
                userId, password);
        UI.closeWindow(com.ericsson.cifwk.taf.execution.TestExecutionEvent.ON_SUITE_FINISH);

        logger.info("Creating new FIREFOX browser instance.");
        final Browser browser = createBrowserInstance();

        if (browser == null) {
            return setResponse(retResp, false,
                    "Unable to login. Could not create browser. Browser is null.");
        }
        logger.info("Created new browser object");

        final String url = "https://" + HostConfigurator.getApache().getIp()
                + "/login";
        logger.info("Opening url: {}", url);
        BrowserTab currentBrowserTab = browser.open(url);
        String currentUrl = currentBrowserTab.getCurrentUrl();
        logger.info("Got browser tab. Url: ", currentUrl);
        currentBrowserTab.maximize();

        //Workaourd for mozilla 'new version' page appearing.
        if (currentUrl.contains("mozilla.org")) {
            logger.warn(
                    "Firefox opened on 'Thank you for choosing mozilla' page. Retrying url: {}",
                    url);
            currentBrowserTab = browser.open(url);
            currentUrl = currentBrowserTab.getCurrentUrl();
            logger.info("Got browser tab, attempt #2. Url: {}", currentUrl);
        }

        final LoginLogoutViewModel loginView = currentBrowserTab
                .getView(LoginLogoutViewModel.class);
        try {
            logger.info("Waiting for Notice Screen to appear.");
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    loginView.getNoticeOkButton(), 5000);
            loginView.getNoticeOkButton().click();
            logger.info("Clicked notice 'OK' button.");
        } catch (final WaitTimedOutException e) {
            return setResponseAndTakeScreenshot(currentBrowserTab, retResp,
                    false, "Timed out while waiting for Notice Screen.",
                    "UiLoginTimeoutWaitingForNoticeScreen");
        }
        try {
            logger.info("Waiting for login page to appear.");
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    loginView.getUsernameInput(), 5000);
        } catch (final WaitTimedOutException e) {
            return setResponseAndTakeScreenshot(
                    currentBrowserTab,
                    retResp,
                    false,
                    String.format(
                            "Failed to log in with userId %s. Timed out while waiting for page to load.",
                            userId), "UiLoginTimeoutWaitingForPageToLoad");
        }
        loginView.getUsernameInput().setText(userId);
        loginView.getPasswordInput().setText(password);
        logger.info("Set username and password fields. Clicking submit button.");
        loginView.getSubmitButton().click();

        try {
            logger.info("Waiting for logout button to appear.");
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    loginView.getLogoutButton(), 5000);
            success = true;
        } catch (final WaitTimedOutException e) {
            return setResponseAndTakeScreenshot(currentBrowserTab, retResp,
                    false,
                    String.format("Failed to log in with userId %s", userId),
                    "UiLoginLoginFailed");
        }
        browserThreadLocal.set(browser);
        retResp.setSuccess(success);
        logger.info("Successfully logged in.");
        return retResp;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ericsson.nms.rv.taf.test.apache.operators.ApacheOperator#logout()
     */
    @Override
    public ApacheResponse logout() {
        ApacheResponse retResp = new ApacheResponse();
        final Browser browser = ApacheUiOperator.getBrowser();
        ApacheUiOperator.skipTestIfUserIsNotLoggedIn(browser);
        BrowserTab currentBrowserTab = null;
        boolean success = false;

        if (browser.isClosed()) {
            logger.info("Browser was already closed. Skipping logout step.");
            checkBrowserWindowIsClosedAndRemoveBrowserThread(browser);
            throw new SkipException("The browser was already closed.");
        }
        try {
            logger.info("Getting the browser tab.");
            currentBrowserTab = browser.getCurrentWindow();
        } catch (final UndeclaredThrowableException ex) {
            logger.warn("UndeclaredThrowableException thrown while trying to get the browser tab.");
            logger.warn("Message: {}", ex.getMessage());
            ex.getUndeclaredThrowable().printStackTrace();
            checkBrowserWindowIsClosedAndRemoveBrowserThread(browser);
            throw new SkipException(
                    "Skipping test as we cannot get the browser tab.");
        } catch (final Exception e) {
            logger.warn("Unknown Exception thrown while trying to get the browser tab.");
            logger.warn("Messge: {}", e.getMessage());
            checkBrowserWindowIsClosedAndRemoveBrowserThread(browser);
            throw new SkipException(
                    "Skipping test as we cannot get the browser tab.");
        }
        final LoginLogoutViewModel loginView = currentBrowserTab
                .getView(LoginLogoutViewModel.class);
        logger.info("Logging out of ENM via UI");
        final Link logout = loginView.getLogoutButton();
        logger.info("Clicking logout button.");
        logout.click();
        logger.info("Clicking message box 'OK' button.");

        final MessageBox messageBox = currentBrowserTab.getMessageBox();
        if (messageBox == null) {
            retResp = setResponseAndTakeScreenshot(currentBrowserTab, retResp,
                    false, "MessageBox is null during logout.",
                    "LogoutUi_MessageBox_null");
        }
        messageBox.clickOk();

        try {
            logger.info("Waiting for notice box 'OK' button to appear.");
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    loginView.getNoticeOkButton(), 5000);
            success = true;
        } catch (final WaitTimedOutException e) {
            retResp = setResponseAndTakeScreenshot(
                    currentBrowserTab,
                    retResp,
                    false,
                    "Timed out waiting for logout notice 'OK' button to appear.",
                    "UiLogoutLogoutFailed");
            checkBrowserWindowIsClosedAndRemoveBrowserThread(browser);
            return retResp;
        }
        checkBrowserWindowIsClosedAndRemoveBrowserThread(browser);
        retResp.setSuccess(success);
        return retResp;
    }

    public static Browser getBrowser() {
        return browserThreadLocal.get();
    }

    public static void skipTestIfUserIsNotLoggedIn(final Browser browser) {
        if (browser == null) {
            throw new SkipException("User is not logged in.");
        }
    }

    /**
     * @return Browser instance
     */
    private Browser createBrowserInstance() {
        Browser browser = ApacheUiOperator.getBrowser();

        if (browser != null) {
            logger.info("Browser was not null. Thread will be removed to allow login.");
            checkBrowserWindowIsClosedAndRemoveBrowserThread(browser);
        }

        BrowserSetup.Builder setup = BrowserSetup.build().withType(
                BrowserType.FIREFOX);
        //.withVersion("31")
        // Capability HAS_NATIVE_EVENTS does not seem to have any effect.
        //.withCapability(CapabilityType.HAS_NATIVE_EVENTS, true);
        //.withSize(BrowserSetup.Resolution._1920x1200);

        // Check if we are running in the cloud and configure proxy settings if so
        if (DataHandler.getAttribute("env_type").toString().equals("cloud")) {
            logger.info("Configured to run in cloud environment - modifying browser to autodetect proxy settings");
            setup = setup.withCapability(CapabilityType.PROXY,
                    new Proxy().setAutodetect(true));
        }

        try {
            browser = UI.newBrowser(setup);
        } catch (final Exception e) {
            checkBrowserWindowIsClosedAndRemoveBrowserThread(browser);
            logger.error("Failed to create FIREFOX browser.");
            e.printStackTrace();
        }

        return browser;
    }

    private ApacheResponse setResponseAndTakeScreenshot(final BrowserTab tab,
            final ApacheResponse response, final boolean success,
            final String error, final String screenshotName) {

        ApacheUiOperator.takeScreenShot(tab, screenshotName);
        return setResponse(response, success, error + " Created screenshot: "
                + screenshotName);
    }

    public static void takeScreenShot(final BrowserTab tab,
            final String screenshotName) {
        if (tab == null) {
            logger.warn("Cannot take screenshot as BrowserTab is null");
        } else if (tab.isClosed()) {
            logger.warn("BrowserTab is closed.");
        } else {
            logger.info("Taking screenshot: {}", screenshotName);
            tab.takeScreenshot(screenshotName);
        }
    }

    public static void pause(final long waitTimeMillis) {
        logger.debug("ApacheUiOperator.pause({})", waitTimeMillis);
        //Wait without using Thread.wait because we suspect that Thread.wait causes selenium SessionNotFoundException
        boolean waiting = true;
        final long endTime = System.currentTimeMillis() + waitTimeMillis;
        do {
            if (System.currentTimeMillis() > endTime) {
                waiting = false;
            }
        } while (waiting);
    }

    private void checkBrowserWindowIsClosedAndRemoveBrowserThread(
            final Browser browser) {
        if (browser != null) {
            if (!browser.isClosed()) {
                logger.info("Browser window has been closed.");
                browser.close();
            }
            browserThreadLocal.remove();
        }
    }

    private ApacheResponse setResponse(final ApacheResponse response,
            final boolean success, final String errorMessage) {
        response.setSuccess(success);
        response.setErrorMessage(errorMessage);
        return response;
    }

    public static void waitUntilLoaderIsHidden(final BrowserTab tab,
            final LoaderViewModel view, final long timeoutTime)
            throws WaitTimedOutException {

        logger.info("Checking if loading widget is visible.");
        final List<UiComponent> loadingWidgets = view.getLoadingWidgets();

        for (int i = 0; i < loadingWidgets.size(); i++) {
            final UiComponent loader = ApacheUiOperator.getUiComponentFromList(
                    i, loadingWidgets, 3);
            if (loader.isDisplayed()) {
                logger.info(
                        "Loader '{}' is displayed. Waiting. Timeout '{}' ms.",
                        i, timeoutTime);
                tab.waitUntilComponentIsHidden(loader, timeoutTime);
            }
        }
        logger.info("All loaders have been cleared.");
    }

    /**
     * Selenium can throw a StaleElementReferenceException when getting a
     * UiComponent from the page. To overcome this we retry.
     *
     * @param componentIndex
     * @param loadingWidgets
     * @param numRetries
     * @return
     */
    private static UiComponent getUiComponentFromList(final int componentIndex,
            final List<UiComponent> loadingWidgets, final int numRetries)
            throws WaitTimedOutException {
        int tryCount = 0;
        UiComponent returnComponent = null;

        while (tryCount < numRetries) {
            try {
                returnComponent = loadingWidgets.get(componentIndex);
                return returnComponent;
            } catch (final StaleElementReferenceException e) {
                logger.info("Caught StaleElementReferenceException while trying to get loader ui component");
            }
            tryCount++;
        }
        throw new WaitTimedOutException(
                String.format(
                        "Caught StaleElementReferenceException while trying to get loader ui component after %d retries.",
                        numRetries));
    };
}
