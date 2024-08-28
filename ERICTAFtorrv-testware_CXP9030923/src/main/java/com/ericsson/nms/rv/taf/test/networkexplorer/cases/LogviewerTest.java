package com.ericsson.nms.rv.taf.test.networkexplorer.cases;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.networkexplorer.operators.LogviewerUiOperator;
import com.google.inject.Inject;

public class LogviewerTest extends TorTestCaseHelper implements TestCase {

    private final Logger logger = LoggerFactory.getLogger(LogviewerTest.class);

    @Inject
    private LogviewerUiOperator logviewerUIOperator;

    private static final String USER_NOT_LOGIN_IN = "User is not logged in";
    private static final String SEARCH_TERM = "?hrea* AND \"Handler\"";
    private static final String SEARCH_TERM_1 = "?hrea*";
    private static final String SEARCH_TERM_2 = "\"Handler\"";
    
    @BeforeTest
    @Parameters({ "testId", "testTitle" })
    public void setup(final String testId, final String testTitle) {
        logger.info("testId:{}, testTitle: {}", testId, testTitle);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @Context(context = Context.UI)
    @Test
    public void testLogviewer() {
        setTestIdAndTitle();
        logger.info("Opening log viewer page");
        assertStepNotNull(USER_NOT_LOGIN_IN,
                logviewerUIOperator.initLogViewer());

        logviewerUIOperator.doSearch(SEARCH_TERM);

        final Set<String> defaultHeaderSet = logviewerUIOperator.getHeaders();

        final Set<String> newColumns = new HashSet<String>(Arrays.asList(
                "Source", "Source_host", "Source_path"));
        logger.info("Selecting columns 'Source', 'Source_host' and 'Source_path");
        logviewerUIOperator.selectColumns(newColumns);

        logviewerUIOperator.doRefresh();

        final List<Map<String, String>> results = logviewerUIOperator
                .getResults();
        assertStepIsTrue("The search result is empty or null.",
                !(results == null) && !results.isEmpty());
        final Map<String, String> firstRow = results.get(0);

        boolean foundMessage = false;
        for (int i = 0; i < results.size(); i++) {
            final String messageColumnValue = results.get(i).get("message");
            if (messageColumnValue.contains(SEARCH_TERM_1)
                    && messageColumnValue.contains(SEARCH_TERM_2)) {
                foundMessage = true;
                break;
            }
        }

        assertStepIsTrue(
                "The search result doesn't contain " + SEARCH_TERM,
                foundMessage);

        final Set<String> newHeaderSet = firstRow.keySet();
        for (final String newHeader : newColumns) {
            if (defaultHeaderSet.contains(newHeader)) {
                assertStepIsTrue(String.format(
                        "The column %s is still showing after unchecked",
                        newHeader), !newHeaderSet.contains(newHeader));
            } else {
                assertStepIsTrue(
                        String.format(
                                "The column %s is not showing after checked",
                                newHeader), newHeaderSet.contains(newHeader));
            }
        }
        assertStepIsTrue("The search input text was successfully cleared.",
                logviewerUIOperator.clickToClearSearchText().isSuccess());
    }

    @Context(context = Context.UI)
    @Test
    public void testLogviewerSelectTime() {
        setTestIdAndTitle();
        logger.info("Opening log viewer page");
        assertStepNotNull(USER_NOT_LOGIN_IN,
                logviewerUIOperator.initLogViewer());
        final Calendar calendar = new GregorianCalendar();
        logviewerUIOperator.setEndTime(calendar);
        calendar.add(Calendar.HOUR, -4);
        logviewerUIOperator.setStartTime(calendar);
        logviewerUIOperator.doSearch();
        assertStepIsTrue("No log generated", !logviewerUIOperator.getResults()
                .isEmpty());
    }

    private void assertStepIsTrue(final String errorMessage,
            final boolean condition) {
        if (!condition) {
            logger.warn(errorMessage);
        }
        assertTrue(condition);
    }

    private void assertStepNotNull(final String errorMessage,
            final Object object) {
        if (object == null) {
            logger.warn(errorMessage);
        }
        assertNotNull(object);
    }

    private void setTestIdAndTitle() {
        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());
    }
}
