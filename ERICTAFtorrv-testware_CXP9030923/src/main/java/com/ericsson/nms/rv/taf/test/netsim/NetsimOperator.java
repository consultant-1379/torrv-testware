package com.ericsson.nms.rv.taf.test.netsim;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.RemoteFileHandler;
import com.ericsson.cifwk.taf.handlers.netsim.*;
import com.ericsson.cifwk.taf.handlers.netsim.commands.NetSimCommands;
import com.ericsson.cifwk.taf.handlers.netsim.domain.Simulation;
import com.ericsson.cifwk.taf.handlers.netsim.domain.SimulationGroup;

/**
 * Created by ewandaf on 17/07/14.
 */
public class NetsimOperator {

    private final static Logger logger = LoggerFactory
            .getLogger(NetsimOperator.class);

    private static final int NETSIM_COMMAND_EXECUTE_TIMEOUT_SECONDS = 300;
    private final Host netSimHost;
    private String[] simulations;
    private static final String NODE_NOT_STARTED = "Not started!";
    private static final String PROBLEM_WITH_COORDINATOR = "Problem when connecting to coordinator:";
    private static final String PM_FILE_COLLECTION_ENABLED = "pmdata:enable;OK";
    // ARNE.xml -> simualtion:NetsimHost
    private static Map<String, String> arneMap = new HashMap<String, String>();

    public NetsimOperator(final String hostName,
            final String... simulationsNames) {
        logger.debug("Getting netsim host information for host {}", hostName);
        this.netSimHost = DataHandler.getHostByName(hostName);
        this.simulations = simulationsNames;
        logger.debug("returned netsim host: " + this.netSimHost.getHostname());
        logger.debug("returned netsim IP: " + this.netSimHost.getIp());
    }

    public NetsimOperator(final String hostName) {
        this.netSimHost = DataHandler.getHostByName(hostName);
    }

    public void createArneFile() {
        NetSimSession netSimSession = null;
        if (simulations.length == 0) {
            final List<String> simulationsNames = getAllSimulationsNames(netSimHost);
            final String[] fileNames = new String[simulationsNames.size()];
            simulations = simulationsNames.toArray(fileNames);
        }

        try {
            netSimSession = NetSimCommandHandler.getSession(netSimHost);
            for (final String simulation : simulations) {
                logger.info("Creating ARNE file on remote netsim " + simulation);
                final NetSimCommand netSimCommand = NetSimCommands
                        .open(simulation);
                final SelectNetworkCommand selectNetworkCommand = new SelectNetworkCommand();
                final CreateArneFileCommand createArneFileCommand = new CreateArneFileCommand();
                createArneFileCommand.setFileName(simulation);
                final String openSimulationRawOutput = netSimSession.exec(
                        NETSIM_COMMAND_EXECUTE_TIMEOUT_SECONDS, netSimCommand)
                        .getRawOutput();
                logger.info(openSimulationRawOutput);
                if (!openSimulationRawOutput
                        .contains("does not seem to be a simulation because there is no file")) {
                    logger.info("Simulation " + simulation + " is opened. "
                            + openSimulationRawOutput);
                    logger.info(netSimSession.exec(
                            NETSIM_COMMAND_EXECUTE_TIMEOUT_SECONDS,
                            selectNetworkCommand).getRawOutput());
                    logger.info(netSimSession.exec(
                            NETSIM_COMMAND_EXECUTE_TIMEOUT_SECONDS,
                            createArneFileCommand).getRawOutput());
                } else {
                    logger.error("No simulation found on netsim "
                            + netSimHost.getHostname());
                    throw new SkipException("No simulation found on netsim "
                            + netSimHost.getHostname());
                }
                logger.info("Finish creating ARNE file for netsim "
                        + simulation);
            }
        } catch (final NetSimException e) {
            logger.error("Caught NetsimException: {}", e.getMessage());
            throw e;
        } finally {
            if (netSimSession != null && !netSimSession.isClosed()) {
                netSimSession.close();
            }
        }
    }

    public void copyArneFileToLocal() {
        final RemoteFileHandler remote = new RemoteFileHandler(netSimHost);
        for (final String simulation : simulations) {
            final String sourceFile = "/netsim/netsimdir/exported_items/"
                    + simulation + "_create.xml";
            final String destFolder = System.getProperty("user.dir")
                    + File.separator + "target";
            final String destFile = destFolder + File.separator + simulation
                    + ".xml";
            if (remote.remoteFileExists(sourceFile)) {
                logger.info("Copy remote: " + sourceFile + " to local: "
                        + destFile + ".");
                remote.copyRemoteFileToLocal(sourceFile, destFile);
                arneMap.put(simulation + ".xml",
                        simulation + ":" + netSimHost.getHostname());
            } else {
                logger.info("Remote file: " + sourceFile
                        + " doesn't exist on NETSim "
                        + netSimHost.getHostname() + ".");
            }

        }
    }

