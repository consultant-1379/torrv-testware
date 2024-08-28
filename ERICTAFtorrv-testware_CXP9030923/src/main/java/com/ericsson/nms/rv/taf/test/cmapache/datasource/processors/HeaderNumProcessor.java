package com.ericsson.nms.rv.taf.test.cmapache.datasource.processors;

import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by ewandaf on 30/07/14.
 */
public class HeaderNumProcessor extends Processor implements NoIndexProcessor {
    private static final Logger logger = LoggerFactory.getLogger(HeaderNumProcessor.class);
    public String process(String template, List<Node> dataRecords) {

        final List<String> list = getStringsByReg("[$][a-zA-Z]+?[0-9]+",
                template);

        for (final String l : list) {
            final String header = getStringsByReg("[a-zA-Z]+", l).get(0);
            final int index = Integer.valueOf(getStringsByReg("[0-9]+", l).get(
                    0));
            if (index >= dataRecords.size()) {
                logger.error("The node data source does not has enough node {}", index);
                throw new IllegalArgumentException("[$]" + header + index + " cannot be replaced with any nodes");
            }
            final Node nodeDetail = dataRecords.get(index);
            final String value = (String) nodeDetail.get(header);
            template = template.replaceAll("[$]" + header + index, value);
        }

        return template;
    }
}
