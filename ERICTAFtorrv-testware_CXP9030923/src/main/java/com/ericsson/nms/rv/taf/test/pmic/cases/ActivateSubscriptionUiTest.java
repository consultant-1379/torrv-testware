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
import com.ericsson.nms.rv.taf.test.pmic.operators.*;

/**
 * Created by epaulki on 09/09/14.
 */
public class ActivateSubscriptionUiTest extends TorTestCaseHelper implements
        TestCase {
    private final Logger logger = LoggerFactory
            .getLogger(ActivateSubscriptionUiTest.class);

    @Inject
    private PmicUiOperator pmicOperator;
    @Inject
    private PmicTestCaseContext context;

    @BeforeTest
    @Parameters({ "pmic.activate.ui.subscription", "testId", "testTitle" })
    public void setup(final String fileName, final String testId,
            final String testTitle) {
        logger.info("testId:{}, testTitle: {}, dataFile: {}", testId,
                testTitle, fileName);
        DataHandler.setAttribute("dataprovider.activate_ui_sub.location",
                fileName);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "activate_ui_sub")
    @Test
    public void testActivateSub(@Input("subName") final String subName) {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        PmicResponse response = assertStepIsTrue(pmicOperator
                .initialiseCurrentBrowserTab());
        final BrowserTab currentBrowserTab = response.getCurrentBrowserTab();

        response = assertStepIsTrue(pmicOperator
                .activateStatisticalSubscription(currentBrowserTab, subName));
        context.setTestContext(response, context.ACTIVATE_SUBSCRIPTION);
    }

    public PmicResponse assertStepIsTrue(final PmicResponse pmicResponse) {
        assertTrue(String.format("%s", pmicResponse.getErrorMessage()),
                pmicResponse.isSuccess());
        return pmicResponse;
    }
}
