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
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.NetworkExplorerRestOperator;
import com.ericsson.nms.rv.taf.test.pmic.operators.PmicResponse;
import com.ericsson.nms.rv.taf.test.pmic.operators.PmicUiOperator;

/**
 * Created by ebioliu on 11/05/14.
 */
public class UpdateSubscriptionUiTest extends TorTestCaseHelper implements
        TestCase {

    private final Logger logger = LoggerFactory
            .getLogger(UpdateSubscriptionUiTest.class);

    @Inject
    private PmicUiOperator pmicOperator;

    @Inject
    NetworkExplorerRestOperator networkExplorerRest;

    @BeforeTest
    @Parameters({ "pmic.update.ui.subscription", "testId", "testTitle" })
    public void setup(final String fileName, final String testId,
            final String testTitle) {
        logger.debug("testId: {}, testTitle: {}, data file: {}", testId,
                testTitle, fileName);
        DataHandler.setAttribute("pmic.update.ui.subscription", fileName);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "update_ui_sub")
    @Test
    public void testUpdateSubscription(@Input("subName") final String subName,
            @Input("newDescription") final String newDescription,
            @Input("nodesToDelete") final List<String> nodesToDelete,
            @Input("collectionName") final String collection,
            @Input("nodesToAdd") final List<String> nodesToAdd,
            @Input("parentCounters") final List<String> parentCounters,
            @Input("subCounters") final List<String> subCounters,
            @Input("ropIntervals") final String ropInterval) {

        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        logger.debug("Update PMIC Subscription {} Description: {}", subName,
                newDescription);

        final PmicResponse response = assertStepIsTrue(pmicOperator
                .initialiseCurrentBrowserTab());
        final BrowserTab currentBrowserTab = response.getCurrentBrowserTab();
        final List<String> nodesInCollection = networkExplorerRest
                .getCollectionNodeList(collection);

        assertStepIsTrue(pmicOperator.updateSubscriptionDescription(
                currentBrowserTab, subName, newDescription));
        assertStepIsTrue(pmicOperator.deleteNodesFromSubscription(
                currentBrowserTab, subName, nodesToDelete));
        assertStepIsTrue(pmicOperator.modifySchedulerDetails(currentBrowserTab,
                subName, ropInterval));
        assertStepIsTrue(pmicOperator.addNodesToSubscriptionViaCollection(
                currentBrowserTab, subName, collection, nodesInCollection));
        assertStepIsTrue(pmicOperator.addNodesToSubscriptionViaNetworkExplorer(
                currentBrowserTab, subName, nodesToAdd));
        assertStepIsTrue(pmicOperator.addCountersToStatisticalSubscription(
                currentBrowserTab, subName, parentCounters, subCounters));
    }

    private PmicResponse assertStepIsTrue(final PmicResponse pmicResponse) {
        assertTrue(pmicResponse.getErrorMessage(), pmicResponse.isSuccess());
        return pmicResponse;
    }
}
