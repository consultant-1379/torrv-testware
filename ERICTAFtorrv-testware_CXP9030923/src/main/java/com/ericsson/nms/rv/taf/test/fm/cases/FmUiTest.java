package com.ericsson.nms.rv.taf.test.fm.cases;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Response;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.DataSourceBuilder;
import com.ericsson.nms.rv.taf.test.fm.operators.FmUiOperator;
import com.ericsson.nms.rv.taf.test.netsim.NetsimOperator;
import com.ericsson.nms.rv.taf.test.netsim.NetsimResponse;
import com.ericsson.nms.rv.taf.test.ui.viewmodels.fm.FmViewModel;
import com.google.inject.Inject;

public class FmUiTest extends TorTestCaseHelper implements TestCase {

    @Inject
    FmUiOperator fmuiOperator;

    Logger logger = LoggerFactory.getLogger(FmUiTest.class);

    @BeforeTest
    @Parameters({ "testId", "testTitle" })
    public void setup(final String testId, final String testTitle) {
        logger.info("testId:{}, testTitle: {}", testId, testTitle);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @Test
    public void testAddNodeAndClickBySearchNetworkObject() {
        setTestIdAndTitle();
        fmuiOperator.getAlarmMonitoringPage();
        final Node node = getFirstNodeOrFailed();
        final String managedElement = node.getManagedElementId();
        assertStepIsTrue(fmuiOperator
                .addNodeBySearchNetworkObject(managedElement));
        assertStepIsTrue(fmuiOperator.clickNetworkElements(managedElement));
    }

    @Test
    public void testRemoveNodeFromList() {
        setTestIdAndTitle();
        fmuiOperator.getAlarmMonitoringPage();
        final Node node = getFirstNodeOrFailed();
        assertStepIsTrue(fmuiOperator.clickNetworkElements(node
                .getManagedElementId()));
        assertStepIsTrue(fmuiOperator.clickNetworkElementsContextMenuButton());
        assertStepIsTrue(fmuiOperator.disableSupervision(node
                .getManagedElementId()));
        assertStepIsTrue(fmuiOperator.verifySuccessIconShown());
        assertStepIsTrue(fmuiOperator.clearAllFiltersAndSearches());
        assertStepIsTrue(fmuiOperator.removeNodeFromList(node
                .getManagedElementId()));
    }

    @Parameters("alarm.problem")
    @Test
    public void verifyAlarmExists(final String alarmProblem) {
        setTestIdAndTitle();
        fmuiOperator.getAlarmMonitoringPage();
        assertStepIsTrue(fmuiOperator.clearAllFiltersAndSearches());
        assertStepIsTrue(fmuiOperator.enterProblemSearchText(alarmProblem));
        assertStepIsTrue(fmuiOperator.getFirstAlarm());
    }

    @Parameters("alarm.problem")
    @Test
    public void testHistoryAlarm(final String alarmProblem) {

        setTestIdAndTitle();
        final Node firstNode = DataSourceBuilder.getNodesInPool().get(0);
        final String netsim = firstNode.getNetsim();
        final String simulation = firstNode.getSimulation();
        final String nodeName = firstNode.getManagedElementId();
        final NetsimOperator netsimOperator = new NetsimOperator(netsim);
        final Date date = new Date();
        final String historyAlarm = alarmProblem + date.toString();
        final NetsimResponse response = netsimOperator.sendAlarm(simulation,
                nodeName, historyAlarm);
        assertTrue(response.getErrorMessage(), response.isSuccess());
        fmuiOperator.getAlarmMonitoringPage();
        assertStepIsTrue(fmuiOperator.enterProblemSearchText(historyAlarm));
        assertStepIsTrue(fmuiOperator.clickAlarmOnTable(0));
        assertStepIsTrue(fmuiOperator.alarmAcknowledge());
        assertStepIsTrue(fmuiOperator.verifySuccessIconShown());
        assertStepIsTrue(fmuiOperator.enterProblemSearchText(historyAlarm));
        assertStepIsTrue(fmuiOperator.clickAlarmOnTable(0));
        assertStepIsTrue(fmuiOperator.clickClearLink());
        assertStepIsTrue(fmuiOperator.verifySuccessIconShown());
        fmuiOperator.getAlarmHistoryPage();
        assertStepIsTrue(fmuiOperator.searchHistoryAlarms(historyAlarm));
        assertStepIsTrue(fmuiOperator.getFirstAlarm());
    }

    @Parameters("alarm.problem")
    @Test
    public void verifyAlarmNotExists(final String alarmProblem) {
        setTestIdAndTitle();
        fmuiOperator.getAlarmMonitoringPage();
        assertStepIsTrue(fmuiOperator.enterProblemSearchText(alarmProblem));
        assertStepIsTrue(fmuiOperator.getFirstAlarm());
    }

    @Parameters({ "alarm.problem", "alarm.comment" })
    @Test
    public void testAddComment(final String alarmProblem,
            final String alarmComment) {
        setTestIdAndTitle();
        fmuiOperator.getAlarmMonitoringPage();
        assertStepIsTrue(fmuiOperator.enterProblemSearchText(alarmProblem));
        assertStepIsTrue(fmuiOperator.getFirstAlarm());
        assertStepIsTrue(fmuiOperator.clickAlarmOnTable(0));
        assertStepIsTrue(fmuiOperator.clickAddCommentLink());
        assertStepIsTrue(fmuiOperator.inputComment(alarmComment));
        assertStepIsTrue(fmuiOperator.clickSaveComment());
        assertStepIsTrue(fmuiOperator.verifySuccessIconShown());
    }

    @Parameters("alarm.problem")
    @Test
    public void testAlarmAcknowledge(final String alarmProblem) {
        setTestIdAndTitle();
        fmuiOperator.getAlarmMonitoringPage();
        assertStepIsTrue(fmuiOperator.enterProblemSearchText(alarmProblem));
        assertStepIsTrue(fmuiOperator.clickAlarmOnTable(0));
        assertStepIsTrue(fmuiOperator.alarmAcknowledge());
        assertStepIsTrue(fmuiOperator.verifySuccessIconShown());
    }

    @Parameters("alarm.problem")
    @Test
    public void testAlarmUnacknowledge(final String alarmProblem) {
        setTestIdAndTitle();
        fmuiOperator.getAlarmMonitoringPage();
        assertStepIsTrue(fmuiOperator.enterProblemSearchText(alarmProblem));
        assertStepIsTrue(fmuiOperator.clickAlarmOnTable(0));
        assertStepIsTrue(fmuiOperator.alarmUnacknowledge());
        assertStepIsTrue(fmuiOperator.verifySuccessIconShown());
    }

    @Parameters("alarm.problem")
    @Test
    public void testClearAlarm(final String alarmProblem) {
        setTestIdAndTitle();
        fmuiOperator.getAlarmMonitoringPage();
        assertStepIsTrue(fmuiOperator.enterProblemSearchText(alarmProblem));
        assertStepIsTrue(fmuiOperator.clickAlarmOnTable(0));
        assertStepIsTrue(fmuiOperator.clickClearLink());
        assertStepIsTrue(fmuiOperator.verifySuccessIconShown());
    }

    @Test
    public void testDisableSupervision() {
        setTestIdAndTitle();
        final Node firstNode = getFirstNodeOrFailed();
        fmuiOperator.getAlarmMonitoringPage();
        assertStepIsTrue(fmuiOperator.disableSupervision(firstNode
                .getManagedElementId()));
        assertStepIsTrue(fmuiOperator.verifySuccessIconShown());
    }

    @Test
    public void testEnableSupervision() {
        setTestIdAndTitle();
        final Node firstNode = getFirstNodeOrFailed();
        fmuiOperator.getAlarmMonitoringPage();
        assertStepIsTrue(fmuiOperator.enableSupervision(firstNode
                .getManagedElementId()));
        assertStepIsTrue(fmuiOperator.verifySuccessIconShown());
    }

    @Test
    public void testPerformAlarmSync() {
        setTestIdAndTitle();
        final Node firstNode = getFirstNodeOrFailed();
        fmuiOperator.getAlarmMonitoringPage();
        assertStepIsTrue(fmuiOperator.clickNetworkElementsContextMenuButton());
        assertStepIsTrue(fmuiOperator.clickNetworkElements(firstNode
                .getManagedElementId()));
        assertStepIsTrue(fmuiOperator.clickInitiateAlarmSync(firstNode
                .getManagedElementId()));
    }

    @Test
    public void testFMHeartBeatAlarm() {
        setTestIdAndTitle();
        final Node firstNode = getFirstNodeOrFailed();
        final String netsim = firstNode.getNetsim();
        final String simulation = firstNode.getSimulation();
        final String nodeName = firstNode.getManagedElementId();
        fmuiOperator.getAlarmMonitoringPage();
        assertStepIsTrue(fmuiOperator
                .selectFilters(FmViewModel.CRITICAL_FILTER));
        final NetsimOperator netsimOperator = new NetsimOperator(netsim);
        assertStepIsTrue(netsimOperator.turnOffNode(simulation, nodeName));
        assertStepIsTrue(fmuiOperator.clearAllFiltersAndSearches());
        verifyHeartbeatAlarm(nodeName);
        assertStepIsTrue(fmuiOperator.selectFilters(FmViewModel.CLEARED_FILTER));
        assertStepIsTrue(netsimOperator.turnOnNode(simulation, nodeName));
        verifyHeartbeatAlarmGone(nodeName);
        assertStepIsTrue(netsimOperator.ceaseAlarms(simulation, nodeName));
    }

    @Parameters("alarm.problem")
    @Test
    public void verifyCeaseAlarmBehaviour(final String alarmProblem) {
        setTestIdAndTitle();
        fmuiOperator.getAlarmMonitoringPage();
        assertStepIsTrue(fmuiOperator.selectFilters(FmViewModel.CLEARED_FILTER));
        assertStepIsTrue(fmuiOperator.enterProblemSearchText(alarmProblem));
        assertStepIsTrue(fmuiOperator.getFirstAlarm());
    }

    private void verifyHeartbeatAlarm(final String nodeName) {
        final int maxWaitInSeconds = 360;
        logger.info("Max wait time {} seconds.", maxWaitInSeconds);
        long elapsed = 0;
        final long beginNano = System.nanoTime();
        boolean isSucceess = false;
        while (elapsed <= maxWaitInSeconds * Math.pow(10, 9)) {
            // These two lines are here, as alarms result doesn't automatically get updated.
            fmuiOperator.selectFilters(FmViewModel.CLEARED_FILTER);
            fmuiOperator.selectFilters(FmViewModel.CRITICAL_FILTER);
            final Map<String, String> firstAlarm = fmuiOperator.getFirstAlarm()
                    .getFirstAlarmMap();
            final String specificProblem = firstAlarm
                    .get(FmViewModel.SPECIFIC_PROBLEM_HEADER);
            final String alarmState = firstAlarm
                    .get(FmViewModel.ALARM_STATE_HEADER);
            final String alarmNode = firstAlarm
                    .get(FmViewModel.OBJECT_OF_REFERENCE_HEADER);
            if (FmViewModel.HEARTBEAT_FAILURE_ALARM.equals(specificProblem)
                    && FmViewModel.ACTIVE_ALARM_STATE.equals(alarmState)
                    && alarmNode.contains(nodeName)) {
                logger.info("Got Heartbeat Alarm!!");
                isSucceess = true;
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(5); // wait 5 seconds
            } catch (final InterruptedException e) {
                fail("Interrupted when waiting for HeartBeat alarms for node");
            }
            fmuiOperator.enterProblemSearchText("Heartbeat Failure");
            final long endNano = System.nanoTime();
            elapsed = endNano - beginNano;
            logger.info("Waiting heartbeat alarm.\n{} seconds elapsed", elapsed
                    / Math.pow(10, 9));
        }
        saveAssertTrue("Oops! Didn't get Heartbeat critical alarm", isSucceess);
    }

    private void verifyHeartbeatAlarmGone(final String nodeName) {
        final int maxWaitInSeconds = 360;
        logger.info("Max wait time {} seconds.", maxWaitInSeconds);
        long elapsed = 0;
        final long beginNano = System.nanoTime();
        boolean isSuccess = false;
        while (elapsed <= maxWaitInSeconds * Math.pow(10, 9)) {
            // These two lines are here, as alarms result doesn't automatically get updated.
            fmuiOperator.selectFilters(FmViewModel.CRITICAL_FILTER);
            fmuiOperator.selectFilters(FmViewModel.CLEARED_FILTER);
            final Map<String, String> firstAlarm = fmuiOperator.getFirstAlarm()
                    .getFirstAlarmMap();
            final String specificProblem = firstAlarm
                    .get(FmViewModel.SPECIFIC_PROBLEM_HEADER);
            final String alarmState = firstAlarm
                    .get(FmViewModel.ALARM_STATE_HEADER);
            final String alarmNode = firstAlarm
                    .get(FmViewModel.OBJECT_OF_REFERENCE_HEADER);
            if (FmViewModel.HEARTBEAT_FAILURE_ALARM.equals(specificProblem)
                    && FmViewModel.CLEARED_ALARM_STATE.equals(alarmState)
                    && alarmNode.contains(nodeName)) {
                logger.info("Heartbeat Alarm is cleared!!");
                isSuccess = true;
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(5); // wait 5 seconds
            } catch (final InterruptedException e) {
                fail("Interrupted when waiting for HeartBeat alarms cleared for node");
            }
            fmuiOperator.enterProblemSearchText("Heartbeat Failure");
            final long endNano = System.nanoTime();
            elapsed = endNano - beginNano;
            logger.info(
                    "Waiting for heartbeat alarm to be cleared.\n{} seconds elapsed",
                    elapsed / Math.pow(10, 9));
        }
        saveAssertTrue("Oops! Didn't get cleared Heartbeat critical alarm",
                isSuccess);
    }

    private static void verifyDataPool() {
        assertTrue("Node pool has no nodes", !DataSourceBuilder
                .getNodesInPool().isEmpty());
    }

    private static Node getFirstNodeOrFailed() {
        verifyDataPool();
        return DataSourceBuilder.getNodesInPool().get(0);
    }

    private static void assertStepIsTrue(final Response response) {
        assertTrue(response.getErrorMessage(), response.isSuccess());
    }

    private void setTestIdAndTitle() {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
    }
}