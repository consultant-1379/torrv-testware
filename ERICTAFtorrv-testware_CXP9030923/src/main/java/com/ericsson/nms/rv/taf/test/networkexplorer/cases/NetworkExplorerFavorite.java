package com.ericsson.nms.rv.taf.test.networkexplorer.cases;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.NetworkExplorerUiOperator;

public class NetworkExplorerFavorite extends TorTestCaseHelper implements
        TestCase {

    private final Logger logger = LoggerFactory
            .getLogger(NetworkExplorerFavorite.class);

    @Inject
    private NetworkExplorerUiOperator networkExplorerUIOperator;

    private static final String USER_NOT_LOGIN_IN = "user is not logged in";

    @BeforeTest
    @Parameters({ "testId", "testTitle" })
    public void setup(String testId, String testTitle) {
        logger.info("testId:{}, testTitle: {}", testId, testTitle);
        // DataHandler.setAttribute("favorite", favorite);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @Test
    @Parameters({ "favorite" })
    public void testCollectionFavorite(String favorite) {
        setTestIdAndTitle();
        final String collectionName = favorite;
        assertNotNull(USER_NOT_LOGIN_IN,
                networkExplorerUIOperator.initNetworkExplorer());
        networkExplorerUIOperator.saveCollectionAsFavorite(collectionName);
        networkExplorerUIOperator.initNetworkExplorer();
        final List<String> favoriteCollections = networkExplorerUIOperator
                .getFavoriteCollections();
        // select again to de-select
        networkExplorerUIOperator.saveCollectionAsFavorite(collectionName);
        assertTrue(
                "The favorite collection doesn't has collection FavoriteTest",
                favoriteCollections.contains(collectionName));
    }

    @Test
    @Parameters({ "favorite" })
    public void testSearchFavorite(String favorite) {
        setTestIdAndTitle();
        final String searchName = favorite;
        assertNotNull(USER_NOT_LOGIN_IN,
                networkExplorerUIOperator.initNetworkExplorer());
        networkExplorerUIOperator.saveSearchAsFavorite(searchName);
        networkExplorerUIOperator.initNetworkExplorer();
        final List<String> favoriteCollections = networkExplorerUIOperator
                .getFavoriteSearches();
        // select again to de-select
        networkExplorerUIOperator.saveSearchAsFavorite(searchName);
        assertTrue("The favorite search doesn't has search " + searchName,
                favoriteCollections.contains(searchName));
    }

    private void setTestIdAndTitle() {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
    }
}
