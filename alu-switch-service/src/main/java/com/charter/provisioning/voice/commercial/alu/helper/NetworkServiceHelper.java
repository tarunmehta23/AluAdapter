package com.charter.provisioning.voice.commercial.alu.helper;

import com.alu.plexwebapi.api.COMProvisioningService;
import com.alu.plexwebapi.api.PlexViewRequestDocument;
import com.alu.plexwebapi.api.PlexViewRequestType;
import com.alu.plexwebapi.api.PlexViewResponseType;
import com.charter.provisioning.voice.commercial.alu.config.CTSSwitchConfiguration;
import com.charter.provisioning.voice.commercial.alu.exceptions.NetworkException;
import com.charter.provisioning.voice.commercial.alu.model.BusinessClassPhone;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class NetworkServiceHelper {

    private final COMProvisioningService secondaryOmcpService;

    private final COMProvisioningService primaryOmcpService;

    private COMProvisioningService runtimeStub;
    
    private final CTSSwitchConfiguration ctsSwitchConfig;

    private String previousServingSwitchName=null;

    private final String SERVICE_ENDPOINT = "ServiceEndPoint";

    private final String PRIMARY_ENDPOINT = "PrimaryEndPoint";

    @Autowired
    public NetworkServiceHelper(COMProvisioningService secondaryOmcpService, COMProvisioningService primaryOmcpService, CTSSwitchConfiguration ctsSwitchConfig) {
        this.secondaryOmcpService = secondaryOmcpService;
        this.primaryOmcpService = primaryOmcpService;
        this.ctsSwitchConfig = ctsSwitchConfig;
    }

    @PostConstruct
    public void init(){
        runtimeStub = primaryOmcpService;
    }

    /**
     * Logs into the Web API, and get the valid SessionId from OMC-P Server.
     * @param plexViewRequest the request object to establish connectivity to OMC-P.
     * @param sessionMap a map containing OMC-P connections.
     *
     * @return the response from OMC-P.
     */
    public PlexViewResponseType retrieveSessionIdFromNetwork(PlexViewRequestType plexViewRequest,  Map<String, String> sessionMap) {
        return executeNetworkCall(plexViewRequest, primaryOmcpService, false, sessionMap );
    }

    /**
     * Orchestrates the termination of the existing session from the OMC-P Server.
     * @param plexViewRequest the request object to establish connectivity to OMC-P.
     * @param sessionMap a map containing OMC-P connections.
     *
     * @return the response from OMC-P.
     */
    public PlexViewResponseType terminateExistingSessionIdFromNetwork(PlexViewRequestType plexViewRequest, Map<String, String> sessionMap ) {
        return executeNetworkCall(plexViewRequest, primaryOmcpService, false, sessionMap );
    }

    /**
     * Terminates the existing session from the OMC-P Server.
     * @param plexViewRequest the request object to establish connectivity to OMC-P.
     * @param sessionMap a map containing OMC-P connections.
     *
     * @return the response from OMC-P.
     */
    public PlexViewResponseType executeNetworkCall(PlexViewRequestType plexViewRequest, Map<String, String> sessionMap) {
        sessionMap.putIfAbsent(SERVICE_ENDPOINT, PRIMARY_ENDPOINT);

        return executeNetworkCall(plexViewRequest, runtimeStub, true, sessionMap);
    }

    private PlexViewResponseType executeNetworkCall(PlexViewRequestType plexViewRequest, COMProvisioningService runtimeStub, boolean failoverFlag, Map<String, String> sessionMap )  {
        PlexViewRequestDocument plexRequestDocument = PlexViewRequestDocument.Factory.newInstance();
        plexRequestDocument.setPlexViewRequest(plexViewRequest);

        log.info("[Request XML={}]: {}",  plexViewRequest.getRequestId(), plexRequestDocument.toString());

        try {
            PlexViewResponseType response = runtimeStub.plexViewRequestOperation(plexRequestDocument).getPlexViewResponse();
            log.info("[Response XML={}]: {}",  plexViewRequest.getRequestId(), response.toString());

            return response;
        } catch (Exception e) {
            if (failoverFlag) {
                return handleFailOverScenario(plexViewRequest, sessionMap);
            }

            log.error("Exception executing network call to OMC-P[{}]: {}", plexViewRequest.getRequestId(), e.getMessage());
            throw new NetworkException(e.getMessage());
        }
    }

    public void assignSwitch(BusinessClassPhone bcp) {
    	String ctsSwitchName= bcp.getSwitchName();

    	if(ctsSwitchName != null && StringUtils.isNotEmpty(ctsSwitchName)) {
    			ctsSwitchConfig.getCTSSwitchConfiguration(ctsSwitchName);
		}else {
    		this.previousServingSwitchName= ctsSwitchConfig.getCtsConfigurations().stream().filter(cts -> !Objects.equals(cts.getSwitchName(), previousServingSwitchName))
                    .findFirst().get().getSwitchName();
    		bcp.setSwitchName(previousServingSwitchName);
    	}
	}

	private PlexViewResponseType handleFailOverScenario(PlexViewRequestType plexViewRequest, Map<String, String> sessionMap) {
        String SECONDARY_ENDPOINT = "SecondaryEndPoint";

        if (SECONDARY_ENDPOINT.equalsIgnoreCase(sessionMap.get(SERVICE_ENDPOINT))) {
            sessionMap.put(SERVICE_ENDPOINT, PRIMARY_ENDPOINT);
            this.runtimeStub = this.primaryOmcpService;
            log.info("Endpoint Switch to Primary");
        } else if (PRIMARY_ENDPOINT.equalsIgnoreCase(sessionMap.get(SERVICE_ENDPOINT))) {
            this.runtimeStub = this.secondaryOmcpService;
            sessionMap.put(SERVICE_ENDPOINT, SECONDARY_ENDPOINT);
            log.info("Endpoint Switch to Secondary");
        } else {
            sessionMap.put(SERVICE_ENDPOINT, PRIMARY_ENDPOINT);
            this.runtimeStub = this.primaryOmcpService;
            log.info("Endpoint Switch to Primary");
        }

        return executeNetworkCall(plexViewRequest, runtimeStub, false, sessionMap);
    }

}
