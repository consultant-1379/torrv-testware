package com.ericsson.nms.rv.taf.test.cmapache.datasource;

import static com.ericsson.nms.rv.taf.test.cmapache.operators.CmApachePropertyKeys.BUILD_DATA_SOURCE_NAME;

import java.io.IOException;
import java.util.*;

import com.ericsson.cifwk.taf.annotations.DataSource;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.apache.cases.UtilityClass;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;
import com.ericsson.nms.rv.taf.test.cmapache.operators.CmApachePropertyKeys;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.DataCsv;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.DataCsvTemplate;

/**
 * Created by ewandaf on 10/12/14.
 */
public class DataCsvDataSource {

    private final String DATA_CSV_TEMPLATE_FILE_KEY = (String) DataHandler
            .getAttribute(CmApachePropertyKeys.DATA_CSV_TEMPLATE_FILE_KEY);

    @DataSource
    public List<Map<String, Object>> dataCsvDataSource() {
        final List<Map<String, Object>> toReturn = new ArrayList<Map<String, Object>>();
        CsvParser templateReader = null;
        try {
            templateReader = new CsvParser(DATA_CSV_TEMPLATE_FILE_KEY);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        final List<Map<String, Object>> templateValues = templateReader
                .getValuesAsMaps();
        final List<Node> nodes = UtilityClass
                .getNodeInDataSource(BUILD_DATA_SOURCE_NAME);
        for (final Map<String, Object> row : templateValues) {
            final DataCsv dataCsv = new DataCsv(row);
            final DataCsvTemplate dataCsvTemplate = new DataCsvTemplate(dataCsv);
            final List<DataCsv> dataCsvs = dataCsvTemplate.buildDataCsv(nodes);
            final List<Map<String, Object>> listMap = buildMap(dataCsvs);
            toReturn.addAll(listMap);
        }
        return toReturn;
    }

    private List<Map<String, Object>> buildMap(List<DataCsv> dataCsvs) {
        final List<Map<String, Object>> toReturn = new ArrayList<Map<String, Object>>();
        for (final DataCsv dataCsv : dataCsvs) {
            toReturn.add(dataCsv.getMap());
        }
        return toReturn;
    }
}
