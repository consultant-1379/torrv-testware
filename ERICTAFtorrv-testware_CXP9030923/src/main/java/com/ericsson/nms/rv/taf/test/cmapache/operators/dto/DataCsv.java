package com.ericsson.nms.rv.taf.test.cmapache.operators.dto;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * This file is mapped to cli command data file.
 *
 */
public class DataCsv {
    Logger logger = LoggerFactory.getLogger(DataCsv.class);
    Map<String, Object> map;

    public DataCsv() {
        map = new HashMap<String, Object>();
    }

    public DataCsv(Map<String, Object> map) {
        this.map = map;
    }

    public Set<String> getKeySet() {
        return map.keySet();
    }

    public void put(String key, String value) {
        map.put(key, value);
    }

    public Map<String, Object> getMap() {
        return this.map;
    }

    public String get(String key) {
        if (map.containsKey(key)) {
            return (String) map.get(key);
        } else {
            logger.error("No {} found in data csv map, return an empty String",
                    key);
            return "";
        }
    }

}
