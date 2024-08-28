package com.ericsson.nms.rv.taf.test.security.cases;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.nms.rv.taf.test.apache.operators.*;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.CsvParser;

/**
 * Created by ewandaf on 01/08/14.
 */
public class InitializeUserPool extends TorTestCaseHelper implements TestCase {
    private final Logger logger = LoggerFactory
            .getLogger(InitializeUserPool.class);

    @Parameters({ "security.csv.file", "testId", "testTitle" })
    @Test
    public void initializeUserPool(String fileName, String testId,
            String testTitle) {
        setTestcase(testId, testTitle);
        final boolean isAllSuccess = true;
        try {
            final CsvParser csvParser = new CsvParser(fileName);
            final List<Map<String, Object>> valueAsMaps = csvParser
                    .getValuesAsMaps();
            for (final Map<String, Object> map : valueAsMaps) {
                final User user = buildUser(map);
                UserPool.addUser((String) map.get("uid"), user);
                logger.info("User [" + user.getUserName()
                        + "] added to user pool");
            }
        } catch (final IOException e) {
            fail("Couldn't find file " + fileName);
            e.printStackTrace();
        }
        assertTrue("All users were not created successfully.", isAllSuccess);
    }

    private User buildUser(Map<String, Object> map) {
        final String userId = (String) map.get("uid");
        final String userName = (String) map.get("userName");
        final String firstName = (String) map.get("firstName");
        final String lastName = (String) map.get("lastName");
        final String password = (String) map.get("password");
        final String email = (String) map.get("email");
        final UserStatus userStatus = UserStatus.getFromString((String) map
                .get("userStatus"));
        final UserType userType = UserType.getFromString((String) map
                .get("userType"));
        final UserRole userRole = UserRole.valueOf((String) map.get("userRole"));
        return new User(userId, userName, firstName, lastName, password, email,
                userStatus, userType, userRole);
    }
}
