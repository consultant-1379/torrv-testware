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
package com.ericsson.nms.rv.taf.test.networkexplorer.cases;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.NetworkExplorerRestOperator;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.NetworkExplorerResponse;

/**
 * Tests Network Explorer.
 *
 * @author epaulki
 */
public class NetworkExplorerDeleteSearchTest extends TorTestCaseHelper
        implements TestCase {
    private final Logger logger = LoggerFactory
            .getLogger(NetworkExplorerDeleteSearchTest.class);

    @Inject
    private NetworkExplorerRestOperator neOperator;

    @BeforeTest
    @Parameters({ "networkexplorer.delete.search.csv.file", "testId",
            "testTitle" })
    public void setup(String networkExplorerFileName, String testId,
            String testTitle) {
        DataHandler.setAttribute("dataprovider.delete_search.location",
                networkExplorerFileName);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "delete_search")
    @Test
    public void deleteSearch(@Input("searchName") String searchName) {

        logger.debug("networkExplorer delete. searchName: {}", searchName);
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        NetworkExplorerResponse response = neOperator
                .deleteSearch(searchName);

        assertTrue(String.format(
                "Failed to delete search '%s'. Message: '%s'. Body '%s'",
                searchName, response.getErrorMessage(), response.getBody()),
                response.isSuccess());

        response = neOperator.getSearch(searchName);
        assertFalse(String.format("Search %s still exists after deleted. Message: %s", searchName, response.getErrorMessage()),
                response.isSuccess());
    }
}
