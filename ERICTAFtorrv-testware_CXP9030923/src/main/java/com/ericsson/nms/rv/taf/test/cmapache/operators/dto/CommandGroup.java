package com.ericsson.nms.rv.taf.test.cmapache.operators.dto;

import java.util.List;

import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;

/**
 *
 * Command Group object will passed into one test case.
 *
 */
public class CommandGroup {
    private List<Command> commands;
    private Node node;

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
