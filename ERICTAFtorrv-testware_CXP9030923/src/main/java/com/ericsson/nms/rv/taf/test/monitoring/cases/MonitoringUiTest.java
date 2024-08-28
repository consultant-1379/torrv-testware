package com.ericsson.nms.rv.taf.test.monitoring.cases;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.execution.TestExecutionEvent;
import com.ericsson.cifwk.taf.ui.UI;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.rv.taf.test.monitoring.cases.dto.MonitoringResponse;
import com.ericsson.nms.rv.taf.test.monitoring.operators.*;
import com.google.inject.Inject;

/**
 * Created by ejocott on 20/08/2014.
 */
public class MonitoringUiTest extends TorTestCaseHelper implements TestCase {

    private static final String TEST_ID = "testId";
    private static final String TEST_TITLE = "testTitle";

    @BeforeSuite
    public void setWindowClosingPolicy() {
        UI.closeWindow(TestExecutionEvent.ON_SUITE_FINISH);
    }

    @BeforeTest
    @Parameters({ TEST_ID, TEST_TITLE })
    public void setup(final String testId, final String testTitle) {
        DataHandler.setAttribute(TEST_ID, testId);
        DataHandler.setAttribute(TEST_TITLE, testTitle);
    }

    @Inject
    private MonitoringUiOperator monitoringUiOperator;
    @Inject
    private MonitoringCliOperator monitoringCliOperator;
    @Inject
    private SfsCliOperator sfsCliOperator;
    private final Logger logger = LoggerFactory
            .getLogger(MonitoringUiTest.class);

    @Test
    public void createTestUsers() {

        setTestcase(DataHandler.getAttribute(TEST_ID).toString(), DataHandler
                .getAttribute(TEST_TITLE).toString());
        MonitoringResponse monitoringResponse = monitoringCliOperator
                .executeCliCommand("ms1", "hostname");
        final String serverHostname = monitoringResponse.getOutput().trim();

        monitoringResponse = monitoringUiOperator.secureLogin("hqadmin",
                "hqadmin");
        assertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        monitoringResponse = monitoringUiOperator.addNewUser("TestUser",
                "Test", "User", "12T3St", "root@" + serverHostname
                        + ".localdomain", "Super User");
        saveAssertTrue("Could not add user", monitoringResponse.getOutput()
                .contains("The requested roles have been assigned to the user"));

        monitoringResponse = monitoringUiOperator.addNewUser(
                "ChangePWTestUser", "ChangePWTest", "ChangeUser", "12T3St",
                "test@test.com", "Super User");
        saveAssertTrue("Could not add user", monitoringResponse.getOutput()
                .contains("The requested roles have been assigned to the user"));

        monitoringResponse = monitoringUiOperator.logout();
        assertTrue("Failed to log out of secure login. Message: {}",
                monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());
    }

    @Test
    public void testSecureAndInsecureLogin() {
        setTestcase(DataHandler.getAttribute(TEST_ID).toString(), DataHandler
                .getAttribute(TEST_TITLE).toString());

        MonitoringResponse monitoringResponse = monitoringUiOperator
                .secureLogin("hqadmin", "hqadmin");
        assertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        final MonitoringResponse logoutResponse = monitoringUiOperator.logout();
        assertTrue(logoutResponse.getErrorMessage(), logoutResponse.isSuccess());

        monitoringResponse = monitoringUiOperator.unsecureLogin("hqadmin",
                "hqadmin");
        assertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        monitoringResponse = monitoringUiOperator.logout();
        assertTrue("Failed to log out of unsecure login. Message: {}",
                monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());
    }

