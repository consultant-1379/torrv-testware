package com.ericsson.nms.rv.taf.test.stkpi.cases;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheUiOperator;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.ApacheResponse;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.CmResponse;
import com.ericsson.nms.rv.taf.test.stkpi.cases.operators.StKpiOperator;
import com.ericsson.nms.rv.taf.test.stkpi.cases.operators.StKpiResponse;

/**
 * Created by ejocott on 27/02/2015.
 */
public class StKpiPMTest extends TorTestCaseHelper implements TestCase {

    private static final Logger logger = LoggerFactory
            .getLogger(StKpiPMTest.class);

    private static final int KPI_REQUIRED_NODE_COUNT = 100;

    @Inject
    private ApacheUiOperator apacheOperator;

    private final String ADMIN_USERNAME = (String) DataHandler
            .getAttribute("admin.username");
    private final String ADMIN_PASSWORD = (String) DataHandler
            .getAttribute("admin.password");

    @Inject
    private StKpiOperator operator;

    @Test
    public void pmNorthBoundCounters() {
        setTestcase("TORRV-ZEPHYR-TC-482",
                "ST_STKPI_PM_01 LTE Counters Northbound TORF-9466");

        final ApacheResponse apacheResp = apacheOperator.login(ADMIN_USERNAME,
                ADMIN_PASSWORD);
        if (!apacheResp.isSuccess()) {
            throw new SkipException(apacheResp.getErrorMessage());
        }

        logger.info(
                "Verifying if {} eNodeBs are subscribed and active in CM cli",
                KPI_REQUIRED_NODE_COUNT);

        final CmResponse cmResponse = operator.getActiveSubscribedNodesCount();

        assertTrue(
                String.format(
                        "There is an insufficient amount of subscribed active nodes in CM cli: Required %s Found %s",
                        KPI_REQUIRED_NODE_COUNT, cmResponse.getCount()),
                cmResponse.getCount() >= KPI_REQUIRED_NODE_COUNT);

        logger.info("Found {} eNodeB instances in CM cli ",
                cmResponse.getCount());

        logger.info(
                "Verifying if {} eNodeBs have collected a ROP in the last 15 minutes ",
                KPI_REQUIRED_NODE_COUNT);
        final StKpiResponse cliResponse = operator
                .verifyPmRopCollected(KPI_REQUIRED_NODE_COUNT);
        assertTrue(cliResponse.getErrorMessages().toString(),
                cliResponse.isSuccess());

        printOverallResults(KPI_REQUIRED_NODE_COUNT, cmResponse.getCount(),
                cliResponse.getSyncedNodes(), cliResponse.getTotalNodes());
    }

    private void printOverallResults(final int kpiRequiredNodeCount,
            final int activeSubscribedNodeCount, final int nodesCollectingROP,
            final int totalNodes) {
        logger.info("===================================================");
        logger.info("Overall PM LTE Counters Northbound KPI Results");
        logger.info("-------------------------------------------");
        logger.info("KPI Required Node Count Set At \t\t\t\t : {}",
                kpiRequiredNodeCount);
        logger.info("Total Active Subscribed Nodes \t\t\t\t : {}",
                activeSubscribedNodeCount);
        logger.info("Total Nodes with counters in ENM \t\t\t\t : {}",
                totalNodes);
        logger.info(
                "Total Nodes Collecting ROP in the last 15 minutes \t\t : {}",
                nodesCollectingROP);
        logger.info(
                "Total Nodes Not Collecting ROP in the last 15 minutes \t : {}",
                totalNodes - nodesCollectingROP);
        logger.info("===================================================");
    }
}