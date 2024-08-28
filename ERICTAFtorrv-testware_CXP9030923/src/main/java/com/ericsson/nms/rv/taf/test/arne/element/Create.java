package com.ericsson.nms.rv.taf.test.arne.element;

/**
 * Created by ewandaf on 17/07/14.
 */
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dafei on 7/16/2014.
 */
public class Create {

//    @XmlElement(name = "ManagedElement")
//    List<ManagedElementXml> managedElementList;

    //    public List<ManagedElementXml> getManagedElementList() {
//        return managedElementList;
//    }


    @XmlElements({
            @XmlElement(name = "ManagedElement", type = ManagedElementXml.class),
            @XmlElement(name="SubNetwork", type = SubNetwork.class)
    })
    private List<Object> managedElementOrSubNetwork;

    public List<Object> getManagedElementOrSubNetwork() {
        if (managedElementOrSubNetwork == null) {
            managedElementOrSubNetwork = new ArrayList<Object>();
        }
        return this.managedElementOrSubNetwork;
    }
}
