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

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import com.ericsson.cifwk.taf.utils.FileFinder;

public class CsvParser {

    private final Logger logger = LoggerFactory.getLogger(CsvParser.class);
    private final List<Map<String, Object>> valuesAsMaps;
    List<String> headerList;

    public CsvParser(String csvFileName) throws FileNotFoundException,
            IOException {
        final List<String> filePath = FileFinder.findFile(csvFileName);
        logger.debug("FileFinder. CSV file name: {}. Found file path list: {}",
                csvFileName, filePath);
        File csvFile = null;
        for (final String singleFilePath : filePath) {
            final File file = new File(singleFilePath);
            if (file.getName().equals(csvFileName)) {
                csvFile = file;
                break;
            }
        }
        if (csvFile == null) {
            throw new FileNotFoundException("Couldn't find file: ["
                    + csvFileName + "]");
        }
        FileReader fr = null;
        CsvMapReader mapReader = null;

        try {
            fr = new FileReader(csvFile);
            mapReader = new CsvMapReader(fr, CsvPreference.EXCEL_PREFERENCE);

            valuesAsMaps = new ArrayList<Map<String, Object>>();
            final String[] headers = mapReader.getHeader(true);
            headerList = Arrays.asList(headers);
            Map<String, String> row;
            Map<String, Object> rowData;

            while ((row = mapReader.read(headers)) != null) {
                rowData = new HashMap<String, Object>();
                for (final String key : headerList) {
                    Object value = row.get(key);
                    if (value == null) {
                        value = "";
                    }
                    rowData.put(key, value);
                }
                valuesAsMaps.add(rowData);
            }

        } finally {
            if (mapReader != null) {
                mapReader.close();
            }
            if (fr != null) {
                fr.close();
            }
        }
    }

    public List<Map<String, Object>> getValuesAsMaps() {
        return valuesAsMaps;
    }

    public List<String> getHeaders() {
        return headerList;
    }

    public int rowCount() {
        return valuesAsMaps.size();
    }

    //    public static void main(String[] args) {
    //        CsvParser p;
    //        try {
    //            p = new CsvParser(
    //                    "C:\\Users\\epaulki\\git\\RV\\torrv-testware\\ERICTAFrvtestware_CXP9030923\\src\\main\\resources\\data\\217_describe_model_attribute_commands.csv");
    //
    //            System.out.println("Headers:\n\t" + p.getHeaders());
    //            List<Map<String, Object>> values = p.getValuesAsMaps();
    //            System.out.println("Row count:" + values.size());
    //            for (Map<String, Object> map : values) {
    //                System.out.println("MAP:");
    //                for (String key : p.getHeaders()) {
    //                    System.out
    //                            .format("\tKey:%s, value:%s\n", key, map.get(key));
    //                }
    //            }
    //        } catch (FileNotFoundException e) {
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //        } catch (IOException e) {
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //        }
    //    }
}
