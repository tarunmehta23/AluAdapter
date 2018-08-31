package com.charter.provisioning.voice.commercial.alu.handler;

import com.alu.plexwebapi.api.PlexViewRequestType;
import com.alu.plexwebapi.api.NgfsSubpartyV2;
import com.alu.plexwebapi.api.PlexViewResponseType;
import com.charter.provisioning.voice.commercial.alu.config.ASBMessageConst;
import com.charter.provisioning.voice.commercial.alu.exceptions.NetworkException;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.helper.AlaCarteFeaturesHelper;
import com.charter.provisioning.voice.commercial.alu.helper.DigitalPhoneRequestHelper;
import com.charter.provisioning.voice.commercial.alu.helper.PersonalComManagerHelper;
import com.charter.provisioning.voice.commercial.alu.model.BusinessClassPhone;
import com.charter.provisioning.voice.commercial.alu.model.CreateDigitalPhoneResponse;
import com.charter.provisioning.voice.commercial.alu.model.response.GetDigitalPhoneResponse;
import com.charter.provisioning.voice.commercial.alu.rollback.async.TransactionRollBackImpl;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class DigitalPhoneHandler {

    @Value("${OMCPSwitchConfiguration.ImsDomain}")
    private String imsDomain;

    @Value("${SubscriberId.MaxLength}")
    private String subscriberIdMaxLength;

    private final DigitalPhoneRequestHelper digitalPhoneRequestHelper;

    private final NetworkHandler networkService;

    private final PersonalComManagerHelper personalComManagerHelper;

    private final AlaCarteFeaturesHelper alaCarteFeaturesHelper;

    private final TransactionRollBackImpl transactionRollBackImpl;

    private final ObjectMapper objectMapper;

    @Autowired
    public DigitalPhoneHandler(DigitalPhoneRequestHelper digitalPhoneRequestHelper, NetworkHandler networkService, PersonalComManagerHelper personalComManagerHelper,
                               AlaCarteFeaturesHelper alaCarteFeaturesHelper, TransactionRollBackImpl transactionRollBackImpl, ObjectMapper objectMapper) {
        this.digitalPhoneRequestHelper = digitalPhoneRequestHelper;
        this.networkService = networkService;
        this.personalComManagerHelper = personalComManagerHelper;
        this.alaCarteFeaturesHelper = alaCarteFeaturesHelper;
        this.transactionRollBackImpl = transactionRollBackImpl;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves IMS ALU OMCP Commercial Subscriber.
     *
     * @param subscriberId  id associated with Digital Phone subscriber.
     * @param correlationId uuid used to trace a transaction through all systems end to end.
     * @return GetDigitalPhoneResponse
     *
     */
    public GetDigitalPhoneResponse retrieveDigitalPhoneSubscriber(String subscriberId, String correlationId) {
        PlexViewResponseType plexViewResponseType;
        boolean ctsSubscriberExists;

        if (subscriberId.length() > Integer.valueOf(subscriberIdMaxLength) || !StringUtils.isAlphanumeric(subscriberId)) {
            log.error("[{}] Subscriber ID be must alphanumeric and between 1 and {} characters, subscriber id: {}.", correlationId, subscriberIdMaxLength, subscriberId);
            throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, String.format("Subscriber ID must be alphanumeric and between 1 and %s characters.", subscriberIdMaxLength));
        }

        PlexViewRequestType ctsQueryResponse = digitalPhoneRequestHelper.buildQueryCTSSubscriberRequest(subscriberId, correlationId);

        try {
            plexViewResponseType = networkService.provisionNetwork(ctsQueryResponse);
            ctsSubscriberExists = digitalPhoneRequestHelper.verifyCTSSubscriber(plexViewResponseType);
        } catch (NetworkException | ProvisioningServiceException e) {
            if (e.getMessage().contains("not found on any FSDB")) {
                log.error("Digital Phone not found.");
                throw new ProvisioningServiceException(HttpStatus.NOT_FOUND, "Digital Phone not found.");
            }

            log.error("[{}] {}", correlationId, e.getMessage());
            throw new ProvisioningServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception while retrieving Digital Phone.");
        }

        if (!ctsSubscriberExists) {
            log.error("[{}] DP Subscriber does not exists in ALU.", correlationId);
            throw new ProvisioningServiceException(HttpStatus.NOT_FOUND, "Digital Phone Subscriber does not exists in ALU.");
        }

        GetDigitalPhoneResponse getDigitalPhoneResponse = buildDigitalPhoneResponse(plexViewResponseType, correlationId);

        if (isSubscriberAutoAttendantWebPortalEnabled(plexViewResponseType)) {
            log.info("[{}] Subscriber AutoAttendant and WebPortal enabled ... querying PCM.", correlationId);
        }

        return getDigitalPhoneResponse;
    }

    /**
     * Deletes an IMS Commercial Subscriber in CTS.
     *
     * @param phoneNumber   the phone number used to query and delete Subscriber in CTS.
     * @param correlationId the correlation id used to track a transaction through each system.
     */
    public void deleteDigitalPhoneSubscriber(String phoneNumber, String correlationId) {
        try {
            NgfsSubpartyV2 ctsSubscriber = querySubscriberByPrimaryPUID(correlationId, phoneNumber);
            String partyId = ctsSubscriber.getPartyId();

            String fsdbName = (ctsSubscriber.getAlternateFsdbFqdn());
            PlexViewRequestType deleteCtsRequest = digitalPhoneRequestHelper.buildDeleteCTSSubscriberRequest(correlationId, partyId, ctsSubscriber.getSwitchNumber(), fsdbName);
            networkService.provisionNetwork(deleteCtsRequest);
            // we are good if we reach here
        } catch (ProvisioningServiceException e) {
            log.debug("[{}] Error while deleting CTS TN", correlationId, e);
            // ignore and continue
        }
    }

    /**
     * Creates an IMS Commercial Subscriber in CTS.
     *
     * @param businessClassPhone the business class phone attributes that will be
     *                           used to create the digital phone.
     * @param transactionId      the correlation id used to track a transaction through each system.
     * @return CreateDigitalPhoneResponse.
     */
    public CreateDigitalPhoneResponse createDigitalPhone(BusinessClassPhone businessClassPhone, String transactionId) {
        alaCarteFeaturesHelper.validateAlaCarteFeatures(businessClassPhone);
        networkService.getNetworkServiceHelper().assignSwitch(businessClassPhone);

        try {
            deleteDigitalPhoneSubscriber(businessClassPhone.getPhoneNumber(), transactionId);

            if (isDisconnectReferralRequest(businessClassPhone)) {
                return createDREFManagedLine(businessClassPhone, transactionId);
            } else {
                return createManagedLine(businessClassPhone, transactionId);
            }
        } catch (NetworkException e) {
            log.error("[{}] NetworkException creating Digital Phone: {}", transactionId, e.getMessage());
            throw new ProvisioningServiceException(e);
        }
    }

    private GetDigitalPhoneResponse buildDigitalPhoneResponse(PlexViewResponseType ctsQueryResponse, String correlationId) {
        JSONObject xmlFragment;
        GetDigitalPhoneResponse getDigitalPhoneResponse;
        String digitalPhoneContent;
        String rootNodeName = "xml-fragment";

        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        try {
            JSONObject xmlJSONObj = XML.toJSONObject(ctsQueryResponse.toString());
            xmlFragment = (JSONObject) xmlJSONObj.get(rootNodeName);
            digitalPhoneContent = xmlFragment.toString();
            digitalPhoneContent = digitalPhoneContent.replaceAll(rootNodeName, GetDigitalPhoneResponse.class.getName());
            getDigitalPhoneResponse = objectMapper.readValue(digitalPhoneContent, GetDigitalPhoneResponse.class);
        } catch (IOException ex) {
            log.error("[{}] Exception building response while retrieving Digital Phone: {}", correlationId, ex.getMessage());
            throw new ProvisioningServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception building response while retrieving Digital Phone.");
        }

        return getDigitalPhoneResponse;
    }

    private boolean isSubscriberAutoAttendantWebPortalEnabled(PlexViewResponseType ctsQueryResponse) {
        return ctsQueryResponse.getAutoAttendant() != null && ctsQueryResponse.getAutoAttendant().getAssigned() &&
                ctsQueryResponse.getWebPortal().getAssigned();
    }

    private CreateDigitalPhoneResponse createManagedLine(BusinessClassPhone bcp, String requestId) {
        boolean pcmUserRequired = false;
        String pcmSwitchName = null;

        if (isRequestFBPFeatureExists(bcp, requestId)) {
            pcmUserRequired = true;
        }

        PlexViewRequestType createCtsRequest = createCTSSubscriber(bcp, pcmUserRequired, requestId);

        if (pcmUserRequired) {
            log.info("[{}] creating PCM user for phone number {}:", requestId, bcp.getPhoneNumber());
            PlexViewRequestType createPCMPlexViewRequest = createPCMSubscriber(bcp, requestId);
            pcmSwitchName = createPCMPlexViewRequest.getSwitchName();
        }

        return CreateDigitalPhoneResponse
                .builder()
                .subscriberId(bcp.getSubscriberId())
                .ctsSwitchName(createCtsRequest.getSwitchName())
                .pcmSwitchName(pcmSwitchName)
                .build();
    }

    private PlexViewRequestType createPCMSubscriber(BusinessClassPhone bcp, String requestId) {
        PlexViewRequestType createPCMPlexViewRequest = personalComManagerHelper.buildCreatePCMSubscriberRequest(bcp, requestId);

        try {
            networkService.provisionNetwork(createPCMPlexViewRequest);
            return createPCMPlexViewRequest;
        } catch (ProvisioningServiceException e) {
            transactionRollBackImpl.rollbackTransactions();
            log.error("Exception creating PCM Subscriber {}", e);
            throw new ProvisioningServiceException("Exception creating PCM Subscriber", e);
        }
    }

    private boolean isRequestFBPFeatureExists(BusinessClassPhone businessClassPhone, String requestId) {
        boolean isPCMRequried = false;

        if (StringUtils.isNotEmpty(businessClassPhone.getBasicCallLog()) || businessClassPhone.getPersonalAttendantFeature() != null
                || StringUtils.isNotEmpty(businessClassPhone.getProfile())) {
            isPCMRequried = true;
            log.info("[{}] Request has one of the CallLog/PAS/Profiles in subscribed state [{}][{}]", requestId, businessClassPhone.getPhoneNumber(), true);
        } else {
            log.info("[{}] Request has none of CallLog/PAS/Profiles in subscribed state [{}][{}]", requestId, businessClassPhone.getPhoneNumber(), true);
        }

        log.info("[{}] Subscriber is PCM Eligible as Per Request [{}][{}]", requestId, businessClassPhone.getPhoneNumber(), isPCMRequried);
        return isPCMRequried;
    }

    private PlexViewRequestType createCTSSubscriber(BusinessClassPhone businessClassPhone, boolean isTransactional, String requestId) {
        PlexViewRequestType createCtsRequest = digitalPhoneRequestHelper.buildCreateCTSSubscriberRequest(businessClassPhone, requestId);
        networkService.provisionNetwork(createCtsRequest);

        if (isTransactional) {
            PlexViewRequestType deleteCtsRequest = digitalPhoneRequestHelper.buildDeleteCTSSubscriberRequest(requestId, businessClassPhone.getSubscriberId(), businessClassPhone.getSwitchName(), null);
            transactionRollBackImpl.getRequestList().add(deleteCtsRequest);
        }

        return createCtsRequest;
    }

    private NgfsSubpartyV2 querySubscriberByPrimaryPUID(String requestId, String TN) {
        String primaryPUID;
        final String PLUS1 = "+1";
        NgfsSubpartyV2 ngfsSubpartyV2;

        if (!TN.startsWith(ASBMessageConst.MLHG_CONTROLLER_PREFIX)) {
            primaryPUID = PLUS1 + TN;
        } else {
            primaryPUID = TN;
        }

        if (imsDomain != null) {
            primaryPUID = String.format("%s@%s", primaryPUID, imsDomain);
            log.info("[{}] PrimaryPUID with domain included {}", requestId, primaryPUID);
        }

        log.info("[{}] PrimaryPUID used in search {}", requestId, primaryPUID);

        PlexViewRequestType ctsSubscriberRequest = digitalPhoneRequestHelper.buildQueryCTSSubPartyRequest(requestId, "PrimaryPUID", primaryPUID);

        PlexViewResponseType response = networkService.provisionNetwork(ctsSubscriberRequest);

        ngfsSubpartyV2 = response.getNgfsSubpartyV2();

        if (ngfsSubpartyV2 != null) {
            log.debug("[{}] PrimaryPUID: {} found on Switch :{}", requestId, primaryPUID, ngfsSubpartyV2.getSwitchNumber());
            return ngfsSubpartyV2;
        } else {
            log.warn("[{}] PrimaryPUID: {} NOT found on Switch ", requestId, primaryPUID);
            throw new ProvisioningServiceException();
        }
    }

    private CreateDigitalPhoneResponse createDREFManagedLine(BusinessClassPhone businessClassPhone, String requestId) {
        PlexViewRequestType request = digitalPhoneRequestHelper.buildCreateCTSSubscriberWithDRefRequest(businessClassPhone, requestId);
        networkService.provisionNetwork(request);

        return CreateDigitalPhoneResponse
                .builder()
                .subscriberId(businessClassPhone.getSubscriberId())
                .ctsSwitchName(request.getSwitchName())
                .build();
    }

    private boolean isDisconnectReferralRequest(BusinessClassPhone businessClassPhone) {
        return businessClassPhone.getDisconnectReferral() != null;
    }

}