    public static String getSimulation(final String destFile) {
        final String value = arneMap.get(destFile);
        if (value == null || value.isEmpty()) {
            logger.error(
                    "getSimulation(): Couldn't get destFile in ARNE MAP: {}",
                    destFile);
        }
        return value.split(":")[0];
    }

    public static String getNetsim(final String destFile) {
        final String value = arneMap.get(destFile);
        if (value == null || value.isEmpty()) {
            logger.error("getNetsim(): Couldn't get destFile in ARNE MAP: {}",
                    destFile);
        }
        return value.split(":")[1];
    }

    /**
     *
     * @param simulation
     *            e.g. LTEE1120-V2limx160-FT-FDD-LTE01
     * @param node
     *            e.g. LTE01ERBS00160
     */
    public NetsimResponse turnOffNode(final String simulation, final String node) {
        final NetsimResponse response = new NetsimResponse();
        NetSimCommandHandler netSimCommandHandler = null;
        try {
            final NetSimCommand openCommand = NetSimCommands.open(simulation);
            final NetSimCommand selectCommand = NetSimCommands
                    .selectnocallback(node);
            final NetSimCommand stopCommand = NetSimCommands.stop();
            netSimCommandHandler = NetSimCommandHandler.getInstance(netSimHost);
            logger.info("Attempt to stop node {} in simulation {}", node,
                    simulation);
            netSimCommandHandler.exec(openCommand, selectCommand, stopCommand);
            logger.info("Node {} in simulation {} is stopped", node, simulation);
            response.setSuccess(true);
            return response;
        } catch (final NetSimException e) {
            response.setSuccess(false);
            response.setErrorMessage("Caught NetsimException: "
                    + e.getMessage());
            return response;
        }
    }

    public NetsimResponse verifyNodeIsStarted(final String node) {
        logger.info("Verfiying node '{}' is started", node);
        final NetsimResponse response = new NetsimResponse();
        NetSimCommandHandler netSimCommandHandler = null;
        try {
            netSimCommandHandler = NetSimCommandHandler.getInstance(netSimHost);
            final boolean success = netSimCommandHandler.isStarted(node);
            response.setSuccess(success);
        } catch (final NetSimException e) {
            response.setSuccess(false);
            response.setErrorMessage("Caught NetsimException: "
                    + e.getMessage());
        }
        return response;
    }

    public NetsimResponse turnOnNode(final String simulation, final String node) {
        final NetsimResponse response = new NetsimResponse();
        NetSimCommandHandler netSimCommandHandler = null;
        try {
            final NetSimCommand openCommand = NetSimCommands.open(simulation);
            final NetSimCommand selectCommand = NetSimCommands
                    .selectnocallback(node);
            final NetSimCommand startCommand = NetSimCommands.start();
            netSimCommandHandler = NetSimCommandHandler.getInstance(netSimHost);
            logger.info("Attempt to start node {} in simulation {}", node,
                    simulation);
            logger.info(netSimCommandHandler.exec(openCommand, selectCommand,
                    startCommand).toString());
            logger.info("Node {} in simulation {} is started", node, simulation);
            response.setSuccess(true);
            return response;
        } catch (final NetSimException e) {
            response.setSuccess(false);
            response.setErrorMessage("Caught NetsimException: "
                    + e.getMessage());
            return response;
        }
    }

    public NetsimResponse ceaseAlarms(final String simulation, final String node) {
        NetSimCommandHandler netSimCommandHandler = null;
        try {
            final NetSimCommand openCommand = NetSimCommands.open(simulation);
            final NetSimCommand selectCommand = NetSimCommands
                    .selectnocallback(node);
            final NetSimCommand ceaseAlarm = NetSimCommands
                    .ceasealarm("all", 0);
            netSimCommandHandler = NetSimCommandHandler.getInstance(netSimHost);
            logger.info(
                    "Attempt to cease all alarms for node {} in simulation {}",
                    node, simulation);
            logger.info(netSimCommandHandler.exec(openCommand, selectCommand,
                    ceaseAlarm).toString());
            logger.info("All alarms in Node {} in simulation {} are ceased",
                    node, simulation);
            final NetsimResponse netsimResponse = new NetsimResponse();
            netsimResponse.setSuccess(true);
            return netsimResponse;
        } catch (final NetSimException e) {
            final NetsimResponse netsimResponse = new NetsimResponse();
            netsimResponse.setSuccess(false);
            netsimResponse
                    .setErrorMessage("Failed to cease Alarms. Exception is "
                            + e.getMessage());
            return netsimResponse;
        }
    }

    private static List<String> getAllSimulationsNames(final Host host) {
        final List<String> toReturn = new ArrayList<>();
        final NetSimCommandHandler netSimCommandHandler = NetSimCommandHandler
                .getInstance(host);
        final SimulationGroup simulations = netSimCommandHandler
                .getAllSimulations();
        for (final Simulation simulation : simulations) {
            toReturn.add(simulation.getName());
        }
        return toReturn;
    }

