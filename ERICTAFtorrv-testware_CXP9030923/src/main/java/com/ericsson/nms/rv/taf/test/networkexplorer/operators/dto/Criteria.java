package com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ewandaf on 27/06/14.
 */
public class Criteria {
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(final String comparator) {
        this.comparator = comparator;
    }

    private String attribute;
    private String comparator;

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    private String value;

    public Criteria(final String attribute, final String comparator,
            final String value) {
        this.attribute = attribute;
        this.comparator = comparator;
        this.value = value;
    }

    public Criteria(final String criteria) {
        final Pattern pattern = Pattern.compile("[^a-zA-Z0-9]+");
        final Matcher matcher = pattern.matcher(criteria);
        if (matcher.find()) {
            final String comparator = matcher.group();
            final String[] cs = criteria.split(comparator);
            this.attribute = cs[0];
            this.comparator = comparator;
            this.value = cs[1];
        }

    }

    @Override
    public String toString() {
        return attribute + ":" + comparator + ":" + value;
    }
}
