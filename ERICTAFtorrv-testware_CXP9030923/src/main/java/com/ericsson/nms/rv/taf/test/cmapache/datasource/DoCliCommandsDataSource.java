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
package com.ericsson.nms.rv.taf.test.cmapache.datasource;

import static com.ericsson.nms.rv.taf.test.cmapache.operators.CmApachePropertyKeys.BUILD_DATA_SOURCE_NAME;
import static com.ericsson.nms.rv.taf.test.cmapache.operators.CmApachePropertyKeys.HEADER_FOR_COMMAND_GROUP;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.DataSource;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.apache.cases.UtilityClass;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;
import com.ericsson.nms.rv.taf.test.cmapache.operators.CmApachePropertyKeys;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.*;

public class DoCliCommandsDataSource {

    private static final Logger logger = LoggerFactory
            .getLogger(DoCliCommandsDataSource.class);
    private final String DO_CLI_COMMANDS_TEMPLATE_FILE = (String) DataHandler
            .getAttribute(CmApachePropertyKeys.DO_CLI_COMMANDS_TEMPLATE_FILE_KEY);

    @DataSource
    public List<Map<String, CommandGroup>> addNodesData() {
        CsvParser templateReader = null;
        try {
            templateReader = new CsvParser(DO_CLI_COMMANDS_TEMPLATE_FILE);
        } catch (final IOException e) {
            logger.error("IOException while reading file: {}",
                    DO_CLI_COMMANDS_TEMPLATE_FILE);
            e.printStackTrace();
        } catch (final Exception e) {
            logger.error("Exception while reading file: {}",
                    DO_CLI_COMMANDS_TEMPLATE_FILE);
            e.printStackTrace();
        }

        final List<Map<String, Object>> templateValues = templateReader
                .getValuesAsMaps();
        final CommandTemplate commandTemplate = new CommandTemplate();
        for (final Map<String, Object> m : templateValues) {
            final Command command = UtilityClass.getObjectFromMap(
                    Command.class, m);
            commandTemplate.add(command);
        }
        final List<Node> nodes = UtilityClass
                .getNodeInDataSource(BUILD_DATA_SOURCE_NAME);
        final List<CommandGroup> commandGroup = commandTemplate
                .buildCommands(nodes);
        return buildMap(commandGroup);
    }

    private static List<Map<String, CommandGroup>> buildMap(
            final List<CommandGroup> commandGroup) {
        final List<Map<String, CommandGroup>> toReturn = new ArrayList<>();
        for (final CommandGroup commandGroup1 : commandGroup) {
            final Map<String, CommandGroup> map = new HashMap<>(1);
            map.put(HEADER_FOR_COMMAND_GROUP, commandGroup1);
            toReturn.add(map);
        }
        return toReturn;
    }
}
