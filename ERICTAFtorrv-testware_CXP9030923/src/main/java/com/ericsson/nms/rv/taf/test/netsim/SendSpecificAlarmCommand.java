package com.ericsson.nms.rv.taf.test.netsim;

import com.ericsson.cifwk.taf.handlers.netsim.Cmd;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimCommand;

/**
 * Created by emicmcf on 30/09/2014.
 */
@Cmd(value = "sendalarm")
public class SendSpecificAlarmCommand implements NetSimCommand {

    //private String problem;
    //private String follower;
    private String specificProblem;

    SendSpecificAlarmCommand() {

    }

    @Cmd(value = "problem", index = 0)
    public String getProblem() {
        return this.specificProblem;
    }

    public SendSpecificAlarmCommand setProblem(String specificProblem) {
        this.specificProblem = specificProblem;
        return this;
    }

    //@Cmd(value = "", index = 1)
    // public String getSpecificProblem(){return "problem=" + this.specificProblem;}

}
