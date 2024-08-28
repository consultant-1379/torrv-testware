package com.ericsson.nms.rv.taf.test.cmapache.datasource;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.rv.taf.test.apache.cases.UtilityClass;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.processors.*;
import com.ericsson.nms.rv.taf.test.cmapache.operators.CmApachePropertyKeys;

public class DataSourceBuilder {

    private static final String ADDED_NODES = CmApachePropertyKeys.BUILD_DATA_SOURCE_NAME;
    private static final Logger logger = LoggerFactory
            .getLogger(DataSourceBuilder.class);

    public static List<List<Map<String, Object>>> getListOfCommandsMap(
            List<Map<String, Object>> template) {
        final List<Node> nodesList = UtilityClass
                .getNodeInDataSource(ADDED_NODES);
        final List<List<Map<String, Object>>> toReturn = buildCommandMap(
                template, nodesList);
        return toReturn;
    }

    public static List<Node> getNodesInPool() {
        final List<Node> dataRecords = UtilityClass
                .getNodeInDataSource(ADDED_NODES);
        logger.debug("The nodes in data source are {}", dataRecords.toString());
        return dataRecords;
    }

    public static Node getRandomNodeInPool() {
        final List<Node> dataRecords = getNodesInPool();
        final Random randomNumber = new Random();
        Node returnRecord = null;
        try {
            returnRecord = dataRecords.get(randomNumber.nextInt(dataRecords
                    .size()));
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
            logger.error("Could Not get Node from Pool: Verify Node Data Source Has Been Built");
        }
        return returnRecord;
    }

    public static List<List<Map<String, Object>>> buildCommandMap(
            List<Map<String, Object>> lists, List<Node> nodes) {

        final List<List<Map<String, Object>>> toReturn = new ArrayList<>();
        final boolean replaceForAllNodes = ifReplaceForAllNodes(lists);

        if (replaceForAllNodes) {
            for (int i = 0; i < nodes.size(); i++) {
                final List<Map<String, Object>> innerList = new ArrayList<>();
                for (final Map<String, Object> mapRow : lists) {
                    final Map<String, Object> innerMap = new HashMap<String, Object>();
                    final Set<String> headerSet = mapRow.keySet();
                    for (final String header : headerSet) {
                        String v = (String) mapRow.get(header);
                        v = replaceTemplate(v, nodes, i);
                        innerMap.put(header, v);
                    }
                    innerList.add(innerMap);
                }
                toReturn.add(innerList);
            }
        } else {
            final List<Map<String, Object>> innerList = new ArrayList<>();
            for (final Map<String, Object> mapRow : lists) {

                final Set<String> headerSet = mapRow.keySet();
                for (final String header : headerSet) {
                    String v = (String) mapRow.get(header);
                    v = replaceTemplate(v, nodes);
                    mapRow.put(header, v);
                }
                innerList.add(mapRow);
            }
            toReturn.add(innerList);
        }

        return toReturn;
    }

    public static String parseLine(String template) {
        final List<Node> dataRecords = UtilityClass
                .getNodeInDataSource(ADDED_NODES);
        return replaceTemplate(template, dataRecords);
    }

    private static boolean ifReplaceForAllNodes(List<Map<String, Object>> lists) {
        boolean toReturn = false;
        for (final Map<String, Object> mapRow : lists) {
            final Set<String> headerSet = mapRow.keySet();
            for (final String header : headerSet) {
                final String v = (String) mapRow.get(header);
                final List<String> foundList = getStringsByReg(
                        "[$][a-zA-Z]+[^0-9|a-zA-Z|_]", v);
                final List<String> fondList2 = getStringsByReg("[$][a-zA-Z]+$",
                        v);
                if (!foundList.isEmpty() || !fondList2.isEmpty()) {
                    toReturn = true;
                    break;
                }
            }
            if (toReturn) {
                break;
            }
        }
        return toReturn;
    }

    private static String replaceTemplate(String template,
            List<Node> dataRecords, int nodeIndex) {

        Factory.setDataRecords(dataRecords);
        template = Factory
                .process(template, new HeaderNumProcessor(),
                        new AllNumProcessor(), new BeginNumProcessor(),
                        new EndNumProcessor(), new BeginProcessor(),
                        new EndProcessor());
        template = Factory.process(template, nodeIndex, new HeaderProcessor());
        return template;
    }

    private static String replaceTemplate(String template,
            List<Node> dataRecords) {

        Factory.setDataRecords(dataRecords);
        template = Factory
                .process(template, new HeaderNumProcessor(),
                        new AllNumProcessor(), new BeginNumProcessor(),
                        new EndNumProcessor(), new BeginProcessor(),
                        new EndProcessor());
        return template;
    }

    public static List<String> getStringsByReg(String regular, String string) {
        final List<String> toReturn = new ArrayList<String>();
        final Pattern p = Pattern.compile(regular);
        final Matcher m = p.matcher(string);
        while (m.find()) {
            toReturn.add(m.group());
        }
        return toReturn;
    }
}