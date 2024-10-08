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

public class CreateUiSubDataSource {

    private final Logger logger = LoggerFactory
            .getLogger(CreateUiSubDataSource.class);

    @DataSource
    public List<Map<String, Object>> uiSubscriptionData() {

        logger.debug("Create Data");
        final String create_sub_file = DataHandler.getAttribute(
                "pmic.create.ui.subscription").toString();
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
            final List<String> nodesToUse = new ArrayList<>();

            final String subName = (String) line.get("subName");
            final String subDescription = (String) line.get("description");
            final String collectionName = (String) line.get("collectionName");
            final String searchCriteria = (String) line.get("searchCriteria");
            final String nodeString = (String) line.get("nodes");
            final String[] nodeNames = nodeString.split(",");

            for (int i = 0; i < nodeNames.length; i++) {
                nodesToUse.add(nodeNames[i]);
            }

            final String parentCounterString = (String) line
                    .get("parentCounters");
            final List<String> parentCounters = Arrays
                    .asList(parentCounterString.split(","));

            final String subCounterString = (String) line.get("subCounters");
            final List<String> subCounters = Arrays.asList(subCounterString
                    .split(","));

            final String ropInterval = (String) line.get("ropInterval");

            final Map<String, Object> lineMap = new HashMap<>();
            lineMap.put("subName", subName);
            lineMap.put("description", subDescription);
            lineMap.put("collectionName", collectionName);
            lineMap.put("searchCriteria", searchCriteria);
            lineMap.put("nodes", nodesToUse);
            lineMap.put("parentCounters", parentCounters);
            lineMap.put("subCounters", subCounters);
            lineMap.put("ropInterval", ropInterval);
            toReturn.add(lineMap);
        }
        return toReturn;
    }
}
