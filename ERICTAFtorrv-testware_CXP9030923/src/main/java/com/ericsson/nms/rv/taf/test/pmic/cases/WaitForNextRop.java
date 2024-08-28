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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.pmic.operators.*;
import com.google.inject.Inject;

public class WaitForNextRop extends TorTestCaseHelper implements TestCase {
    private final Logger logger = LoggerFactory.getLogger(WaitForNextRop.class);

    @Inject
    private PmicCliOperator pmicCliOperator;
    @Inject
    private PmicTestCaseContext context;

    @BeforeTest
    @Parameters({ "pmic.ropInterval.subscription", "testId", "testTitle" })
    public void setup(final String fileName, final String testId,
            final String testTitle) {
        logger.info("testId:{}, testTitle: {}", testId, testTitle);
        DataHandler
                .setAttribute("dataprovider.rop_interval.location", fileName);
        DataHandler.setAttribute("dataprovider.rop_interval.type", "csv");
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "rop_interval")
    @Test
    public void waitUntilNextRopBegins(
            @Input("ropInterval") final int ropInterval) {
        logger.debug("Waiting for next rop to begin.");
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
        context.checkIfNodesHaveBeenAdded();
        context.verifySubscriptionIsActive();
        final PmicResponse filesCollectedResponse = pmicCliOperator
                .waitUntilNextRop(ropInterval);
        assertTrue(String.format("Unable to wait until next ROP: '%s'",
                filesCollectedResponse.getErrorMessage()),
                filesCollectedResponse.isSuccess());
    }

}