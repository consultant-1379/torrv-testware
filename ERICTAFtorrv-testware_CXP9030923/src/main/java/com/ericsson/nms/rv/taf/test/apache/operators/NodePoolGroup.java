package com.ericsson.nms.rv.taf.test.apache.operators;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ewandaf on 08/07/14.
 */
public final class NodePoolGroup {
    private static Logger logger = LoggerFactory.getLogger(NodePoolGroup.class);
    private static Map<String, NodePool> map = new ConcurrentHashMap<>();

    public static NodePool getNodePool(String groupName) {
        logger.debug("Get Node pool from node group {}", groupName);
        return map.get(groupName);
    }

    public static void putNodePool(String nodes, NodePool nodePool) {
        logger.debug("Adding Nodes {} into node pool", nodes);
        map.put(nodes, nodePool);
        logger.debug("Current node pools in group are {}", map.keySet());
    }
}