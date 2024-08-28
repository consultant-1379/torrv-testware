package com.ericsson.nms.rv.taf.test.apache.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.tools.http.*;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.rv.taf.test.apache.operators.dto.ApacheResponse;

/**
 * Created by ewandaf on 21/05/14.
 */
@Operator(context = Context.REST)
public class ApacheRestOperator implements ApacheOperator {
    private static ThreadLocal<HttpTool> httpToolThreadLocal = new ThreadLocal<HttpTool>();
    private static ThreadLocal<Boolean> userIsLoggedInThreadLocal = new ThreadLocal<Boolean>();
    private static final int APACHE_LOGOUT_RESPONSE_CODE = 200;
    private static final String VALID_LOGIN = "0";
    private static final Logger logger = LoggerFactory
            .getLogger(ApacheRestOperator.class);
    public static final int HTTPTOOL_TIMEOUT_SECONDS = Integer
            .parseInt(DataHandler.getAttribute("httptool.timeout.seconds")
                    .toString());

    @Override
    public ApacheResponse login(final String userId, final String password) {

        logger.info("Logging in to ENM via REST with user/password: {}/{}",
                userId, password);

        final ApacheResponse retResp = new ApacheResponse();
        HttpTool httpTool = ApacheRestOperator.getHttpTool();

        if (httpTool == null) {
            userIsLoggedInThreadLocal.set(Boolean.FALSE);
            httpTool = HttpToolBuilder.newBuilder(HostConfigurator.getApache())
                    .timeout(HTTPTOOL_TIMEOUT_SECONDS).useHttpsIfProvided(true)
                    .trustSslCertificates(true).followRedirect(false).build();

            final HttpResponse response = httpTool.request()
                    .body("IDToken1", userId).body("IDToken2", password)
                    .post("/login");

            final String authErrorCode = response.getHeaders().get(
                    "X-AuthErrorCode");
            final boolean success = VALID_LOGIN.equals(authErrorCode);
            logger.debug("login. X-AuthErrorCode:{}", authErrorCode);
            retResp.setSuccess(success);
            retResp.setAuthErrorCode(authErrorCode);
            retResp.setStatusCode(response.getResponseCode().getCode());

            if (success) {
                httpTool.addCookie("TorUserID", userId);
                userIsLoggedInThreadLocal.set(Boolean.TRUE);
            } else {
                logger.warn("Failed to log in. Return code: {}, Body: {}",
                        response.getResponseCode().getCode(),
                        response.getBody());
                if (!VALID_LOGIN.equals(authErrorCode)) {
                    retResp.setErrorMessage(String
                            .format("Invalid Credentials. Authentication Error Code:%s",
                                    authErrorCode));
                } else {
                    retResp.setErrorMessage(response.getStatusLine());
                }
            }
            httpToolThreadLocal.set(httpTool);
        } else {
            userIsLoggedInThreadLocal.set(Boolean.TRUE);
            retResp.setSuccess(false);
            retResp.setErrorMessage("A user is already logged in.");
        }
        return retResp;
    }

    @Override
    public ApacheResponse logout() {
        final ApacheResponse retResp = new ApacheResponse();
        final HttpTool httpTool = ApacheRestOperator.getHttpTool();
        logger.info("Logging out of ENM via REST.");
        ApacheRestOperator.skipTestIfUserIsNotLoggedIn();

        final HttpResponse response = httpTool.request().get("/logout");
        retResp.setStatusCode(response.getResponseCode().getCode());

        if (response.getResponseCode().getCode() != APACHE_LOGOUT_RESPONSE_CODE) {
            retResp.setSuccess(false);
            retResp.setErrorMessage(response.getStatusLine());
        } else {
            retResp.setSuccess(true);
        }
        httpTool.close();
        httpToolThreadLocal.remove();

        return retResp;
    }

    public static HttpTool getHttpTool() {
        return httpToolThreadLocal.get();
    }

    public static Boolean isUserLoggedIn() {
        return userIsLoggedInThreadLocal.get().booleanValue();
    }

    public static void skipTestIfUserIsNotLoggedIn() {
        final boolean isLoggedIn = ApacheRestOperator.isUserLoggedIn();
        if (ApacheRestOperator.getHttpTool() == null) {
            throw new SkipException(
                    "User is not logged in. HttpTool is null. isUserLoggedIn value is "
                            + isLoggedIn);
        }
        if (!isLoggedIn) {
            throw new SkipException(
                    "User is not logged in. HttpTool is not null.");
        }
    }
}
