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
package com.ericsson.nms.rv.taf.test.cmapache.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.WaitTimedOutException;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheUiOperator;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.CmResponse;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.CliAppViewModel;

/**
 * @author epaulki
 */
public class CmCommandUiOperator {

    private final Logger logger = LoggerFactory
            .getLogger(CmCommandUiOperator.class);
    private static final int COMPONENT_TIMEOUT_MILLIS = 10000;
    private static final long LOADING_WIDGET_TIMEOUT_MILLIS = 1000 * 60 * 3;

    public CmCommandUiOperator() {
    }

    /**
     * @param command
     *            CM CLI command to be executed.
     * @param expectedResultContainsString
     *            This operation only examines the final line in the result
     *            query. This has the number or objects returned. Example: '1
     *            instance(s)' or '1 config(s)'. It checks is this line contains
     *            the string passed in.
     * @return
     */
    public CmResponse doCliCommand(final String command,
            final String expectedResultContainsString) {

        logger.info("Executing Command: {}, Expected Result: {}", command,
                expectedResultContainsString);
        final CmResponse retResp = new CmResponse();
        final Browser browser = ApacheUiOperator.getBrowser();
        ApacheUiOperator.skipTestIfUserIsNotLoggedIn(browser);

        // Do command
        final BrowserTab currentBrowserTab = browser.getCurrentWindow();

        final CliAppViewModel cliView = currentBrowserTab
                .getView(CliAppViewModel.class);
        final TextBox inputBox = cliView.getInput();
        UiComponent result = null;

        final String url = "https://" + HostConfigurator.getApache().getIp()
                + "/#cliapp";
        logger.info("Opening page: {}", url);
        currentBrowserTab.open(url);

        try {
            ApacheUiOperator.waitUntilLoaderIsHidden(currentBrowserTab,
                    cliView, LOADING_WIDGET_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            return setResponse(retResp, false,
                    "Failed to open cliapp page. Loader widget did not disappear.");
        }

        try {
            logger.info("Waiting for input box to appear.");
            currentBrowserTab.waitUntilComponentIsDisplayed(inputBox,
                    COMPONENT_TIMEOUT_MILLIS);
            logger.info("Found input box.");
        } catch (final WaitTimedOutException e) {
            return setResponse(retResp, false,
                    "Failed to open cliapp page. Input box did not appear.");
        }
        logger.info("Checking that input box is enabled.");
        if (!inputBox.isEnabled()) {
            logger.warn("Input box is not enabled.");
            ApacheUiOperator.takeScreenShot(currentBrowserTab,
                    "InputBoxNotEnabled");
        }
        logger.info("Entering command '{}' to input box.", command);
        try {
            inputBox.setText(command);
            inputBox.sendKeys("\r");
        } catch (final Exception e) {
            ApacheUiOperator.takeScreenShot(currentBrowserTab,
                    "ErrorEnteringCommand");
            return setResponse(
                    retResp,
                    false,
                    "Failed to enter command into input box. Message: "
                            + e.getMessage());
        }

        try {
            ApacheUiOperator.waitUntilLoaderIsHidden(currentBrowserTab,
                    cliView, LOADING_WIDGET_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            return setResponse(retResp, false,
                    "Failed to get results from command. " + e.getMessage());
        }

        try {
            logger.info("Getting Cli App result.");
            result = cliView.getSimpleResult();
            logger.info("Waiting for result to appear. Result displayed: {}",
                    result.isDisplayed());
            currentBrowserTab.waitUntilComponentIsDisplayed(result,
                    COMPONENT_TIMEOUT_MILLIS);
            logger.info("Got result object.");
        } catch (final WaitTimedOutException e) {
            logger.warn("WaitTimedOutException. {}", e.getMessage());
            return setResponse(retResp, false, String.format(
                    "Failed to get result of command '%s'. "
                            + "Result did not appear within the given time.",
                    command));
        }

        // Check results
        logger.info("Checking results.");
        if (result.getText().trim()
                .contains(expectedResultContainsString.trim())) {
            retResp.setSuccess(true);
            logger.info("Correct result found.");
            retResp.setBody(result.getText());
        } else {
            logger.info("Incorrect result found: {}", result.getText());
            ApacheUiOperator.takeScreenShot(currentBrowserTab,
                    "CliUiResultIncorrect");
            return setResponse(
                    retResp,
                    false,
                    String.format(
                            "Result did not contain expected result. Result: %s, Expected: %s",
                            result.getText(), expectedResultContainsString));
        }
        return retResp;
    }

    private CmResponse setResponse(final CmResponse response,
            final boolean success, final String error) {
        response.setSuccess(success);
        response.setErrorMessage(error);
        return response;
    }
}