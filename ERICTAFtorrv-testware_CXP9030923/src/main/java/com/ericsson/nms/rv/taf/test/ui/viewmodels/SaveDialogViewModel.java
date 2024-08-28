package com.ericsson.nms.rv.taf.test.ui.viewmodels;


import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.*;

public class SaveDialogViewModel extends GenericViewModel {

    @UiComponentMapping(selector=".eaNetworkExplorer-rCollectionActionBar-saveAs")
    private Link saveAsLink;

    @UiComponentMapping(selector="//input[contains(@class, 'eaNetworkExplorer-wSaveDialog-collectionName')]", selectorType = SelectorType.XPATH)
    private TextBox NameTextBox;

    @UiComponentMapping(selector=".eaNetworkExplorer-wSaveDialog-collectionRadioButton")
    private RadioButton collectionRadioButton;

    @UiComponentMapping(selector=".eaNetworkExplorer-wSaveDialog-savedSearchRadioButton")
    private RadioButton savedSearchRadioButton;

    @UiComponentMapping(selector=".ebDialog-primaryActionButton")
    private Button saveButton;

    @UiComponentMapping(selector = "//input[contains(@class, 'eaNetworkExplorer-wSaveDialog-radioPrivate')]", selectorType = SelectorType.XPATH)
    private UiComponent privatePermissionRadio;

    @UiComponentMapping(selector = "//input[contains(@class, 'eaNetworkExplorer-wSaveDialog-radioPublic')]", selectorType = SelectorType.XPATH)
    private UiComponent publicPermissionRadio;

    public Link getSaveAsLink() {
        return saveAsLink;
    }

    public TextBox getNameTextBox() {
        return NameTextBox;
    }

    public RadioButton getCollectionRadioButton() {
        return collectionRadioButton;
    }

    public RadioButton getSavedSearchRadioButton() {
        return savedSearchRadioButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public UiComponent getNotificationLabel() {
        return getViewComponent(".ebNotification-label", UiComponent.class);
    }

    public UiComponent getPrivatePermissionRadio() {
        return privatePermissionRadio;
    }

    public UiComponent getPublicPermissionRadio() {
        return publicPermissionRadio;
    }
}
