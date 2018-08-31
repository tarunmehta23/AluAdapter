package com.charter.provisioning.voice.commercial.alu.session;

import com.charter.provisioning.voice.commercial.alu.exceptions.NetworkException;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@Data
public class SessionHandler {

    private final SessionRequestHelper sessionHelper;

    private Map<String, String> sessionMap = new HashMap<>();

    private String sessionId = null;

    @Value("${OMCPSwitchConfiguration.UserId}")
    private String omcpUserName;

    @Value("${OMCPSwitchConfiguration.Password}")
    private String omcpPassword;

    @Autowired
    public SessionHandler(SessionRequestHelper sessionHelper) {
        this.sessionHelper = sessionHelper;
    }

    /**
     * Re-establishes connection to OMC-P.
     *
     * @param requestId the correlation id for the request.
     * @return String session ID.
     */
    synchronized String reestablishSession(String requestId) throws ProvisioningServiceException, NetworkException {
        terminateSession(requestId);
        return establishSession(requestId);
    }

    /**
     * Voids the connection to OMC-P.
     */
    public void voidSession() {
        this.sessionId = null;
        this.sessionMap.remove("SessionId");
    }

    /**
     * Terminates the connection to OMC-P.
     */
    public void logout() throws NetworkException  {
        log.info("Initiating OMC-P Logout");
        terminateSession("OMC-P_LOGOUT_ON_ADAPTER_SHUTDOWN");
        log.info("OMC-P Logout .. OK");
    }

    /**
     * Terminate the given session to OMC-P
     * @param requestId the correlation id for the request.
     * @param sessionId the session id for the OMC-P connection.
     */
    synchronized void terminateSession(String requestId, String sessionId) {
        try {
            sessionHelper.logoff(requestId, sessionId, sessionMap);
            log.info("[{}] Session Terminated ={}", requestId, sessionId);
            voidSession();
        } catch (ProvisioningServiceException e) {
            log.warn("exception terminating OMC-P session [{}]:", requestId, e.getMessage());
        }
    }

    private synchronized void terminateSession(String requestId)   {
        try {
            sessionHelper.logoff(requestId, this.sessionId, sessionMap);

            log.info("[{}] Session Terminated ={}", requestId, sessionId);

            voidSession();
            log.debug("[{}] Session Terminated ={}", requestId, sessionId);
        } catch (ProvisioningServiceException e) {
            log.error("exception terminating OMC-P session [{}]:", requestId, e.getMessage());
        }
    }

    public synchronized String establishSession(String requestId) {
        log.info("[{}] SessionId={}" ,requestId, sessionId);

        String existingSessionId = this.sessionMap.get("SessionId");

        if (this.sessionId == null) {
            if ((existingSessionId != null)) {
                log.info("[{}] Terminating existing session: {}", requestId, existingSessionId);
                terminateSession("UUID_EXISTING_SESSION_TERMINATION", existingSessionId);
            }

            this.sessionId = sessionHelper.getLoginSessionId(requestId, omcpUserName, omcpPassword, sessionMap);
            this.sessionMap.put("SessionId", sessionId);
        }

        return this.sessionId;
    }

}
