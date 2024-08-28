package com.ericsson.nms.rv.taf.test.netsim;

import com.ericsson.cifwk.taf.handlers.netsim.Cmd;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimCommand;

/**
 * Created by ewandaf on 17/07/14.
 */
@Cmd(value = ".select network")
public class SelectNetworkCommand implements NetSimCommand {

    SelectNetworkCommand() {

    }

    //    @Cmd(value = "target", index = 0)
    //    public String getTarget() {
    //        return this.target;
    //    }
    //
    //    public SelectNetworkCommand setTarget(String value) {
    //        this.target = value;
    //        return this;
    //    }
}
