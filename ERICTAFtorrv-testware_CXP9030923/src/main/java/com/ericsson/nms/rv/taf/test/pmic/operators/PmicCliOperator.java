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
package com.ericsson.nms.rv.taf.test.pmic.operators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.rv.taf.test.monitoring.operators.HostHelper;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

public class PmicCliOperator {
    public static final Logger logger = LoggerFactory
            .getLogger(PmicCliOperator.class);
    private final int FILE_COLLECTION_TIME = 65;

    private CLICommandHelper getSvc1() {
        final Host host = HostHelper.getHostByName("svc1");
        return new CLICommandHelper(host);
    }

    /**
     * Execute a command via ssh on svc-1
     *
     * @param command
     *            - This is the command to execute on svc1's pmServ instance
     *
     * @return PmicResponse
     */
    public PmicResponse executeCliCommandsOnPmServ(final String command) {
        final PmicResponse pmicResp = new PmicResponse();
        CLICommandHelper cliCommandHelper = getSvc1();

        String response = null;

        if (cliCommandHelper == null) {
            return setResponse(pmicResp, false,
                    "User was not able to get cliCommandHelper for SVC1.");
        }

        // User to login as Su user on svc1
        final User suUser = new User();
        suUser.setUsername("");
        suUser.setPassword("12shroot");

        // User to login as pmserv from svc1
        final User pmServUser = new User();
        pmServUser.setUsername("root");
        pmServUser.setPassword("passw0rd");

        cliCommandHelper = cliCommandHelper.newHopBuilder().hop(suUser)
                .hop(HostConfigurator.getPmService(), pmServUser).build();

        logger.info("Executing command on pmserv: {}", command);
        cliCommandHelper.interactWithShell(command);
        response = cliCommandHelper.getShell().read();

        cliCommandHelper.interactWithShell("exit");
        cliCommandHelper.disconnect();

        pmicResp.setOutput(response);
        pmicResp.setSuccess(true);
        return pmicResp;
    }

    public PmicResponse verifyXmlFilesUpdated(
            final Map<String, String> nodesToXmlFilesBeforeRop,
            final Map<String, String> nodesToXmlFilesAfterRop) {
        final PmicResponse response = new PmicResponse();
        final MapDifference<String, String> diff = Maps.difference(
                nodesToXmlFilesBeforeRop, nodesToXmlFilesAfterRop);
        final Map<String, String> filesNotUpdated = diff.entriesInCommon();
        if (!filesNotUpdated.isEmpty()
                || nodesToXmlFilesAfterRop.values().size() == 0) {
            return setResponse(response, false, filesNotUpdated.keySet()
                    .toString());
        }
        response.setSuccess(true);
        logger.debug("All symbolic links have been updated");
        return response;
    }

    public Map<String, String> mapNodesToXmlFilesInDirectory(
            final String[] xmlFilesCollected, final List<String> nodesToCheck) {
        final Map<String, String> nodesXmlMap = new HashMap<>();
        for (final String xmlFile : xmlFilesCollected) {
            for (final String node : nodesToCheck) {
                if (xmlFile.contains(node)) {
                    nodesXmlMap.put(node, xmlFile);
                }
            }
        }
        return nodesXmlMap;
    }

    public PmicResponse waitUntilNextRop(final int ropInterval) {
        final PmicResponse pmicResponse = new PmicResponse();
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("mm");
        final SimpleDateFormat sdfFullTime = new SimpleDateFormat("HH:mm:ss");
        logger.info("Current System time: {}",
                sdfFullTime.format(cal.getTime()));

        final int minutes = Integer.parseInt(sdf.format(cal.getTime()));
        int waitTime = 0;
        if (ropInterval == 1) {
            waitTime = ropInterval;
        } else if (minutes % ropInterval == 0) {
            waitTime = 0;
        } else if (minutes < 15) {
            waitTime = ropInterval - minutes;
        } else if (minutes < 30) {
            waitTime = (ropInterval * 2) - minutes;
        } else if (minutes < 45) {
            waitTime = (ropInterval * 3) - minutes;
        } else {
            waitTime = (ropInterval * 4) - minutes;
        }

        logger.info("Waiting {} minutes for next ROP to start", waitTime);
        try {
            TimeUnit.MINUTES.sleep(waitTime);
        } catch (final InterruptedException e) {
            return setResponse(pmicResponse, false, "Sleep was interrupted.");
        }
        pmicResponse.setSuccess(true);
        logger.debug("Successfully waited until the next ROP");
        return pmicResponse;
    }

