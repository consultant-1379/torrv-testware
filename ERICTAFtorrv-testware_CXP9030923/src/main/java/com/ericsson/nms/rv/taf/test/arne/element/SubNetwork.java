package com.ericsson.nms.rv.taf.test.arne.element;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by ewandaf on 18/07/14.
 */
public class SubNetwork {

    @XmlElement(name = "ManagedElement", type = ManagedElementXml.class)
    private List<ManagedElementXml> managedElementList;

    public List<ManagedElementXml> getManagedElementList() {
        return managedElementList;
    }
}
