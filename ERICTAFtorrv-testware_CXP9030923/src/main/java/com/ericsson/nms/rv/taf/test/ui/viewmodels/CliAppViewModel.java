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
package com.ericsson.nms.rv.taf.test.ui.viewmodels;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;

public class CliAppViewModel extends GenericViewModel implements
        LoaderViewModel {

    @UiComponentMapping(id = "cliInput")
    private TextBox cliFormInput;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "//div[@class='eaCliApp-cliTextWidget-p'][last()]")
    private UiComponent simpleResult;

    @UiComponentMapping(".eaContainer-LoaderWidget")
    private UiComponent pageLoader;

    @UiComponentMapping(".eaCliApp-loadingWidget")
    private UiComponent appLoader;

    public TextBox getInput() {
        return cliFormInput;
    }

    /**
     * @return the ui component containing the final result summary text. E.g. 2
     *         instance(s)
     */
    public UiComponent getSimpleResult() {
        return simpleResult;
    }

    @Override
    public List<UiComponent> getLoadingWidgets() {
        final ArrayList<UiComponent> list = new ArrayList<UiComponent>();
        list.add(pageLoader);
        list.add(appLoader);
        return list;
    }
}
