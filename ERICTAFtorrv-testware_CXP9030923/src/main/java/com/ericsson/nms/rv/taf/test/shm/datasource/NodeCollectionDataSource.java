package com.ericsson.nms.rv.taf.test.shm.datasource;

import com.ericsson.cifwk.taf.annotations.DataSource;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.CsvParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ejocott on 11/11/2014.
 */
public class NodeCollectionDataSource {

    @DataSource
    public static List<Map<String, Object>> getNodesFromCollection(String NodeList) {
        CsvParser csvReader = null;
        try {
            csvReader = new CsvParser(NodeList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> fileValues = csvReader.getValuesAsMaps();
        return fileValues;
    }

}
