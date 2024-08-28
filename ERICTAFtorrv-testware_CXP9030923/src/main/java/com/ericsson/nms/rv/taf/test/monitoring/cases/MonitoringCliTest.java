package com.ericsson.nms.rv.taf.test.monitoring.cases;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.CsvParser;
import com.ericsson.nms.rv.taf.test.monitoring.cases.dto.MonitoringResponse;
import com.ericsson.nms.rv.taf.test.monitoring.operators.MonitoringCliOperator;
import com.ericsson.nms.rv.taf.test.monitoring.operators.MonitoringPropertyKeys;
import com.google.inject.Inject;

/**
 * Created by ejocott on 25/08/2014.
 */
public class MonitoringCliTest extends TorTestCaseHelper implements TestCase {

    @Inject
    public static final String COMMAND_HEADER = "command";
    public static final String EXPECTED_BODY_RESPONSE_CONTAINS_HEADER = "expectedOut";
    public static final String SERVER_TYPE_HEADER = "serverType";

    @Inject
    private MonitoringCliOperator cliOperator;

    @BeforeTest
    @Parameters({ "commands.template.csv.file", "testId", "testTitle" })
    public void setup(String templateFileName, String testId, String testTitle) {
        DataHandler.setAttribute(
                MonitoringPropertyKeys.DO_CLI_COMMANDS_FILE_KEY,
                templateFileName);
        DataHandler.setAttribute("testId", testId);
        DataHandler.setAttribute("testTitle", testTitle);
    }

    @Test
    public void executeCliCommand() throws InterruptedException,
            TimeoutException {

        final String commandListCSVFileName = (String) DataHandler
                .getAttribute(MonitoringPropertyKeys.DO_CLI_COMMANDS_FILE_KEY);

        setTestcase(DataHandler.getAttribute("testId").toString(), DataHandler
                .getAttribute("testTitle").toString());

        CsvParser reader = null;
        try {
            reader = new CsvParser(commandListCSVFileName);
        } catch (final FileNotFoundException e) {
            fail("Cannot find file: {}", commandListCSVFileName);
        } catch (final IOException e) {
            fail("IOException reading file: {}", commandListCSVFileName);
        }
        final List<Map<String, Object>> csvLines = reader.getValuesAsMaps();

        for (final Map<String, Object> map : csvLines) {
            final String command = (String) map.get(COMMAND_HEADER);
            final String expectedOut = (String) map
                    .get(EXPECTED_BODY_RESPONSE_CONTAINS_HEADER);
            final String serverType = (String) map.get(SERVER_TYPE_HEADER);

            final MonitoringResponse response = cliOperator.executeCliCommand(
                    serverType, command, expectedOut);

            saveAssertTrue(response.getErrorMessage(), response.isSuccess());
        }
        cliOperator.resetCommandHelper();
    }
}
