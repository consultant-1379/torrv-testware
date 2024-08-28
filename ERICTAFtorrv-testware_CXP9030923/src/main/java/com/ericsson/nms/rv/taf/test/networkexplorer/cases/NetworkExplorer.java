package com.ericsson.nms.rv.taf.test.networkexplorer.cases;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.NetworkExplorerRestOperator;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.NetworkExplorerUiOperator;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.NetworkExplorerResponse;
import com.google.inject.Inject;

/**
 * Created by ewandaf on 04/12/14.
 */
public class NetworkExplorer extends TorTestCaseHelper implements TestCase {

    private final Logger logger = LoggerFactory
            .getLogger(NetworkExplorer.class);

    private static final String INTERSECT_TWO_SEARCHES_TEMPLATE = "select search %s from search %s";
    private static final String INTERSECT_TWO_COLLECTIONS_TEMPLATE = "select collection %s from collection %s";

    @Inject
    private NetworkExplorerUiOperator networkExplorerUIOperator;

    @Inject
    private NetworkExplorerRestOperator networkExplorerRestOperator;

    @BeforeTest
    @Parameters({ "testId", "testTitle" })
    public void setup(final String testId, final String testTitle) {
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @Test
    @Parameters({ "searchA", "searchB" })
    public void testIntersectingDifferentSavedSearches(final String searchA,
            final String searchB) {
        setTestIdAndTitle();
        networkExplorerUIOperator.initNetworkExplorer();
        logger.info(
                "Test intersecting saved searches using the following command: {}",
                String.format(INTERSECT_TWO_SEARCHES_TEMPLATE, searchA, searchB));
        final NetworkExplorerResponse responseA = networkExplorerRestOperator
                .getSearch(searchA);
        final NetworkExplorerResponse responseB = networkExplorerRestOperator
                .getSearch(searchB);
        final String searchQueryA = networkExplorerRestOperator
                .getSearchQueryFromSearchResponse(responseA);
        final String searchQueryB = networkExplorerRestOperator
                .getSearchQueryFromSearchResponse(responseB);

        NetworkExplorerResponse searchResponse = new NetworkExplorerResponse();
        searchResponse = networkExplorerUIOperator.doSearch(String.format(
                INTERSECT_TWO_SEARCHES_TEMPLATE, searchA, searchB));
        assertTrue(searchResponse.getErrorMessage(), searchResponse.isSuccess());

        final List<Map<String, String>> results = networkExplorerUIOperator
                .getResults();
        assertNotNull("The searchQuery A is null", searchQueryA);
        assertNotNull("The searchQuery B is null", searchQueryB);
        assertTrue("Intersecting two searches returns no results.",
                results.size() == 1);
        final String networkElementName = results.get(0).get("Name");
        assertTrue(
                "The intersecting result is not correct",
                searchQueryA.contains(networkElementName)
                        || searchQueryB.contains(networkElementName));
    }

    @Test
    @Parameters({ "collectionA", "collectionB" })
    public void testIntersectingDifferentSavedCollections(
            final String collectionA, final String collectionB) {
        setTestIdAndTitle();

        networkExplorerUIOperator.initNetworkExplorer();
        logger.info(
                "Test intersecting saved collections using the following command: {}",
                String.format(INTERSECT_TWO_COLLECTIONS_TEMPLATE, collectionA,
                        collectionB));
        NetworkExplorerResponse searchResponse = new NetworkExplorerResponse();
        searchResponse = networkExplorerUIOperator.doSearch(String.format(
                INTERSECT_TWO_COLLECTIONS_TEMPLATE, collectionA, collectionB));
        assertTrue(searchResponse.getErrorMessage(), searchResponse.isSuccess());

        final NetworkExplorerResponse responseA = networkExplorerRestOperator
                .getCollection(collectionA, null);
        final NetworkExplorerResponse responseB = networkExplorerRestOperator
                .getCollection(collectionB, null);
        final List<String> collectionResultA = networkExplorerRestOperator
                .getElementListFromCollectionResponse(responseA);
        final List<String> collectionResultB = networkExplorerRestOperator
                .getElementListFromCollectionResponse(responseB);

        final List<String> commonCollectionResult = getCommonElements(
                collectionResultA, collectionResultB);

        final List<Map<String, String>> results = networkExplorerUIOperator
                .getResults();

        logger.info(
                "NetworkExplorer results: {}. Common collection results: {}",
                results.toString(), commonCollectionResult.toString());
        assertEquals("The size of real result is not correct",
                commonCollectionResult.size(), results.size());

        for (final Map<String, String> row : results) {
            final String networkElementInRow = row.get("Network Element");
            saveAssertTrue(String.format(
                    "%s is not the common element between two collections",
                    networkElementInRow),
                    commonCollectionResult.contains(networkElementInRow));
        }
    }

    private List<String> getCommonElements(final List<String> searchResultA,
            final List<String> searchResultB) {
        logger.info("Getting common results between two lists generated from search results.");
        final List<String> toReturn = new ArrayList<String>();
        for (final String a : searchResultA) {
            if (searchResultB.contains(a)) {
                toReturn.add(a);
            }
        }
        return toReturn;
    }

    private void setTestIdAndTitle() {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
    }
}
