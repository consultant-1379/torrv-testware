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
package com.ericsson.nms.rv.taf.test.apache.cases;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.nms.rv.taf.test.apache.operators.*;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.ApacheResponse;

public class ApacheUiLoginLogoutTest extends TorTestCaseHelper implements
        TestCase {

    @Inject
    private ApacheUiOperator apacheOperator;

    private final Logger logger = org.slf4j.LoggerFactory
            .getLogger(ApacheUiLoginLogoutTest.class);

    @Test
    @Parameters({ "user.uid", "testId", "testTitle" })
    public void loginParamDriven(final String userUid, final String testId,
            final String testTitle) {

        setTestcase(testId, testTitle);
        final User user = UserPool.getUserByID(userUid);
        if (user == null) {
            logger.warn("Cannot find user with uid + [" + userUid + "]");
        }
        final String userId = user.getUserName();
        final String password = user.getPassword();
        final ApacheResponse response = apacheOperator.login(userId, password);
        if (!response.isSuccess()) {
            logger.warn("Failed to login with user '{}'. Message: '{}'",
                    userId, response.getErrorMessage());
        }
        assertTrue(response.isSuccess());
    }

    @Test
    @Parameters({ "user.uid", "testId", "testTitle" })
    public void logoutParamDriven(final String userUid, final String testId,
            final String testTitle) {

        setTestcase(testId, testTitle);
        final User user = UserPool.getUserByID(userUid);
        if (user == null) {
            logger.warn("Cannot find user with uid + [" + userUid + "]");
        }
        final String userId = user.getUserName();
        final ApacheResponse response = apacheOperator.logout();
        if (!response.isSuccess()) {
            logger.warn(
                    "Failed to log out with user '{}'. Status code '{}'. Message: '{}'",
                    userId, response.getStatusCode(),
                    response.getErrorMessage());
        }
        assertTrue(response.isSuccess());
    }
}
