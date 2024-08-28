package com.ericsson.nms.rv.taf.test.arne.element;


import javax.xml.bind.annotation.XmlElement;

/**
 * Created by ewandaf on 17/07/14.
 */

public class Connectivity {
//    @XmlElementWrapper(name = "DEFAULT")
    @XmlElement(name="DEFAULT")
    private Default aDefault;

    public Default getaDefault() {
        return aDefault;
    }
}