    /**
     * @param filesBefore
     *            - Files collected before the rop
     * @param filesAfter
     *            - Files collected after the rop
     * @return PmicResponse
     */
    public PmicResponse checkFileCollectionSuccessful(
            final List<String> nodeList, final List<String> filesBeforeRop,
            final List<String> filesAfterRop) {
        final PmicResponse filesCollectionResponse = new PmicResponse();
        final List<String> missedFiles = new ArrayList<>();

        for (int i = 0; i < nodeList.size(); i++) {
            if (!(filesBeforeRop.get(i).equals(filesAfterRop.get(i)))) {
                logger.debug("File collection Successful: {}", nodeList.get(i));
                filesCollectionResponse.setSuccess(true);
            } else {
                logger.warn("File collection Failed: {}", nodeList.get(i));
                missedFiles.add(nodeList.get(i).trim());
            }
        }

        if (!missedFiles.isEmpty()) {
            return setResponse(filesCollectionResponse, false,
                    missedFiles.toString());
        }
        filesCollectionResponse.setSuccess(true);
        logger.debug("All files collected successfully");
        return filesCollectionResponse;
    }

    private PmicResponse setLastXmlFileCollectedForNode(
            final String monitoringResponse, final PmicResponse pmicResponse) {
        final String[] xmlFiles = monitoringResponse.split("\r\n");
        final String lastXmlInDirectory = xmlFiles[xmlFiles.length - 1];
        logger.debug("Last xml file collected:: {}", lastXmlInDirectory);
        pmicResponse.setLastXmlFileName(lastXmlInDirectory);
        return pmicResponse;
    }

    public PmicResponse waitForFileCollection(int minutes) {
        final PmicResponse pmicResponse = new PmicResponse();
        if (minutes == 1) {
            minutes += 2;
        } else {
            minutes += 5;
        }
        logger.info("Waiting {} minutes for ROP to complete.", minutes);

        try {
            TimeUnit.MINUTES.sleep(minutes);
        } catch (final InterruptedException e) {
            return setResponse(pmicResponse, false, "Sleep was interrupted.");
        }
        pmicResponse.setSuccess(true);
        logger.debug("Successfully waited for ROP completion.");
        return pmicResponse;
    }

    private PmicResponse setResponse(final PmicResponse response,
            final boolean success, final String error) {
        response.setSuccess(success);
        response.setErrorMessage(error);
        return response;
    }

    /**
     * @param nodeList
     *            -List of nodes used to check their directories
     * @param directory
     *            - common directory prefix for each node
     * @return - return PmicResponse containing a list with the last file from
     *         each node directory added
     */
    public PmicResponse executeCommandAndCreateListOfFilesCollected(
            final List<String> nodeList, final String directory) {
        PmicResponse pmicResponse = new PmicResponse();
        final List<String> filesInDirectory = new ArrayList<>();
        for (final String node : nodeList) {
            pmicResponse = executeCliCommandsOnPmServ(directory + node.trim()
                    + ";ls");
            pmicResponse = setLastXmlFileCollectedForNode(
                    pmicResponse.getOutput(), pmicResponse);
            filesInDirectory.add(pmicResponse.getLastXmlFileName());
        }
        if (!pmicResponse.isSuccess()) {
            return pmicResponse;
        }
        pmicResponse.setListOfFilesInDirectory(filesInDirectory);
        pmicResponse.setSuccess(true);
        logger.debug("Command executed successfully and list of files collected generated");
        return pmicResponse;
    }

