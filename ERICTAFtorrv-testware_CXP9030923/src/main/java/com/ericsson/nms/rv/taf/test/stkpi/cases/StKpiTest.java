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
package com.ericsson.nms.rv.taf.test.stkpi.cases;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.nms.rv.taf.test.stkpi.cases.operators.StKpiOperator;
import com.ericsson.nms.rv.taf.test.stkpi.cases.operators.StKpiResponse;

public class StKpiTest extends TorTestCaseHelper implements TestCase {
    private static final Logger logger = LoggerFactory
            .getLogger(StKpiTest.class);

    private static int SYNC_TIME_SECONDS = 30;

    @Inject
    private StKpiOperator operator;

    @Test
    public void singleNodeSync() {
        setTestcase(
                "TORRV-ZEPHYR-TC-484",
                String.format(
                        "ST_STKPI_CM_Synch_01 LTE ENodeB Synch TORF-889. For each node, the time taken for the Topology and Attribute Sync is less than %d seconds.",
                        SYNC_TIME_SECONDS));

        // Skip test if response file does not exist.
        StKpiResponse response = operator.verifyResponseFileExists();
        if (!response.isSuccess()) {
            logger.warn("Result file created by Network Sync Test does not exist. That file is the input for this test.");
            throw new SkipException(
                    "Result file created by Network Sync Test does not exist. That file is the input for this test.");
        }

        response = operator.readResponseFromFile();
        String errorMessage = "";
        if (!response.isSuccess()) {
            errorMessage += "Failed to get a valid response from the sync command via node_populator.\n"
                    + "Errors:\n";
            for (final String message : response.getErrorMessages()) {
                errorMessage += message + "\n";
            }
            errorMessage += "Command Output:\n";
            errorMessage += response.getOutput();
        }

        boolean allNodesSynced = false;
        // Prevent divide by zero error
        if (response.getTotalNodes() > 0) {
            allNodesSynced = (response.getSyncedNodes() / response
                    .getTotalNodes()) == 1;
        }

        logger.info("Assert that all nodes have synced successfully");
        saveAssertTrue(
                String.format(
                        "Some nodes were not synchronized. %d of %d nodes successfully synchronized.",
                        response.getSyncedNodes(), response.getTotalNodes()),
                allNodesSynced);

        logger.info("Assert that each nodes has synced within {} seconds.",
                SYNC_TIME_SECONDS);
        assertTrue(errorMessage, response.isSuccess());
        assertTrue(String.format(
                "The max sync time taken is greater than %s seconds.",
                SYNC_TIME_SECONDS),
                response.getMaxSyncTime() <= SYNC_TIME_SECONDS);
    }

}
