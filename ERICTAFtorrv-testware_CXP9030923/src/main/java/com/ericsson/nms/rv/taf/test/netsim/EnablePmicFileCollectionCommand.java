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
package com.ericsson.nms.rv.taf.test.netsim;

import com.ericsson.cifwk.taf.handlers.netsim.Cmd;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimCommand;

@Cmd(value = "pmdata:enable")
public class EnablePmicFileCollectionCommand implements NetSimCommand {
    public EnablePmicFileCollectionCommand() {

    }
}
