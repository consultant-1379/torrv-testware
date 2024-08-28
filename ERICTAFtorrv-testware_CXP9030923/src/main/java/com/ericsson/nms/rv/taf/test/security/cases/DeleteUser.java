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
public class DeleteUser extends TorTestCaseHelper implements TestCase {
    private final Logger logger = LoggerFactory.getLogger(DeleteUser.class);

    @Parameters({ "testId", "testTitle" })
    @Test
    public void testDeleteAllUser(String testId, String testTitle) {
        setTestcase(testId, testTitle);
        boolean isAllSuccess = true;
        final SecurityRestOperator securityRestOperator = new SecurityRestOperator();
        SecurityResponse securityResponse = securityRestOperator.createCookie();

        if (securityResponse.isSuccess()) {
            logger.info("Created cookie for administrator user.");
        } else {
            fail(String.format("Failed to create openIDM cookie for administrator user. Error: %s", securityResponse.getError()));
        }

        final List<User> users = UserPool.getAllUsers();
        for (final User user : users) {
            securityResponse = securityRestOperator.deleteUser(user
                    .getUserName());
            if (securityResponse.isSuccess()) {
                logger.info("User: [" + user.getUserName()
                        + "] deleted successfully");
            } else {
                logger.error("User: [" + user.getUserName()
                        + "] not deleted successfully. "
                        + securityResponse.getError());
            }
            isAllSuccess = isAllSuccess && securityResponse.isSuccess();
        }
        assertTrue("All users were not deleted successfully", isAllSuccess);
    }
}
