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
public class NetworkExplorerDoQueryTest extends TorTestCaseHelper implements
        TestCase {
    private final Logger logger = LoggerFactory
            .getLogger(NetworkExplorerDoQueryTest.class);

    @Inject
    private NetworkExplorerRestOperator neOperator;

    @BeforeTest
    @Parameters({ "networkexplorer.do.query.csv.file", "testId", "testTitle" })
    public void setup(String fileName, String testId, String testTitle) {
        DataHandler.setAttribute("dataprovider.network_explorer.location",
                fileName);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @DataDriven(name = "network_explorer")
    @Test
    public void doQuery(@Input("query") String query) {

        logger.debug("doQuery. query: {}", query);
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        final NetworkExplorerResponse response = neOperator.doQuery(query);

        assertTrue(String.format("Failed to do query '%s', message: '%s'",
                query, response.getErrorMessage()), response.isSuccess());
    }
}
