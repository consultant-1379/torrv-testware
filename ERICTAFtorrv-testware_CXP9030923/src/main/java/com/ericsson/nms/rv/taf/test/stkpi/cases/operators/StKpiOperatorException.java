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
package com.ericsson.nms.rv.taf.test.stkpi.cases.operators;

public class StKpiOperatorException extends Exception {

    private static final long serialVersionUID = 1L;

    public StKpiOperatorException() {
    }

    public StKpiOperatorException(final String message) {
        super(message);
    }

    public StKpiOperatorException(final String paramString,
            final Throwable paramThrowable) {
        super(paramString, paramThrowable);
    }

    public StKpiOperatorException(final Throwable paramThrowable) {
        super(paramThrowable);
    }

    protected StKpiOperatorException(final String paramString,
            final Throwable paramThrowable, final boolean paramBoolean1,
            final boolean paramBoolean2) {
        super(paramString, paramThrowable, paramBoolean1, paramBoolean2);
    }
}
