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
package com.ericsson.nms.rv.taf.test.cmapache.cases;

import static com.ericsson.nms.rv.taf.test.cmapache.operators.CmApachePropertyKeys.*;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.*;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.apache.operators.NodePool;
import com.ericsson.nms.rv.taf.test.apache.operators.NodePoolGroup;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;
import com.ericsson.nms.rv.taf.test.cmapache.operators.CmCommandRestOperator;
import com.ericsson.nms.rv.taf.test.cmapache.operators.dto.*;

public class DeleteNodeTest extends TorTestCaseHelper implements TestCase {
    @Inject
    private CmCommandRestOperator cmOperator;

    @Inject
    private TestContext deleteNodeContext;

    private static final Logger logger = LoggerFactory
            .getLogger(DeleteNodeTest.class);

    private final static String CM_HEARTBEAT_SUPERVISION = "CmNodeHeartbeatSupervision";
    private final static String FM_ALARM_SUPERVISION = "FmAlarmSupervision";
    private final static String SHM_INVENTORY_SUPERVISION = "InventorySupervision";

    @BeforeTest
    @Parameters({ XML_COMMANDS_TEMPLATE, TEST_ID_ATTRIBUTE,
            TEST_TITLE_ATTRIBUTE })
    public void setup(final String templateFileName, final String testId,
            final String testTitle) {
        logger.info("templateFileName: {}; testId:{}, testTitle: {}",
                templateFileName, testId, testTitle);
        DataHandler.setAttribute(DO_CLI_COMMANDS_TEMPLATE_FILE_KEY,
                templateFileName);
        DataHandler.setAttribute(TEST_ID_ATTRIBUTE, testId);
        DataHandler.setAttribute(TEST_TITLE_ATTRIBUTE, testTitle);
    }

    @DataDriven(name = "cm_do_cli_commands")
    @Test
    public void deleteNode(
            @Input(HEADER_FOR_COMMAND_GROUP) final CommandGroup commandGroup) {
        setTestcase(DataHandler.getAttribute(TEST_ID_ATTRIBUTE).toString(),
                DataHandler.getAttribute(TEST_TITLE_ATTRIBUTE).toString());

        final long startTime = System.currentTimeMillis();
        final boolean isAllSuccess = true;
        final List<Command> commands = commandGroup.getCommands();
        final String nodeName = commandGroup.getNode().getManagedElementId();
        if (nodeHasBeenDeleted(nodeName)) {
            throw new SkipException(
                    String.format(
                            "Skip delete command on this node: %s as it has already been deleted.",
                            nodeName));
        }

        if (supervisionMoExists(commands.get(0), CM_HEARTBEAT_SUPERVISION)) {
            turnOffMoSupervision(commands.get(1), CM_HEARTBEAT_SUPERVISION);
        }

        turnOffMoSupervision(commands.get(2), FM_ALARM_SUPERVISION);

        if (supervisionMoExists(commands.get(3), SHM_INVENTORY_SUPERVISION)) {
            turnOffMoSupervision(commands.get(4), SHM_INVENTORY_SUPERVISION);
        }

        deleteMeContext(commands.get(5));
        deleteNetworkElement(commands.get(6));

        final long totalTime = System.currentTimeMillis() - startTime;
        logger.info("Total time in ms to delete node: {}", totalTime);
        returnNodesBackToPool(isAllSuccess, commandGroup);
    }

    private CmResponse doCommand(final Command commandObject) {
        final String command = (String) commandObject.get(COMMAND_HEADER);
        final String expectedBodyContains = (String) commandObject
                .get(EXPECTED_BODY_RESPONSE_CONTAINS_HEADER);

        return cmOperator.doCliCommand(command, expectedBodyContains);
    }

    private boolean supervisionMoExists(final Command commandObject,
            final String moName) {
        final CmResponse resp = doCommand(commandObject);
        if (!resp.isSuccess()) {
            logger.warn(
                    "Failed to find {} MO. Skipping set command. Message: {}. Body: {}",
                    moName, resp.getErrorMessage(), resp.getBody());
        }
        return resp.isSuccess();
    }

    private void turnOffMoSupervision(final Command command, final String moName) {
        final CmResponse resp = doCommand(command);
        if (!resp.isSuccess()) {
            logger.warn("Failed to deactivate {} MO. Message: {}, Body: {}",
                    moName, resp.getErrorMessage(), resp.getBody());
        }
    }

    private void deleteMeContext(final Command command) {
        /*
         * Mecontext delete command now returns 1 instance(s) if successful
         */
        final CmResponse response = doCommand(command);
        if (response.isSuccess()) {
            logger.info("Sucessfully deleted MeContext.");
        } else {
            logger.error("Failed to delete MeContext. Body: {}",
                    response.getBody());
        }
    }

    private void deleteNetworkElement(final Command command) {
        final CmResponse response = doCommand(command);
        if (response.isSuccess()) {
            logger.info("Sucessfully deleted NetworkElement tree.");
        } else {
            logger.error("Failed to delete NetworkElement tree. Body: {}",
                    response.getBody());
        }
    }

    private void returnNodesBackToPool(final boolean isSuccess,
            final CommandGroup commandGroup) {

        logger.debug(
                "returnNodesBackToPool. isSuccess: {}, commandGroup Null: {}",
                isSuccess, commandGroup == null);
        final Node node = commandGroup.getNode();
        final String nodePoolGroup = node.getNodePoolGroup();
        final NodePool nodePool = NodePoolGroup.getNodePool(nodePoolGroup);

        if (isSuccess && commandGroup != null) {
            if (nodePool != null) {
                nodePool.returnObject(node);
                logger.debug("Returned node {} back to pool {}",
                        node.toString(), nodePoolGroup);
                deleteNodeContext.setAttribute(node.getManagedElementId()
                        .trim(), true);
            } else {
                logger.warn("Didn't get node pool named {} for node {}",
                        nodePoolGroup, node.toString());
            }
        } else {
            logger.debug("Failed to return node {} back to pool {}",
                    node.toString(), nodePoolGroup);
        }
    }

    private boolean nodeHasBeenDeleted(final String nodeName) {
        if (deleteNodeContext.getAttribute(nodeName) != null) {
            return true;
        }
        return false;
    }
}