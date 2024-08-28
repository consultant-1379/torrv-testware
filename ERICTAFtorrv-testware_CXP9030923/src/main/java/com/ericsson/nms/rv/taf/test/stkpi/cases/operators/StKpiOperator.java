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
package com.ericsson.nms.rv.taf.test.stkpi.cases.operators;

import java.io.*;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.json.simple.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.*;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.rv.taf.test.cmapache.operators.CmCommandUiOperator;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.CmResponse;
import com.ericsson.nms.rv.taf.test.fm.operators.FmUiOperator;
import com.ericsson.nms.rv.taf.test.netsim.NetsimOperator;
import com.ericsson.nms.rv.taf.test.netsim.NetsimResponse;

public class StKpiOperator {

    private final Logger logger = LoggerFactory.getLogger(StKpiOperator.class);
    private static final String SYNC_COMMAND_TEMPLATE = "/opt/ericsson/enmutils/bin/node_populator manage %s";
    private static final String WORKLOAD_LIST_ALL_JSON = "/opt/ericsson/enmutils/bin/workload list all --json";
    private static final String GET_NODES_COLLECTING_ROP = "find /ericsson/pmic1/XML/* -maxdepth 0 -type d -mmin -15 | wc -l";
    private static final String GET_NODES_ADDED_TO_ENM = "find /ericsson/pmic1/XML/* -maxdepth 0 -type d | wc -l";
    private static final String PM_COUNTER_XML_DIR = "/ericsson/pmic1/XML";
    private static final String FIND_PM_COUNTER_XML_DIR = "ls "
            + PM_COUNTER_XML_DIR;
    private static final String REGEX_IP_ADDRESS = "\\d*\\.\\d*\\.\\d*\\.\\d*";
    private static final String REGEX_NUMBER = "^\\d*";
    private static final String NBALARM_IP_COMMAND = "cat /etc/hosts | grep svc-1-nbalarmirp";
    private static final String VISINAMING_PUB_IP_COMMAND = "cat /etc/hosts | grep visinamingnb-pub";
    private static final String TEST_CLIENT_CD_COMMAND = "cd /opt/ericsson/com.ericsson.oss.nbi.fm/test_client/";
    private static final String TEST_CLIENT_SUBSCRIBE_COMMAND_TEMPLATE = "./testclient.sh subscribe category 1f1 nshost %s";
    private static final String TEST_CLIENT_SUBSCRIBE_RESPONSE = "connection success";
    private static final String PRINT_USER_HOSTNAME_COMMAND = "echo $USER@$HOSTNAME";

    @Inject
    FmUiOperator fmuiOperator;

    @Inject
    CmCommandUiOperator cmUIOperator;

