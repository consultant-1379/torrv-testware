package com.ericsson.nms.rv.taf.test.monitoring.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;

/**
 * Created by ewandaf on 08/09/14.
 */
@Operator(context = Context.CLI)
public class SfsCliOperator {

    private static CLICommandHelper ms1ToSfs;
    private static Logger logger = LoggerFactory
            .getLogger(SfsCliOperator.class);

    private static CLICommandHelper getCLICommandHelper(String hostName) {
        final Host host = HostHelper.getHostByName(hostName);
        return new CLICommandHelper(host);
    }

    public void openShell() {
        ms1ToSfs = getCLICommandHelper("ms1");
        ms1ToSfs.openShell(); // Open a shell instance
        ms1ToSfs.runInteractiveScript("su - storadm"); // Execute the script
                                                       // test.sh
        ms1ToSfs.expect("$");
        ms1ToSfs.interactWithShell("ssh support@nasconsole");
        ms1ToSfs.expect("#");
    }

    public boolean isClosed() {
        return ms1ToSfs.isClosed();
    }

    public int closeAndValidateShell() {
        return ms1ToSfs.closeAndValidateShell();
    }

    public void expectShellClosure() {
        ms1ToSfs.expectShellClosure();
    }

    public int getShellExitValue() {
        return ms1ToSfs.getShellExitValue();
    }

    public int getCommandExitValue() {
        return ms1ToSfs.getCommandExitValue();
    }

    public void execute(String cmd) {
        ms1ToSfs.interactWithShell(cmd);
    }

    public String simpleExec(String command) {
        if (ms1ToSfs == null) {
            throw new IllegalArgumentException(
                    "Shell connected to SFS is not created");
        }
        ms1ToSfs.interactWithShell(command);
        ms1ToSfs.expect("#");
        final String stdOut = ms1ToSfs.getStdOut();
        final String[] stdOutArray = stdOut.split("\n");
        String toReturn = "";
        for (int i = 1; i < stdOutArray.length - 1; i++) {
            toReturn += stdOutArray[i] + "\n";
        }
        return toReturn;
    }

    public boolean executeCliCommands(String cmd, String expectedResult) {
        return this.simpleExec(cmd).contains(expectedResult);
    }

    public boolean executeCliCommands(String cmd) {
        this.simpleExec(cmd);
        final boolean toReturn = this.getCommandExitValue() == 0 ? true : false;
        return toReturn;
    }

    public boolean sshToTheOtherNode() {
        String hostname = this.simpleExec("hostname").trim();
        final int lastIndex = hostname.length() - 1;
        int i = (hostname.charAt(lastIndex)) - '0';
        i = (i == 1) ? ++i : --i;
        hostname = hostname.substring(0, lastIndex) + i;
        this.simpleExec("ssh " + hostname);
        final boolean toReturn = this.getCommandExitValue() == 0 ? true : false;
        return toReturn;
    }

    public boolean updateCamIP(String ip) {
        final String properties = "/etc/hyperic/agent/agent.properties";
        final String statusCommand = "sed -n '/agent.setup.camIP/p' "
                + properties;
        logger.info(
                "Updating agent.setup.camIP, executing status command : {}",
                statusCommand);
        final String before = this.simpleExec(statusCommand);
        logger.info("The agent.setup.camIP value before update: {}", before);

        final String modCommand = "sed -i '/agent.setup.camIP=/s/\\(^.*=\\).*/\\1"
                + ip + "/g' " + properties;
        logger.info("Updating agent.setup.camIP, executing modify command: {}",
                modCommand);
        final boolean toReturn = this.executeCliCommands(modCommand);

        logger.info(
                "Updating agent.setup.camIP, executing status command : {}",
                statusCommand);
        final String after = this.simpleExec(statusCommand);
        logger.info("The agent.setup.camIP value after update {}", after);
        return toReturn;
    }

    public void restartHypericAgent() {
        logger.info("Restarting Hyperic Agent");
        this.execute("service hyperic-agent restart");
    }

    public boolean isHypericAgentRunning() {
        return this.simpleExec("service hyperic-agent status").contains("HQ Agent is running");
    }

    public String getHostName() {
        return this.simpleExec("hostname");
    }

    public boolean removeDataDirectory() {
        return this.executeCliCommands("rm -f /opt/hyperic/hyperic-hqee-agent/data/*");
    }

    public boolean stopHypericAgent() {
        return this.executeCliCommands("service hyperic-agent stop");
    }

    public boolean startHypericAgent() {
        return this.executeCliCommands("service hyperic-agent start");
    }
}