    /**
     * @param fileDeletionPeriodRemaining
     *            -Period of time before file is deleted
     * @param oldestXmlFileInDirectory
     *            - This is the first/oldest xml file in the symbolic link
     *            directory
     * @return pmicResponse
     */
    public PmicResponse verifySymbolicLinkDeletion(
            final String oldestXmlFileInDirectory) {
        final PmicResponse pmicResponse = new PmicResponse();
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmm");
        final Date systemDate = cal.getTime();

        final String patternTemplateTime = "(\\-)(\\d{4})(\\+)";
        final String patternTemplateDate = "(A)(\\d{8})";
        final Pattern time = Pattern.compile(patternTemplateTime);
        final Pattern date = Pattern.compile(patternTemplateDate);
        final Matcher matchTime = time.matcher(oldestXmlFileInDirectory);
        final Matcher matchDate = date.matcher(oldestXmlFileInDirectory);

        if (matchTime.find() && matchDate.find()) {
            final String oldestXmlTime = matchDate.group(2) + "."
                    + matchTime.group(2);
            Date oldestXmlCollected = null;
            try {
                oldestXmlCollected = sdf.parse(oldestXmlTime);
            } catch (final ParseException e) {
                return setResponse(pmicResponse, false,
                        "Parse exception occured.");
            }
            final long differenceInMillis = systemDate.getTime()
                    - oldestXmlCollected.getTime();
            logger.info("Oldest File timestamp: {}", matchTime.group(2));
            if (TimeUnit.MILLISECONDS.toMinutes(differenceInMillis) <= FILE_COLLECTION_TIME) {
                pmicResponse.setSuccess(true);
                logger.debug("File deletion of symbolic links was successful.");

            } else {
                return setResponse(pmicResponse, false, String.format(
                        "Oldest file in directory is %s minutes old.",
                        TimeUnit.MILLISECONDS.toMinutes(differenceInMillis)));
            }
        } else {
            return setResponse(pmicResponse, false,
                    "No files found in the directory.");
        }
        return pmicResponse;
    }

    /**
     * @param commandOutput
     *            Output from the previous command. Aim is to extract date time
     *            from this string
     * @return pmicResponse
     */
    public PmicResponse calculateWaitTimeAndWait(final String commandOutput) {
        final PmicResponse pmicResponse = new PmicResponse();
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date systemDate = cal.getTime();
        final String patternTemplateTime = "[^,]*";
        final Pattern time = Pattern.compile(patternTemplateTime);
        final Matcher matchTime = time.matcher(commandOutput);
        Date timeDeletionInitialised = null;
        if (matchTime.find()) {
            try {
                timeDeletionInitialised = sdf.parse(matchTime.group(0));
            } catch (final ParseException e) {
                return setResponse(pmicResponse, false,
                        "Parse exception occured.");
            }
        } else {
            return setResponse(
                    pmicResponse,
                    false,
                    String.format("Unable to find pattern in the string passed in."));
        }
        timeDeletionInitialised = DateUtils
                .addHours(timeDeletionInitialised, 2);
        final long timeToWaitInMinutes = TimeUnit.MILLISECONDS
                .toMinutes(timeDeletionInitialised.getTime()
                        - systemDate.getTime());
        try {
            logger.info("Waiting {} minutes for file deletion",
                    timeToWaitInMinutes + 2);
            TimeUnit.MINUTES.sleep(timeToWaitInMinutes + 2);
        } catch (final InterruptedException e) {
            return setResponse(pmicResponse, false, "Sleep was interrupted.");
        }
        pmicResponse.setSuccess(true);
        return pmicResponse;
    }
}
