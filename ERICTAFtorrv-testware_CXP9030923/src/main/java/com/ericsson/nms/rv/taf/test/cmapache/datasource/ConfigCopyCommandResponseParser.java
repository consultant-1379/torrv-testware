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
package com.ericsson.nms.rv.taf.test.cmapache.datasource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.CmResponse;

public class ConfigCopyCommandResponseParser {

    private static final Logger logger = LoggerFactory
            .getLogger(ConfigCopyCommandResponseParser.class);

    // Assumes only one row of data returned from command.
    public static int getNumberNodesCopied(CmResponse response)
            throws ParseException {
        final JSONParser parser = new JSONParser();
        final JSONObject json = (JSONObject) parser.parse(response.getBody());
        final JSONObject responseDto = (JSONObject) json.get("responseDto");
        final JSONArray elementsArray = (JSONArray) responseDto.get("elements");
        final JSONObject row = getElementByDtoType(elementsArray, "row");
        final JSONObject headerRow = getElementByDtoType(elementsArray,
                "headerRow");

        if (row == null) {
            logger.error("No data found for config status command.");
            return -1;
        }
        if (headerRow == null) {
            logger.error("No header data found for config status command.");
            return -1;
        }

        final int indexOfNodesCopied = getIndexOfElementWithHeader(headerRow,
                "Nodes copied");
        if (indexOfNodesCopied == -1) {
            logger.error("Failed to get index of element with header {} from config status command result.");
            return -1;
        }
        final int valueOfNodesCopied = Integer
                .parseInt(getValueOfElementAtIndex(row, indexOfNodesCopied));
        return valueOfNodesCopied;
    }

    private static String getValueOfElementAtIndex(JSONObject row, int index) {
        final JSONArray array = (JSONArray) row.get("elements");
        final JSONObject object = (JSONObject) array.get(index);
        return object.get("value").toString();
    }

    private static int getIndexOfElementWithHeader(JSONObject headerRow,
            String header) {
        final JSONArray array = (JSONArray) headerRow.get("elements");
        int index = 0;
        for (final Object object : array) {
            final JSONObject jsonObject = (JSONObject) object;
            if (jsonObject.get("value").toString().equals(header)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private static JSONObject getElementByDtoType(JSONArray elementsArray,
            String dtoTypeValue) {
        for (final Object object : elementsArray) {
            final JSONObject jsonObject = (JSONObject) object;

            if (jsonObject.get("dtoType").toString().equals(dtoTypeValue)) {
                return jsonObject;
            }
        }
        return null;
    }

    public static String getJobNumber(String body) {
        final Pattern jobPattern = Pattern
                .compile("Copy nodes started with job ID (-?\\d+)");
        final Matcher jobMatcher = jobPattern.matcher(body);
        if (jobMatcher.find()) {
            return jobMatcher.group(1);
        }
        return "";
    }
}