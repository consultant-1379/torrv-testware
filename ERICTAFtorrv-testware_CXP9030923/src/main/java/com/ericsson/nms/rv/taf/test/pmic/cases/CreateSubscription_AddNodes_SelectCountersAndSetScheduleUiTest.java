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
import com.ericsson.nms.rv.taf.test.pmic.operators.*;

public class CreateSubscription_AddNodes_SelectCountersAndSetScheduleUiTest
        extends TorTestCaseHelper implements TestCase {

    private final Logger logger = LoggerFactory
            .getLogger(CreateSubscription_AddNodes_SelectCountersAndSetScheduleUiTest.class);

    @Inject
    private PmicUiOperator pmicOperator;

    @Inject
    private NetworkExplorerRestOperator networkExplorerRest;

    @Inject
    private PmicTestCaseContext context;

    @BeforeTest
    @Parameters({ "pmic.create.ui.subscription", "testId", "testTitle" })
    public void setup(final String fileName, final String testId,
            final String testTitle) {
        logger.debug("testId: {}, testTitle: {}, data file: {}", testId,
                testTitle, fileName);
        DataHandler.setAttribute("pmic.create.ui.subscription", fileName);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "create_ui_sub")
    @Test
    public void testCreateSubscription(@Input("subName") final String subName,
            @Input("description") final String description,
            @Input("collectionName") final String collection,
            @Input("nodes") final List<String> nodes,
            @Input("parentCounters") final List<String> parentCounters,
            @Input("subCounters") final List<String> subCounters,
            @Input("ropInterval") final String ropInterval) {

        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        logger.info("Creating PMIC Subscription: {}, Desc: {}", subName,
                description);

        final List<String> nodesInCollection = networkExplorerRest
                .getCollectionNodeList(collection);

        PmicResponse response = assertStepIsTrue(pmicOperator
                .initialiseCurrentBrowserTab());
        final BrowserTab currentBrowserTab = response.getCurrentBrowserTab();

        assertStepIsTrue(pmicOperator.createStatisticalSubscription(
                currentBrowserTab, subName, description));

        response = assertStepIsTrue(pmicOperator
                .addNodesToSubscriptionViaCollection(currentBrowserTab,
                        subName, collection, nodesInCollection));

        context.setTestContext(response,
                context.ADD_COLLECTION_VIA_NETWORK_EXPLORER);

        response = assertStepIsTrue(pmicOperator
                .addNodesToSubscriptionViaNetworkExplorer(currentBrowserTab,
                        subName, nodes));
        context.setTestContext(response, context.ADD_NODES_VIA_NETWORK_EXPLORER);

        assertStepIsTrue(pmicOperator.addCountersToStatisticalSubscription(
                currentBrowserTab, subName, parentCounters, subCounters));

        assertStepIsTrue(pmicOperator.modifyRopSchedulerDetails(
                currentBrowserTab, subName, ropInterval));
    }

    private PmicResponse assertStepIsTrue(final PmicResponse pmicResponse) {
        if (!pmicResponse.isSuccess()) {
            logger.warn("Assert fail: {}", pmicResponse.getErrorMessage());
        }
        assertTrue(String.format("%s", pmicResponse.getErrorMessage()),
                pmicResponse.isSuccess());
        return pmicResponse;
    }
}