    /**
     * @param identifier
     * @param range
     *            optional field in the format &lt;number&gt; or
     *            &ltnumber&gt;-&ltnumber&gt;. May be null or blank.
     * @return
     */
    public StKpiResponse doNetworkSync(final String identifier, String range) {

        final StKpiResponse response = new StKpiResponse();
        response.setSuccess(true);

        if (range == null || range.trim().equals("")) {
            logger.debug("range is null or empty");
            range = "";
        } else if (!range.matches("\\d+-\\d+|\\d+")) {
            logger.debug("range is incorrect format");
            response.setSuccess(false);
            response.addErrorMessage(String
                    .format("The range value passed to the operator was not in the format <integer>-<integer> or <integer>: %s",
                            range));
            return response;
        }

        String command = String.format(SYNC_COMMAND_TEMPLATE, identifier);
        if (range != "") {
            command += " " + range;
        }
        command += " verbose";
        logger.info("Executing command: {}", command);

        CLICommandHelper cli = null;
        String cliResp = "";
        try {
            cli = getCommandHelperMs();
            cliResp = cli.simpleExec(command);
        } catch (final Exception e) {
            logger.warn("Exception with CLICommandHelper. Message: {}",
                    e.getMessage());
        } finally {
            cli.disconnect();
        }
        response.setOutput(cliResp);

        // Parse values from result string
        final Pattern syncPattern = Pattern
                .compile("NODES MANAGED: (\\d*)/(\\d*)");
        final Pattern resultPattern = Pattern
                .compile("MIN: (\\d*.\\d*)s  AVG: (\\d*.\\d*)s  MAX: (\\d*.\\d*)s");
        final Pattern timePattern = Pattern
                .compile("EXECUTION TIME: (\\d*)h:(\\d*)m:(\\d*)s");
        final Matcher syncMatcher = syncPattern.matcher(cliResp);
        final Matcher resultMatcher = resultPattern.matcher(cliResp);
        final Matcher timeMatcher = timePattern.matcher(cliResp);

        int totalNodes, syncedNodes;
        int networkSyncTimeHr, networkSyncTimeMin, networkSyncTimeSec;
        final int networkSyncTimeTotalSec;
        float minSyncTime, avgSyncTime, maxSyncTime;

        if (syncMatcher.find()) {
            syncedNodes = Integer.parseInt(syncMatcher.group(1));
            totalNodes = Integer.parseInt(syncMatcher.group(2));

            response.setTotalNodes(totalNodes);
            response.setSyncedNodes(syncedNodes);
        } else {
            response.setSuccess(false);
            response.addErrorMessage("Failed to find number of nodes synced in node_populator response.");
        }

        if (resultMatcher.find()) {
            minSyncTime = Float.parseFloat(resultMatcher.group(1));
            avgSyncTime = Float.parseFloat(resultMatcher.group(2));
            maxSyncTime = Float.parseFloat(resultMatcher.group(3));

            response.setMinSyncTime(minSyncTime);
            response.setAvgSyncTime(avgSyncTime);
            response.setMaxSyncTime(maxSyncTime);
        } else {
            response.setSuccess(false);
            response.addErrorMessage("Failed to find min, avg and max sync time in node_populator response.");
        }

        if (timeMatcher.find()) {
            networkSyncTimeHr = Integer.parseInt(timeMatcher.group(1));
            networkSyncTimeMin = Integer.parseInt(timeMatcher.group(2));
            networkSyncTimeSec = Integer.parseInt(timeMatcher.group(3));
            networkSyncTimeTotalSec = (networkSyncTimeHr * 60 * 60)
                    + (networkSyncTimeMin * 60) + (networkSyncTimeSec);
            response.setNetworkSyncTimeSecs(networkSyncTimeTotalSec);
        } else {
            response.setSuccess(false);
            response.addErrorMessage("Failed to find total sync time in node_populator response.");
        }
        return response;
    }

