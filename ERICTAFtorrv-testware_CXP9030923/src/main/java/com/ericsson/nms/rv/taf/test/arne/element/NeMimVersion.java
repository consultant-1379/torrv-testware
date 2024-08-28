package com.ericsson.nms.rv.taf.test.arne.element;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by ewandaf on 17/07/14.
 */
public class NeMimVersion {
    @XmlAttribute(name = "string")
    private String string;

    public String getString() {
        return string;
    }
}
