package com.ericsson.nms.rv.taf.test.ui.viewmodels;

import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

public class DeleteSavedSearchViewModel extends GenericViewModel {

    //*[contains(@class,'ebComponentList-item')][normalize-space(text())='" + name + "']"

    @UiComponentMapping(selector = "//*[contains(@class,'ebBtn-caption')][text()='Delete Saved Search']", selectorType = SelectorType.XPATH)
    private UiComponent deleteSavedSearch;

    @UiComponentMapping(selector = "//*[contains(@class,'ebBtn-caption')][normalize-space(text())='Cancel']", selectorType = SelectorType.XPATH)
    private UiComponent cancelButton;

    public UiComponent getDeleteSearchButton() {
        return deleteSavedSearch;
    }

    public UiComponent getCancelButton() {
        return cancelButton;
    }
}