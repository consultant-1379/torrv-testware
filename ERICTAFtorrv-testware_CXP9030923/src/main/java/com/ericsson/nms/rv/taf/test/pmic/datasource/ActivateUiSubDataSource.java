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
package com.ericsson.nms.rv.taf.test.pmic.datasource;

import static se.ericsson.jcat.fw.ng.JcatNGTestBase.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.DataSource;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.CsvParser;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.DataSourceBuilder;

public class ActivateUiSubDataSource {
    private final Logger logger = LoggerFactory
            .getLogger(ActivateUiSubDataSource.class);

    @DataSource
    public List<Map<String, Object>> uiSubscriptionData() {

        logger.debug("Create Data");
        final String create_sub_file = DataHandler.getAttribute(
                "dataprovider.activate_ui_sub.location").toString();
        final List<Map<String, Object>> toReturn = new ArrayList<>();

        // Read the CSV file
        CsvParser reader = null;
        try {
            reader = new CsvParser(create_sub_file);
        } catch (final FileNotFoundException e) {
            fail("Cannot find file: {}", create_sub_file);
        } catch (final IOException e) {
            fail("IOException reading file: {}", create_sub_file);
        }

        final List<Map<String, Object>> fileValues = reader.getValuesAsMaps();
        DataSourceBuilder.getListOfCommandsMap(fileValues);

        for (final Map<String, Object> line : fileValues) {

            final String subName = (String) line.get("subName");
            final String subDescription = (String) line.get("description");
            final String nodeList = (String) line.get("nodes");
            final List<String> nodes = Arrays.asList(nodeList.split(","));
            final String command = (String) line.get("command");
            final String expectedResultContains = (String) line
                    .get("expectedResultContains");
            final String expectedResultContainsDelete = (String) line
                    .get("expectedResultContainsDelete");
            final String directory = (String) line.get("directory");
            final String symbolicDirectory = (String) line
                    .get("symbolicDirectory");
            final String ropInterval = (String) line.get("ropInterval");
            final String waitTime = (String) line.get("waitTime");

            final Map<String, Object> lineMap = new HashMap<>();
            lineMap.put("subName", subName);
            lineMap.put("description", subDescription);
            lineMap.put("nodes", nodes);
            lineMap.put("command", command);
            lineMap.put("expectedResultContains", expectedResultContains);
            lineMap.put("expectedResultContainsDelete",
                    expectedResultContainsDelete);
            lineMap.put("directory", directory);
            lineMap.put("symbolicDirectory", symbolicDirectory);
            lineMap.put("ropInterval", ropInterval);
            lineMap.put("waitTime", waitTime);
            toReturn.add(lineMap);
        }
        return toReturn;
    }
}
