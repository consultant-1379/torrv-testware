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

public class UpdateUiSubDataSource {

    private final Logger logger = LoggerFactory
            .getLogger(UpdateUiSubDataSource.class);

    @DataSource
    public List<Map<String, Object>> updateUiSubscriptionData() {

        logger.debug("Create Data");
        final String data_file = DataHandler.getAttribute(
                "pmic.update.ui.subscription").toString();
        final List<Map<String, Object>> toReturn = new ArrayList<>();

        // Read the CSV file
        CsvParser reader = null;
        try {
            reader = new CsvParser(data_file);
        } catch (final FileNotFoundException e) {
            fail("Cannot find file: {}", data_file);
        } catch (final IOException e) {
            fail("IOException reading file: {}", data_file);
        }

        final List<Map<String, Object>> fileValues = reader.getValuesAsMaps();
        DataSourceBuilder.getListOfCommandsMap(fileValues);

        for (final Map<String, Object> line : fileValues) {
            final List<String> nodesToAdd = new ArrayList<>();
            final List<String> nodesToDelete = new ArrayList<>();

            final String subName = (String) line.get("subName");
            final String newDescription = (String) line.get("newDescription");
            final String ropIntervals = (String) line.get("ropIntervals");
            final String collectionName = (String) line.get("collectionName");
            final String searchCriteria = (String) line.get("searchCriteria");

            final String nodesToDeleteString = (String) line
                    .get("nodesToDelete");
            final String[] nodesToDeleteNames = nodesToDeleteString.split(",");

            for (int i = 0; i < nodesToDeleteNames.length; i++) {
                nodesToDelete.add(nodesToDeleteNames[i]);
            }

            final String nodesToAddString = (String) line.get("nodesToAdd");
            final String[] nodesToAddNames = nodesToAddString.split(",");

            for (int i = 0; i < nodesToAddNames.length; i++) {
                nodesToAdd.add(nodesToAddNames[i]);
            }

            final String parentCounterString = (String) line
                    .get("parentCounters");
            final List<String> parentCounters = Arrays
                    .asList(parentCounterString.split(","));

            final String subCounterString = (String) line.get("subCounters");
            final List<String> subCounters = Arrays.asList(subCounterString
                    .split(","));

            final Map<String, Object> lineMap = new HashMap<>();
            lineMap.put("subName", subName);
            lineMap.put("newDescription", newDescription);
            lineMap.put("nodesToDelete", nodesToDelete);
            lineMap.put("ropIntervals", ropIntervals);
            lineMap.put("collectionName", collectionName);
            lineMap.put("searchCriteria", searchCriteria);
            lineMap.put("nodesToAdd", nodesToAdd);
            lineMap.put("parentCounters", parentCounters);
            lineMap.put("subCounters", subCounters);

            toReturn.add(lineMap);
        }
        return toReturn;
    }
}
