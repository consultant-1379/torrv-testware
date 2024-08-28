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
package com.ericsson.nms.rv.taf.test.pmic.cases;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.nms.rv.taf.test.pmic.operators.PmicResponse;
import com.ericsson.nms.rv.taf.test.pmic.operators.PmicUiOperator;

public class DeleteSubscriptionUiTest extends TorTestCaseHelper implements
        TestCase {

    private final Logger logger = LoggerFactory
            .getLogger(DeleteSubscriptionUiTest.class);

    @Inject
    private PmicUiOperator pmicOperator;

    @BeforeTest
    @Parameters({ "pmic.delete.ui.subscription", "testId", "testTitle" })
    public void setup(final String fileName, final String testId,
            final String testTitle) {
        logger.debug("testId: {}, testTitle: {}, data file: {}", testId,
                testTitle, fileName);
        DataHandler.setAttribute("dataprovider.delete_ui_sub.location",
                fileName);
        DataHandler.setAttribute("dataprovider.delete_ui_sub.type", "csv");
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "delete_ui_sub")
    @Test
    public void testDeleteSubscription(@Input("subName") final String subName) {
        logger.debug("Deleting PMIC Subscription: {}", subName);
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
        PmicResponse response = pmicOperator.initialiseCurrentBrowserTab();
        assertTrue(String.format("%s", response.getErrorMessage()),
                response.isSuccess());

        final BrowserTab currentBrowserTab = response.getCurrentBrowserTab();

        response = pmicOperator.deleteStatisticalSubscription(
                currentBrowserTab, subName);
        assertTrue(String.format("%s", response.getErrorMessage()),
                response.isSuccess());
    }
}
