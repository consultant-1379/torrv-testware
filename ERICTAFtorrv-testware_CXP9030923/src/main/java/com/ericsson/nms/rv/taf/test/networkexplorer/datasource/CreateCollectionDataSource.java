package com.ericsson.nms.rv.taf.test.networkexplorer.datasource;

import com.ericsson.cifwk.taf.annotations.DataSource;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.CsvParser;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.DataSourceBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ewandaf on 10/07/14.
 */
public class CreateCollectionDataSource {

    private final String CREATE_COLLECTION_TEMPLATE_FILE = (String) DataHandler
            .getAttribute("networkexplorer.create.collection.template.file");

    @DataSource
    public List<Map<String, Object>>  createCollection() {
        CsvParser csvReader = null;
        try {
            csvReader = new CsvParser(CREATE_COLLECTION_TEMPLATE_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> fileValues = csvReader.getValuesAsMaps();
        List<List<Map<String, Object>>> commandsMapList = DataSourceBuilder.getListOfCommandsMap(fileValues);
        List<Map<String, Object>> toReturn = commandsMapList.get(0);
        return toReturn;
    }
}
