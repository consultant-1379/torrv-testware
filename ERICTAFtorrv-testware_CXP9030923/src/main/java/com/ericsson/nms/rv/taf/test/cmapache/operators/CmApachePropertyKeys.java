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
package com.ericsson.nms.rv.taf.test.cmapache.operators;

import com.ericsson.nms.rv.taf.test.apache.cases.UtilityClass;

public class CmApachePropertyKeys {

    // Sync Node
    public static final String SYNC_NODE_TIMEOUT_SECONDS_KEY = "cm.apache.sync.node.timeout.seconds";
    public static final String SYNC_NODE_SLEEP_TIME_SECONDS_KEY = "cm.apache.sync.node.sleep.time.seconds";

    // Do Cli Commands
    public static final String DO_CLI_COMMANDS_POLL_HEAD_TIMEOUT_SECONDS = "cm.apache.cm.command.head.poll.timeout.seconds";
    public static final String DO_CLI_COMMANDS_TEMPLATE_FILE_KEY = "cm.apache.do.cli.commands.template.file";
    public static final String DO_CLI_COMMANDS_FILE_KEY = "cm.apache.do.cli.commands.file";

    public static final String HEADER_FOR_COMMAND_GROUP = "commandList";

    public static final String COMMAND_HEADER = "command";
    public static final String EXPECTED_BODY_RESPONSE_CONTAINS_HEADER = "expectedBodyContains";
    public static final String POLL_INTERVAL_MILLIS_HEADER = "pollIntervalMillis";
    public static final String POLL_TIMEOUT_MILLIS_HEADER = "pollTimeoutMillis";
    public static final String EXPECTED_NODES_COPIED_HEADER = "expectedNodesCopied";

    public static final String TEST_ID_ATTRIBUTE = "testId";
    public static final String TEST_TITLE_ATTRIBUTE = "testTitle";

    public static final String XML_COMMANDS_TEMPLATE = "commands.template.csv.file";
    public static final String XML_COMMANDS_CSV = "commands.csv.file.name";

    public static final String BUILD_DATA_SOURCE_NAME = UtilityClass.BUILD_DATA_SOURCE_NAME;
    public static final String DATA_CSV_TEMPLATE_FILE_KEY = "data.csv.template.file";
}
