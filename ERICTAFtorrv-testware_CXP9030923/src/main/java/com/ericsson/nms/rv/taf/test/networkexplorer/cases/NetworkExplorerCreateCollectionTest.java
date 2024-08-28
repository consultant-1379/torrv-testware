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

import java.util.List;

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
public class NetworkExplorerCreateCollectionTest extends TorTestCaseHelper
        implements TestCase {
    private final Logger logger = LoggerFactory
            .getLogger(NetworkExplorerCreateCollectionTest.class);

    @Inject
    private NetworkExplorerRestOperator neOperator;

    @BeforeTest
    @Parameters({ "data.csv.template", "testId", "testTitle" })
    public void setup(String networkExplorerFileName, String testId,
            String testTitle) {
        DataHandler.setAttribute(DATA_CSV_TEMPLATE_FILE_KEY,
                networkExplorerFileName);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "data_csv_template")
    @Test
    public void createCollection(
            @Input("collectionName") String collectionName,
            @Input("managedElementIdList") String managedElementIdListStr,
            @Input("query") String query, @Input("category") String category) {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
        logger.info(
                "Creating Collection. collectionName: {}, managedElementIdListStr: {}, query: {}",
                collectionName, managedElementIdListStr, query);

        final Category c = Category.valueOf(category);

        final List<String> managedElementIdList = neOperator
                .stringToList(managedElementIdListStr);

        final NetworkExplorerResponse response = neOperator.createCollection(
                managedElementIdList, collectionName, query, c);

        assertTrue(String.format(
                "Failed to create collection '%s'. Message: '%s'",
                collectionName, response.getErrorMessage()),
                response.isSuccess());
    }
}