    public NetsimResponse sendAlarm(final String simulation, final String node,
            final String problem) {
        logger.info("Sending alarm. Simulation: {}, Node: {}, Problem: {}",
                simulation, node, problem);
        NetSimCommandHandler netSimCommandHandler = null;
        NetsimResponse response = new NetsimResponse();
        final SimpleDateFormat sdfFullTime = new SimpleDateFormat("HH:mm:ss");

        try {
            netSimCommandHandler = NetSimCommandHandler.getInstance(netSimHost);
            logger.info("Opening netsim connection");
            final NetSimCommand openCommand = NetSimCommands.open(simulation);
            final NetSimCommand selectCommand = NetSimCommands
                    .selectnocallback(node);
            final SendSpecificAlarmCommand sendAlarm = new SendSpecificAlarmCommand();
            sendAlarm.setProblem(problem);
            logger.info(
                    "Attempt to send an alarm from node {} in simulation {}.",
                    node, simulation);

            final Map<NetSimContext, NetSimResult> netsimResult = netSimCommandHandler
                    .exec(openCommand, selectCommand, sendAlarm);
            final Date dateSent = Calendar.getInstance().getTime();
            final String timeAlarmSent = sdfFullTime.format(dateSent);
            response.setTimeAlarmSent(timeAlarmSent);
            response.setTimeAlarmSentMillis(dateSent.getTime());

            logger.info("Alarm is sent from node {} in simulation {}.", node,
                    simulation);
            logger.info(netsimResult.values().toString());

            response = verifyNodeIsStarted(response, netsimResult);
            if (!response.isSuccess()) {
                return response;
            }
            response = checkConnectionSuccess(response, netsimResult);
            if (!response.isSuccess()) {
                return response;
            }
        } catch (final NetSimException e) {
            logger.info(
                    "NetSimException during netsim operator sendAlarm. Message: {}",
                    e.getMessage());
            response.setSuccess(false);
            response.setErrorMessage("Caught NetsimException: "
                    + e.getMessage());
            return response;
        }
        response.setSuccess(true);
        return response;
    }

    public NetsimResponse enablePmicFileCollection(final String simulation,
            final String node) {
        NetSimCommandHandler netSimCommandHandler = null;
        NetsimResponse response = new NetsimResponse();
        try {
            netSimCommandHandler = NetSimCommandHandler.getInstance(netSimHost);
            final Map<NetSimContext, NetSimResult> netsimResult = netSimCommandHandler
                    .exec(NetSimCommands.open(simulation),
                            NetSimCommands.selectnocallback(node),
                            new EnablePmicFileCollectionCommand());
            response = verifyNodeIsStarted(response, netsimResult);
            if (!response.isSuccess()) {
                return response;
            }
            response = verifyFileCollectionWasEnabled(response, netsimResult);
            if (!response.isSuccess()) {
                return response;
            }
            logger.info("Pmic file collection successfully enabled on {}.",
                    node);
        } catch (final NetSimException e) {
            response.setSuccess(false);
            response.setErrorMessage("Caught NetsimException: "
                    + e.getMessage());

        }
        return response;
    }

    private NetsimResponse verifyFileCollectionWasEnabled(
            final NetsimResponse response,
            final Map<NetSimContext, NetSimResult> netsimResult) {
        final String enablePmicFileCollectionResponse = netsimResult.values()
                .toString().replaceAll("\\s", "").replaceAll("\\n", "");
        if (!enablePmicFileCollectionResponse
                .contains(PM_FILE_COLLECTION_ENABLED)) {
            response.setErrorMessage("Pmic file collection has not been enabled.");
            response.setSuccess(false);
            return response;
        }
        response.setSuccess(true);
        return response;
    }

    private NetsimResponse checkConnectionSuccess(
            final NetsimResponse response,
            final Map<NetSimContext, NetSimResult> netsimResult) {
        logger.info("Checking connection status");
        final String[] netsimResults = netsimResult.values().toString()
                .split("\\n");
        for (int i = 0; i < netsimResults.length; i++) {
            if (netsimResults[i].trim().contains(PROBLEM_WITH_COORDINATOR)) {
                response.setSuccess(false);
                response.setErrorMessage(String
                        .format("Problem with connection. Check that Netsim node is turned on.%nNetsim output: %s",
                                netsimResults.toString()));
                return response;
            }
        }
        response.setSuccess(true);
        logger.info("Connected to Netsim.");
        return response;
    }

    private NetsimResponse verifyNodeIsStarted(final NetsimResponse response,
            final Map<NetSimContext, NetSimResult> netsimResult) {
        logger.info("Verifying if node is started");
        final String[] netsimResults = netsimResult.values().toString()
                .split("\\n");
        for (int i = 0; i < netsimResults.length; i++) {
            if (netsimResults[i].trim().equals(NODE_NOT_STARTED)) {
                logger.info("Node is not started");
                response.setSuccess(false);
                response.setErrorMessage("Command failed. Node is not turned on.");
                return response;
            }
        }
        response.setSuccess(true);
        logger.info("Node is started.");
        return response;
    }
}
