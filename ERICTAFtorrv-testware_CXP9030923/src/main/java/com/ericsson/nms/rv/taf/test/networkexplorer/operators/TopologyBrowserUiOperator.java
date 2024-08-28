package com.ericsson.nms.rv.taf.test.networkexplorer.operators;

import java.util.List;

import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheUiOperator;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.NetworkExplorerResponse;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.topologybrowser.SaveChangeViewModel;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.topologybrowser.TopologyBrowserModel;

public class TopologyBrowserUiOperator extends NetworkExplorerUiOperator {
    private final Logger logger = LoggerFactory
            .getLogger(TopologyBrowserUiOperator.class);

    private final int SHORT_PAUSE_MILLIS = 2000;
    private final int UI_COMPONENT_TIMEOUT_MILLIS = 15000;
    private final int CHANGE_ATTRIBUTE_WAIT_TIME_MILLIS = 500;

    public NetworkExplorerResponse expandAttributesColumn() {
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        final TopologyBrowserModel view = currentBrowserTab
                .getView(TopologyBrowserModel.class);
        logger.info("Clicking 'Attributes -->' button.");
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    view.getAttributeButton(), UI_COMPONENT_TIMEOUT_MILLIS)
                    .click();
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Failed to expand 'Attributes' column.");
        }
        logger.info("Checking right arrow uiComponent exists.");
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    view.getRightArrowIcon(), UI_COMPONENT_TIMEOUT_MILLIS);
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Right arrow icon never displayed. Therefore column has not been expanded.");
        }
        // Short pause to make sure column has expanded fully
        ApacheUiOperator.pause(SHORT_PAUSE_MILLIS);
        logger.info("Attributes button has been clicked and column is now visible.");
        response.setSuccess(true);
        return response;
    }

    public NetworkExplorerResponse clickEditAttributesLink() {
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        final TopologyBrowserModel view = currentBrowserTab
                .getView(TopologyBrowserModel.class);
        logger.info("Clicking 'Edit Attributes' link.");
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    view.getEditAttributesLink(), UI_COMPONENT_TIMEOUT_MILLIS)
                    .click();
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Failed to click edit attributes link.");
        }
        logger.info("Edit attributes button has been clicked.");
        response.setSuccess(true);
        return response;
    }

    public NetworkExplorerResponse saveChanges() {
        logger.info("Saving changes.");
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        final TopologyBrowserModel view = currentBrowserTab
                .getView(TopologyBrowserModel.class);
        final SaveChangeViewModel saveChangeViewModel = currentBrowserTab
                .getView(SaveChangeViewModel.class);

        logger.info("Checking that the 'Save' button is enabled");
        final UiComponent saveButton = view.getSaveButton();
        final String cssClass = saveButton.getProperty("class");
        logger.debug("Css class on save button {}", cssClass);

        if (cssClass.contains("ebBtn_disabled")) {
            ApacheUiOperator.takeScreenShot(currentBrowserTab,
                    "Save_button_disabled_top_browser");
            return setResponse(response, false,
                    "Save button is disabled in topology browser when editing property.");
        } else {
            logger.info("'Save' button is enabled");
        }

        logger.info("Clicking 'Save Changes' button.");
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    view.getSaveButton(), UI_COMPONENT_TIMEOUT_MILLIS).click();
        } catch (final WaitTimedOutException e) {
            logger.debug("Timed out waiting for 'save' button to appear.");
            return setResponse(response, false,
                    "Save button failed to display.");
        }
        logger.info("Clicking 'Save Changes' button on popup window.");
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    saveChangeViewModel.getSaveChangesButton(),
                    UI_COMPONENT_TIMEOUT_MILLIS).click();
        } catch (final WaitTimedOutException e) {
            ApacheUiOperator.takeScreenShot(currentBrowserTab,
                    "Timeout_save_changes_popup_top_browser");
            return setResponse(response, false,
                    "Failed to click save changes popup.");
        }
        logger.info("Changes have been saved.");
        response.setSuccess(true);
        return response;
    }

    public NetworkExplorerResponse clickSearchForAnObject() {
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        final TopologyBrowserModel view = currentBrowserTab
                .getView(TopologyBrowserModel.class);
        logger.info("Clicking search for an object link.");
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    view.getSearchForAnObjectLink(),
                    UI_COMPONENT_TIMEOUT_MILLIS).click();
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Failed to click 'Search for an Object' link.");
        }
        response.setSuccess(true);
        logger.info("Search was executed successfully.");
        return response;
    }

    public NetworkExplorerResponse updateAttribute(final String key,
            final String value) {
        logger.info("Updating attribte '{}' to '{}'", key, value);
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        final TopologyBrowserModel view = currentBrowserTab
                .getView(TopologyBrowserModel.class);
        // Pause to allow attributes list to appear
        ApacheUiOperator.pause(SHORT_PAUSE_MILLIS);
        final List<UiComponent> attributes = view
                .getPropertyAttributesInEditMode();
        for (final UiComponent uiComponent : attributes) {
            final String attribute = getAttributeNameFromUiComponent(uiComponent);
            if (key.equals(attribute)) {
                final List<UiComponent> valueComponents = getListOfUiComponentDescendants(uiComponent);
                if (!valueComponents.isEmpty()) {
                    try {
                        logger.info(
                                "Clearing textbox and sending string '{}' to attribute {}'s update textbox.",
                                value, attribute);
                        currentBrowserTab
                                .waitUntilComponentIsDisplayed(
                                        valueComponents.get(0),
                                        UI_COMPONENT_TIMEOUT_MILLIS)
                                .sendKeys(
                                        "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b",
                                        value, Keys.TAB);
                        ApacheUiOperator
                                .pause(CHANGE_ATTRIBUTE_WAIT_TIME_MILLIS);
                    } catch (final WaitTimedOutException e) {
                        return setResponse(response, false,
                                "Unable to input values into edit attribute text box.");
                    }
                } else {
                    logger.info(
                            "Sending string {} to attribute {}'s update textbox.",
                            value, attribute, Keys.TAB);

                    try {
                        currentBrowserTab.waitUntilComponentIsDisplayed(
                                valueComponents.get(0),
                                UI_COMPONENT_TIMEOUT_MILLIS).sendKeys(value,
                                Keys.TAB);
                        ApacheUiOperator
                                .pause(CHANGE_ATTRIBUTE_WAIT_TIME_MILLIS);
                    } catch (final WaitTimedOutException e) {
                        ApacheUiOperator.takeScreenShot(currentBrowserTab,
                                "Timeout_edit_attribute_box_top_browser");
                        return setResponse(response, false,
                                "Unable to input values into edit attribute text box.");
                    }
                }
            }
        }
        logger.info("Attribute '{}' has been updated with the value '{}'.",
                key, value);
        response.setSuccess(true);
        return response;
    }

    public NetworkExplorerResponse expandTreeByAttributeName(
            final String attributeName) {
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        final TopologyBrowserModel view = currentBrowserTab
                .getView(TopologyBrowserModel.class);
        logger.info(
                "Clicking on tree elements of topology browser by attribute name: {}.",
                attributeName);
        try {
            currentBrowserTab.waitUntilComponentIsDisplayed(
                    view.getTreeElementByAttributeName(attributeName),
                    UI_COMPONENT_TIMEOUT_MILLIS).click();
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false,
                    "Expand tree by attribute failed.");
        }
        logger.info(
                "Topology browser tree has been expanded on '{}' attribute.",
                attributeName);
        response.setSuccess(true);
        return response;
    }

    public NetworkExplorerResponse filterAttributesByName(
            final String attributeName) {
        final NetworkExplorerResponse response = new NetworkExplorerResponse();
        final TopologyBrowserModel view = currentBrowserTab
                .getView(TopologyBrowserModel.class);
        logger.info("Filtering attributes list by name: {}.", attributeName);
        try {
            ((TextBox) currentBrowserTab.waitUntilComponentIsDisplayed(
                    view.getFilterInputBox(), UI_COMPONENT_TIMEOUT_MILLIS))
                    .setText(attributeName);
        } catch (final WaitTimedOutException e) {
            return setResponse(response, false, "Search box failed to display.");
        }
        logger.info("Attributes have been filtered by '{}' term.",
                attributeName);
        response.setSuccess(true);
        return response;
    }

    private List<UiComponent> getListOfUiComponentDescendants(
            final UiComponent uiComponent) {
        logger.info("Getting list of uiComponent descendents for attributes column.");
        return uiComponent.getDescendantsBySelector(SelectorType.XPATH,
                "//input[contains(@class, 'ebInput')]");
    }

    private String getAttributeNameFromUiComponent(final UiComponent uiComponent) {
        logger.info("Getting attribute name from uiComponent.");
        return uiComponent
                .getDescendantsBySelector(SelectorType.XPATH,
                        "//div[contains(@class, 'ebText_alternative')]").get(0)
                .getText();
    }
}
