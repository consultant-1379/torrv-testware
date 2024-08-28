package com.ericsson.nms.rv.taf.test.security.operators;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.json.simple.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.rv.taf.test.apache.operators.User;

/**
 * Created by ewandaf on 20/06/14.
 */
@Operator(context = Context.REST)
public class SecurityRestOperator {

    private static final Logger logger = LoggerFactory
            .getLogger(SecurityRestOperator.class);
    private static final String X_OPEN_IDM_USERNAME_VALUE = (String) DataHandler
            .getAttribute("admin.username");
    private static final String X_OPEN_IDM_PASSWORD_VALUE = (String) DataHandler
            .getAttribute("admin.password");
    private static final String COOKIE_PATH = "/tmp/addUserCookieRv.txt";
    private static ThreadLocal<CLICommandHelper> cliCommandHelperThreadLocal = new ThreadLocal<>();
    private final ArrayList<String> userList = new ArrayList<String>();

    public SecurityResponse createCookie() {
        final CLICommandHelper cliCommandHelper = getCliCommandHelpler();

        final String command = "curl -c "
                + COOKIE_PATH
                + " --cacert /ericsson/tor/data/certificates/sso/ssoserverapache.crt -X POST \"https://"
                + HostConfigurator.getApache().getIp() + "/login?IDToken1="
                + X_OPEN_IDM_USERNAME_VALUE + "&IDToken2="
                + X_OPEN_IDM_PASSWORD_VALUE + "\"";

        String result = null;
        final SecurityResponse securityResponse = new SecurityResponse();

        try {
            logger.debug("Executing command: {}", command);
            result = cliCommandHelper.simpleExec(command);
        } catch (final Exception e) {
            securityResponse.setSuccess(false);
            securityResponse
                    .setError("Error thrown by CLICommandHelper. Cause:\n "
                            + e.getMessage());
            return securityResponse;
        }

        if (result != null && result.isEmpty()) {
            securityResponse.setSuccess(true);
        } else {
            securityResponse.setSuccess(false);
            securityResponse.setError(result);
        }
        return securityResponse;
    }

    public SecurityResponse addUser(final User user) {
        final CLICommandHelper cliCommandHelper = getCliCommandHelpler();
        final JSONObject jsonObject = buildUserJsonObject(user);
        final String data = jsonObject.toString();

        final String command = "curl --header \"X-Requested-With: XMLHttpRequest\" -b "
                + COOKIE_PATH
                + " --cacert /ericsson/tor/data/certificates/sso/ssoserverapache.crt"
                + " --request PUT --data '"
                + data.toString()
                + "' https://"
                + HostConfigurator.getApache().getIp()
                + "/openidm/managed/user/" + user.getUserName();

        String result = null;
        SecurityResponse securityResponse = null;

        try {
            logger.debug("Executing command add user: {}", command);
            result = cliCommandHelper.simpleExec(command);
        } catch (final Exception e) {
            securityResponse = new SecurityResponse();
            securityResponse.setSuccess(false);
            securityResponse
                    .setError("Error thrown by CLICommandHelper. Cause:\n "
                            + e.getMessage());
            return securityResponse;
        }
        final JSONObject jsonResult = (JSONObject) JSONValue.parse(result);
        securityResponse = new SecurityResponse(jsonResult);
        return securityResponse;
    }

    public SecurityResponse disablePasswordReset(final User user) {
        final CLICommandHelper cliCommandHelper = getCliCommandHelpler();

        final String command = "curl --header \"X-Requested-With: XMLHttpRequest\" --cookie "
                + COOKIE_PATH
                + " --cacert /ericsson/tor/data/certificates/sso/ssoserverapache.crt"
                + " --request POST -d '[\""
                + user.getUserName()
                + "\"]'"
                + " \"https://"
                + HostConfigurator.getApache().getIp()
                + "/openidm/endpoint/passwordReset?state=false\"";

        String result = null;
        SecurityResponse securityResponse = null;

        try {
            logger.debug("Executing command disable password reset: {}",
                    command);
            result = cliCommandHelper.simpleExec(command);
        } catch (final Exception e) {
            securityResponse = new SecurityResponse();
            securityResponse.setSuccess(false);
            securityResponse
                    .setError("Error thrown by CLICommandHelper. Cause:\n "
                            + e.getMessage());
            return securityResponse;
        }

        logger.debug("Result: {}", result);
        final Object resultObj = JSONValue.parse(result);
        JSONObject jsonResult = null;
        if (resultObj instanceof JSONArray) {
            jsonResult = (JSONObject) ((JSONArray) resultObj).get(0);
        } else if (resultObj instanceof JSONObject) {
            jsonResult = (JSONObject) resultObj;
        } else {
            securityResponse = new SecurityResponse();
            securityResponse.setSuccess(false);
            securityResponse
                    .setError("Disable password reset command did not return expected JSON format. Response:\n "
                            + result);
            return securityResponse;
        }
        securityResponse = new SecurityResponse(jsonResult);
        return securityResponse;
    }

