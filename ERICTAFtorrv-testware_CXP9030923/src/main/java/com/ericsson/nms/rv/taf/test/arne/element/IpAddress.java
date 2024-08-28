package com.ericsson.nms.rv.taf.test.arne.element;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by ewandaf on 17/07/14.
 */
public class IpAddress {
    @XmlAttribute(name = "string")
    private String ipAddress;

    @XmlAttribute(name = "ip_v4")
    private String ipv4Address;

    public String getIpAddress() {
        return ipAddress;
    }

    public String getIpv4Address() {
        return ipv4Address;
    }
}
