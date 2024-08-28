package com.ericsson.nms.rv.taf.test.monitoring.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.nms.rv.taf.test.monitoring.cases.dto.MonitoringResponse;

@Operator(context = Context.CLI)
public class MonitoringCliOperator {

    private static Logger logger = LoggerFactory
            .getLogger(MonitoringCliOperator.class);
    private static ThreadLocal<CLICommandHelper> cliCommandHelperThreadLocal = new ThreadLocal<>();
    private String currentServerType;
    private CLICommandHelper cliCommandHelper;

    public CLICommandHelper getCliCommandHelpler(String serverType) {

        if (currentServerType == null) {
            currentServerType = serverType;
        }

        if (currentServerType.equals(serverType)) {
            cliCommandHelper = cliCommandHelperThreadLocal.get();
        }

        if (cliCommandHelper == null || !currentServerType.equals(serverType)) {
            currentServerType = serverType;
            final Host host = HostHelper.getHostByName(serverType);
            if (host != null) {
                cliCommandHelper = new CLICommandHelper(host);
                cliCommandHelperThreadLocal.set(cliCommandHelper);
            }
        }
        return cliCommandHelper;
    }

    /**
     * Execute a command via ssh on the server provided
     *
     * @param serverType
     *            - ms1,sc1,sc2
     * @return MonitoringResponse
     */
    public MonitoringResponse executeCliCommand(String serverType,
            String command, String expectedOut) {

        logger.info("Executing command on server {}: {}. Expected output: {}",
                serverType, command, expectedOut);

        final MonitoringResponse retResp = new MonitoringResponse();
        final CLICommandHelper cliCommandHelper = getCliCommandHelpler(serverType);

        String response;

        if (cliCommandHelper == null) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String.format(
                    "User was not able to log in to %s", serverType));
            return retResp;
        }

        response = cliCommandHelper.simpleExec(command).trim();

        if (!response.contains(expectedOut)) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String
                    .format("Command '%s', did not contain expected output '%s' on server '%s'",
                            command, expectedOut, serverType));
            closeCliCommandHelper(cliCommandHelper);
            return retResp;
        }

        retResp.setOutput(response);
        retResp.setSuccess(true);
        closeCliCommandHelper(cliCommandHelper);
        return retResp;
    }

    /**
     * Execute a command via ssh on the server provided
     *
     * @param serverType
     *            - ms1,sc1,sc2
     * @return MonitoringResponse
     */
    public MonitoringResponse executeCliCommand(String serverType,
            String command) {

        logger.info("Executing command {} on server {}", command, serverType);

        final MonitoringResponse retResp = new MonitoringResponse();
        final CLICommandHelper cliCommandHelper = getCliCommandHelpler(serverType);

        String response;

        if (cliCommandHelper == null) {
            retResp.setSuccess(false);
            retResp.setErrorMessage("User was not able to log in to "
                    + serverType);
            return retResp;
        }

        response = cliCommandHelper.simpleExec(command).trim();
        if (response == null) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(String
                    .format("Command '%s' did not execute on '%s'", command,
                            serverType));
            closeCliCommandHelper(cliCommandHelper);
            return retResp;
        }

        retResp.setOutput(response);
        retResp.setSuccess(true);
        closeCliCommandHelper(cliCommandHelper);
        return retResp;
    }

    private void closeCliCommandHelper(CLICommandHelper cliCommandHelper) {
        cliCommandHelper.closeAndValidateShell();
        cliCommandHelper.disconnect();
    }

    /**
     * Method to exit shell session, ensure it's closed and remove the local
     * thread.
     */
    public void resetCommandHelper() {
        cliCommandHelper.simpleExec("exit");
        cliCommandHelper.expectShellClosure(5);
        cliCommandHelper.isClosed();
        cliCommandHelperThreadLocal.remove();
    }
}