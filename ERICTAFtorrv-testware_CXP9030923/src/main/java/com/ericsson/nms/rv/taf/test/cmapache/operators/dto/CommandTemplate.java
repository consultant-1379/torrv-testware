package com.ericsson.nms.rv.taf.test.cmapache.operators.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;
import com.ericsson.nms.rv.taf.test.cmapache.datasource.processors.*;

public class CommandTemplate {
    List<Command> commandsAndExpectedResult;

    public CommandTemplate() {
        commandsAndExpectedResult = new ArrayList<Command>();
    }

    public void add(Command c) {
        commandsAndExpectedResult.add(c);
    }

    public List<CommandGroup> buildCommands(List<Node> nodes) {
        final List<CommandGroup> toReturn = new ArrayList<>();
        final boolean replaceForAllNodes = this.ifReplaceForAllNodes();
        if (replaceForAllNodes) {
            for (int i = 0; i < nodes.size(); i++) {
                final List<Command> innerList = new ArrayList<Command>();
                for (final Command c : commandsAndExpectedResult) {
                    final String command = replaceTemplate(c.getCommand(),
                            nodes, i);
                    final String expectedResult = replaceTemplate(
                            c.getExpectedBodyContains(), nodes, i);
                    final Command replacedCommand = new Command(command,
                            expectedResult);
                    innerList.add(replacedCommand);
                }
                final CommandGroup commandGroup = new CommandGroup();
                commandGroup.setCommands(innerList);
                commandGroup.setNode(nodes.get(i));
                toReturn.add(commandGroup);
            }
        } else {
            final List<Command> innerList = new ArrayList<Command>();
            for (final Command c : commandsAndExpectedResult) {
                final String command = replaceTemplate(c.getCommand(), nodes);
                final String expectedResult = replaceTemplate(
                        c.getExpectedBodyContains(), nodes);
                final Command replacedCommand = new Command(command,
                        expectedResult);
                innerList.add(replacedCommand);
            }
            final CommandGroup commandGroup = new CommandGroup();
            commandGroup.setCommands(innerList);
            commandGroup.setNode(null);
            toReturn.add(commandGroup);
        }
        return toReturn;
    }

    private boolean ifReplaceForAllNodes() {
        boolean toReturn = false;
        for (final Command c : commandsAndExpectedResult) {
            final List<String> foundList = getStringsByReg(
                    "[$][a-zA-Z]+[^0-9|a-zA-Z|_]", c.getCommand());
            final List<String> fondList2 = getStringsByReg("[$][a-zA-Z]+$",
                    c.getCommand());
            final List<String> foundList3 = getStringsByReg(
                    "[$][a-zA-Z]+[^0-9|a-zA-Z|_]", c.getExpectedBodyContains());
            final List<String> foundList4 = getStringsByReg("[$][a-zA-Z]+$",
                    c.getExpectedBodyContains());

            if (!foundList.isEmpty() || !fondList2.isEmpty()
                    || !foundList3.isEmpty() || !foundList4.isEmpty()) {
                toReturn = true;
                break;
            }
        }
        return toReturn;
    }

    private static String replaceTemplate(String template,
            List<Node> dataRecords, int nodeIndex) {

        Factory.setDataRecords(dataRecords);

        template = Factory
                .process(template, new HeaderNumProcessor(),
                        new AllNumProcessor(), new BeginNumProcessor(),
                        new EndNumProcessor(), new BeginProcessor(),
                        new EndProcessor());
        template = Factory.process(template, nodeIndex, new HeaderProcessor());
        return template;
    }

    private static String replaceTemplate(String template,
            List<Node> dataRecords) {

        Factory.setDataRecords(dataRecords);
        template = Factory
                .process(template, new HeaderNumProcessor(),
                        new AllNumProcessor(), new BeginNumProcessor(),
                        new EndNumProcessor(), new BeginProcessor(),
                        new EndProcessor());
        return template;
    }

    public static List<String> getStringsByReg(String regular, String string) {
        final List<String> toReturn = new ArrayList<String>();
        final Pattern p = Pattern.compile(regular);
        final Matcher m = p.matcher(string);
        while (m.find()) {
            toReturn.add(m.group());
        }
        return toReturn;
    }

}
