package com.charter.provisioning.voice.commercial.alu.handler;

import com.alu.plexwebapi.api.PlexViewRequestType;
import com.alu.plexwebapi.api.PlexViewResponseType;
import com.charter.provisioning.voice.commercial.alu.exceptions.OMCPSessionInvalidOrSessionTimeoutException;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.helper.NetworkServiceHelper;
import com.charter.provisioning.voice.commercial.alu.session.SessionHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Data
public class NetworkHandler {

    private final NetworkServiceHelper networkServiceHelper;

    private final SessionHandler sessionHandler;

    private final String SESSION_ID = "SessionId";

    @Autowired
    public NetworkHandler(NetworkServiceHelper networkServiceHelper, SessionHandler sessionHandler) {
        this.networkServiceHelper = networkServiceHelper;
        this.sessionHandler = sessionHandler;
    }

    /**
     * Submits request to the OMC-P Server.
     *
     * @param plexViewRequest the request object to send to OMC-P.
     * @return the response from object from OMC-P.
     */
    public PlexViewResponseType provisionNetwork(PlexViewRequestType plexViewRequest) {
        String sessionId = sessionHandler.getSessionMap().get(SESSION_ID);

        if (sessionId == null) {
            plexViewRequest.setSessionId(sessionHandler.establishSession(plexViewRequest.getRequestId()));
        } else {
            plexViewRequest.setSessionId(sessionId);
        }

        PlexViewResponseType response = networkServiceHelper.executeNetworkCall(plexViewRequest, sessionHandler.getSessionMap());

        try {
            sessionHandler.getSessionHelper().validate(response);
        } catch (OMCPSessionInvalidOrSessionTimeoutException e) {
            reSubmitRequest(plexViewRequest);
        }

        validateResponse(plexViewRequest.getRequestId(), response);
        return response;
    }

    private void reSubmitRequest(PlexViewRequestType resubmitRequest) {
        sessionHandler.voidSession();
        sessionHandler.establishSession(resubmitRequest.getRequestId());
        resubmitRequest.setSessionId(sessionHandler.getSessionMap().get(SESSION_ID));
        networkServiceHelper.executeNetworkCall(resubmitRequest, sessionHandler.getSessionMap());
    }

    private void validateResponse(String requestId, PlexViewResponseType plexViewResponse) {
        if (plexViewResponse.getFailureCode() != null) {
            String errMessage = String.format("%s=%s", plexViewResponse.getFailureCode(), plexViewResponse.getFailureReason());

            log.error("[Error Message from Response={}]: {}", requestId, errMessage);
            throw new ProvisioningServiceException(errMessage);
        }
    }
}