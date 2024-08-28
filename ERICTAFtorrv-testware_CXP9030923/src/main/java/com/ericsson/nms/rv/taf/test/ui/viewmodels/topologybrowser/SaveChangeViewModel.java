package com.ericsson.nms.rv.taf.test.ui.viewmodels.topologybrowser;

import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

/**
 * Created by ewandaf on 03/07/14.
 */
public class SaveChangeViewModel extends GenericViewModel {

    //*[contains(@class,'ebComponentList-item')][normalize-space(text())='" + name + "']"

    @UiComponentMapping(selector = "//*[contains(@class,'ebBtn-caption')][text()='Save Changes']", selectorType = SelectorType.XPATH)
    private UiComponent saveChangesButton;

    @UiComponentMapping(selector = "//*[contains(@class,'ebBtn-caption')][normalize-space(text())='Cancel']", selectorType = SelectorType.XPATH)
    private UiComponent cancelButton;

    public UiComponent getSaveChangesButton() {
        return saveChangesButton;
    }

    public UiComponent getCancelButton() {
        return cancelButton;
    }
}
