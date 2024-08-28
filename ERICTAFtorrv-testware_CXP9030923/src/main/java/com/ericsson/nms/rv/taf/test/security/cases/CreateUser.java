package com.ericsson.nms.rv.taf.test.security.cases;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.nms.rv.taf.test.apache.operators.User;
import com.ericsson.nms.rv.taf.test.apache.operators.UserPool;
import com.ericsson.nms.rv.taf.test.security.operators.SecurityResponse;
import com.ericsson.nms.rv.taf.test.security.operators.SecurityRestOperator;

/**
 * Created by ewandaf on 20/06/14.
 */
public class CreateUser extends TorTestCaseHelper implements TestCase {
    private final Logger logger = LoggerFactory.getLogger(CreateUser.class);

    @Parameters({ "testId", "testTitle" })
    @Test
    public void testCreateAllUser(String testId, String testTitle) {
        setTestcase(testId, testTitle);
        boolean isAllSuccess = true;
        final SecurityRestOperator securityRestOperator = new SecurityRestOperator();
        final List<User> users = UserPool.getAllUsers();
        SecurityResponse securityResponse = null;

        securityResponse = securityRestOperator.createCookie();
        if (securityResponse.isSuccess()) {
            logger.info("Created openIDM cookie for administrator user.");
        } else {
            fail(String.format("Failed to create openIDM cookie for administrator user. Error: %s", securityResponse.getError()));
        }

        for (final User user : users) {
            securityResponse = securityRestOperator.addUser(user);
            if (securityResponse.isSuccess()) {
                logger.info("User: [" + user.getUserName()
                        + "] created successfully");
            } else {
                logger.error("User: [" + user.getUserName()
                        + "] not created successfully. "
                        + securityResponse.getError());
            }
            isAllSuccess = isAllSuccess && securityResponse.isSuccess();

            securityResponse = securityRestOperator.disablePasswordReset(user);
            if (securityResponse.isSuccess()) {
                logger.info("User: [" + user.getUserName()
                        + "] password reset disabled successfully");
            } else {
                logger.error("User: [" + user.getUserName()
                        + "] password reset was not disabled successfully. "
                        + securityResponse.getError());
            }
            isAllSuccess = isAllSuccess && securityResponse.isSuccess();

            securityResponse = securityRestOperator.setUserRole(user);
            if (securityResponse.isSuccess()) {
                logger.info("User: [" + user.getUserName()
                        + "] role [" + user.getUserRole() + "] set successfully");
            } else {
                logger.error("User: [" + user.getUserName()

                        + "] role [" + user.getUserRole() + "] is not set successfully. "

                        + securityResponse.getError());
            }
            isAllSuccess = isAllSuccess && securityResponse.isSuccess();
        }
        assertTrue("All users were not successfully created.", isAllSuccess);
    }
}