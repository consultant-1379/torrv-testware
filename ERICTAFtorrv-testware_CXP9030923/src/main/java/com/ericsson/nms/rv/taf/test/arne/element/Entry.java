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
package com.ericsson.nms.rv.taf.test.arne.element;

import javax.xml.bind.annotation.XmlElement;

public class Entry {

    @XmlElement(name = "Type")
    private Type type;

    @XmlElement(name = "User")
    private User user;

    @XmlElement(name = "Password")
    private Password password;

    public Type getType() {
        return type;
    }

    public User getUser() {
        return user;
    }

    public Password getPassword() {
        return password;
    }
}
