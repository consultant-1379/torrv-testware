package com.ericsson.nms.rv.taf.test.arne.element;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by ewandaf on 17/07/14.
 */
public class Default {
    @XmlElement(name = "ipAddress")
    private IpAddress ipAddress;

    public IpAddress getIpAddress() {
        return ipAddress;
    }
}
