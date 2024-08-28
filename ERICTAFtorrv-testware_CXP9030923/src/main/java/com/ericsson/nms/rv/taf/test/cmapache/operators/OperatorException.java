package com.ericsson.nms.rv.taf.test.cmapache.operators;

import org.slf4j.Logger;

public class OperatorException extends RuntimeException{


    public OperatorException(Logger logger, String format, Object... argArray) {
        logger.error(format, argArray);
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }
}
