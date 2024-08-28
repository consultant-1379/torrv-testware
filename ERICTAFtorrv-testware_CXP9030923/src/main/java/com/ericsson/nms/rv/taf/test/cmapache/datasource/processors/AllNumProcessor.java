package com.ericsson.nms.rv.taf.test.cmapache.datasource.processors;

import java.util.List;

import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;

public class AllNumProcessor extends Processor implements NoIndexProcessor {

    @Override
    public String process(String template, List<Node> dataRecords) {
        final String numOfNodes = String.valueOf(dataRecords.size());
        template = template.replaceAll("[$]ALL_NUM", numOfNodes);
        return template;
    }
}