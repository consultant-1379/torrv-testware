package com.ericsson.nms.rv.taf.test.fm.cases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.DataSourceBuilder;
import com.ericsson.nms.rv.taf.test.netsim.NetsimOperator;
import com.ericsson.nms.rv.taf.test.netsim.NetsimResponse;

public class FmRestTest extends TorTestCaseHelper implements TestCase {

    Logger logger = LoggerFactory.getLogger(FmRestTest.class);

    @BeforeTest
    @Parameters({ "testId", "testTitle" })
    public void setup(final String testId, final String testTitle) {
        logger.info("testId:{}, testTitle: {}", testId, testTitle);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @Parameters("alarm.problem")
    @Test
    public void testFmSendAlarm(final String alarmProblem) {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
        verifyDataPool();
        final Node firstNode = DataSourceBuilder.getNodesInPool().get(0);
        final String netsim = firstNode.getNetsim();
        final String simulation = firstNode.getSimulation();
        final String nodeName = firstNode.getManagedElementId();
        final NetsimOperator netsimOperator = new NetsimOperator(netsim);
        final NetsimResponse response = netsimOperator.sendAlarm(simulation,
                nodeName, alarmProblem);
        assertTrue(response.getErrorMessage(), response.isSuccess());
    }

    @Test
    public void ceaseAlarms() {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
        verifyDataPool();
        final Node firstNode = DataSourceBuilder.getNodesInPool().get(0);
        final String netsim = firstNode.getNetsim();
        final String simulation = firstNode.getSimulation();
        final String nodeName = firstNode.getManagedElementId();
        final NetsimOperator netsimOperator = new NetsimOperator(netsim);
        final NetsimResponse response = netsimOperator.ceaseAlarms(simulation,
                nodeName);
        assertTrue(response.getErrorMessage(), response.isSuccess());
    }

    private static void verifyDataPool() {
        assertTrue("Node pool has no nodes", !DataSourceBuilder
                .getNodesInPool().isEmpty());
    }
}
