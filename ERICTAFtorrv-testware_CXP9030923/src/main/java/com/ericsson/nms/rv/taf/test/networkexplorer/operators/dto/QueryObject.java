package com.ericsson.nms.rv.taf.test.networkexplorer.operators.dto;

import java.util.List;

/**
 * Created by ewandaf on 30/06/14.
 */
public class QueryObject {
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    private String name;

    public QueryObject getChild() {
        return child;
    }

    public void setChild(final QueryObject child) {
        this.child = child;
    }

    private QueryObject child;

    public List<Criteria> getCriteriaList() {
        return criteriaList;
    }

    public void setCriteriaList(final List<Criteria> criteriaList) {
        this.criteriaList = criteriaList;
    }

    private List<Criteria> criteriaList;

    public QueryObject(final String name, final QueryObject child,
            final List<Criteria> criteriaList) {
        this.name = name;
        this.child = child;
        this.criteriaList = criteriaList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Name: ");
        sb.append(name);
        sb.append(", criteria list: ");
        sb.append(criteriaList.toString());

        if (child != null) {
            sb.append(" Child: ");
            sb.append(child.toString());
        }
        return sb.toString();
    }
}