    public boolean writeResponseToFile(final StKpiResponse response) {
        final Path propertiesFile = Paths.get(System.getProperty("user.dir")
                + File.separator + "response.obj");
        logger.info("Saving file: {}", propertiesFile);
        OutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            try {
                fileOutputStream = Files.newOutputStream(propertiesFile);
                objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                return true;
            } finally {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            }
        } catch (final IOException e) {
            logger.error("Failed to write response to properties file");
            return false;
        }
    }

    public StKpiResponse readResponseFromFile() {
        final Path propertiesFile = Paths.get(System.getProperty("user.dir")
                + File.separator + "response.obj");
        logger.info("Reading file: {}", propertiesFile);
        InputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            try {
                fileInputStream = Files.newInputStream(propertiesFile);
                objectInputStream = new ObjectInputStream(fileInputStream);
                final StKpiResponse response = (StKpiResponse) objectInputStream
                        .readObject();
                return response;
            } finally {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            }
        } catch (final ClassNotFoundException e) {
            logger.error("Failed to read response object from file. Response object does not exist in file.");
            return null;
        } catch (final IOException e) {
            logger.error("Failed to read response object from file. File does not exist.");
            return null;
        }
    }

    public StKpiResponse verifyResponseFileExists() {
        final Path propertiesFile = Paths.get(System.getProperty("user.dir")
                + File.separator + "response.obj");
        logger.info("Checking existence of file: {}", propertiesFile);
        final StKpiResponse response = new StKpiResponse();
        response.setSuccess(propertiesFile.toFile().exists());
        return response;
    }

    /**
     * Runs command 'worklist list all --json' and returns the result as a
     * json.org.simple.JSONObject.
     *
     * @return JSONObject containing result from workload command.
     */
    public JSONObject getWorkloadData() {
        logger.info("Getting workload tool node information");
        CLICommandHelper cli = null;
        int exitValue = 0;
        String resultString = "";

        try {
            cli = getCommandHelperMs();
            logger.info("Got CLICommandHelper. Calling command: {}",
                    WORKLOAD_LIST_ALL_JSON);
            resultString = cli.simpleExec(WORKLOAD_LIST_ALL_JSON);
            exitValue = cli.getCommandExitValue();
            logger.info("Command exit value {}, result string: {}", exitValue,
                    resultString);
        } catch (final Exception e) {
            logger.warn("Error with CLICommandHelper. Message: {}",
                    e.getMessage());
            return null;
        } finally {
            cli.disconnect();
        }

        if (exitValue != 0) {
            logger.warn(
                    "Failed to get workload data from command '{}'. Exit value: {}, Return string: {}.",
                    WORKLOAD_LIST_ALL_JSON, exitValue, resultString);
            return null;
        }
        logger.info("Got workload data.");

        //Workaround for bug http://jira-nam.lmera.ericsson.se/browse/TORF-60697
        if (!resultString.startsWith("{")) {
            resultString = resultString.substring(resultString.indexOf("{"));
        }

        final JSONObject returnObj = (JSONObject) JSONValue.parse(resultString);
        if (returnObj == null) {
            logger.warn(
                    "Failed to parse results from command to JSON. String value: {}",
                    resultString);
            return null;
        }
        return returnObj;
    }

    /**
     * Given the workloadData JSON, this method searches for the node at the
     * indexes supplied. JSON is structured as netsims/simulations/nodes/. Note
     * that objects are not sorted in JSON so this may give different return
     * values for the same input index set.
     *
     * @param netsimIndex
     * @param simulationIndex
     * @param nodeIndex
     * @param workloadData
     * @return
     */
    public JSONObject getNode(final int netsimIndex, final int simulationIndex,
            final int nodeIndex, final JSONObject workloadData) {

        final JSONArray netsims = (JSONArray) workloadData.get("netsims");
        if (netsims == null) {
            logger.warn("Failed to get netsims array from workload JSON");
            return null;
        }

        if (netsims.size() == 0 || netsims.size() - 1 < netsimIndex) {
            logger.warn("'{}' netsims found. There is no netsim at index '{}'",
                    netsims.size(), netsimIndex);
            return null;
        }

        final JSONObject netsim = (JSONObject) netsims.get(netsimIndex);
        final JSONArray simulations = (JSONArray) netsim.get("simulations");

        if (simulations == null) {
            logger.warn(
                    "Failed to get simulations object from netsim at index '{}'. Netsim name: '{}'",
                    netsimIndex, netsim.get("name"));
            return null;
        }

        if (simulations.size() == 0 || simulations.size() - 1 < simulationIndex) {
            logger.warn(
                    "'{}' simulations found. There is no simulations at index '{}'",
                    simulations.size(), simulationIndex);
            return null;
        }

        final JSONObject simulation = (JSONObject) simulations
                .get(simulationIndex);
        final JSONArray nodes = (JSONArray) simulation.get("nodes");

        if (nodes == null) {
            logger.warn(
                    "Failed to get nodes array from simulation at index '{}'. Simulation name: '{}'",
                    simulationIndex, simulation.get("name"));
            return null;
        }

        if (nodes.size() == 0 || nodes.size() - 1 < nodeIndex) {
            logger.warn("'{}' nodes found. There is no nodes at index '{}'",
                    nodes.size(), nodeIndex);
            return null;
        }

        return (JSONObject) nodes.get(nodeIndex);
    }

    private CLICommandHelper getCommandHelperMs() throws StKpiOperatorException {
        final Host ms1Host = HostConfigurator.getMS();
        if (ms1Host == null) {
            logger.warn("Failed to get MS host details from HostConfigurator.");
            throw new StKpiOperatorException(
                    "Failed to get MS host details from HostConfigurator.");
        }
        logger.info("Connecting to MS1. Details: {}:{}, {}/{}",
                ms1Host.getIp(), ms1Host.getPort().get(Ports.SSH),
                ms1Host.getUser(), ms1Host.getPass());

        return new CLICommandHelper(ms1Host);
    }

    private CLICommandHelper getCommandHelperSvc1()
            throws StKpiOperatorException {
        final CLICommandHelper cli = getCommandHelperMs();
        final Host svc1 = HostConfigurator.getSVC1();
        if (svc1 == null) {
            logger.warn("Failed to get SVC-1 host details from HostConfigurator.");
            throw new StKpiOperatorException(
                    "Failed to get SVC-1 host details from HostConfigurator.");
        }
        logger.info("SVC1. Details: {}:{}, {}/{}", svc1.getIp(), svc1.getPort()
                .get(Ports.SSH), svc1.getUser(), svc1.getPass());

        logger.info("Current user@server: {}",
                cli.execute(PRINT_USER_HOSTNAME_COMMAND));
        logger.info("hopping to svc1");
        cli.newHopBuilder().hop(svc1).build();
        logger.info("Hopped to {}", cli.execute(PRINT_USER_HOSTNAME_COMMAND));
        return cli;
    }

    public StKpiResponse subscribeToNMSAndSendFmAlarm(
            final NetsimOperator netsimOperator, final String simulationName,
            final String nodeName, final String alarmProblem) {

        logger.info("Subscribe to NMS and send FM alarm");
        final StKpiResponse response = new StKpiResponse();
        NetsimResponse netsimResponse;
        final String commandResponse = "";
        CLICommandHelper cmdHelper = null;

        final Host nbAlarmHost = DataHandler.getHostByName("nbalarmirp_1");
        if (nbAlarmHost == null) {
            return getFailedResponse("Could not find host nbalarmirp_1 in host.properties.json file");
        }

        try {
            cmdHelper = getCommandHelperSvc1();

            logger.info("Getting IP address for svc-1-nbalarmirp");
            nbAlarmHost.setIp(executeAndExtractResult(cmdHelper, 0,
                    "Failed to get IP for svc-1-nbalarmirp",
                    NBALARM_IP_COMMAND, REGEX_IP_ADDRESS));

            logger.info("Hopping to svc-1-nbalarmirp");
            cmdHelper.newHopBuilder().hop(nbAlarmHost).build();
            logger.info("Hopped to {}",
                    cmdHelper.execute(PRINT_USER_HOSTNAME_COMMAND));

            logger.info("Getting IP address for visinamingnb-pub");
            final String visinamingnbPubIp = executeAndExtractResult(cmdHelper,
                    0, "Failed to get IP address for visinamingnb-pub",
                    VISINAMING_PUB_IP_COMMAND, REGEX_IP_ADDRESS);

            logger.info("Changing to test_client directory");
            execute(cmdHelper,
                    0,
                    "Failed to change to FM subscription test_client directory.",
                    TEST_CLIENT_CD_COMMAND);

            logger.info("Subscribing to FM using test_client");
            interactAndExtractResult(cmdHelper, String.format(
                    TEST_CLIENT_SUBSCRIBE_COMMAND_TEMPLATE, visinamingnbPubIp),
                    0, TEST_CLIENT_SUBSCRIBE_RESPONSE, 10,
                    "Failed to subscribe to NMS via test_client app.");

        } catch (final Exception e) {
            logger.info(
                    "{} was thrown while subscribing to the NMS. Message: ", e
                            .getClass().getSimpleName(), e.getMessage());

            e.printStackTrace();
            return getFailedResponse(String.format(
                    "%s was thrown while subscribing to the NMS. Message: %s",
                    e.getClass().getSimpleName(), e.getMessage()));
        } finally {
            cmdHelper.disconnect();
        }

        netsimResponse = netsimOperator.sendAlarm(simulationName, nodeName,
                alarmProblem);
        if (!netsimResponse.isSuccess()) {
            logger.info("Failed to send alarm '{}'. Message: {}", alarmProblem,
                    netsimResponse.getErrorMessage());
            response.addErrorMessage(netsimResponse.getErrorMessage());
            response.setSuccess(false);
            return response;
        }

        response.setTimeAlarmSent(netsimResponse.getTimeAlarmSent());
        response.setOutput(commandResponse);
        response.setSuccess(true);
        return response;
    }

    public StKpiResponse verifyAlarmVisibleWithinFiveSeconds(
            final String alarmProblem, final String node,
            final String timeAlarmSent, final String nmsCommandOutput) {
        logger.info("Verify alarm is visible within 5 seconds");
        final StKpiResponse response = new StKpiResponse();
        int matchingCommandResponse = 0;
        String alarmRecievedMatch = "";

        logger.debug("Alarm command output: {}", nmsCommandOutput);
        final String[] alarmCommandResponses = nmsCommandOutput
                .split("\\*\\*\\* END \\*\\*\\*");
        for (int i = 0; i < alarmCommandResponses.length; i++) {
            if (alarmCommandResponses[i].contains(alarmProblem)
                    && alarmCommandResponses[i].contains(node)) {
                logger.info(
                        "AlarmProblem: {} and node: {} have been found in the NMS output.",
                        alarmProblem, node);
                matchingCommandResponse = i;
                break;
            } else {
                matchingCommandResponse = -1;
            }
        }
        if (matchingCommandResponse == -1) {
            response.setSuccess(false);
            response.addErrorMessage(String
                    .format("AlarmProblem '%s' and node '%s' have not been found in the NMS output.",
                            alarmProblem, node));
            return response;
        }

        final String regexTimeAlarmRecieved = "(\\d{2}:\\d{2}:\\d{2})";
        final Pattern patternTimeAlarmRecieved = Pattern
                .compile(regexTimeAlarmRecieved);
        final Matcher matcher = patternTimeAlarmRecieved
                .matcher(alarmCommandResponses[matchingCommandResponse]);
        if (matcher.find()) {
            alarmRecievedMatch = matcher.group(0);
        } else {
            response.setSuccess(false);
            response.addErrorMessage("Alarm was not found in the output stream.");
            return response;
        }

        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date timeAlarmRecieved = null;
        Date timeAlarmIssued = null;
        try {
            timeAlarmRecieved = format.parse(alarmRecievedMatch);
            timeAlarmIssued = format.parse(timeAlarmSent);
        } catch (final ParseException e) {
            response.setSuccess(false);
            response.addErrorMessage("Parse Exception occured while parsing time.");
            return response;
        }
        // 'difference' is given in milliseconds, hence the division by 1000 is to get time in seconds.
        logger.info("Time alarm sent from Netsim: {}", timeAlarmSent);
        logger.info("Time alarm recieved in NMS: {}", alarmRecievedMatch);
        final long difference = timeAlarmRecieved.getTime()
                - timeAlarmIssued.getTime();
        if (difference / 1000 > 5 || difference < 0) {
            logger.info("Alarm took {} seconds to appear in the NMS",
                    difference / 1000);
            response.setSuccess(false);
            response.addErrorMessage("Alarm failed to appear in the NMS within 5 seconds of being sent.");
            return response;
        }
        response.setSuccess(true);
        return response;
    }

    /**
     * Executes cli command through web interface to check how many nodes are
     * subscribed and active
     *
     * @return CmResponse
     */
    public CmResponse getActiveSubscribedNodesCount() {
        String cmCliOutput = "";
        final CmResponse cmResponse = cmUIOperator.doCliCommand(
                "cmedit get * PMICScannerInfo.*", "instance(s)");
        if (cmResponse.isSuccess()) {
            cmCliOutput = cmResponse.getBody();
            try {
                cmResponse.setCount(Integer.parseInt(cmCliOutput.replaceAll(
                        "[^\\d.]", "")));
            } catch (final NumberFormatException e) {
                logger.error(
                        "Failed to parse output form cm cli to integer: Output {}",
                        cmResponse.getBody());
                cmResponse.setSuccess(false);
            }
        } else {
            logger.error(cmResponse.getErrorMessage());
        }

        return cmResponse;
    }

    /**
     * Checks to see if the ROPs are being collected on SVC1 in ENM
     *
     * @param kpiReqNodeCount
     *            - The amount of node required to be collecting to pass the
     *            test
     * @return StKpiResponse with total nodes added and total nodes synced
     */
    public StKpiResponse verifyPmRopCollected(final int kpiReqNodeCount) {
        final StKpiResponse response = new StKpiResponse();
        int nodesCollectingRop = 0;
        int totalNodes;
        CLICommandHelper cmdHelper = null;

        try {
            cmdHelper = getCommandHelperSvc1();

            logger.info("Checking that '{}' directory exists on SVC-1 ",
                    PM_COUNTER_XML_DIR);
            execute(cmdHelper, 0, "PMIC directory does not exist on server",
                    FIND_PM_COUNTER_XML_DIR);
            logger.info("Found '{}' directory", PM_COUNTER_XML_DIR);

            logger.info("Getting count of nodes which have collected a ROP on SVC-1");
            String commandResponse = executeAndExtractResult(
                    cmdHelper,
                    0,
                    "Failed to get count of nodes which have collected a ROP on SVC-1",
                    GET_NODES_ADDED_TO_ENM, REGEX_NUMBER);

            totalNodes = Integer.parseInt(commandResponse.trim());
            response.setTotalNodes(totalNodes);
            logger.info("{} nodes have collected a ROP on SVC-1", totalNodes);

            logger.info(
                    "Getting count of nodes which have collected a ROP in the last 15 minutes",
                    kpiReqNodeCount);

            commandResponse = executeAndExtractResult(
                    cmdHelper,
                    0,
                    "Failed to get count of nodes which have collected a ROP in the last 15 minutes",
                    GET_NODES_COLLECTING_ROP, REGEX_NUMBER);

            nodesCollectingRop = Integer.parseInt(commandResponse.trim());
            response.setSyncedNodes(nodesCollectingRop);
            logger.info(
                    "{} nodes have collected a ROP on SC-1 in the last 15 minutes",
                    nodesCollectingRop);

        } catch (final Exception e) {
            logger.info(
                    "{} was thrown while verifying PM rop collection. Message: ",
                    e.getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
            return getFailedResponse(String
                    .format("%s was thrown while verifying PM rop collection. Message: %s",
                            e.getClass().getSimpleName(), e.getMessage()));
        } finally {
            cmdHelper.disconnect();
        }

        logger.info(
                "Verifying enough nodes are collecting a ROP every 15 minutes: Requirement {} nodes",
                kpiReqNodeCount);
        if (nodesCollectingRop >= kpiReqNodeCount) {
            logger.info(
                    "Found {} eNodeBs which have received a ROP in the last 15 minutes",
                    nodesCollectingRop);
            response.setSuccess(true);
        } else {
            response.setSuccess(false);
            response.setTotalNodes(totalNodes);
            response.setSyncedNodes(nodesCollectingRop);
            response.addErrorMessage(String
                    .format("There is an insufficient amount of "
                            + "nodes collecting a ROP in the last 15 minutes: Required %s Found %s",
                            kpiReqNodeCount, nodesCollectingRop));
        }
        return response;
    }

    /**
     * This method wraps the CLICommandHelper.execute(String command) method and
     * throws an StKpiOperatorException if the result code does not equal
     * expectedResponseCode.
     *
     * Use this method if the command is a simple one that returns immediately.
     *
     * @param cmdHelper
     * @param expectedResponseCode
     * @param errorMessage
     * @param command
     * @return
     * @throws StKpiOperatorException
     */
    private String execute(final CLICommandHelper cmdHelper,
            final int expectedResponseCode, final String errorMessage,
            final String command) throws StKpiOperatorException {

        logger.info("Executing command: {}", command);
        final String result = cmdHelper.execute(command);
        final int responseCode = cmdHelper.getCommandExitValue();
        logger.info("Command result. Code: {}. Text: {}", responseCode, result);

        if (responseCode != expectedResponseCode) {
            logger.warn(
                    "{}. Command: [{}]. Expected return code: {}, actual return code: {}. Response: {}",
                    errorMessage, command, expectedResponseCode, responseCode,
                    result);
            throw new StKpiOperatorException(
                    String.format(
                            "%s. Command: [%s]. Expected: %s, actual: %s. Response: %s",
                            errorMessage, command, expectedResponseCode,
                            responseCode, result));
        }
        return result;
    }

    /**
     * This method wraps the CLICommandHelper.execute(String command) method and
     * throws an StKpiOperatorException if the result code does not equal
     * expectedResponseCode. It returns the pattern specified by
     * extractResultRegex.
     *
     * Use this method if the command is a simple one that returns immediately
     * otherwise use interactAndExtractResult.
     *
     * @param cmdHelper
     * @param expectedResponseCode
     * @param errorMessage
     * @param command
     * @param extractResultRegex
     * @return
     * @throws StKpiOperatorException
     *             if the pattern is not matched.
     */
    private String executeAndExtractResult(final CLICommandHelper cmdHelper,
            final int expectedResponseCode, final String errorMessage,
            final String command, final String extractResultRegex)
            throws StKpiOperatorException {

        final String result = execute(cmdHelper, expectedResponseCode,
                errorMessage, command);
        final Pattern regex = Pattern.compile(extractResultRegex);
        final Matcher resultMatcher = regex.matcher(result);

        if (resultMatcher.find()) {
            final String extract = resultMatcher.group();
            logger.info(
                    "Found pattern [{}] in result [{}]. Return value: [{}]",
                    extractResultRegex, result, extract);
            return extract;
        } else {
            logger.info(
                    "Failed to extract response with pattern: [{}] in result [{}] for command [{}]",
                    extractResultRegex, result, command);
            throw new StKpiOperatorException(
                    String.format(
                            "Failed to extract response with pattern [%s] in result [%s] for command [%s]",
                            extractResultRegex, result, command));
        }
    }

    /**
     * Uses CLICommandHelper.interactWithShell to execute a command and extracts
     * the first occurrence of extractResultRegex. Throws an exception if the
     * expectedResultRegex is not found in the command response.
     *
     * Use this method if the command is interactive or takes some time to
     * complete otherwise use executeAndExtractResult.
     *
     * @param cmdHelper
     * @param command
     * @param expectedResponseCode
     * @param extractResultRegex
     * @param timeoutSeconds
     * @param errorMessage
     * @return
     * @throws StKpiOperatorException
     */
    private String interactAndExtractResult(final CLICommandHelper cmdHelper,
            final String command, final int expectedResponseCode,
            final String extractResultRegex, final int timeoutSeconds,
            final String errorMessage) throws StKpiOperatorException {

        String response = "";
        final Pattern regex = Pattern.compile(extractResultRegex);

        try {
            logger.info("Executing interaction command: {}", command);
            cmdHelper.interactWithShell(command);
            response = cmdHelper.expect(regex, timeoutSeconds);
            logger.info("Response: {}", response);
        } catch (final TimeoutException e) {
            logger.info("Command failed. Message: {}", e.getMessage());
            throw new StKpiOperatorException(
                    String.format(
                            "TimeoutException on command: %s. Timout time seconds: %d. Message: %s",
                            timeoutSeconds, e.getMessage()));
        }

        final int responseCode = cmdHelper.getCommandExitValue();

        if (responseCode != expectedResponseCode) {
            logger.warn(
                    "{}. Command: [{}]. Expected return code: {}, actual return code: {}. Response: {}",
                    errorMessage, command, expectedResponseCode, responseCode,
                    response);
            throw new StKpiOperatorException(
                    String.format(
                            "%s. Command: [%s]. Expected: %s, actual: %s. Response: %s",
                            errorMessage, command, expectedResponseCode,
                            responseCode, response));
        }

        final Matcher resultMatcher = regex.matcher(response);

        if (resultMatcher.find()) {
            final String extract = resultMatcher.group();
            logger.info("Extracted response : [{}]", extract);
            return extract;
        } else {
            logger.info("Failed to extract response with pattern: {}",
                    extractResultRegex);
            throw new StKpiOperatorException(
                    String.format(
                            "Failed to extract response with pattern: %s for command %s",
                            extractResultRegex, command));
        }
    }

    private StKpiResponse getFailedResponse(final String errorMessage) {
        final StKpiResponse response = new StKpiResponse();
        response.setSuccess(false);
        response.addErrorMessage(errorMessage);
        return response;
    }

    static String readFile(final String path) throws IOException {
        final byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }
}