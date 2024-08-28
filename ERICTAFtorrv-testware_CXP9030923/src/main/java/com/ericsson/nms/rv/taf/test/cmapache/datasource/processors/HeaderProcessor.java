package com.ericsson.nms.rv.taf.test.cmapache.datasource.processors;

import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;

import java.util.List;
import java.util.Map;

/**
 * Created by ewandaf on 30/07/14.
 */
public class HeaderProcessor extends Processor implements NodeIndexProcessor {

    public String process(String template, List<Node> dataRecords, int index) {

        int nodeIndex = index;
        final List<String> list = getStringsByReg(
                "[$][a-zA-Z]+[^0-9|a-zA-Z|_]?", template);
        for (String l : list) {
            final String header = getStringsByReg("[a-zA-Z]+", l).get(0);
            final Node nodeDetail1 = dataRecords.get(nodeIndex)
                    ;
            final String value = (String) nodeDetail1.get(header);
            template = template.replaceAll("[$]" + header, value);
        }

        return template;
    }

}
