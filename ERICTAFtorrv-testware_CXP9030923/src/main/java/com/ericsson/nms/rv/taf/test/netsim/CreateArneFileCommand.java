package com.ericsson.nms.rv.taf.test.netsim;

import com.ericsson.cifwk.taf.handlers.netsim.Cmd;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimCommand;

/**
 * Created by ewandaf on 17/07/14.
 */
@Cmd(value=".createarne R12.2")
public class CreateArneFileCommand implements NetSimCommand {

    private String fileName;

    CreateArneFileCommand() {

    }

    @Cmd(value = "fileName", index = 0)
    public String getFileName() {
        return this.fileName;
    }

    @Cmd(value = "parameters", index = 1)
    public String getParameters() {
        return "NETSim %nename secret IP secure sites no_external_associations";
    }

    public CreateArneFileCommand setFileName(String value) {
        this.fileName = value;
        return this;
    }
}
