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
import com.ericsson.nms.rv.taf.test.pmic.operators.*;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.pmic.PMInitiationViewModel;

public class AddNodesToAnActiveSubscriptionUiTest extends TorTestCaseHelper
        implements TestCase {

    private final Logger logger = LoggerFactory
            .getLogger(AddNodesToAnActiveSubscriptionUiTest.class);
    @Inject
    private PmicUiOperator pmicOperator;
    @Inject
    private CmCommandUiOperator cmOperator;
    @Inject
    private PmicTestCaseContext context;

    @BeforeTest
    @Parameters({ "pmic.active.add.nodes", "testId", "testTitle" })
    public void setup(final String fileName, final String testId,
            final String testTitle) {
        logger.debug("testId: {}, testTitle: {}, data file: {}", testId,
                testTitle, fileName);
        DataHandler.setAttribute("dataprovider.activate_ui_sub.location",
                fileName);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "activate_ui_sub")
    @Test
    public void testAddNodesToAnActiveSubscription(
            @Input("subName") final String subName,
            @Input("nodes") final List<String> nodes,
            @Input("command") final String command,
            @Input("expectedResultContains") final String expectedResultContains,
            @Input("waitTime") final int waitTime) {
        logger.debug("Adding nodes to PMIC Subscription: {}", subName);
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        PmicResponse response = assertStepIsTrue(pmicOperator
                .initialiseCurrentBrowserTab());
        final BrowserTab currentBrowserTab = response.getCurrentBrowserTab();
        response = assertStepIsTrue(pmicOperator
                .verifySubscriptionStatus(currentBrowserTab, subName,
                        PMInitiationViewModel.ACTIVE_STATUS));
        context.setTestContext(response, context.ACTIVATE_SUBSCRIPTION);
        response = assertStepIsTrue(pmicOperator
                .addNodesToSubscriptionViaNetworkExplorer(currentBrowserTab,
                        subName, nodes));
        context.setTestContext(response, context.ADD_NODE_ACTIVE_SUBSCRIPTION);

        // Check Pmic scanner info has updated using cli
        logger.info("Checking PMICScannerInfo has been updated through CliApp");
        final CmResponse cmResp = cmOperator.doCliCommand(command,
                expectedResultContains);
        assertTrue(String.format(
                "Failed while executing command: '%s'. Message: '%s'", command,
                cmResp.getErrorMessage()), cmResp.isSuccess());
        logger.info("PMICScannerInfo has been updated successfully");

    }

    private PmicResponse assertStepIsTrue(final PmicResponse pmicResponse) {
        assertTrue(String.format("%s", pmicResponse.getErrorMessage()),
                pmicResponse.isSuccess());
        return pmicResponse;
    }
}
