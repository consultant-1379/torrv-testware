package com.ericsson.nms.rv.taf.test.ui.viewmodels;

import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

public class DeleteSavedCollectionViewModel extends GenericViewModel {

    //*[contains(@class,'ebComponentList-item')][normalize-space(text())='" + name + "']"

    @UiComponentMapping(selector = "//*[contains(@class,'ebBtn-caption')][text()='Delete Collection']", selectorType = SelectorType.XPATH)
    private UiComponent deleteSavedSearch;

    @UiComponentMapping(selector = "//*[contains(@class,'ebBtn-caption')][normalize-space(text())='Cancel']", selectorType = SelectorType.XPATH)
    private UiComponent cancelButton;

    public UiComponent getDeleteCollectionButton() {
        return deleteSavedSearch;
    }

    public UiComponent getCancelButton() {
        return cancelButton;
    }
}