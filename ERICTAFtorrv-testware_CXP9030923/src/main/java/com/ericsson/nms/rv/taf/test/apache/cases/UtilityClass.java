package com.ericsson.nms.rv.taf.test.apache.cases;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.DataRecordModifier;
import com.ericsson.nms.rv.taf.test.apache.operators.NodePool;
import com.ericsson.nms.rv.taf.test.apache.operators.NodePoolGroup;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;
import com.ericsson.nms.rv.taf.test.arne.operator.ArneOperator;
import com.ericsson.nms.rv.taf.test.netsim.NetsimOperator;

public class UtilityClass {

    private final static Logger logger = LoggerFactory
            .getLogger(UtilityClass.class);
    private static final String NODE_POOL_FILE_LIST = "node.default.pool.file.list";
    public static final String NETSIM_NAMES = "netsim.names";
    public static final String NETSIM_SIMULATIONS = "netsim.simulations";
    public static final String NODE_CSV_SUFFIX = ".csv";
    public static final String ARNE_XML_SUFFIX = ".xml";
    public static final String BUILD_DATA_SOURCE_NAME = "added_nodes";
    private static final String NODE_SPECIAL_POOL_FILE_LIST = "node.special.pool.file.list";
    private static final String FETCH_NODES = "fetchNodes";

    public static ArneOperator getArneOperator(final String simulation) {
        return new ArneOperator(simulation + ARNE_XML_SUFFIX, simulation
                + NODE_CSV_SUFFIX);
    }

    public static NetsimOperator getNetsimOperator(final String netsim,
            final String... simulations) {
        return new NetsimOperator(netsim, simulations);
    }

    public static List<String> getNodePoolFileList() {
        return getAttribute(NODE_POOL_FILE_LIST);
    }

    public static void setNodePoolFileList(final List<String> list) {
        final String value = buildCommaSeparatedStringFromList(list);
        setAttribute(NODE_POOL_FILE_LIST, value);
    }

    public static List<String> appendNodeCsvSuffix(final List<String> list) {
        final List<String> result = new ArrayList<String>(list.size());
        for (final String l : list) {
            result.add(l + NODE_CSV_SUFFIX);
        }
        return result;
    }

    public static String buildCommaSeparatedStringFromList(
            final List<String> list) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i));
            stringBuilder.append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    public static List<String> getSpecialNodePoolFileList() {
        return getAttribute(NODE_SPECIAL_POOL_FILE_LIST);
    }

    public static List<String> getNetsimNames() {
        return getAttribute(NETSIM_NAMES);
    }

    public static List<String> getNetsimSimulations() {
        return getAttribute(NETSIM_SIMULATIONS);
    }

    public static boolean isFetchNodes() {
        final String isFetchNodesString = getAttribute(FETCH_NODES).get(0);
        return Boolean.parseBoolean(isFetchNodesString);
    }

    public static <T> T getObjectFromMap(final Class<T> clazz,
            final Map<String, Object> map) {
        T t = null;
        try {
            t = clazz.newInstance();
        } catch (final InstantiationException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
        for (final String key : map.keySet()) {
            if (key != null) {
                final String setterName = getSetterName(key);
                Method setterMethod = null;
                try {
                    setterMethod = clazz.getMethod(setterName, String.class);
                } catch (final NoSuchMethodException e) {
                    e.printStackTrace();
                    logger.error("Node method called {} in class", setterName,
                            clazz.getName());
                }
                try {
                    setterMethod.invoke(t, map.get(key));
                } catch (final IllegalAccessException e) {
                    e.printStackTrace();
                } catch (final InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return t;
    }

    public static String[] toStringArray(final List<String> list) {
        final String[] array = new String[list.size()];
        list.toArray(array);
        return array;
    }

    public static void addDataRecordIntoDataSource(final String dataSourceName,
            final Object dataRecord) {
        final DataRecordModifier modifiableDataRecord = TafTestContext
                .getContext().dataSource(dataSourceName).addRecord();
        modifiableDataRecord.setField("Node", dataRecord);
    }

    public static List<Node> getNodeInDataSource(final String dataSource) {
        final List<DataRecord> listOfRecords = new ArrayList<DataRecord>();
        for (final DataRecord record : TafTestContext.getContext().dataSource(
                dataSource)) {
            listOfRecords.add(record);
        }
        final List<Node> toReturn = new ArrayList<Node>(listOfRecords.size());
        for (final DataRecord dataRecord : listOfRecords) {
            final Node node = dataRecord.getFieldValue("Node");
            toReturn.add(node);
        }
        return toReturn;
    }

    public static NodePool getNodePoolInGroup(final int i,
            final String[] groupNamesArray) {
        final int groupIndex = i % (groupNamesArray.length);
        final NodePool nodePool = NodePoolGroup
                .getNodePool(groupNamesArray[groupIndex]);
        logger.info("Get node Pool {} from group {}", nodePool.toString(),
                groupNamesArray[groupIndex]);
        return nodePool;
    }

    private static List<String> getAttribute(final String key) {
        List<String> toReturn = new ArrayList<String>();
        final Object value = DataHandler.getAttribute(key);
        if (value instanceof String) {
            final String stringValue = (String) value;
            if (!stringValue.isEmpty()) {
                toReturn.add((String) value);
            }
        }
        if (value instanceof Collection) {
            toReturn = (List<String>) value;
        }
        logger.info("Get property {} : {}", key, toReturn);
        return toReturn;
    }

    private static void setAttribute(final String key, final String value) {
        DataHandler.setAttribute(key, value);
        logger.info("{} property is set to {}", key, value);
    }

    private static String getSetterName(final String s) {
        final String firstPosition = s.substring(0, 1).toUpperCase();
        return "set" + firstPosition + s.substring(1, s.length());
    }

}
