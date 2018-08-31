package com.charter.provisioning.voice.commercial.alu.session;

import com.alu.plexwebapi.api.PlexViewRequestType;
import com.alu.plexwebapi.api.PlexViewRequestType.Factory;
import com.alu.plexwebapi.api.PlexViewResponseType;
import com.charter.provisioning.voice.commercial.alu.config.OMCPCommands;
import com.charter.provisioning.voice.commercial.alu.exceptions.LogonException;
import com.charter.provisioning.voice.commercial.alu.exceptions.OMCPSessionInvalidOrSessionTimeoutException;
import com.charter.provisioning.voice.commercial.alu.helper.NetworkServiceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class SessionRequestHelper {

    private final NetworkServiceHelper networkServiceHelper;

    public static final String SESSION_INVALID = "30001";

    public static final String SESSION_TIMED_OUT = "30002";

    @Autowired
    public SessionRequestHelper(NetworkServiceHelper networkServiceHelper) {
        this.networkServiceHelper = networkServiceHelper;
    }

    /**
     * Logs into the Web API, and get the valid SessionId from OMC-P Server.
     * @param requestId the correlation id.
     * @param userName the user name used to established a connection to OMC-P.
     * @param password the password used to established a connection to OMC-P.
     * @param sessionMap a map containing OMC-P connections.
     *
     * @return the session id of the OMC-P connection.
     */
    String getLoginSessionId(String requestId, String userName, String password, Map<String, String> sessionMap) {
        PlexViewResponseType loginResponse = processLoginRequest(requestId, userName, password, sessionMap);
        validateLogonResponse(loginResponse);

        log.info("[{}] OMC-P login successful, SessionId={}", requestId, loginResponse.getSessionId());

        return loginResponse.getSessionId();
    }

    /**
     * To log off the Web API, enter the following CANC-USER command
     * @param requestId the correlation id.
     * @param sessionId the session id of the OMC-P connection.
     * @param sessionMap a map containing OMC-P connections.
     *
     * @return indicator if logoff was successful.
     */
    boolean logoff(String requestId, String sessionId, Map<String, String> sessionMap) {
        if (StringUtils.isEmpty(sessionId)) {
            return true;
        }

        PlexViewResponseType logoffResponse = processLogoffRequest(requestId, sessionId, sessionMap);
        boolean logoffSuccess = validate(logoffResponse);

        if (logoffSuccess) {
            log.info("[{}] OMC-P logout successful, for SessionId={}", requestId, sessionId);
        }

        return logoffSuccess;
    }

    private PlexViewResponseType processLoginRequest(String uuid, String userName, String password, Map<String, String> sessionMap) {
        PlexViewRequestType loginRequest = createLoginRequest(uuid, userName, password);
        return networkServiceHelper.retrieveSessionIdFromNetwork(loginRequest, sessionMap);
    }

    private PlexViewResponseType processLogoffRequest(String requestId, String sessionId, Map<String, String> sessionMap) {
        PlexViewRequestType logoffRequest = createLogoffRequest(requestId, sessionId);
        return networkServiceHelper.terminateExistingSessionIdFromNetwork(logoffRequest, sessionMap);
    }

    private void validateLogonResponse(PlexViewResponseType logonResponse) {
        if (logonResponse.getFailureCode() != null) {
            String message = logonResponse.getFailureCode() + "=" + logonResponse.getFailureReason();
            log.error("OMC-P Login failed: {}", message);
            throw new LogonException(message, logonResponse);
        }
    }

    private PlexViewRequestType createLoginRequest(String uuid, String userName, String password) {
        PlexViewRequestType plexViewRequest = Factory.newInstance();
        plexViewRequest.setCommand(OMCPCommands.ACT_USER);
        plexViewRequest.setRequestId(uuid);
        plexViewRequest.setUserName(userName);
        plexViewRequest.setPassWord(password);

        return plexViewRequest;
    }

    private PlexViewRequestType createLogoffRequest(String uuid, String sessionId) {
        PlexViewRequestType plexViewRequest = Factory.newInstance();
        plexViewRequest.setCommand(OMCPCommands.CANC_USER);
        plexViewRequest.setRequestId(uuid);
        plexViewRequest.setSessionId(sessionId);
        return plexViewRequest;
    }

    public boolean validate(PlexViewResponseType plexViewResponse) {
        if (plexViewResponse.getFailureCode() != null) {
            String errMessage = String.format("%s=%s", plexViewResponse.getFailureCode(), plexViewResponse.getFailureReason());

            if (SESSION_INVALID.equalsIgnoreCase(plexViewResponse.getFailureCode().toString())) {
                log.error("Invalid CTS Switch session : {}.", errMessage);
                throw new OMCPSessionInvalidOrSessionTimeoutException(errMessage);
            }

            if (SESSION_TIMED_OUT.equalsIgnoreCase(plexViewResponse.getFailureCode().toString())) {
                log.error("CTS Switch session timed out: {}.", errMessage);
                throw new OMCPSessionInvalidOrSessionTimeoutException(errMessage);
            }
        }
        return true;
    }

}