    @Test
    public void testCreateJbossMonitoringAlert() {
        setTestcase(DataHandler.getAttribute(TEST_ID).toString(), DataHandler
                .getAttribute(TEST_TITLE).toString());

        final MonitoringResponse loginResponse = monitoringUiOperator
                .secureLogin("hqadmin", "hqadmin");
        assertTrue(loginResponse.getErrorMessage(), loginResponse.isSuccess());

        monitoringUiOperator.openAlertDefinitionConfiguration("Servers",
                "CMServ_su_0_jee_cfg");
        monitoringUiOperator.setNewAlertConfiguration("testAlert",
                "Test Alert Description", "Availability", 100, "<");
        List<String> alertList = monitoringUiOperator.getListOfAlertNames(
                "Servers", "CMServ_su_0_jee_cfg");

        logger.info("Assert that testAlert exists.");
        saveAssertTrue("Alert list did not contain testAlert",
                alertList.contains("testAlert"));

        monitoringUiOperator.deleteAlert("Servers", "CMServ_su_0_jee_cfg",
                "testAlert");
        alertList = monitoringUiOperator.getListOfAlertNames("Servers",
                "CMServ_su_0_jee_cfg");

        logger.info("Assert that testAlert was deleted.");
        saveAssertFalse("testAlert was not deleted successfully",
                alertList.contains("testAlert"));

        final MonitoringResponse logoutResponse = monitoringUiOperator.logout();
        assertTrue(logoutResponse.getErrorMessage(), logoutResponse.isSuccess());
    }

