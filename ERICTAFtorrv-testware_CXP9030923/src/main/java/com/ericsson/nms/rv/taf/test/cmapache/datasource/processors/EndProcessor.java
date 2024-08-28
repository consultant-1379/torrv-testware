package com.ericsson.nms.rv.taf.test.cmapache.datasource.processors;

import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by ewandaf on 30/07/14.
 */
public class EndProcessor extends Processor implements NoIndexProcessor{

    private static final Logger logger = LoggerFactory.getLogger(EndProcessor.class);
    public String process(String template, List<Node> dataRecords) {


        final List<String> list7 = getStringsByReg(
                "[$][0-9]+[a-zA-Z]+_END[0-9]+", template);


        for (final String l : list7) {
            final List<String> nums = getStringsByReg("[0-9]+", l);
            final String header = getStringsByReg("[a-zA-Z]+", l).get(0);
            final int length = Integer.valueOf(nums.get(0));
            final int indexNode = Integer.valueOf(nums.get(1));
            if (indexNode >= dataRecords.size()) {
                logger.error("The node data source does not has enough node {}", indexNode);
                throw new IllegalArgumentException("[$]" + length + header + "_END" + indexNode + " cannot be replaced with any nodes");
            }
            final String value = (String) dataRecords.get(indexNode)
                    .get(header);
            final String subString = "*"
                    + value.substring(value.length() - length);
            template = template.replaceAll("[$]" + length + header + "_END"
                    + indexNode, subString);
        }


        return template;
    }
}
