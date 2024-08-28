package com.ericsson.nms.rv.taf.test.arne.element;

/**
 * Created by ewandaf on 17/07/14.
 */
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Dafei on 7/16/2014.
 */

public class ManagedElementXml {
    @XmlElement(name = "Connectivity")
    private Connectivity connectivity;

    @XmlElement(name = "ManagedElementId")
    private ManagedElementId managedElementId;

    @XmlElement(name = "neMIMVersion")
    private NeMimVersion neMIMVersion;

    @XmlAttribute(name = "sourceType")
    private String sourceType;

    @XmlElement(name = "Tss")
    private Tss tss;

    public Connectivity getConnectivity() {
        return connectivity;
    }

    public String getSourceType() {
        return sourceType;
    }

    public ManagedElementId getManagedElementId() {
        return managedElementId;
    }

    public NeMimVersion getNeMIMVersion() {
        return neMIMVersion;
    }

    public Tss getTss() {
        return tss;
    }
}