    @Test
    public void testLinkEmailAddressToAlert() {
        setTestcase(DataHandler.getAttribute(TEST_ID).toString(), DataHandler
                .getAttribute(TEST_TITLE).toString());

        final MonitoringResponse hostnameResponse = monitoringCliOperator
                .executeCliCommand("ms1", "hostname");
        final String serverHostname = hostnameResponse.getOutput().trim();

        final MonitoringResponse loginResponse = monitoringUiOperator
                .secureLogin("hqadmin", "hqadmin");
        assertTrue(loginResponse.getErrorMessage(), loginResponse.isSuccess());

        final MonitoringResponse uiResp = monitoringUiOperator.linkUserToAlert(
                "TestUser", "Cobbler Availability = 0%", "Services",
                serverHostname + " cobblerd");
        saveAssertTrue(
                String.format("Failed to add user 'TestUser' to Monitoring Tool"),
                uiResp.getOutput().contains("Successfully added users"));
        monitoringUiOperator.logout();
        final String countEmailAlertsCommand = "grep \"Subject: \\[OSS-MT\\] \\!\\!\\! - Cobbler Availability = 0% "
                + serverHostname
                + " cobblerd\" /var/spool/mail/root | grep -v fixed | wc -l";
        MonitoringResponse cliResponse = monitoringCliOperator
                .executeCliCommand("ms1", countEmailAlertsCommand);

        saveAssertTrue(String.format("Failed to get email count for '%s'",
                serverHostname), cliResponse.isSuccess());

        final int initialEmailAlertCount = Integer.parseInt(cliResponse
                .getOutput().trim());

        int currentEmailAlertCount = 0;
        logger.info("Stopping Cobbler");
        monitoringCliOperator.executeCliCommand("ms1", "service cobblerd stop");
        cliResponse = monitoringCliOperator.executeCliCommand("ms1",
                "service cobblerd status", "cobblerd is stopped");
        saveAssertTrue(
                String.format("Failed to stop cobbler on '%s'", serverHostname),
                cliResponse.isSuccess());

        int timeout = 180;

        do {
            logger.info(
                    "Waiting for email alert to be recieved : Current Email Count {} : Timeout in {} seconds",
                    currentEmailAlertCount, timeout);
            currentEmailAlertCount = Integer.parseInt(monitoringCliOperator
                    .executeCliCommand("ms1", countEmailAlertsCommand)
                    .getOutput());
            timeout -= 5;
            //stop cobblerd service if started by puppet
            if (monitoringCliOperator
                    .executeCliCommand("ms1", "service cobblerd status")
                    .getOutput().contains("is running")) {
                monitoringCliOperator.executeCliCommand("ms1",
                        "service cobblerd stop");
            }
            try {
                Thread.sleep(5000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

        } while (cliResponse.isSuccess()
                && initialEmailAlertCount >= currentEmailAlertCount
                && timeout >= 0);

        saveAssertTrue("Timeout reached : " + timeout + " seconds",
                timeout >= 0);
        saveAssertTrue(
                String.format(
                        "Email not recieved : Initial Email Count: %s Current Email Count: %s ",
                        initialEmailAlertCount, currentEmailAlertCount),
                initialEmailAlertCount < currentEmailAlertCount);

        monitoringCliOperator
                .executeCliCommand("ms1", "service cobblerd start");
        cliResponse = monitoringCliOperator.executeCliCommand("ms1",
                "service cobblerd status");
        saveAssertTrue("Cobbler did not start", cliResponse.getOutput()
                .contains("is running"));

        MonitoringResponse monitoringResponse = monitoringUiOperator
                .secureLogin("hqadmin", "hqadmin");
        saveAssertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        logger.info("Resetting Command Helper");
        monitoringCliOperator.resetCommandHelper();

        monitoringResponse = monitoringUiOperator.logout();
        assertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());
    }

    @Test
    public void testAutoDiscovery() {
        setTestcase(DataHandler.getAttribute(TEST_ID).toString(), DataHandler
                .getAttribute(TEST_TITLE).toString());

        final String camIP = HostConfigurator.getMS().getIp();
        logger.info("Creating SSH tunnel to SFS");
        sfsCliOperator.openShell();

        logger.info("Connecting to SFS");
        String platformName = sfsCliOperator.getHostName().trim();
        logger.info("Connected to host: {}", platformName);
        logger.info("Checking if Hyperic Agent is running on SFS: {}",
                platformName);
        boolean isRunning = sfsCliOperator.isHypericAgentRunning();

        if (isRunning) {
            logger.info("Hyperic Agent is running on SFS: {}", platformName);
            final boolean isUpdated = sfsCliOperator.updateCamIP(camIP);
            if (isUpdated) {
                sfsCliOperator.restartHypericAgent();
            } else {
                sfsCliOperator.expectShellClosure();
                fail("Failed to set agent.setup.camIP to " + camIP);
            }
        } else { // hyperic is running on the other node of sfs
            logger.info(
                    "Hyperic Agent is not running on SFS: {}. SSH to other node.",
                    platformName);
            final boolean sshToOtherNodeSuccess = sfsCliOperator
                    .sshToTheOtherNode();
            platformName = sfsCliOperator.getHostName().trim();

            if (sshToOtherNodeSuccess) {
                logger.info("Connected to host: {}", platformName);

                logger.info("Checking if Hyperic Agent is running on SFS: {}",
                        platformName);
                isRunning = sfsCliOperator.isHypericAgentRunning();
                assertTrue(String.format("Hyperic is not running on SFS: %s",
                        platformName), isRunning);

                final boolean isUpdated = sfsCliOperator.updateCamIP(camIP);
                if (isUpdated) {
                    sfsCliOperator.stopHypericAgent();
                    sfsCliOperator.removeDataDirectory();
                    sfsCliOperator.startHypericAgent();
                } else {
                    sfsCliOperator.expectShellClosure();
                    fail("Failed to set agent.setup.camIP to " + camIP);
                }
            } else {
                sfsCliOperator.expectShellClosure();
                fail("Failed to ssh to the other node");
            }
        }

        //      next veirfy auto-discovery on hyperic page
        final MonitoringResponse loginResponse = monitoringUiOperator
                .secureLogin("hqadmin", "hqadmin");
        assertTrue(loginResponse.getErrorMessage(), loginResponse.isSuccess());

        try {
            final long maxWaitTime = (long) (420 * Math.pow(10, 9)); // 420 seconds
            final int sleepTime = 15;
            final long start = System.nanoTime();
            long elapse = 0;
            while (elapse < maxWaitTime) {
                if (monitoringUiOperator.autoDiscoveryResourceContainsName(
                        platformName).isDisplayed()) {
                    logger.info("SFS is auto-discovered!");
                    final MonitoringResponse addInventoryResponse = monitoringUiOperator
                            .addIntoInventory(monitoringUiOperator
                                    .autoDiscoveryResourceContainsName(platformName));
                    saveAssertTrue(addInventoryResponse.getErrorMessage(),
                            addInventoryResponse.isSuccess());
                    break;
                }
                logger.info(
                        "Waiting SFS is auto-discovered... {} seconds passed. Max waiting time {} mins.",
                        elapse / Math.pow(10, 9), 7);
                TimeUnit.SECONDS.sleep(sleepTime);
                monitoringUiOperator.refresh();
                elapse = System.nanoTime() - start;
            }
        } catch (final InterruptedException e) {
            fail("Interrupted when waiting auto-discovery SFS");
        } finally {
            final MonitoringResponse deleteResourceResponse = monitoringUiOperator
                    .deleteResourceType("Platforms", platformName);
            saveAssertTrue(deleteResourceResponse.getErrorMessage(),
                    deleteResourceResponse.isSuccess());

            final MonitoringResponse logoutResponse = monitoringUiOperator
                    .logout();
            assertTrue(logoutResponse.getErrorMessage(),
                    logoutResponse.isSuccess());
        }
    }

    @Test
    public void testLinkEmailToJBossAlert() {
        setTestcase(DataHandler.getAttribute(TEST_ID).toString(), DataHandler
                .getAttribute(TEST_TITLE).toString());
        final MonitoringResponse hostnameResponse = monitoringCliOperator
                .executeCliCommand("ms1", "hostname");

        final MonitoringResponse loginResponse = monitoringUiOperator
                .secureLogin("hqadmin", "hqadmin");
        assertTrue(loginResponse.getErrorMessage(), loginResponse.isSuccess());

        MonitoringResponse cliResponse;

        monitoringUiOperator.openAlertDefinitionConfiguration("Servers",
                "FMServ_su_0_jee_cfg");
        monitoringUiOperator.setNewAlertConfiguration("testAlert",
                "Test Alert Description", "Availability", 100, "<");
        final List<String> alertList = monitoringUiOperator
                .getListOfAlertNames("Servers", "FMServ_su_0_jee_cfg");

        saveAssertTrue("Alert list did not contain testAlert",
                alertList.contains("testAlert"));

        monitoringUiOperator.linkUserToAlert("TestUser", "testAlert",
                "Servers", "FMServ_su_0_jee_cfg");
        monitoringUiOperator.logout();
        monitoringCliOperator.executeCliCommand("sc1", "hostname");

        final String countEmailAlertsCommand = "grep \"Subject: \\[OSS-MT\\] \\!\\! - testAlert FMServ_su_0_jee_cfg\" /var/spool/mail/root | grep -v fixed | wc -l";
        final MonitoringResponse cliEmailCountResponse = monitoringCliOperator
                .executeCliCommand("ms1", countEmailAlertsCommand);

        final int initialEmailAlertCount = Integer
                .parseInt(cliEmailCountResponse.getOutput().trim());

        int currentEmailAlertCount;

        logger.info("Issued command 'amf-adm lock safSu=FMServ_App-SuType-0,safSg=FMServ,safApp=FMServ_App' on sc-1");
        monitoringCliOperator
                .executeCliCommand("sc1",
                        "amf-adm lock safSu=FMServ_App-SuType-0,safSg=FMServ,safApp=FMServ_App");

        int timeout = 240;

        do {
            cliResponse = monitoringCliOperator.executeCliCommand("sc1",
                    "amf-state su all | grep -A4 FMServ_App-SuType-0");
            logger.info(
                    "Locking Service unit FMServ_App-SuType-0 : Timeout in {} seconds",
                    timeout);
            timeout -= 5;
            try {
                Thread.sleep(5000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

        } while (cliResponse.getOutput().contains("TERMINATING")
                && timeout >= 0);

        cliResponse = monitoringCliOperator.executeCliCommand("sc1",
                "amf-state su all | grep -A4 FMServ_App-SuType-0",
                "saAmfSUAdminState=LOCKED");

        saveAssertTrue(
                "Could not lock service unit: output "
                        + cliResponse.getOutput(), cliResponse.getOutput()
                        .contains("UNINSTANTIATED"));
        timeout = 180;
        do {
            currentEmailAlertCount = Integer.parseInt(monitoringCliOperator
                    .executeCliCommand("ms1", countEmailAlertsCommand)
                    .getOutput());
            logger.info(
                    "Waiting for email alert to be recieved : Current Email Count {} : Timeout in {} seconds",
                    currentEmailAlertCount, timeout);
            timeout -= 5;
            try {
                Thread.sleep(5000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        } while (initialEmailAlertCount >= currentEmailAlertCount
                && timeout >= 0);

        saveAssertTrue(
                String.format(
                        "Email not recieved : Initial Email Count: %s Current Email Count: %s ",
                        initialEmailAlertCount, currentEmailAlertCount),
                initialEmailAlertCount < currentEmailAlertCount);
        monitoringUiOperator.secureLogin("hqadmin", "hqadmin");
        monitoringUiOperator.deleteAlert("Servers", "FMServ_su_0_jee_cfg",
                "testAlert");
        final MonitoringResponse logoutResponse = monitoringUiOperator.logout();
        assertTrue(logoutResponse.getErrorMessage(), logoutResponse.isSuccess());

        logger.info("Issued command 'amf-adm unlock safSu=FMServ_App-SuType-0,safSg=FMServ,safApp=FMServ_App' on sc-1");
        monitoringCliOperator
                .executeCliCommand("sc1",
                        "amf-adm unlock safSu=FMServ_App-SuType-0,safSg=FMServ,safApp=FMServ_App");
        timeout = 240;
        do {
            cliResponse = monitoringCliOperator.executeCliCommand("sc1",
                    "amf-state su all | grep -A4 FMServ_App-SuType-0");
            logger.info(
                    "Unlocking Service unit FMServ_App-SuType-0 : Timeout in {} seconds",
                    timeout);
            timeout -= 5;
            try {
                Thread.sleep(5000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        } while (!cliResponse.getOutput().contains(
                "saAmfSUPresenceState=INSTANTIATED")
                && timeout >= 0);
        saveAssertTrue(
                "FMServ Jboss Service Unit didn't come back online",
                cliResponse.getOutput().contains(
                        "saAmfSUPresenceState=INSTANTIATED"));
        monitoringCliOperator.resetCommandHelper();

    }

    @Test
    public void testEMC() {
        setTestcase(DataHandler.getAttribute(TEST_ID).toString(), DataHandler
                .getAttribute(TEST_TITLE).toString());

        final MonitoringResponse cliResp0 = monitoringCliOperator
                .executeCliCommand("ms1", "hostname");
        final String msHostname = cliResp0.getOutput();
        final String serverName = msHostname + "-vnx";
        final String serverType = "EMC VNX Storage";
        final String serverInstallPath = "/opt/Navisphere/bin/";

        // Get SED param sanBase_storeIPv4IP1
        /*
         * final String spaIp = DataHandler.getAttribute("host.vnx.spa")
         * .toString();
         */
        final MonitoringResponse cliResp1 = monitoringCliOperator
                .executeCliCommand(
                        "ms1",
                        "litp /inventory/deployment1/sanBase/ show -rvvv | grep storeIPv4IP1 | cut -d: -f2 | sed -e 's/^ \"//' -e 's/\"$//'");
        final String spaIp = cliResp1.getOutput();

        /*
         * final String spbIp = DataHandler.getAttribute("host.vnx.spb")
         * .toString();
         */
        final MonitoringResponse cliResp2 = monitoringCliOperator
                .executeCliCommand(
                        "ms1",
                        "litp /inventory/deployment1/sanBase/ show -rvvv | grep storeIPv4IP2 | cut -d: -f2 | sed -e 's/^ \"//' -e 's/\"$//'");
        final String spbIp = cliResp2.getOutput();

        final MonitoringResponse loginResponse = monitoringUiOperator
                .secureLogin("hqadmin", "hqadmin");
        assertTrue(loginResponse.getErrorMessage(), loginResponse.isSuccess());

        MonitoringResponse monitoringResponse = monitoringUiOperator
                .addEmcVnxServerInPlatform(msHostname, serverName, serverType,
                        serverInstallPath, spaIp, spbIp);
        saveAssertTrue("Failed to add server " + serverName
                + ". Error message: " + monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());
        monitoringResponse = monitoringUiOperator.deleteResourceType("Servers",
                serverName);
        saveAssertTrue("Failed to delete " + serverName + ". Error message: "
                + monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        monitoringResponse = monitoringUiOperator.logout();
        assertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());
    }

    @Test
    public void testHqAdminChangePassword() {

        setTestcase(DataHandler.getAttribute(TEST_ID).toString(), DataHandler
                .getAttribute(TEST_TITLE).toString());

        MonitoringResponse monitoringResponse = monitoringUiOperator
                .secureLogin("hqadmin", "hqadmin");
        saveAssertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        monitoringResponse = monitoringUiOperator.changeUserPassword(
                "ChangePWTestUser", "hqadmin", "TestPassword");
        saveAssertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());
        monitoringUiOperator.logout();

        monitoringUiOperator.secureLogin("ChangePWTestUser", "TestPassword");
        saveAssertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        monitoringResponse = monitoringUiOperator.logout();
        assertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());
    }

    @Test
    public void ServersVisibleInHyperic() {
        setTestcase(DataHandler.getAttribute(TEST_ID).toString(), DataHandler
                .getAttribute(TEST_TITLE).toString());

        MonitoringResponse monitoringResponse = monitoringUiOperator
                .secureLogin("hqadmin", "hqadmin");
        assertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        String hostname = monitoringCliOperator.executeCliCommand("ms1",
                "hostname").getOutput();
        monitoringResponse = monitoringUiOperator.checkResourceAvailability(
                "Platforms", hostname);
        saveAssertTrue("Platform " + hostname + " is currently not Available",
                monitoringResponse.isSuccess());

        hostname = monitoringCliOperator.executeCliCommand("sc1", "hostname")
                .getOutput();
        monitoringResponse = monitoringUiOperator.checkResourceAvailability(
                "Platforms", hostname);
        saveAssertTrue("Platform " + hostname + " is currently not Available",
                monitoringResponse.isSuccess());

        hostname = monitoringCliOperator.executeCliCommand("sc2", "hostname")
                .getOutput();
        monitoringResponse = monitoringUiOperator.checkResourceAvailability(
                "Platforms", hostname);
        saveAssertTrue("Platform " + hostname + " is currently not Available",
                monitoringResponse.isSuccess());

        monitoringResponse = monitoringUiOperator.logout();
        assertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());
    }

    @Test
    public void deleteTestUsers() {
        MonitoringResponse monitoringResponse = monitoringUiOperator
                .secureLogin("hqadmin", "hqadmin");
        assertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        monitoringResponse = monitoringUiOperator.deleteUser("TestUser");
        saveAssertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        monitoringResponse = monitoringUiOperator
                .deleteUser("ChangePWTestUser");
        saveAssertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        monitoringResponse = monitoringUiOperator.logout();
        assertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());
    }

    @Test
    public void checkHQHealthStatus(){
        MonitoringResponse monitoringResponse = monitoringUiOperator
                .secureLogin("hqadmin", "hqadmin");
        assertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        logger.info("Verifying Hyperic Server Health Status");
        monitoringResponse = monitoringUiOperator.checkHQHealthCPU();
        saveAssertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        logger.info("Verifying time offsets between Hyperic Servers and Agents");
        monitoringResponse = monitoringUiOperator.checkHQTimeOffset();
        saveAssertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());

        monitoringResponse = monitoringUiOperator.logout();
        saveAssertTrue(monitoringResponse.getErrorMessage(),
                monitoringResponse.isSuccess());
    }
}