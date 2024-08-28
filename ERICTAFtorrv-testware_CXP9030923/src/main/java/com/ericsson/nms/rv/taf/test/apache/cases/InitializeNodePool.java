package com.ericsson.nms.rv.taf.test.apache.cases;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.nms.rv.taf.test.apache.operators.NodePool;
import com.ericsson.nms.rv.taf.test.apache.operators.NodePoolGroup;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;
import com.ericsson.nms.rv.taf.test.arne.operator.ArneOperator;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.CsvParser;
import com.ericsson.nms.rv.taf.test.netsim.NetsimOperator;

public class InitializeNodePool extends TorTestCaseHelper implements TestCase {

    private final static Logger logger = LoggerFactory
            .getLogger(InitializeNodePool.class);
    private static final String NODE_POOL_GROUP = "NodePoolGroup";

    @BeforeTest
    public void setup() {
        generateNodeCsvFromNetsim();

        final List<String> defaultNodePoolFileList = UtilityClass
                .getNodePoolFileList();
        setUpNodePoolGroup(defaultNodePoolFileList);
        final List<String> specialNodePoolFileList = UtilityClass
                .getSpecialNodePoolFileList();
        setUpNodePoolGroup(specialNodePoolFileList);
    }

    private static void setUpNodePoolGroup(final List<String> nodeCsvfiles) {
        for (int i = 0; i < nodeCsvfiles.size(); i++) {
            final NodePool nodePool = new NodePool();
            final String fileName = nodeCsvfiles.get(i);
            final String groupName = fileName;
            try {
                final CsvParser csvParser = new CsvParser(fileName);
                final List<Map<String, Object>> valueAsMaps = csvParser
                        .getValuesAsMaps();
                for (final Map<String, Object> map : valueAsMaps) {
                    // assign a node pool group to each node, so each node can
                    // be returned back to the correct node pool group
                    map.put(NODE_POOL_GROUP, groupName);
                    final Node node = UtilityClass.getObjectFromMap(Node.class,
                            map);
                    nodePool.returnObject(node);
                }
                NodePoolGroup.putNodePool(groupName, nodePool);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateNodeCsvFromNetsim() {
        logger.info("Fetching netsim XML and generating node csv file.");
        final boolean fetchNodes = UtilityClass.isFetchNodes();
        if (fetchNodes) {
            logger.info("Fetching nodes from netsim");
            final List<String> netsimList = UtilityClass.getNetsimNames();
            final List<String> simulationList = UtilityClass
                    .getNetsimSimulations();
            final List<String> simulationWithNodeCsvSuffix = UtilityClass
                    .appendNodeCsvSuffix(simulationList);

            logger.debug("Netsim(s): " + netsimList.toString());
            logger.debug("Simulations(s): " + simulationList.toString());

            if (netsimList.size() != 0) {
                final String[] netsimArray = UtilityClass
                        .toStringArray(netsimList);
                final String[] simulationsArray = UtilityClass
                        .toStringArray(simulationList);
                fetchNode(netsimArray, simulationsArray);
                generateNodeCsv(simulationsArray);
                setNodePool(simulationWithNodeCsvSuffix);
            } else {
                logger.error("Tried to fetch node from netsim but netsim defined in property is empty");
            }
        }
    }

    private void setNodePool(final List<String> list) {
        UtilityClass.setNodePoolFileList(list);
    }

    private void generateNodeCsv(final String[] simulationsArray) {
        logger.info("Start generating node csv files");
        for (final String simulation : simulationsArray) {
            logger.info("Start generating csv file for simulation: {}",
                    simulation);
            final ArneOperator arneOperator = UtilityClass
                    .getArneOperator(simulation);
            arneOperator.generateNodeCsv();
        }
        logger.info("Finish generating node csv files");
    }

    private void fetchNode(final String[] netsimNames,
            final String... simulations) {
        logger.info("Start fetching ARNE xml");
        for (final String netsim : netsimNames) {
            final NetsimOperator netsimOperator = UtilityClass
                    .getNetsimOperator(netsim, simulations);
            logger.info("Start creating arne files .");
            netsimOperator.createArneFile();
            logger.info("Start copying arne files to local.");
            netsimOperator.copyArneFileToLocal();
        }
        logger.info("Finish fetching ARNE xml");
    }

    @Context(context = Context.REST)
    @TestId(id = "Setup Step", title = "PRE-TEST-SETUP. Initialize node pool.")
    @Test
    public void initializeNodePool() {

    }
}
