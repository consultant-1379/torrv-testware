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
package com.ericsson.nms.rv.taf.test.pmic.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.nms.rv.taf.test.apache.operators.ApacheRestOperator;

/**
 * @author eantcul
 *
 */

public class PmicPibOperator {
    private final Logger logger = LoggerFactory
            .getLogger(PmicPibOperator.class);

    public PmicResponse pibCommand(final String command,
            final String expectedResultContains) {
        final PmicResponse pmicResp = new PmicResponse();
        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();
        final HttpResponse httpResp = httpTool.get(command);
        final String commandResponse = httpResp.getBody();
        if (!expectedResultContains.equals(commandResponse)) {
            return setResponse(pmicResp, false, String.format(
                    "Expected: %s but found %s.", expectedResultContains,
                    commandResponse));
        }
        logger.info("Successfully executed the command.");
        pmicResp.setSuccess(true);
        return pmicResp;
    }

    private PmicResponse setResponse(final PmicResponse response,
            final boolean success, final String error) {
        response.setSuccess(success);
        response.setErrorMessage(error);
        return response;
    }
}
