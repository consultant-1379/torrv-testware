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
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.stkpi.cases.operators.StKpiOperator;
import com.ericsson.nms.rv.taf.test.stkpi.cases.operators.StKpiResponse;

public class NetworkSyncTest extends TorTestCaseHelper implements TestCase {

    private static final Logger logger = LoggerFactory
            .getLogger(NetworkSyncTest.class);

    private static final String identifier = (String) DataHandler
            .getAttribute("stkpi.network.sync.identifier");
    private static final String range = (String) DataHandler
            .getAttribute("stkpi.network.sync.range");
    private static final String expectedTimeSecondsStr = (String) DataHandler
            .getAttribute("stkpi.network.sync.max.time.seconds");

    @Inject
    private StKpiOperator operator;

    @Test
    public void networkSyncTest() {

        setTestcase("TORRV-ZEPHYR-TC-485",
                "ST_STKPI_CM_Synch_02 LTE Network Synch TORF-889");
        logger.debug(
                "Executing test: NetworkSyncTest. identifier: '{}', range: '{}', expectedTimeSeconds: '{}'",
                identifier, range, expectedTimeSecondsStr);

        final StKpiResponse response = operator
                .doNetworkSync(identifier, range);

        int expectedTimeSeconds = 0;
        try {
            expectedTimeSeconds = Integer.parseInt(expectedTimeSecondsStr);
        } catch (final NumberFormatException e) {
            fail("Configuration value stkpi.network.sync.max.time.seconds in file stkpi.properties is not a number.");
        }

        /*
         * Assertions: 1. The sync command executed. 2. All nodes were synced.
         * 3. All nodes were synced before the threshold.
         */

        // Log errors that occurred while calling the sync command
        logger.info("Asserting that the node_populator command completed successfully...");
        String errorMessage = "";
        if (!response.isSuccess()) {
            errorMessage += "Failed to get a valid response from the sync command via node_populator.\n"
                    + "Errors:\n";
            for (final String message : response.getErrorMessages()) {
                errorMessage += message + "\n";
            }
            errorMessage += "Command Output:\n";
            errorMessage += response.getOutput();
            errorMessage += "\n";
        } else {
            logger.info("node_populator command completed successfully.");
        }
        assertTrue(errorMessage, response.isSuccess());

        operator.writeResponseToFile(response);

        boolean allNodesSynced = false;
        // Prevent divide by zero error
        if (response.getTotalNodes() > 0) {
            allNodesSynced = (response.getSyncedNodes() / response
                    .getTotalNodes()) == 1;
        }
        final boolean allNodesSyncedOnTime = response.getNetworkSyncTimeSecs() <= expectedTimeSeconds;

        logger.info("Asserting that all nodes were synchronized...");
        assertTrue(
                String.format(
                        "Some nodes were not synchronized. %d of %d nodes successfully synchronized.",
                        response.getSyncedNodes(), response.getTotalNodes()),
                allNodesSynced);
        logger.info("All nodes were synchronized.");

        logger.info(
                "Asserting that all nodes were synchronized within {} seconds...",
                expectedTimeSeconds);
        assertTrue(String.format(
                "Network sync did not complete within the given time. "
                        + "Expected final time in seconds: %d. "
                        + "Actual time in seconds: %d. "
                        + "Overrun in seconds: %d", expectedTimeSeconds,
                response.getNetworkSyncTimeSecs(),
                response.getNetworkSyncTimeSecs() - expectedTimeSeconds),
                allNodesSyncedOnTime);
        logger.info("All nodes were synchronized within {} seconds...",
                expectedTimeSeconds);
    }
}