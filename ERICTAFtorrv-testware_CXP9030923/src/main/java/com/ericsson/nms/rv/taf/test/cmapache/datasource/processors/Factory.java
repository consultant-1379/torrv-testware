package com.ericsson.nms.rv.taf.test.cmapache.datasource.processors;

import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ewandaf on 30/07/14.
 */
public class Factory {

    static List<Node> dataRecords = new ArrayList<>();

    public static void setDataRecords(List<Node> dataRecordList) {
        dataRecords = dataRecordList;
    }

    public static String process(String template, int nodeIndex, NodeIndexProcessor... processors) {
        for (NodeIndexProcessor processor : processors) {
            template = processor.process(template, dataRecords, nodeIndex);
        }
        return template;
    }

    public static String process(String template, NoIndexProcessor... processors) {
        for (NoIndexProcessor processor : processors) {
            template = processor.process(template, dataRecords);
        }
        return template;
    }
}
