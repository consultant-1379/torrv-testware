package com.ericsson.nms.rv.taf.test.cmapache.datasource.processors;

import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;

import java.util.List;

/**
 * Created by ewandaf on 30/07/14.
 */
public interface NodeIndexProcessor {
    String process(String template, List<Node> dataRecords, int index);
}