    /**
     * Sets the user security role via the userRole field in the user object.
     *
     * @param user
     *            - user object with user to be set
     * @return SecurityResponse
     */
    public SecurityResponse setUserRole(final User user) {
        final CLICommandHelper cliCommandHelper = getCliCommandHelpler();
        final String command = "curl -s -m 30 -k -X GET --digest -H \"X-Requested-With: XMLHttpRequest\" -H \'X-Usernames: "
                + user.getUserName()
                + "\' --cookie "
                + COOKIE_PATH
                + " --cacert /ericsson/tor/data/certificates/sso/ssoserverapache.crt "
                + "'https://"
                + HostConfigurator.getApache().getIp()
                + "/openidm/endpoint/manageRole?action=adduser&rName="
                + user.getUserRole().getUserRoleValue() + "'";

        String result = null;
        final SecurityResponse securityResponse = new SecurityResponse();

        try {
            logger.debug("Executing command set admin role: {}", command);
            result = cliCommandHelper.simpleExec(command);
        } catch (final Exception e) {
            securityResponse.setSuccess(false);
            securityResponse
                    .setError("Error thrown by CLICommandHelper. Cause:\n "
                            + e.getMessage());
            return securityResponse;
        }
        if (result != null && !result.contains("error")) {
            securityResponse.setSuccess(true);
        } else {
            securityResponse.setSuccess(false);
            securityResponse.setError(result);
        }
        return securityResponse;
    }

    public SecurityResponse deleteUser(final String userName) {
        final CLICommandHelper cliCommandHelper = getCliCommandHelpler();

        final String command = "curl --header \"If-Match: \\\"*\\\"\" --header \"X-Requested-With: XMLHttpRequest\" -b "
                + COOKIE_PATH
                + " --cacert /ericsson/tor/data/certificates/sso/ssoserverapache.crt --request DELETE https://"
                + HostConfigurator.getApache().getIp()
                + "/openidm/managed/user/" + userName;

        String result = null;
        final SecurityResponse securityResponse = new SecurityResponse();

        try {
            logger.debug("Executing command delete user: {}", command);
            result = cliCommandHelper.simpleExec(command);
        } catch (final Exception e) {
            securityResponse.setSuccess(false);
            securityResponse
                    .setError("Error thrown by CLICommandHelper. Cause:\n "
                            + e.getMessage());
            return securityResponse;
        }

        if (result != null && result.isEmpty()) {
            securityResponse.setSuccess(true);
        } else {
            securityResponse.setSuccess(false);
            securityResponse.setError(result);
        }
        return securityResponse;
    }

    private CLICommandHelper getCliCommandHelpler() {
        CLICommandHelper cliCommandHelper = cliCommandHelperThreadLocal.get();

        if (HostConfigurator.isKvm()) {
            final Host host = HostConfigurator.getMS();
            if (cliCommandHelper == null) {
                if (host != null) {
                    cliCommandHelper = new CLICommandHelper(host);
                    cliCommandHelperThreadLocal.set(cliCommandHelper);
                }
            }
        } else {
            final Host host = HostConfigurator.getSC1();
            if (cliCommandHelper == null) {
                if (host != null) {
                    cliCommandHelper = new CLICommandHelper(host);
                    cliCommandHelperThreadLocal.set(cliCommandHelper);
                }
            }
        }

        return cliCommandHelper;
    }

    @SuppressWarnings("unchecked")
    private JSONObject buildUserJsonObject(final User user) {
        if (user == null) {
            return null;
        }
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("userName", user.getUserName());
        jsonObject.put("firstName", user.getFirstName());
        jsonObject.put("lastName", user.getLastName());
        jsonObject.put("password", user.getPassword());
        jsonObject.put("email", user.getEmail());
        jsonObject.put("status", user.getStatus().getUserStatus());
        jsonObject.put("userType", user.getUserType().getUserType());
        return jsonObject;
    }

    @SuppressWarnings("unchecked")
    private JSONObject buildRoleJsonObject(final User user) {
        if (user == null) {
            return null;
        }
        userList.add(user.get_id());
        final String users = StringUtils.join(userList, ',');
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("replace", "uniqueMember");
        jsonObject.put("value", users + ", administrator");
        return jsonObject;
    }
}
