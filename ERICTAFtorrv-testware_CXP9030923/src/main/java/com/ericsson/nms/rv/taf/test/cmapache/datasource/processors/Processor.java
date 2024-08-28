package com.ericsson.nms.rv.taf.test.cmapache.datasource.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ewandaf on 30/07/14.
 */
public abstract class Processor {

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
