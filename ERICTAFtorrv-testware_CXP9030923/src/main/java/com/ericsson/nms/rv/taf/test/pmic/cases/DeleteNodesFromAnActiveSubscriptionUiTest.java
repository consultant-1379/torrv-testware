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

import java.util.List;

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
import com.ericsson.nms.rv.taf.test.cmapache.operators.CmCommandUiOperator;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.CmResponse;
import com.ericsson.nms.rv.taf.test.pmic.operators.PmicResponse;
import com.ericsson.nms.rv.taf.test.pmic.operators.PmicUiOperator;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.pmic.PMInitiationViewModel;

public class DeleteNodesFromAnActiveSubscriptionUiTest extends
        TorTestCaseHelper implements TestCase {
    private final Logger logger = LoggerFactory
            .getLogger(DeleteNodesFromAnActiveSubscriptionUiTest.class);

    @Inject
    private PmicUiOperator pmicOperator;
    @Inject
    private CmCommandUiOperator cmOperator;

    @BeforeTest
    @Parameters({ "pmic.delete.node", "testId", "testTitle" })
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
    public void testDeleteNodesFromAnActiveSubscription(
            @Input("subName") final String subName,
            @Input("nodes") final List<String> nodes,
            @Input("command") final String command,
            @Input("expectedResultContainsDelete") final String expectedResultContains) {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
        logger.debug("Deleteing nodes in PMIC Subscription: {}", subName);
        final PmicResponse deleteResponse = assertStepIsTrue(pmicOperator
                .initialiseCurrentBrowserTab());
        final BrowserTab currentBrowserTab = deleteResponse
                .getCurrentBrowserTab();
        assertStepIsTrue(pmicOperator
                .verifySubscriptionStatus(currentBrowserTab, subName,
                        PMInitiationViewModel.ACTIVE_STATUS));
        assertStepIsTrue(pmicOperator.deleteNodesFromSubscription(
                currentBrowserTab, subName, nodes));

        // Check Pmic scanner info has updated using cli
        final CmResponse cmResp = cmOperator.doCliCommand(command,
                expectedResultContains);
        assertTrue(String.format(
                "Failed while executing command: '%s'. Message: '%s'", command,
                cmResp.getErrorMessage()), cmResp.isSuccess());
    }

    private PmicResponse assertStepIsTrue(final PmicResponse pmicResponse) {
        assertTrue(String.format("%s", pmicResponse.getErrorMessage()),
                pmicResponse.isSuccess());
        return pmicResponse;
    }

}
