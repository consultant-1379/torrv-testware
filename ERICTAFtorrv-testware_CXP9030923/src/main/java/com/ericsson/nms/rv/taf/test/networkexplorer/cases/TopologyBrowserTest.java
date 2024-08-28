package com.ericsson.nms.rv.taf.test.networkexplorer.cases;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.apache.cases.UtilityClass;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.TopologyBrowserUiOperator;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.NetworkExplorerResponse;
import com.google.inject.Inject;

public class TopologyBrowserTest extends TorTestCaseHelper implements TestCase {
    private final Logger logger = LoggerFactory
            .getLogger(TopologyBrowserTest.class);
    private static final String ADDED_NODES = "added_nodes";

    @Inject
    private TopologyBrowserUiOperator topologyBrowserUIOperator;

    @BeforeTest
    @Parameters({ "testId", "testTitle", "topology.browser.data" })
    public void setup(final String testId, final String testTitle,
            final String fileName) {
        logger.info("testId:{}, testTitle: {}", testId, testTitle);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
        DataHandler.setAttribute("dataprovider.topology_data.location",
                fileName);
        DataHandler.setAttribute("dataprovider.topology_data.type", "csv");
    }

    @DataDriven(name = "topology_data")
    @Test
    public void testTopologyBrowser(
            @Input("searchValueTemplate") final String searchValueTemplate,
            @Input("expandTreeValue") final String expandTreeValue,
            @Input("filterAttribute") final String filterAttribute,
            @Input("updateAttributeValue") final String updateAttributeValue,
            @Input("searchMeContext") final String searchMeContext) {

        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        final List<Node> nodesList = UtilityClass
                .getNodeInDataSource(ADDED_NODES);
        final Node node = nodesList.get(0);
        assertNotNull("No Node available for search query.", node);
        final String searchValue = String.format(searchValueTemplate,
                node.getManagedElementId());

        // NetworkExplorer UI
        topologyBrowserUIOperator.initNetworkExplorer();

        logger.info("Do search with value: {}", searchValue);
        assertStepIsTrue(topologyBrowserUIOperator.doSearch(searchValue));

        logger.info("Open result in topology browser.");
        assertStepIsTrue(topologyBrowserUIOperator.openWithTopologyBrowser(1));

        //TopologyBrowser UI
        logger.info("Expanding topology browser tree widget on value: {}",
                expandTreeValue);
        assertStepIsTrue(topologyBrowserUIOperator
                .expandTreeByAttributeName(expandTreeValue));

        logger.info("Expanding topology browser sliding attributes column.",
                expandTreeValue);
        assertStepIsTrue(topologyBrowserUIOperator.expandAttributesColumn());

        logger.info("Filtering attribute on filter: {}", filterAttribute);
        assertStepIsTrue(topologyBrowserUIOperator
                .filterAttributesByName(filterAttribute));

        assertStepIsTrue(topologyBrowserUIOperator.clickEditAttributesLink());

        assertStepIsTrue(topologyBrowserUIOperator.updateAttribute(
                filterAttribute, updateAttributeValue));

        assertStepIsTrue(topologyBrowserUIOperator.saveChanges());

        assertStepIsTrue(topologyBrowserUIOperator.clickSearchForAnObject());

        // Network Explorer UI
        assertStepIsTrue(topologyBrowserUIOperator.doSearch(searchMeContext
                + " " + filterAttribute + " = " + updateAttributeValue));

        logger.info("Asserting that network explorer results are not empty.");
        assertTrue(!topologyBrowserUIOperator.getResults().isEmpty());
    }

    private NetworkExplorerResponse assertStepIsTrue(
            final NetworkExplorerResponse response) {
        if (!response.isSuccess()) {
            logger.warn(response.getErrorMessage());
        }
        assertTrue(response.getErrorMessage(), response.isSuccess());
        return response;
    }
}
