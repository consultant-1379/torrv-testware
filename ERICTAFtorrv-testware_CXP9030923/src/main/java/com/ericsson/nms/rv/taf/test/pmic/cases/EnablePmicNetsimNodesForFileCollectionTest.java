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
package com.ericsson.nms.rv.taf.test.pmic.cases;

import java.util.List;

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

public class EnablePmicNetsimNodesForFileCollectionTest extends
        TorTestCaseHelper implements TestCase {

    private final Logger logger = LoggerFactory
            .getLogger(EnablePmicNetsimNodesForFileCollectionTest.class);

    @BeforeTest
    @Parameters({ "testId", "testTitle" })
    public void setup(final String testId, final String testTitle) {
        logger.debug("testId: {}, testTitle: {}", testId, testTitle);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @Test
    public void testEnablePmicNetsimNodesForFileCollection() {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
        final List<Node> nodes = DataSourceBuilder.getNodesInPool();

        for (final Node node : nodes) {
            final String host = node.getNetsim();
            final String simulation = node.getSimulation();
            final String nodeName = node.getManagedElementId();
            final NetsimOperator netsimOperator = new NetsimOperator(host);
            final NetsimResponse response = netsimOperator
                    .enablePmicFileCollection(simulation, nodeName);
            saveAssertTrue(response.getErrorMessage(), response.isSuccess());
        }
    }
}