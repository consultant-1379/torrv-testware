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

import static com.ericsson.nms.rv.taf.test.cmapache.operators.CmApachePropertyKeys.DATA_CSV_TEMPLATE_FILE_KEY;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.Category;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.NetworkExplorerRestOperator;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto.NetworkExplorerResponse;

/**
 * Tests Network Explorer.
 *
 * @author epaulki
 */
public class NetworkExplorerCreateSearchTest extends TorTestCaseHelper
        implements TestCase {
    private final Logger logger = LoggerFactory
            .getLogger(NetworkExplorerCreateSearchTest.class);

    @Inject
    private NetworkExplorerRestOperator neOperator;

    @BeforeTest
    @Parameters({ "data.csv.template", "testId", "testTitle" })
    public void setup(final String networkExplorerFileName,
            final String testId, final String testTitle) {
        DataHandler.setAttribute(DATA_CSV_TEMPLATE_FILE_KEY,
                networkExplorerFileName);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "data_csv_template")
    @Test
    public void createSearch(@Input("searchName") final String searchName,
            @Input("searchQuery") final String query,
            @Input("category") final String category) {

        logger.debug("networkExplorerCreate. searchName: {}, searchQuery: {}",
                searchName, query);
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
        final Category c = Category.valueOf(category);
        final NetworkExplorerResponse response = neOperator.createSearch(
                searchName, query, c);

        if (!response.isSuccess()) {
            logger.warn("Failed to create search '{}'. Message: '{}'",
                    searchName, response.getErrorMessage());
        }
        assertTrue(response.isSuccess());
    }
}