package com.ericsson.nms.rv.taf.test.cmapache.operators.dto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * This file is mapped to cli command data file.
 *
 */
public class Command {
    String command;
    String expectedBodyContains;

    public Command() {

    }

    public Command(String command, String expectedResult) {
        this.command = command;
        this.expectedBodyContains = expectedResult;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getExpectedBodyContains() {
        return expectedBodyContains;
    }

    public void setExpectedBodyContains(String expectedResult) {
        this.expectedBodyContains = expectedResult;
    }

    public Object get(String key) {
        final Class c = this.getClass();
        try {
            final Method m = c.getMethod(getGetter(key));
            return m.invoke(this);
        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (final InvocationTargetException e) {
            e.printStackTrace();
            return null;
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getGetter(String key) {
        return "get" + key.substring(0, 1).toUpperCase()
                + key.substring(1, key.length());
    }
}
