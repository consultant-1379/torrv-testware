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
package com.ericsson.nms.rv.taf.test.pmic.operators;

import org.testng.SkipException;

import com.ericsson.cifwk.taf.TestContext;
import com.google.inject.Inject;

public class PmicTestCaseContext {
    public final String ADD_COLLECTION_VIA_NETWORK_EXPLORER = "addCollectionViaNetExp";
    public final String ADD_NODES_VIA_NETWORK_EXPLORER = "addNodesViaNetExp";
    public final String ADD_NODE_ACTIVE_SUBSCRIPTION = "addNodeToActiveSubscription";
    public final String ACTIVATE_SUBSCRIPTION = "activateSubscription";

    @Inject
    TestContext context;

    /**
     * @param response
     *            - object is passed in to verify if the test has passed or
     *            failed
     * @param testName
     *            - is used to map the specific test to true(passed) or
     *            false(failed)
     */
    public void setTestContext(PmicResponse response, String testName) {
        if (response.isSuccess()) {
            context.setAttribute(testName, true);
        } else {
            context.setAttribute(testName, false);
        }
    }

    /**
     * @param testName
     *            - is used to get the corresponding testcases result from the
     *            map. If no result (null) exists false is returned.
     */
    public boolean getTestContext(String testName) {
        if (context.getAttribute(testName) == null) {
            context.setAttribute(testName, false);
        }
        return context.getAttribute(testName);
    }

    /**
     * This is a check to see if nodes have been successfully added to the
     * subscription.
     *
     * @throws SkipException
     *             Thrown if nodes have not been added to the subscription. Will
     *             mark test which is being checked as Skipped in JCat report.
     */
    public void checkIfNodesHaveBeenAdded() throws SkipException {
        final boolean addCollectionPassed = getTestContext(ADD_COLLECTION_VIA_NETWORK_EXPLORER);
        final boolean addNodesPassed = getTestContext(ADD_NODES_VIA_NETWORK_EXPLORER);
        final boolean addNodeToActiveSubscriptionPassed = getTestContext(ADD_NODE_ACTIVE_SUBSCRIPTION);
        if (!addCollectionPassed && !addNodesPassed
                && !addNodeToActiveSubscriptionPassed) {
            throw new SkipException(
                    "No nodes have been added to the subscription. Therefore no files have been collected. Skipping this test step");
        }
    }

    /**
     * This is a check to see if the subscription has been successfully
     * activated.
     *
     * @throws SkipException
     *             Thrown if subscription has not been activated. Will mark test
     *             which is being checked as Skipped in JCat report.
     */
    public void verifySubscriptionIsActive() throws SkipException {
        final boolean subscriptionActivated = getTestContext(ACTIVATE_SUBSCRIPTION);
        if (!subscriptionActivated) {
            throw new SkipException(
                    "Subscription has not been activated. Therefore no files have been collected. Skipping this test step");
        }
    }
}
