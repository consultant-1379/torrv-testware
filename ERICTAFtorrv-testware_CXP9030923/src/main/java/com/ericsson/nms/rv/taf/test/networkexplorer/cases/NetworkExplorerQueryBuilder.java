package com.ericsson.nms.rv.taf.test.networkexplorer.cases;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.NetworkExplorerUiOperator;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.QueryObject;

public class NetworkExplorerQueryBuilder extends TorTestCaseHelper implements
        TestCase {

    private final Logger logger = LoggerFactory
            .getLogger(NetworkExplorerQueryBuilder.class);

    private static final String USER_NOT_LOGIN_IN = "user is not logged in";

    @Inject
    private NetworkExplorerUiOperator networkExplorerUIOperator;

    @BeforeTest
    @Parameters({ "testId", "testTitle", "collectionName" })
    public void setup(final String testId, final String testTitle,
            final String collectionName) {
        logger.info("testId:{}, testTitle: {}", testId, testTitle);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
        DataHandler.setAttribute("collectionName", collectionName);
    }

    @DataDriven(name = "criteria_builder")
    @Test
    public void testQueryBuilderCollection(
            @Input("queryObject") final QueryObject queryObject) {

        setTestIdAndTitle();
        logger.info("Create collection through criteria builder. Query: {}",
                queryObject.toString());

        final String collectionName = DataHandler
                .getAttribute("collectionName").toString(); // "criteriaBuilder_collection";

        networkExplorerUIOperator.initNetworkExplorer();
        networkExplorerUIOperator.switchToCriteriaBuilder();
        networkExplorerUIOperator
                .clickManagedObjectAndWaitForDropdownButtonToAppear();

        logger.info("Building query in query builder");
        addQueryRootObject(networkExplorerUIOperator, queryObject);
        addQueryElementTree(networkExplorerUIOperator, queryObject.getChild());

        networkExplorerUIOperator.executeSearch();
        final List<Map<String, String>> results1 = networkExplorerUIOperator
                .getResults();
        networkExplorerUIOperator.hideObject(1);
        networkExplorerUIOperator.executeSearch();
        final List<Map<String, String>> results2 = networkExplorerUIOperator
                .getResults();
        saveAssertEquals(
                "The number of results before hiding object and after not differ 1",
                1, results1.size() - results2.size());

        networkExplorerUIOperator.switchToSearch();
        networkExplorerUIOperator.saveCollectionForNRows(collectionName, 1);

        networkExplorerUIOperator.clickViewAllCollections();
        networkExplorerUIOperator.clickSearch(collectionName);
        assertTrue("The saved collection returns empty result",
                !networkExplorerUIOperator.getResults().isEmpty());
    }

    @DataDriven(name = "criteria_builder")
    @Test
    public void testQueryBuilderSearch(
            @Input("queryObject") final QueryObject queryObject) {
        setTestIdAndTitle();
        final String searchName = DataHandler.getAttribute("collectionName")
                .toString();
        assertNotNull(USER_NOT_LOGIN_IN,
                networkExplorerUIOperator.initNetworkExplorer());
        networkExplorerUIOperator.switchToCriteriaBuilder();
        networkExplorerUIOperator
                .clickManagedObjectAndWaitForDropdownButtonToAppear();
        logger.info("Building query in query builder");
        addQueryRootObject(networkExplorerUIOperator, queryObject);
        addQueryElementTree(networkExplorerUIOperator, queryObject.getChild());
        networkExplorerUIOperator.executeSearch();
        final List<Map<String, String>> results1 = networkExplorerUIOperator
                .getResults();
        networkExplorerUIOperator.hideObject(1);
        networkExplorerUIOperator.executeSearch();
        final List<Map<String, String>> results2 = networkExplorerUIOperator
                .getResults();
        saveAssertEquals(
                "The number of results before hiding object and after not differ 1",
                1, results1.size() - results2.size());

        networkExplorerUIOperator.switchToSearch();
        networkExplorerUIOperator.saveSavedSearch(searchName);
        networkExplorerUIOperator.clickViewAllSavedSearches();
        networkExplorerUIOperator.clickSearch(searchName);
        assertTrue("The saved search returns empty result",
                !networkExplorerUIOperator.getResults().isEmpty());
    }

    public void addQueryRootObject(
            final NetworkExplorerUiOperator networkExplorerUIOperator,
            final QueryObject queryObject) {

        logger.info("Adding query root {} at ui input object index 1",
                queryObject.getName());
        networkExplorerUIOperator.selectMoiNameFromQueryBuilderSection(1,
                queryObject.getName());
    }

    public void addQueryElementTree(
            final NetworkExplorerUiOperator networkExplorerUIOperator,
            final QueryObject queryObject) {

        logger.info("Adding query sub-tree {}", queryObject.getName());
        // Query Builder is split into input sections using css class 'eaNetworkExplorer-wQueryItem-wrapper'
        int queryBuilderQueryItemNumber = 2;
        // Criteria input areas are detected by css class 'eaNetworkExplorer-wCriteriaAttribute'
        int criteriaInputAreaNumber = 1;

        // Add query item 2
        networkExplorerUIOperator.selectMoiNameFromQueryBuilderSection(
                queryBuilderQueryItemNumber, queryObject.getName());
        networkExplorerUIOperator.addCriteria(queryBuilderQueryItemNumber,
                criteriaInputAreaNumber, queryObject.getCriteriaList());

        criteriaInputAreaNumber += queryObject.getCriteriaList().size();
        QueryObject currentQueryObject = queryObject.getChild();

        while (currentQueryObject != null) {
            // Add query item 3 and subsequent query items.
            queryBuilderQueryItemNumber += 1;
            networkExplorerUIOperator.clickAddChildButton();
            networkExplorerUIOperator.selectMoiNameFromQueryBuilderSection(
                    queryBuilderQueryItemNumber, currentQueryObject.getName());
            networkExplorerUIOperator.addCriteria(queryBuilderQueryItemNumber,
                    criteriaInputAreaNumber,
                    currentQueryObject.getCriteriaList());
            criteriaInputAreaNumber += currentQueryObject.getCriteriaList()
                    .size();
            currentQueryObject = currentQueryObject.getChild();
        }
    }

    private void setTestIdAndTitle() {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
    }
}
