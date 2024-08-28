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
package com.ericsson.nms.rv.taf.test.stkpi.cases;

import javax.inject.Inject;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheUiOperator;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.ApacheResponse;
import com.ericsson.nms.rv.taf.test.fm.operators.FmAlarmResponse;
import com.ericsson.nms.rv.taf.test.fm.operators.FmUiOperator;
import com.ericsson.nms.rv.taf.test.netsim.NetsimOperator;
import com.ericsson.nms.rv.taf.test.netsim.NetsimResponse;
import com.ericsson.nms.rv.taf.test.stkpi.cases.operators.StKpiOperator;
import com.ericsson.nms.rv.taf.test.stkpi.cases.operators.StKpiResponse;

public class StKpiFmTests extends TorTestCaseHelper implements TestCase {
    private static final Logger logger = LoggerFactory
            .getLogger(StKpiFmTests.class);

    @Inject
    private StKpiOperator operator;

    @Inject
    FmUiOperator fmUiOperator;

    @Inject
    private ApacheUiOperator apacheOperator;

    private final String ADMIN_USERNAME = (String) DataHandler
            .getAttribute("admin.username");
    private final String ADMIN_PASSWORD = (String) DataHandler
            .getAttribute("admin.password");
    private boolean fmSupervisionWasEnabledForTestNode = false;

    @Test
    public void fmAlarmReportingToGui() {
        setTestcase(
                "TORRV-ZEPHYR-TC-480",
                "ST_STKPI_FM_01 Alarm visible In GUI TORF-9311. Alarm should get displayed in GUI within 10 seconds of being sent");
        final String alarmProblem = "Stkpi_test_TORRV-3643";
        final JSONObject workloadData = operator.getWorkloadData();
        assertNotNull("Call to workload tool failed to return any node data",
                workloadData);
        final JSONObject firstNode = operator.getNode(0, 0, 0, workloadData);
        assertNotNull("Cannot find first node in workload JSON", firstNode);
        String netsim = (String) firstNode.get("netsim");
        assertNotNull("First node netsim information is missing", netsim);
        final String simulation = (String) firstNode.get("simulation");
        assertNotNull("First node simulation information is missing",
                simulation);
        final String netsimAndNodeName = (String) firstNode.get("name");
        assertNotNull("First node full name information is missing",
                netsimAndNodeName);
        final String nodeName = (String) firstNode.get("node_name");
        assertNotNull("First node short name information is missing", nodeName);

        logger.info(
                "Got node information: netsim:{}, simulation:{}, node full name:{}, node short name: {}",
                netsim, simulation, netsimAndNodeName, nodeName);

        //Convert netsim name netsimlin548.athtem.eei.ericsson.se to netsimlin548
        netsim = netsim.substring(0, netsim.indexOf("."));
        logger.info("Using netsim host with hostname = {}", netsim);
        final NetsimOperator netsimOperator = new NetsimOperator(netsim);

        NetsimResponse verifyNodeStartedResp = netsimOperator
                .verifyNodeIsStarted(nodeName);
        logger.info("Verify node is started: {}, success: {}",
                netsimAndNodeName, verifyNodeStartedResp.isSuccess());

        if (!verifyNodeStartedResp.isSuccess()) {
            logger.info("Turning on node.");
            final NetsimResponse turnOnNodeResp = netsimOperator.turnOnNode(
                    simulation, nodeName);
            assertTrue(String.format(
                    "Failed to turn on node %s on simulation %s", nodeName,
                    simulation), turnOnNodeResp.isSuccess());

            verifyNodeStartedResp = netsimOperator
                    .verifyNodeIsStarted(nodeName);
            assertTrue("Node is not started after issuing the start command",
                    verifyNodeStartedResp.isSuccess());
        }

        if (!fmGuiSetupStep(netsimAndNodeName, alarmProblem)) {
            throw new SkipException(
                    "Failed to initialise FM gui. Refer to logs for more info.");
        }

        final NetsimResponse netsimResponse = netsimOperator.sendAlarm(
                simulation, nodeName, alarmProblem);

        final FmAlarmResponse fmAlarmResponse = fmUiOperator.pollForAlarm(
                alarmProblem, 20000);

        if (!fmGuiTeardownStep(netsimAndNodeName)) {
            logger.warn("FM teardown step failed. Refer to logs for more info.");
        }

        final long totalTime = fmAlarmResponse.getTimeAlarmReadMillis()
                - netsimResponse.getTimeAlarmSentMillis();

        assertTrue(String.format(
                "Send alarm from netsim has failed. Error message: %s",
                netsimResponse.getErrorMessage()), netsimResponse.isSuccess());
        assertTrue(String.format("Alarm '%s' failed to appear on FM GUI.",
                alarmProblem), fmAlarmResponse.isSuccess());
        assertTrue(
                String.format(
                        "Alarm appeared in Gui too late. Expected time less than 10000 milliseconds, actual time %d milliseconds.",
                        totalTime), (totalTime <= 10000));
        logger.info(
                "RESULT: FmAlarm '{}' appeared in Gui '{}' milliseconds after being sent",
                alarmProblem, totalTime);
    }

