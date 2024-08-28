package com.ericsson.nms.rv.taf.test.ui.viewmodels.monitoringbrowser;

import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.Button;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.Link;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;

public class LoginLogoutViewModel extends GenericViewModel {

    @UiComponentMapping(id="usernameInput")
    private TextBox usernameInput;

    @UiComponentMapping(id="passwordInput")
    private TextBox passwordInput;

    @UiComponentMapping(".torLogin-Holder-formButton")
    private Button submitButton;

    @UiComponentMapping(".torLogin-Holder-title")
    private UiComponent monToolHeading;


    public TextBox getUsernameInput() {
        return usernameInput;
    }

    public TextBox getPasswordInput() {
        return passwordInput;
    }

    public Button getSubmitButton() {
        return submitButton;
    }

    public Link getLogoutButton() {
        return this.getViewComponent(SelectorType.XPATH, "//a[normalize-space(text())='Sign Out']", Link.class);
    }

    public UiComponent getMonToolHeadingText() {
        return monToolHeading;
    }
}