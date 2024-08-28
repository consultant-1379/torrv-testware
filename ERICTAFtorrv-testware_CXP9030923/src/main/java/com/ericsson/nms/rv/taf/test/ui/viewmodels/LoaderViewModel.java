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

import java.util.List;

import com.ericsson.cifwk.taf.ui.core.UiComponent;

public interface LoaderViewModel {

    /**
     * ENM pages can have multiple loaders with different names. Provide a list
     * of loader widgets.
     *
     * @return A non null list of 0 or more loader components.
     */
    List<UiComponent> getLoadingWidgets();
}
