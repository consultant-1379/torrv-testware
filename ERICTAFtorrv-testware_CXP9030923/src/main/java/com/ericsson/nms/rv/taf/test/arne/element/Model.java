package com.ericsson.nms.rv.taf.test.arne.element;

/**
 * Created by ewandaf on 17/07/14.
 */
import javax.xml.bind.annotation.*;

/**
 * Created by Dafei on 7/16/2014.
 */
@XmlRootElement(name = "Model")

public class Model {
    @XmlElement(name = "Create")
    private Create create;

    public Create getCreateList() {
        return create;
    }
}
