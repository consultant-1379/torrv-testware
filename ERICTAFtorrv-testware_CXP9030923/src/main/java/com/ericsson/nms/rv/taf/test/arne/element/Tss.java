/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.rv.taf.test.arne.element;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class Tss {

    @XmlElements({ @XmlElement(name = "Entry", type = Entry.class) })
    private List<Entry> entries;

    public List<Entry> geEntryList() {
        if (entries == null) {
            entries = new ArrayList<Entry>();
        }
        return this.entries;
    }
}