    @Test
    public void fmAlarmReportingToNms() {
        setTestcase(
                "TORRV-ZEPHYR-TC-481",
                "ST_STKPI_FM_02 Alarm Reporting to NMS TORF-9311. Alarm Reporting to NMS, within 5 Seconds");

        //Setup node and netsim info
        final String alarmProblem = "StKpi_test_TORRV-3647";
        final JSONObject workloadData = operator.getWorkloadData();
        assertNotNull("Call to workload tool failed to return any node data",
                workloadData);
        final JSONObject firstNode = operator.getNode(0, 0, 0, workloadData);
        assertNotNull("Cannot find first node in workload JSON", firstNode);
        String netsim = (String) firstNode.get("netsim");
        assertNotNull("First node netsim information is missing", netsim);
        final String simulation = (String) firstNode.get("simulation");
        assertNotNull("First node simulation information is missing",
                simulation);
        final String netsimAndNodeName = (String) firstNode.get("name");
        assertNotNull("First node full name information is missing",
                netsimAndNodeName);
        final String nodeName = (String) firstNode.get("node_name");
        assertNotNull("First node short name information is missing", nodeName);

        logger.info(
                "Got node information: netsim:{}, simulation:{}, node full name:{}, node short name: {}",
                netsim, simulation, netsimAndNodeName, nodeName);

        //Convert the long name to the short name to match the host.properties file
        netsim = netsim.substring(0, netsim.indexOf("."));
        logger.info("Using netsim host with hostname = {}", netsim);
        final NetsimOperator netsimOperator = new NetsimOperator(netsim);

        //Start the node if not already started.
        NetsimResponse verifyNodeStartedResp = netsimOperator
                .verifyNodeIsStarted(nodeName);
        logger.info("Verify node is started: {}, success: {}",
                netsimAndNodeName, verifyNodeStartedResp.isSuccess());

        if (!verifyNodeStartedResp.isSuccess()) {
            logger.info("Turning on node.");
            final NetsimResponse turnOnNodeResp = netsimOperator.turnOnNode(
                    simulation, nodeName);
            assertTrue(String.format(
                    "Failed to turn on node %s on simulation %s", nodeName,
                    simulation), turnOnNodeResp.isSuccess());

            verifyNodeStartedResp = netsimOperator
                    .verifyNodeIsStarted(nodeName);
            assertTrue("Node is not started after issuing the start command",
                    verifyNodeStartedResp.isSuccess());
        }

        //Setup the GUI
        if (!fmGuiSetupStep(netsimAndNodeName, alarmProblem)) {
            throw new SkipException(
                    "Failed to initialise FM gui. Refer to logs for more info.");
        }

        //Send an alarm
        final StKpiResponse subscribeResponse = operator
                .subscribeToNMSAndSendFmAlarm(netsimOperator, simulation,
                        nodeName, alarmProblem);

        //Monitor the GUI for alarm
        StKpiResponse stKpiResponse = null;
        if (subscribeResponse.isSuccess()) {
            stKpiResponse = operator.verifyAlarmVisibleWithinFiveSeconds(
                    alarmProblem, nodeName,
                    subscribeResponse.getTimeAlarmSent(),
                    subscribeResponse.getOutput());
            logger.info("Verification result: {}.", stKpiResponse.isSuccess());
        }

        //Teardown GUI
        if (!fmGuiTeardownStep(netsimAndNodeName)) {
            logger.warn("FM teardown step failed. Refer to logs for more info.");
        }

        //Do assertions
        assertTrue(String.format(
                "Failed to subscribe to the NMS. Error message: %s",
                subscribeResponse.getErrorMessages().toString()),
                subscribeResponse.isSuccess());
        if (stKpiResponse != null) {
            assertTrue(
                    String.format(
                            "Alarm failed to appear in the NMS within 5 seconds. Alarm name: %s. Error Messages: %s",
                            alarmProblem, stKpiResponse.getErrorMessages()
                                    .toString()), stKpiResponse.isSuccess());
        }

    }

