package com.ericsson.nms.rv.taf.test.arne.element;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by Dafei on 7/16/2014.
 */

public class ManagedElementId {

	@XmlAttribute(name = "string")
	private String string;

	public String getString() {
		return string;
	}
}
