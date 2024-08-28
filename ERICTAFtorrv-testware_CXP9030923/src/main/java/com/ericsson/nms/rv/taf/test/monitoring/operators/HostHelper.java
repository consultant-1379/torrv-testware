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
package com.ericsson.nms.rv.taf.test.monitoring.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.nms.host.HostConfigurator;

public class HostHelper {

    public static final Logger logger = LoggerFactory
            .getLogger(HostHelper.class);

    public static Host getHostByName(String hostName) {
        if (hostName.equals("ms1")) {
            return HostConfigurator.getMS();
        } else if (hostName.equals("httpd")) {
            return HostConfigurator.getApache();
        } else if (hostName.equals("svc1")) {
            return HostConfigurator.getSVC1();
        } else if (hostName.equals("svc2")) {
            return HostConfigurator.getSVC2();
        }
        logger.error("Host '{}' is not supported by HostConfigurator.",
                hostName);
        return null;
    }
}