    private boolean fmGuiSetupStep(final String netsimAndNodeName,
            final String alarmProblem) {
        logger.info("");
        logger.info("FM Gui Setup: START");

        //Login
        final ApacheResponse apacheResp = apacheOperator.login(ADMIN_USERNAME,
                ADMIN_PASSWORD);
        if (!apacheResp.isSuccess()) {
            logger.warn(apacheResp.getErrorMessage());
            return false;
        }

        //Open alarm monitor page
        fmUiOperator.getAlarmMonitoringPage();

        //Remove node from list
        FmAlarmResponse fmResp = fmUiOperator
                .removeNodeFromList(netsimAndNodeName);
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
        } else {
            logger.info("Node {} removed from Network Elements list",
                    netsimAndNodeName);
        }

        //Get node from network explorer
        fmResp = fmUiOperator.addNodeBySearchNetworkObject(netsimAndNodeName);
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
            return false;
        }

        //Enable supervision
        fmResp = fmUiOperator.enableSupervision(netsimAndNodeName);
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
            return false;
        }

        //Save fm supervision state in local boolean
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
            return false;
        } else {
            fmSupervisionWasEnabledForTestNode = fmResp.getNotificationLabel()
                    .getText().contains("Alarm supervision is already enabled");
            logger.info("FM Supervision was previously set for test node: {}",
                    fmSupervisionWasEnabledForTestNode);
        }

        //Create filter in GUI
        fmResp = fmUiOperator.enterProblemSearchText(alarmProblem);
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
            return false;
        }
        logger.info("FM Gui Setup: END\n");
        return true;
    }

    private boolean fmGuiTeardownStep(final String netsimAndNodeName) {
        logger.info("");
        logger.info("FM Gui Teardown: START");

        //Clear alarm
        FmAlarmResponse fmResp = fmUiOperator.clearAlarm(netsimAndNodeName);
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
        }

        //Disable supervision
        if (!fmSupervisionWasEnabledForTestNode) {
            fmResp = fmUiOperator.disableSupervision(netsimAndNodeName);
            if (!fmResp.isSuccess()) {
                logger.warn(fmResp.getErrorMessage());
            }
        } else {
            logger.info("Fm teardown steps: skipping disable supervision as it was enabled before test.");
        }

        //Remove node from list
        fmResp = fmUiOperator.removeNodeFromList(netsimAndNodeName);
        if (!fmResp.isSuccess()) {
            logger.warn(fmResp.getErrorMessage());
        } else {
            logger.info("Node {} removed from Network Elements list",
                    netsimAndNodeName);
        }

        //Log out
        final ApacheResponse apacheResp = apacheOperator.logout();
        if (!apacheResp.isSuccess()) {
            logger.warn(apacheResp.getErrorMessage());
        }
        logger.info("FM Gui Teardown: END\n");
        return true;
    }
}
