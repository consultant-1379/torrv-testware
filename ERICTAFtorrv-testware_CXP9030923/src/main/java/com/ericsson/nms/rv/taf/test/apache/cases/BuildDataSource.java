package com.ericsson.nms.rv.taf.test.apache.cases;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.*;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.nms.rv.taf.test.apache.operators.NodePool;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;

public class BuildDataSource extends TorTestCaseHelper implements TestCase {

    private final Logger logger = LoggerFactory
            .getLogger(BuildDataSource.class);
    private static final String ADDED_NODES = UtilityClass.BUILD_DATA_SOURCE_NAME;

    @Context(context = Context.REST)
    @TestId(id = "Setup Step", title = "COMMON. Build data source.")
    @Test
    public void buildDataSource() {
    }

    @BeforeTest
    @Parameters({ "groupNames", "numOfNodes" })
    public void setup(final String groupNames, final String numOfNodes) {
        logger.info(
                "Start building data source: groupNames is {}, numOfNodes is {}",
                groupNames, numOfNodes);
        String[] groupNamesArray = groupNames.split(",");
        if (groupNames.isEmpty()) {
            final List<String> pools = UtilityClass.getNodePoolFileList();
            groupNamesArray = pools.toArray(groupNamesArray);
        }
        final int numberOfNodes = Integer.valueOf(numOfNodes);
        TafTestContext.getContext().removeDataSource(ADDED_NODES);
        for (int i = 0; i < numberOfNodes; i++) {
            final NodePool nodePool = UtilityClass.getNodePoolInGroup(i,
                    groupNamesArray);
            if (nodePool != null) {
                logger.debug(
                        "Trying to fetch one node from pool {} where has {} nodes.",
                        nodePool.toString(), nodePool.size());
                final Node dataRecord = nodePool.borrowObject();
                if (dataRecord != null) {
                    UtilityClass.addDataRecordIntoDataSource(ADDED_NODES,
                            dataRecord);
                }
                if (nodePool.size() == 0) {
                    logger.error(
                            "Expecting {} nodes, but no nodes are left in the node pool: {}.",
                            numberOfNodes, nodePool);
                }
            }
        }
        final List<Node> list = new ArrayList<Node>();
        for (final DataRecord record : TafTestContext.getContext().dataSource(
                ADDED_NODES)) {
            final Node node = record.getFieldValue("Node");
            list.add(node);
        }
        logger.debug("Node data source {}", list);
    }
}
