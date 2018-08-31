package com.charter.provisioning.voice.commercial.alu.helper;

import com.alu.plexwebapi.api.NgfsCallbarringV2;
import com.alu.plexwebapi.api.NgfsCallblockingV2;
import com.alu.plexwebapi.api.NgfsCallinglineidV2;
import com.alu.plexwebapi.api.NgfsCallinglineidV2.CallingLineIdRestriction;
import com.alu.plexwebapi.api.NgfsCallinglineidV2.OrigLineIdRestrictionLevel;
import com.alu.plexwebapi.api.NgfsCarrierselectionV2;
import com.alu.plexwebapi.api.NgfsDialingplanV2;
import com.alu.plexwebapi.api.NgfsFilterV2;
import com.alu.plexwebapi.api.NgfsInterceptreferralV2;
import com.alu.plexwebapi.api.NgfsPinserviceV2;
import com.alu.plexwebapi.api.NgfsSearchOrV2;
import com.alu.plexwebapi.api.NgfsSearchV2;
import com.alu.plexwebapi.api.NgfsSettzpathV2;
import com.alu.plexwebapi.api.NgfsSubpartyV2;
import com.alu.plexwebapi.api.PlexViewRequestType;
import com.alu.plexwebapi.api.PlexViewRequestType.Factory;
import com.alu.plexwebapi.api.PlexViewResponseType;
import com.charter.provisioning.voice.commercial.alu.config.ASBMessageConst;
import com.charter.provisioning.voice.commercial.alu.config.CTSSwitchConfiguration;
import com.charter.provisioning.voice.commercial.alu.config.OMCPCommands;
import com.charter.provisioning.voice.commercial.alu.config.ServiceConfig;
import com.charter.provisioning.voice.commercial.alu.enums.BlockingCodes;
import com.charter.provisioning.voice.commercial.alu.enums.PlexViewCatalog;
import com.charter.provisioning.voice.commercial.alu.enums.PlexViewResponseStatusCode;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.model.BusinessClassPhone;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class DigitalPhoneRequestHelper {

    private final ServiceConfig ctsServiceConfig;

    private final AlaCarteFeaturesHelper alaCarteFeaturesHelper;

    private final CTSSwitchConfiguration ctsSwitchConfig;

    private final String AUTO_SELECT_POSTFIX = "[AutoSelect]";

    @Autowired
    public DigitalPhoneRequestHelper(ServiceConfig ctsServiceConfig, AlaCarteFeaturesHelper alaCarteFeaturesHelper, CTSSwitchConfiguration ctsSwitchConfig) {
        this.ctsServiceConfig = ctsServiceConfig;
        this.alaCarteFeaturesHelper = alaCarteFeaturesHelper;
        this.ctsSwitchConfig = ctsSwitchConfig;
    }

    /**
     * Creates the request object to query the CTS subscriber by subscriber id.
     *
     * @param subscriberId the unique id of the subscriber.
     * @param transactionId the correlation id used to track a transaction through each system.
     * @return PlexViewRequestType - Request to query CTS subscriber.
     *
     */
    public PlexViewRequestType buildQueryCTSSubscriberRequest(String subscriberId, String transactionId) {
        PlexViewRequestType plexViewRequestTemplate = getPlexViewRequestTemplate(OMCPCommands.RTRV_NGFS_SUBSCRIBER_V2, null, "", transactionId);
        plexViewRequestTemplate.setAid(subscriberId);

        return plexViewRequestTemplate;
    }

    /**
     * Verifies that the response returned from CTS is valid.
     *
     * @param queryResponse response from CTS.
     * @return boolean - valid response.
     *
     */
    public boolean verifyCTSSubscriber(PlexViewResponseType queryResponse) {
        if(PlexViewResponseStatusCode.SUCCESS.statusCode().equals(queryResponse.getStatus().toString())){
            return true;
        }
        else if(PlexViewResponseStatusCode.OBJECT_NOT_EXISTS.statusCode().equals(queryResponse.getFailureCode().toString())){
            return false;
        }
        else{
            log.error("CTS Query failed");
            throw new ProvisioningServiceException(HttpStatus.NOT_FOUND, "CTS Query failed");
        }
    }

    /**
     * Creates the PlexViewRequest with DREF which will be used to creates an IMS Commercial Subscriber in CTS and PCM.
     *
     * @param businessClassPhone the business class phone attributes that will be used to create the digital phone.
     * @param transactionId the correlation id used to track a transaction through each system.
     * @return PlexViewRequestType - Request to query CTS subscriber with DREF.
     *
     */
    public PlexViewRequestType buildCreateCTSSubscriberWithDRefRequest(final BusinessClassPhone businessClassPhone, String transactionId) {
        log.debug("building CreateCTSSubscriberWithDRefRequest for transactionId:{}", transactionId);

        PlexViewRequestType plxViewTmplt = getCreatePlexViewRequestTemplate(businessClassPhone, transactionId);

        populateDRefSubParty(businessClassPhone, plxViewTmplt);
        populateInterceptReferral(businessClassPhone, plxViewTmplt, transactionId);

        return plxViewTmplt;
    }

    /**
     * Builds Query CTS Subscriber /search by telephoneNumber
     *
     * @param transactionId - correlation id used for auditing.
     * @param searchBy - field to query the CTS switch.
     * @param telephoneNumber - value to query the CTS switch.
     *
     */
    public PlexViewRequestType buildQueryCTSSubPartyRequest(String transactionId, String searchBy, String telephoneNumber) {
        String PUBLIC_UID = "publicUID";

        PlexViewRequestType plexViewRequestTemplate = getPlexViewRequestTemplate(OMCPCommands.RTRV_NGFS_SUBPARTY_V2,
                null, null, transactionId);

        NgfsFilterV2 filterObj = com.alu.plexwebapi.api.NgfsFilterV2.Factory.newInstance();

        if (OMCPCommands.PUBLICUIDx.equalsIgnoreCase(searchBy)) {
            NgfsSearchOrV2 searchMulti = com.alu.plexwebapi.api.NgfsSearchOrV2.Factory.newInstance();

            for (int i = 1; i <= 9; i++) {
                searchMulti.setSearchTypeArray(i,createNgfsSearchV2(PUBLIC_UID, telephoneNumber, i));
            }

            filterObj.setSearchOr(searchMulti);
        } else {
            filterObj.setSearch(createNgfsSearchV2(telephoneNumber, searchBy));
        }

        plexViewRequestTemplate.setFilter(filterObj);

        return plexViewRequestTemplate;
    }

    /**
     * Builds Delete CTS Subscriber Request for given PartyId.
     *
     * @param transactionId - correlation id used for auditing.
     * @param partyId - unique identifier to query the CTS switch for deletion.
     * @param switchName - name of the switch.
     * @param fsdbName - FSDB name.
     * @return  PlexViewRequestType - CTS response for deleting subscriber.
     *
     */
    public PlexViewRequestType buildDeleteCTSSubscriberRequest(String transactionId, String partyId, String switchName, String fsdbName) {
        PlexViewRequestType plxViewTmplt = getPlexViewRequestTemplate(OMCPCommands.DLT_NGFS_SUBSCRIBER_V2, switchName,
                fsdbName, transactionId);

        NgfsSubpartyV2 subParty = com.alu.plexwebapi.api.NgfsSubpartyV2.Factory.newInstance();
        subParty.setPartyId(partyId);
        plxViewTmplt.setSubParty(subParty);

        return plxViewTmplt;
    }

    /**
     * Builds Query CTS Subscriber /search by telephoneNumber.
     *
     * @param businessClassPhone - business class phone request from the client.
     * @param transactionId - correlation id used for auditing.
     *
     */
    public PlexViewRequestType buildCreateCTSSubscriberRequest(BusinessClassPhone businessClassPhone, String transactionId) {
        log.info("[{}] buildCreateCTSSubscriberRequest().", transactionId);

        PlexViewRequestType plxViewTmplt = getCreatePlexViewRequestTemplate(businessClassPhone, transactionId);

        // Basic BCP Service populate with 5 objects
        // 1.SubParty 2.CallingLineId 3. DialingPlan 4. CarrierSelection 5. WebPortal
        // (Default)
        populateSubParty(businessClassPhone, plxViewTmplt);
        populateCallingLineId(businessClassPhone, plxViewTmplt);
        populateDialingPlan(businessClassPhone, plxViewTmplt);
        populateCarrierSelection(businessClassPhone, plxViewTmplt);
        populatePinService(businessClassPhone, plxViewTmplt);
        populateBlockingCodes(businessClassPhone, plxViewTmplt);

        alaCarteFeaturesHelper.populateAlaCarteFeatures(businessClassPhone, plxViewTmplt, transactionId);

        populateTZPath(businessClassPhone, plxViewTmplt);

		/*
		 * if (ASBMessageHelper.isAccountCodeRequest(businessClassPhone)) {
		 * populateAccountCodes(businessClassPhone, plxViewTmplt); }
		 */

        // Valid only on BC01 subscriber for voicemail
        plxViewTmplt.getSubParty().setPublicUID1(businessClassPhone.getPhoneNumber());

        return plxViewTmplt;
    }

    private NgfsSearchV2 createNgfsSearchV2(String publicId, String telephoneNumber, int i ){
        NgfsSearchV2 searchCondition = com.alu.plexwebapi.api.NgfsSearchV2.Factory.newInstance();
        searchCondition.setCaseSensitive(Boolean.toString(false));
        searchCondition.setSearchstring(telephoneNumber);
        searchCondition.setOperation("Contains");
        searchCondition.setSearchby(String.format("%s%d", publicId , i));

        return searchCondition;
    }

    private NgfsSearchV2 createNgfsSearchV2(String telephoneNumber, String searchBy){
        NgfsSearchV2 searchObj = com.alu.plexwebapi.api.NgfsSearchV2.Factory.newInstance();
        searchObj.setCaseSensitive(Boolean.toString(false));
        searchObj.setSearchstring(telephoneNumber);
        searchObj.setOperation("Equals");
        searchObj.setSearchby(searchBy);

        return searchObj;
    }

    private PlexViewRequestType getCreatePlexViewRequestTemplate(BusinessClassPhone businessClassPhone, String requestId) {
        String switchName = businessClassPhone.getSwitchName();

        // Get this value from yml file
        String fsdbName =  ctsSwitchConfig.getCtsConfigurations().get(0).getFsdbName();

        return getPlexViewRequestTemplate(OMCPCommands.ENT_NGFS_SUBSCRIBER_V2, switchName, fsdbName, requestId);
    }

    private PlexViewRequestType getPlexViewRequestTemplate(String command, String ctsSwitchName, String fsdbName,
                                                           String requestId) {
        PlexViewRequestType plxViewTmplt = Factory.newInstance();

        plxViewTmplt.setCommand(command);

        if (fsdbName != null && StringUtils.isNotEmpty(fsdbName)) {
            plxViewTmplt.setFsdb(fsdbName);
        }

        if (ctsSwitchName != null && StringUtils.isNotEmpty(ctsSwitchName)) {
            plxViewTmplt.setSwitchName(ctsSwitchName);
        }

        plxViewTmplt.setRequestId(requestId);
        plxViewTmplt.setMaxRows(new BigInteger("-1"));

        return plxViewTmplt;
    }

    private void populateDRefSubParty(BusinessClassPhone businessClassPhone, PlexViewRequestType plxViewTmplt) {
        NgfsSubpartyV2 subParty = NgfsSubpartyV2.Factory.newInstance();

        // Populate Defaults
        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_PHONE_NUMBER,
                PlexViewCatalog.SubParty.name(), subParty);

        String tnWithPlus1Prefix = getTNWithPlus1Prefix(businessClassPhone);
        subParty.setPrimaryPUID(tnWithPlus1Prefix);

        // Populate PartyId - Party Id should be populated with "[AutoSelect]" appended
        // at the end
        subParty.setPartyId(businessClassPhone.getSubscriberId() + AUTO_SELECT_POSTFIX);

        // populate DisplayName
        String displayName = businessClassPhone.getDisplayName();

        if (StringUtils.isNotEmpty(displayName)) {
            subParty.setDisplayName(displayName);
        }

        plxViewTmplt.setSubParty(subParty);
    }

    private void populateInterceptReferral(BusinessClassPhone businessClassPhone, PlexViewRequestType plxViewTmplt, String requestId) {
        log.info("populating intercept referral for requestId: {}", requestId);

        createInterceptReferral(businessClassPhone, plxViewTmplt);
    }

    private void createInterceptReferral(BusinessClassPhone businessClassPhone, PlexViewRequestType plexViewRequestTemplate) {
        String tnWithPlus1Prefix = getTNWithPlus1Prefix(businessClassPhone);

        NgfsInterceptreferralV2 interceptReferral = NgfsInterceptreferralV2.Factory.newInstance();

        interceptReferral.setPublicUID(tnWithPlus1Prefix);

        interceptReferral.setAssigned(true);

        String newDN= businessClassPhone.getDisconnectReferral().getNewDN();

        if(StringUtils.isNotEmpty(newDN)) {
            interceptReferral.setNewDN(newDN);
        }

        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE,
                ASBMessageConst.ASB_ACTION_DISCNT_REFERRAL, PlexViewCatalog.InterceptReferral.name(), interceptReferral);

        plexViewRequestTemplate.setInterceptReferral(interceptReferral);
    }

    private String getTNWithPlus1Prefix(BusinessClassPhone businessClassPhone) {
        String PLUS1 = "+1";
        return String.format("%s%s", PLUS1, businessClassPhone.getPhoneNumber());
    }

    private void populateTZPath(BusinessClassPhone businessClassPhone, PlexViewRequestType plxViewTmplt) {
        final String DEFAULT_TIMEZONE = "America/New_York";
        NgfsSettzpathV2 tzPath = NgfsSettzpathV2.Factory.newInstance();
        // Populate Defaults
        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_ID_TIMEZONE,
                PlexViewCatalog.SetTZPath.name(), tzPath);

        tzPath.setPublicUID(getTNWithPlus1Prefix(businessClassPhone));
        tzPath.setTZPath(businessClassPhone.getTimeZone() == null ? DEFAULT_TIMEZONE
                : (StringUtils.isEmpty(getTimeZoneMap().get(businessClassPhone.getTimeZone())) ? DEFAULT_TIMEZONE
                : getTimeZoneMap().get(businessClassPhone.getTimeZone())));
        tzPath.setPerPuid(false);
        tzPath.setAssigned(true);
        plxViewTmplt.setSetTZPath(tzPath);

    }
    private void populateBlockingCodes(BusinessClassPhone businessClassPhone, PlexViewRequestType plxViewTmplt) {
        Set<String> codes = new HashSet<>();

        if(businessClassPhone.getBlockingCodes()!= null ) {
            codes.addAll(businessClassPhone.getBlockingCodes());
        }

        if (codes.size() > 0 ) {
            plxViewTmplt.setCallBarring(createNgfsCallBarringV2(codes, businessClassPhone));
            // IBR map to CallBlocking

            Boolean inBoundBlocking = codes.contains(BlockingCodes.CODE_IBR
                    .getCode());

            if (inBoundBlocking) {
                plxViewTmplt.setCallBlocking(createNgfsCallBlockingV2(businessClassPhone));
            }
        }
    }

    private NgfsCallbarringV2 createNgfsCallBarringV2(Set<String> codes, BusinessClassPhone businessClassPhone){
        NgfsCallbarringV2 callBarring = NgfsCallbarringV2.Factory.newInstance();

        // ACC is taken out completely 02/16/2011
        Boolean isPremiumService = codes.contains(BlockingCodes.CODE_900.getCode())
                || codes.contains(BlockingCodes.CODE_976.getCode());
        Boolean international = codes.contains(BlockingCodes.CODE_INTL.getCode());
        Boolean local = codes.contains(BlockingCodes.CODE_ZERO_PLUS
                .getCode())
                || codes.contains(BlockingCodes.CODE_NDA.getCode());
        Boolean dirAsstNxxCalls = codes.contains(BlockingCodes.CODE_DA
                .getCode())
                || codes.contains(BlockingCodes.CODE_411.getCode());
        Boolean fgbDldCrr = codes.contains(BlockingCodes.CODE_CASUAL
                .getCode());

        callBarring.setPublicUID(getTNWithPlus1Prefix(businessClassPhone));
        callBarring.setAssigned(true);
        callBarring.setPremiumServ(isPremiumService);
        callBarring.setInternational(international);
        callBarring.setLocal(local);
        callBarring.setDirectoryAssist(dirAsstNxxCalls);
        callBarring.setFGBDialedCarrier(fgbDldCrr);
        callBarring.setFGDDialedCarrier(fgbDldCrr);
        callBarring.setCallBarringAll(false);
        callBarring.setInterLataToll(false);
        callBarring.setIntraLataToll(false);
        callBarring.setPerPuid(false);

        return callBarring;
    }

    private NgfsCallblockingV2 createNgfsCallBlockingV2(BusinessClassPhone businessClassPhone){
        NgfsCallblockingV2 callBlocking = NgfsCallblockingV2.Factory.newInstance();

        callBlocking.setPublicUID(getTNWithPlus1Prefix(businessClassPhone));
        callBlocking.setAssigned(true);
        callBlocking.setAll(true);
        callBlocking.setUserCtrlAll(false);
        callBlocking.setPerPuid(false);

        return callBlocking;
    }

    private void populatePinService(BusinessClassPhone businessClassPhone, PlexViewRequestType plxViewTmplt) {
        String pin = businessClassPhone.getPhoneNumber().substring(6);

        NgfsPinserviceV2 pinService = NgfsPinserviceV2.Factory.newInstance();
        pinService.setPublicUID(getTNWithPlus1Prefix(businessClassPhone));
        pinService.setAssigned(true);
        pinService.setPin(pin);
        plxViewTmplt.setPinService(pinService);
    }

    private void populateCarrierSelection(BusinessClassPhone businessClassPhone, PlexViewRequestType plxViewTmplt) {
        final String PIC_CD_PFIX = "1";

        NgfsCarrierselectionV2 carrierSelection = NgfsCarrierselectionV2.Factory.newInstance();

        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_PIC,
                PlexViewCatalog.CarrierSelection.name(), carrierSelection);
        carrierSelection.setIntraLataTollCarrier(new Integer(PIC_CD_PFIX + businessClassPhone.getLpic()));
        carrierSelection.setPresubscribedCarrier3(new Integer(PIC_CD_PFIX + businessClassPhone.getIpic()));
        carrierSelection.setLongDistanceCarrier(new Integer(PIC_CD_PFIX + businessClassPhone.getLpic()));
        carrierSelection.setAssigned(true);
        String tnWithPlus1Prefix = getTNWithPlus1Prefix(businessClassPhone);
        carrierSelection.setPublicUID(tnWithPlus1Prefix);
        plxViewTmplt.setCarrierSelection(carrierSelection);
    }

    private void populateDialingPlan(BusinessClassPhone businessClassPhone, PlexViewRequestType plxViewTmplt) {
        NgfsDialingplanV2 dialingPlan = NgfsDialingplanV2.Factory.newInstance();

        // Populate Defaults
        if (businessClassPhone.getBlockingCodes() != null && businessClassPhone.getBlockingCodes().contains("OBR")) {

            ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_BLOCKING_CODES,
                    PlexViewCatalog.DialingPlan.name(), dialingPlan);
        } else {
            ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_DIAL_PLAN_ID,
                    PlexViewCatalog.DialingPlan.name(), dialingPlan);
        }

        dialingPlan.setPrefixandFeatureCode(new Integer(businessClassPhone.getDialPlanId()));
        dialingPlan.setPublicUID(getTNWithPlus1Prefix(businessClassPhone));
        dialingPlan.setAssigned(true);

        // Populate DialPlanId
        plxViewTmplt.setDialingPlan(dialingPlan);
    }

    private void populateSubParty(BusinessClassPhone businessClassPhone, PlexViewRequestType plxViewTmplt) {
        NgfsSubpartyV2 subParty =  com.alu.plexwebapi.api.NgfsSubpartyV2.Factory.newInstance();

        // Populate Defaults
        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_PHONE_NUMBER,
                PlexViewCatalog.SubParty.name(), subParty);

        String tnWithPlus1Prefix = getTNWithPlus1Prefix(businessClassPhone);
        String pin = businessClassPhone.getPhoneNumber().substring(6);

        subParty.setPrimaryPUID(tnWithPlus1Prefix);

        // Todo Set PIN -
		/*
		 * boolean comInterface = isComInterfaceEnabled(businessClassPhone.getUuid());
		 * if(!comInterface){ subParty.setPin(pin); }
		 */

        subParty.setPin(pin);
        // Populate PartyId - Party Id should be populated with "[AutoSelect]" appended
        // at the end
        subParty.setPartyId(businessClassPhone.getSubscriberId() + AUTO_SELECT_POSTFIX);


        // populate DisplayName
        String dName = businessClassPhone.getDisplayName();

        if (StringUtils.isNotEmpty(dName)) {
            subParty.setDisplayName(dName);
        }

        plxViewTmplt.setSubParty(subParty);
    }

    /**
     * For create Requests only - First Time Create Privacy settings
     */
    private void populateCallingLineId(BusinessClassPhone businessClassPhone, PlexViewRequestType plxViewTmplt) {
        final String PRIVACY = "PPS";
        final String PPS_STATUS_FULL = "Full";
        final String PPS_ID_VALUE_STATUS_NONE = "None";

        NgfsCallinglineidV2 callingLineId = NgfsCallinglineidV2.Factory.newInstance();

        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, PRIVACY,
                PlexViewCatalog.CallingLineId.name(), callingLineId);

        String privacy = businessClassPhone.getPps();

        if (PPS_STATUS_FULL.equalsIgnoreCase(privacy)) {
            callingLineId.setCallingLineIdRestriction(CallingLineIdRestriction.PRIVATE);
            callingLineId.setOrigLineIdRestrictionLevel(OrigLineIdRestrictionLevel.ASSERTED);
        } else if (PPS_ID_VALUE_STATUS_NONE.equalsIgnoreCase(privacy)) {
            callingLineId.setCallingLineIdRestriction(CallingLineIdRestriction.PUBLIC);
            callingLineId.setOrigLineIdRestrictionLevel(OrigLineIdRestrictionLevel.NONE);

        }

        callingLineId.setAssigned(true);
        String tnWithPlus1Prefix = getTNWithPlus1Prefix(businessClassPhone);
        callingLineId.setPublicUID(tnWithPlus1Prefix);
        plxViewTmplt.setCallingLineId(callingLineId);
    }

    private Map<String, String> getTimeZoneMap() {
        return new HashMap<String, String>() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {
                put("US/Alaska", "America/Anchorage");
                put("US/Arizona", "America/Phoenix");
                put("US/Central", "America/Chicago");
                put("US/East-Indiana", "America/Indianapolis");
                put("US/Eastern", "America/New_York");
                put("US/Hawaii", "US/Hawaii");
                put("US/Aleutian", "US/Hawaii");
                put("US/Indiana-Starke", "US/Indiana-Starke");
                put("US/Michigan", "America/Detroit");
                put("US/Mountain", "America/Denver");
                put("US/Pacific", "America/Los_Angeles");
                put("US/Pacific-New", "America/Los_Angeles");
                put("US/Samoa", "US/Samoa");
            }
        };

    }
}
