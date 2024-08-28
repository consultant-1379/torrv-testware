package com.ericsson.nms.rv.taf.test.cmapache.operators.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.processors.*;

public class DataCsvTemplate {
    DataCsv dataCsv;

    public DataCsvTemplate(DataCsv dataCsv) {
        this.dataCsv = dataCsv;
    }

    public List<DataCsv> buildDataCsv(List<Node> nodes) {
        final List<DataCsv> toReturn = new ArrayList<>();
        final boolean replaceForAllNodes = this.ifReplaceForAllNodes();
        if (replaceForAllNodes) {
            for (int i = 0; i < nodes.size(); i++) {
                final DataCsv replacedDataCsv = new DataCsv();
                for (final String key : dataCsv.getKeySet()) {
                    final String value = replaceTemplate(dataCsv.get(key),
                            nodes, i);
                    replacedDataCsv.put(key, value);
                }
                toReturn.add(replacedDataCsv);
            }
        } else {
            final DataCsv replacedDataCsv = new DataCsv();
            for (final String key : dataCsv.getKeySet()) {
                final String value = replaceTemplate(dataCsv.get(key), nodes);
                replacedDataCsv.put(key, value);
            }
            toReturn.add(replacedDataCsv);
        }
        return toReturn;
    }

    private boolean ifReplaceForAllNodes() {
        boolean toReturn = false;

        for (final String key : dataCsv.getKeySet()) {
            final List<String> foundList = getStringsByReg(
                    "[$][a-zA-Z]+[^0-9|a-zA-Z|_]", dataCsv.get(key));
            final List<String> fondList2 = getStringsByReg("[$][a-zA-Z]+$",
                    dataCsv.get(key));
            if (!foundList.isEmpty() || !fondList2.isEmpty()) {
                toReturn = true;
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
