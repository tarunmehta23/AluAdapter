package com.charter.provisioning.voice.commercial.alu.helper;

import com.alu.plexwebapi.api.NgfsAutoattendantV2;
import com.alu.plexwebapi.api.NgfsCallbarringV2;
import com.alu.plexwebapi.api.NgfsCallblockingV2;
import com.alu.plexwebapi.api.NgfsCallforwardingbusyV2;
import com.alu.plexwebapi.api.NgfsCallforwardingnoansV2;
import com.alu.plexwebapi.api.NgfsCallforwardingvariV2;
import com.alu.plexwebapi.api.NgfsCalltransferV2;
import com.alu.plexwebapi.api.NgfsOnedigitspeeddialV2;
import com.alu.plexwebapi.api.NgfsPinserviceV2;
import com.alu.plexwebapi.api.NgfsRemoteuserV2;
import com.alu.plexwebapi.api.NgfsSeqringingV2;
import com.alu.plexwebapi.api.NgfsSimultaneousringingV2;
import com.alu.plexwebapi.api.NgfsWebportalV2;
import com.alu.plexwebapi.api.PlexViewRequestType;
import com.charter.provisioning.voice.commercial.alu.config.ASBMessageConst;
import com.charter.provisioning.voice.commercial.alu.config.ServiceConfig;
import com.charter.provisioning.voice.commercial.alu.enums.PlexViewCatalog;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.model.BusinessClassPhone;
import com.charter.provisioning.voice.commercial.alu.model.CallForwardBusy;
import com.charter.provisioning.voice.commercial.alu.model.CallForwardNoAnswer;
import com.charter.provisioning.voice.commercial.alu.model.CallForwardUnconditional;
import com.charter.provisioning.voice.commercial.alu.model.CallTransfer;
import com.charter.provisioning.voice.commercial.alu.model.OneDigitSpeedDialing;
import com.charter.provisioning.voice.commercial.alu.model.RemoteUser;
import com.charter.provisioning.voice.commercial.alu.model.SequentialRinging;
import com.charter.provisioning.voice.commercial.alu.model.SimultaneousRing;
import com.charter.provisioning.voice.commercial.alu.util.Crypt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class AlaCarteFeaturesHelper {

    private final ServiceConfig ctsServiceConfig;

    @Value("${CTSAlaCarteConfiguration.MaxSequentialRings}")
    private int maxSequentialRings;

    @Value("${CTSAlaCarteConfiguration.MaxSimultaneousRings}")
    private int maxSimultaneousRings;

    @Value("${CTSAlaCarteConfiguration.MaxMultipleRings}")
    private int maxMultipleRings;

    @Value("$CTSAlaCarteConfiguration.ComInterfaceEnabled")
    private String comInterfaceEnabled;
    
    @Value("${CTSAlaCarteConfiguration.MaxNumberOfRings}")
    private int maxNumberOfRings;

    @Autowired
    public AlaCarteFeaturesHelper(ServiceConfig ctsServiceConfig) {
        this.ctsServiceConfig = ctsServiceConfig;
    }

    void populateAlaCarteFeatures(BusinessClassPhone bcp, PlexViewRequestType plxViewTmplt, String requestId) throws ProvisioningServiceException {
        //Sequential Ring (MRC)
        if (bcp.getSequentialRingingFeature() != null) {
            updateSeqRinging(bcp, plxViewTmplt, requestId);
        }
        //Simultaneous Ring (MRC)
        if (bcp.getSimultaneousRingFeature() != null) {
            updateSimultaneousRinging(bcp, plxViewTmplt, requestId);
        }
        //Remote Office (MRC)
        if (bcp.getRemoteUserFeature() != null) {
            updateRemoteUser(bcp, plxViewTmplt, requestId);
        }
        //Personal Answering Service (MRC)
        if (bcp.getPersonalAttendantFeature() != null && bcp.getPersonalAttendantFeature()) {
            updateCallTransferAutoAttendant(bcp, plxViewTmplt, requestId);
        }
        //DistinctiveRing Feature (MRC)
        if (bcp.getMultipleRingPatternFeature() != null) {
            updateMultipleRingPattern(bcp, plxViewTmplt, requestId);
        }
        //Suspend Line (Portal only feature)
        if (bcp.getSuspendLineFeature() != null && bcp.getSuspendLineFeature()){
            updateServiceSuspension(bcp, plxViewTmplt, requestId);
        }
        
		// Call Forward Busy (Portal only feature)
		if (bcp.getCallForwardBusy() != null) {
			updateCallForwardingBusy(bcp, plxViewTmplt, requestId);
		}

		// Call Forward Unconditional (Portal only feature)
		if (bcp.getCallForwardUnconditional() != null) {
			updateCallForwardingVari(bcp, plxViewTmplt, requestId);
		}

		// Call Transfer (Portal only feature)
		if (bcp.getCallTransfer() != null) {
			updateCallTransfer(bcp, plxViewTmplt, requestId);
		}
		
		// Call Forward No Answer (Portal only feature)
		if (bcp.getCallForwardNoAnswer() != null) {
			updateCallForwardingNoAns(bcp, plxViewTmplt, requestId);
		}
		
		// Call Forward No Answer (Portal only feature)
		if (bcp.getOneDigitSpeedDialing() != null) {
			updateOneDigitSpeedDial(bcp, plxViewTmplt, requestId);
		}
		
    }

	public void validateAlaCarteFeatures(BusinessClassPhone bcp){
        if(bcp.getSimultaneousRingFeature() != null) {
            SimultaneousRing simultaneousRing = bcp.getSimultaneousRingFeature();

            if (simultaneousRing.getIsActivated() == null) {
                log.error("SimultaneousRing: activated was not populated, requires a value of true or false.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "SimultaneousRing: activated was not populated, requires a value of true or false.");
            }

            if (simultaneousRing.getIsSubscribed() == null) {
                log.error("SimultaneousRing: subscribed was not populated, requires a value of true or false.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "SimultaneousRing: subscribed was not populated, requires a value of true or false.");
            }

            if(!simultaneousRing.getIsSubscribed()){
                log.error("SimultaneousRing: subscribed cannot be false while activated is true.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "SimultaneousRing: subscribed cannot be false, requires a value of true only.");
            }

            if(simultaneousRing.getPuidNums().size() > maxSimultaneousRings) {
                log.error("Simultaneous Ring: Only {} DNs allowed in the request, actual DNs {}", maxSimultaneousRings,
                        simultaneousRing.getPuidNums().size());
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, String.format("Simultaneous Ring: Only %d DNs allowed in the request, actual DNs %d", maxSimultaneousRings,
                        simultaneousRing.getPuidNums().size()));
            }
        }

        if(bcp.getSequentialRingingFeature() != null) {
            SequentialRinging sequentialRinging = bcp.getSequentialRingingFeature();

            if (sequentialRinging.getIsActivated() == null) {
                log.error("SequentialRing: activated was not populated, requires a value of true or false.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "SequentialRing: activated was not populated, requires a value of true or false.");
            }

            if (sequentialRinging.getIsSubscribed() == null) {
                log.error("SequentialRing: subscribed was not populated, requires a value of true or false.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "SequentialRing: subscribed was not populated, requires a value of true or false.");
            }

            if(!sequentialRinging.getIsSubscribed()){
                log.error("SequentialRing: subscribed cannot be false while activated is true.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "SequentialRing: subscribed cannot be false, requires a value of true only.");
            }

            if (sequentialRinging.getSequentialRing().size() > maxSequentialRings) {
                log.error("Sequential Ring: More than allowed DN's provided in the request, allowed {}, actual {}.",
                        maxSequentialRings, sequentialRinging.getSequentialRing().size());
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, String.format("Sequential Ring: More than allowed DN's provided in the request, allowed %d, actual %d.",
                        maxSequentialRings, sequentialRinging.getSequentialRing().size()));
            }
        }

        if(bcp.getRemoteUserFeature() != null) {
            RemoteUser remoteUser = bcp.getRemoteUserFeature();

            if (StringUtils.isEmpty(remoteUser.getPin())) {
                log.error("CTS Remote User/Office: Pin is required.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CTS Remote User/Office: Pin is required.");
            }
        }
        
        // validating call forward busy
        if(bcp.getCallForwardBusy() != null) {
        	CallForwardBusy callForwardBusy = bcp.getCallForwardBusy();

            if (callForwardBusy.getIsActivated() == null) {
                log.error("CallForwardBusy: activated was not populated, requires a value of true or false.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallForwardBusy: activated was not populated, requires a value of true or false.");
            }

            if (callForwardBusy.getIsSubscribed() == null) {
                log.error("CallForwardBusy: subscribed was not populated, requires a value of true or false.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallForwardBusy: subscribed was not populated, requires a value of true or false.");
            }

            if(!callForwardBusy.getIsSubscribed()){
                log.error("CallForwardBusy: subscribed cannot be false while activated is true.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallForwardBusy: subscribed cannot be false, requires a value of true only.");
            }
            
            if(StringUtils.isBlank(callForwardBusy.getForwardDN()) && callForwardBusy.getIsActivated()){
                log.error("CallForwardBusy: The forward property value has to be set in the request before it can be enabled.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallForwardBusy: The forward property value has to be set in the request before it can be enabled.");
            }
        }
        
        // validating call forward no answer
        if(bcp.getCallForwardNoAnswer() != null) {
        	CallForwardNoAnswer callForwardNoAnswer = bcp.getCallForwardNoAnswer();

            if (callForwardNoAnswer.getIsActivated() == null) {
                log.error("CallForwardNoAnswer: activated was not populated, requires a value of true or false.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallForwardNoAnswer: activated was not populated, requires a value of true or false.");
            }

            if (callForwardNoAnswer.getIsSubscribed() == null) {
                log.error("CallForwardNoAnswer: subscribed was not populated, requires a value of true or false.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallForwardNoAnswer: subscribed was not populated, requires a value of true or false.");
            }

            if(!callForwardNoAnswer.getIsSubscribed()){
                log.error("CallForwardNoAnswer: subscribed cannot be false while activated is true.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallForwardNoAnswer: subscribed cannot be false, requires a value of true only.");
            }
            
            if(StringUtils.isBlank(callForwardNoAnswer.getForwardDN()) && callForwardNoAnswer.getIsActivated()){
                log.error("CallForwardNoAnswer: The forward property value has to be set in the request before it can be enabled.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallForwardNoAnswer: The forward property value has to be set in the request before it can be enabled.");
            }
            
			if (StringUtils.isNotEmpty(callForwardNoAnswer.getNumRings())) {
				try {
					if (Integer.valueOf(callForwardNoAnswer.getNumRings()) > maxNumberOfRings)
                        log.error("CallForwardNoAnswer: Maximum value for Number of rings is {}.", maxNumberOfRings);
                    throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST,
								String.format("CallForwardNoAnswer: Maximum value for Number of rings is %d.", maxNumberOfRings));
				} catch (NumberFormatException e) {
                    log.error("CallForwardNoAnswer: Number of rings should be numeric.");
                    throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST,
							"CallForwardNoAnswer: Number of rings should be numeric.");
				}
			}
        }
       
        // validating call forward unconditional
        if(bcp.getCallForwardUnconditional() != null) {
        	CallForwardUnconditional callForwardUnconditional = bcp.getCallForwardUnconditional();

            if (callForwardUnconditional.getIsActivated() == null) {
                log.error("CallForwardUnconditional: activated was not populated, requires a value of true or false.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallForwardUnconditional: activated was not populated, requires a value of true or false.");
            }

            if (callForwardUnconditional.getIsSubscribed() == null) {
                log.error("CallForwardUnconditional: subscribed was not populated, requires a value of true or false.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallForwardUnconditional: subscribed was not populated, requires a value of true or false.");
            }

            if(!callForwardUnconditional.getIsSubscribed()){
                log.error("CallForwardUnconditional: subscribed cannot be false while activated is true.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallForwardUnconditional: subscribed cannot be false, requires a value of true only.");
            }
            
            if(StringUtils.isBlank(callForwardUnconditional.getForwardDN()) && callForwardUnconditional.getIsActivated()){
                log.error("CallForwardUnconditional: The forward property value has to be set in the request before it can be enabled.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallForwardUnconditional: The forward property value has to be set in the request before it can be enabled.");
            }
        }
        
        // validating call Transfer
        if(bcp.getCallTransfer() != null) {
        	CallTransfer callTransfer = bcp.getCallTransfer();

            if (callTransfer.getIsSubscribed() == null) {
                log.error("CallTransfer: subscribed was not populated, requires a value of true or false.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallTransfer: subscribed was not populated, requires a value of true or false.");
            }

            if (!callTransfer.getIsSubscribed()) {
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "CallTransfer: subscribed cannot be false, requires a value of true only.");
            }
        }
        
        // validating single digit speed dialing 
        if(bcp.getOneDigitSpeedDialing() != null) {
        	OneDigitSpeedDialing oneDigitSpeedDialing = bcp.getOneDigitSpeedDialing();

            if (oneDigitSpeedDialing.getIsSubscribed() == null) {
                log.error("OneDigitSpeedDialing: subscribed was not populated, requires a value of true or false.");
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "OneDigitSpeedDialing: subscribed was not populated, requires a value of true or false.");
            }

            if (!oneDigitSpeedDialing.getIsSubscribed()) {
                throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, "OneDigitSpeedDialing: subscribed cannot be false, requires a value of true only.");
            }
        }
    }
	
	
    private void updateSeqRinging(BusinessClassPhone bcp, PlexViewRequestType plxViewTmplt, String requestId) throws ProvisioningServiceException {
        log.info("[{}]Adding Sequential Ring feature: {}", requestId, bcp.getSequentialRingingFeature().toString());

        StringBuilder ringingList = new StringBuilder();

        SequentialRinging sequentialRing = bcp.getSequentialRingingFeature();

        NgfsSeqringingV2 seqringingV2 = NgfsSeqringingV2.Factory.newInstance();

        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_ACTION_SEQ_RING,
                PlexViewCatalog.SeqRinging.name(), seqringingV2);

        seqringingV2.setActivated(sequentialRing.getIsActivated());
        seqringingV2.setAssigned(sequentialRing.getIsSubscribed());
        seqringingV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));

        for (Map.Entry<String, String> sequentialRingList : sequentialRing.getSequentialRing().entrySet()) {
            String numRings = sequentialRingList.getValue();
            String phoneNumber = sequentialRingList.getKey();

            if (StringUtils.isEmpty(numRings)) {
                numRings = String.valueOf(ASBMessageConst.DEFAULT_NUM_RINGS);
                log.debug("[{}]Updating Sequential Ringing: rings not present, assign default value - [{}]", requestId, numRings);
            } else {
                log.debug("[{}]Update Sequential Ringing: rings present in request - [{}]", requestId, numRings);
            }

            String noAnsTimeOut = getNoAnsTimeOut(numRings);

            if (bcp.getPhoneNumber().equalsIgnoreCase(phoneNumber)) {
                ringingList.append("null^null^true^true");
                ringingList.append("^");
                ringingList.append(noAnsTimeOut);
                ringingList.append("^false");
            } else {
                if (ringingList.length() > 0) {
                    ringingList.append("`");
                }

                ringingList.append(phoneNumber);
                ringingList.append("^");
                ringingList.append(ASBMessageConst.STANDARD_RING_PATTERN_NAME);
                ringingList.append("^true^true");
                ringingList.append("^");
                ringingList.append(noAnsTimeOut);
                ringingList.append("^false");
            }
        }

        seqringingV2.setRingingList(ringingList.toString());

        plxViewTmplt.setSeqRinging(seqringingV2);
    }

    private void updateRemoteUser(BusinessClassPhone bcp, PlexViewRequestType plexViewRequestType, String requestId) throws ProvisioningServiceException {
        log.info("[{}]Adding Remote User/Office feature: {}", requestId, bcp.getRemoteUserFeature().toString());

        NgfsRemoteuserV2 remoteUserV2 = NgfsRemoteuserV2.Factory.newInstance();
        NgfsPinserviceV2 pinServiceV2 = NgfsPinserviceV2.Factory.newInstance();
        String pin;

        RemoteUser remoteUser = bcp.getRemoteUserFeature();

        pin = decryptPin(remoteUser.getPin());

        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_ACTION_REMOTE_OFFICE,
                PlexViewCatalog.RemoteUser.name(), remoteUserV2);

        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_ACTION_PINSERVICE,
                PlexViewCatalog.PinService.name(), pinServiceV2);

        remoteUserV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));
        remoteUserV2.setAssigned(true);

        if (Boolean.valueOf(comInterfaceEnabled)) {
            plexViewRequestType.getNgfsSubpartyV2().setPin(pin);
        }

        pinServiceV2.setPin(pin);
        pinServiceV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));
        pinServiceV2.setAssigned(true);

        plexViewRequestType.setPinService(pinServiceV2);
        plexViewRequestType.setRemoteUser(remoteUserV2);
    }

    private void updateCallTransferAutoAttendant(BusinessClassPhone bcp, PlexViewRequestType plexViewRequestType, String requestId) throws ProvisioningServiceException {
        log.info("[{}]Adding Personal Answering Service feature: {}", requestId, bcp.getPersonalAttendantFeature());

        NgfsCalltransferV2 callTransferV2 = NgfsCalltransferV2.Factory.newInstance();
        NgfsAutoattendantV2 autoAttendantV2 = NgfsAutoattendantV2.Factory.newInstance();
        NgfsWebportalV2 webPortalV2 = NgfsWebportalV2.Factory.newInstance();

        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_ACTION_PERSONAL_ANS,
                PlexViewCatalog.CallTransfer.name(), callTransferV2);

        callTransferV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));
        callTransferV2.setAssigned(true);

        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_ACTION_PERSONAL_ANS,
                PlexViewCatalog.AutoAttendant.name(), autoAttendantV2);

        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, "Default",
                PlexViewCatalog.WebPortal.name(), webPortalV2);

        webPortalV2.setPerPuid(false);
        webPortalV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));

        autoAttendantV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));
        autoAttendantV2.setAssigned(true);

        plexViewRequestType.setAutoAttendant(autoAttendantV2);
        plexViewRequestType.setCallTransfer(callTransferV2);
        plexViewRequestType.setWebPortal(webPortalV2);
    }

    private void updateMultipleRingPattern(BusinessClassPhone bcp, PlexViewRequestType plexViewRequestType, String requestId) throws ProvisioningServiceException {
/*
        log.info("[{}]Adding Multiple Ring Pattern feature for phone number: {}", requestId, bcp.getPhoneNumber());

        NgfsMultipleringpatternV2 multipleRingPatternV2 = NgfsMultipleringpatternV2.Factory.newInstance();

        if (bcp.getMultipleRingPatternFeature().getRingPatterns().size() > maxMultipleRings) {
            throw new ProvisioningServiceException(String.format("[%s]Multiple Ring: Only %d Distinctive ring patterns are supported, actual %d",
                    requestId, maxMultipleRings, bcp.getMultipleRingPatternFeature().getRingPatterns().size()));
        }

        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_ACTION_DRING,
                String.format("%s-STANDARD", PlexViewCatalog.MultipleRingPattern.name()), multipleRingPatternV2);

        multipleRingPatternV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));
        multipleRingPatternV2.setAssigned(true);

        NgfsMultipleringpatternV2[] multipleringpatternV2s = new NgfsMultipleringpatternV2[bcp.getMultipleRingPatternFeature().getRingPatterns().size()];
        multipleringpatternV2s[1] = multipleRingPatternV2;
        plexViewRequestType.addNewMultipleRingPattern();
        //plexViewRequestType.setMultipleRingPatternArray(1, multipleRingPatternV2);

        int mrpIdx = 1;

        for (Map.Entry<String, RingPattern> multipleRingPattern : bcp.getMultipleRingPatternFeature().getRingPatterns().entrySet()) {
            String phoneNumber = multipleRingPattern.getKey();
            RingPattern patternName = multipleRingPattern.getValue();

            if (patternName != null && StringUtils.isNotEmpty(phoneNumber)) {
                if (patternName.name().toUpperCase().startsWith("RINGPATTERN")) {

                    mrpIdx++;

                    try {
                        PropertyUtils.setProperty(plexViewRequestType.getSubParty(), String.format("publicUID%d", mrpIdx), formatPublicUID(bcp.getPhoneNumber()));
                    }
                    catch (Exception ex) {
                        log.error("Exception processing multiple ring pattern request id, {}:", requestId, ex.getMessage());
                    }

                    multipleRingPatternV2 = NgfsMultipleringpatternV2.Factory.newInstance();

                    ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_ACTION_DRING,
                            String.format("%s-STANDARD", PlexViewCatalog.MultipleRingPattern.name()), multipleRingPatternV2);

                    multipleRingPatternV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));
                    plexViewRequestType.setMultipleRingPatternArray(mrpIdx, multipleRingPatternV2);

                }
            }
        }*/
    }

    private void updateSimultaneousRinging(BusinessClassPhone bcp, PlexViewRequestType plexViewRequestType, String requestId) throws ProvisioningServiceException {
        log.info("[{}]Adding Simultaneous Ringing feature: {}", requestId, bcp.getSimultaneousRingFeature().toString());

        NgfsSimultaneousringingV2 simultaneousRingingV2 = NgfsSimultaneousringingV2.Factory.newInstance();

        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_ACTION_SIMUL_RING,
                PlexViewCatalog.SimultaneousRinging.name(), simultaneousRingingV2);

        simultaneousRingingV2.setActivated(bcp.getSimultaneousRingFeature().getIsActivated());
        simultaneousRingingV2.setAssigned(bcp.getSimultaneousRingFeature().getIsSubscribed());
        simultaneousRingingV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));

        int simDNIndex = 0;

        for (String puid : bcp.getSimultaneousRingFeature().getPuidNums()) {
            if (StringUtils.isNotEmpty(puid)) {
                simDNIndex++;

                if (simDNIndex <= maxSimultaneousRings) {
                    try {
                        PropertyUtils.setProperty(simultaneousRingingV2, String.format("%s%s", "puidNum", simDNIndex), puid);
                        PropertyUtils.setProperty(simultaneousRingingV2, String.format("%s%s", "ringPattern", simDNIndex), getRingPattern(simDNIndex));
                        PropertyUtils.setProperty(simultaneousRingingV2, String.format("%s%s", "familyFlag", simDNIndex), true);
                    } catch (Exception ex) {
                        log.error("[{}] Failed to set the PuidNumX for Index: {}", requestId, simDNIndex, ex);
                    }
                } else {
                    log.error("[{}]Simultaneous Ring: Only %d DNs allowed in the request, actual DNs %d",requestId, maxSimultaneousRings,
                            bcp.getSimultaneousRingFeature().getPuidNums().size());
                    throw new ProvisioningServiceException(HttpStatus.BAD_REQUEST, String.format("Simultaneous Ring: Only %d DNs allowed in the request, actual DNs %d", maxSimultaneousRings,
                            bcp.getSimultaneousRingFeature().getPuidNums().size()));
                }
            }
        }

        plexViewRequestType.setSimultaneousRinging(simultaneousRingingV2);
    }

    private void updateServiceSuspension(BusinessClassPhone bcp, PlexViewRequestType plexViewRequestType, String requestId) throws ProvisioningServiceException {
        log.info("[{}]Adding Suspend Line feature: {}", requestId, bcp.getSuspendLineFeature());

        NgfsCallbarringV2 ngfsCallBarringV2 = NgfsCallbarringV2.Factory.newInstance();
        NgfsCallblockingV2 ngfsCallBlockingV2 = NgfsCallblockingV2.Factory.newInstance();

        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_ACTION_SUSPENDED_LINE,
                PlexViewCatalog.CallBarring.name(), ngfsCallBarringV2);

        ngfsCallBarringV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));

        ngfsCallBarringV2.setCallBarringAll(ngfsCallBarringV2.getAssigned());

        ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE, ASBMessageConst.ASB_ACTION_SUSPENDED_LINE,
                PlexViewCatalog.CallBlocking.name(), ngfsCallBlockingV2);

        ngfsCallBlockingV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));


        plexViewRequestType.setCallBarring(ngfsCallBarringV2);
        plexViewRequestType.setCallBlocking(ngfsCallBlockingV2);
    }
    
	private void updateCallForwardingBusy(BusinessClassPhone bcp, PlexViewRequestType plxViewTmplt, String requestId)
			throws ProvisioningServiceException {
		log.info("[{}]Adding Call Forward Busy feature: {}", requestId, bcp.getCallForwardBusy().toString());

		CallForwardBusy callForwardBusy = bcp.getCallForwardBusy();

		// create object of NgfsCallforwardingbusyV2 using omcp-switch-client, it has
		// all classes, just pick one as per need
		NgfsCallforwardingbusyV2 callForwardingBusyV2 = NgfsCallforwardingbusyV2.Factory.newInstance();

		// Reading CTSServiceConfig.xml file based on BCP managed line and feature name,
		// populating default values in NgfsCallforwardingbusyV2 class.
		ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE,
				ASBMessageConst.ASB_ACTION_CFB, PlexViewCatalog.CallForwardingBusy.name(), callForwardingBusyV2);

		callForwardingBusyV2.setActivated(callForwardBusy.getIsActivated());
		callForwardingBusyV2.setAssigned(callForwardBusy.getIsSubscribed());
		callForwardingBusyV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));

		String forwardDN = callForwardBusy.getForwardDN();
		if (StringUtils.isNotEmpty(forwardDN)) {
			callForwardingBusyV2.setForwardToDN(forwardDN);
		}

		// setting NgfsCallforwardingbusyV2 to PlexViewRequestType
		plxViewTmplt.setCallForwardingBusy(callForwardingBusyV2);

	}
	
	private void updateCallForwardingVari(BusinessClassPhone bcp, PlexViewRequestType plxViewTmplt, String requestId)
			throws ProvisioningServiceException {
		log.info("[{}]Adding Call Forward Unconditional feature: {}", requestId,
				bcp.getCallForwardUnconditional().toString());

		CallForwardUnconditional callForwardUnconditional = bcp.getCallForwardUnconditional();

		// create object of NgfsCallforwardingvariV2 using omcp-switch-client, it has
		// all classes, just pick one as per need
		NgfsCallforwardingvariV2 callForwardingVariV2 = NgfsCallforwardingvariV2.Factory.newInstance();

		// Reading CTSServiceConfig.xml file based on BCP managed line and feature name,
		// populating default values in NgfsCallforwardingvariV2 class.
		ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE,
				ASBMessageConst.ASB_ACTION_CFU, PlexViewCatalog.CallForwardingVari.name(), callForwardingVariV2);

		callForwardingVariV2.setActivated(callForwardUnconditional.getIsActivated());
		callForwardingVariV2.setAssigned(callForwardUnconditional.getIsSubscribed());
		callForwardingVariV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));

		String forwardDN = callForwardUnconditional.getForwardDN();
		if (StringUtils.isNotEmpty(forwardDN)) {
			callForwardingVariV2.setForwardToDN(forwardDN);
		}

		// setting NgfsCallforwardingvariV2 to PlexViewRequestType
		plxViewTmplt.setCallForwardingVari(callForwardingVariV2);

	}
	
	private void updateCallTransfer(BusinessClassPhone bcp, PlexViewRequestType plxViewTmplt, String requestId)
			throws ProvisioningServiceException {
		log.info("[{}]Adding Call Transfer feature: {}", requestId, bcp.getCallTransfer().toString());

		CallTransfer callTransfer = bcp.getCallTransfer();

		// create object of NgfsCalltransferV2 using omcp-switch-client, it has
		// all classes, just pick one as per need
		NgfsCalltransferV2 callTransferV2 = NgfsCalltransferV2.Factory.newInstance();

		// Reading CTSServiceConfig.xml file based on BCP managed line and feature name,
		// populating default values in NgfsCalltransferV2 class.
		ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE,
				ASBMessageConst.ASB_ACTION_CALL_TRANSFER, PlexViewCatalog.CallTransfer.name(), callTransferV2);

		callTransferV2.setAssigned(callTransfer.getIsSubscribed());
		callTransferV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));

		// setting NgfsCalltransferV2 to PlexViewRequestType
		plxViewTmplt.setCallTransfer(callTransferV2);

	}
	
	private void updateCallForwardingNoAns(BusinessClassPhone bcp, PlexViewRequestType plxViewTmplt, String requestId)
			throws ProvisioningServiceException {
		log.info("[{}]Adding Call Forward No Answer feature: {}", requestId, bcp.getCallForwardNoAnswer().toString());

		CallForwardNoAnswer callForwardNoAnswer = bcp.getCallForwardNoAnswer();

		// create object of NgfsCallforwardingnoansV2 using omcp-switch-client, it has
		// all classes, just pick one as per need
		NgfsCallforwardingnoansV2 callForwardingNoAnsV2 = NgfsCallforwardingnoansV2.Factory.newInstance();

		// Reading CTSServiceConfig.xml file based on BCP managed line and feature name,
		// populating default values in NgfsCallforwardingnoansV2 class.
		ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE,
				ASBMessageConst.ASB_ACTION_CFNA, PlexViewCatalog.CallForwardingNoAns.name(), callForwardingNoAnsV2);

		callForwardingNoAnsV2.setActivated(callForwardNoAnswer.getIsActivated());
		callForwardingNoAnsV2.setAssigned(callForwardNoAnswer.getIsSubscribed());
		callForwardingNoAnsV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));

		String forwardDN = callForwardNoAnswer.getForwardDN();
		if (StringUtils.isNotEmpty(forwardDN)) {
			callForwardingNoAnsV2.setForwardToDN(forwardDN);
		}

		// populated default value for number of rings above
		String numRings = callForwardNoAnswer.getNumRings();
		if (StringUtils.isNotEmpty(numRings)) {
			callForwardingNoAnsV2.setNoAnswerTimeout(Short.parseShort(getNoAnsTimeOut(numRings)));
		}

		// setting NgfsCallforwardingnoansV2 to PlexViewRequestType
		plxViewTmplt.setCallForwardingNoAns(callForwardingNoAnsV2);

	}
	
	private void updateOneDigitSpeedDial(BusinessClassPhone bcp, PlexViewRequestType plxViewTmplt, String requestId)
			throws ProvisioningServiceException {
		log.info("[{}]Adding One Digit Speed Dial feature: {}", requestId, bcp.getOneDigitSpeedDialing().toString());

		OneDigitSpeedDialing oneDigitSpeedDialing = bcp.getOneDigitSpeedDialing();

		// create object of NgfsOnedigitspeeddialV2 using omcp-switch-client, it has
		// all classes, just pick one as per need
		NgfsOnedigitspeeddialV2 oneDigitSpeedDialV2 = NgfsOnedigitspeeddialV2.Factory.newInstance();

		// Reading CTSServiceConfig.xml file based on BCP managed line and feature name,
		// populating default values in NgfsOnedigitspeeddialV2 class.
		ctsServiceConfig.populateCatalogDefaultValues(ASBMessageConst.ASB_PROP_TYPE_BCP_MNGDLINE,
				ASBMessageConst.ASB_ACTION_SC1D, PlexViewCatalog.OneDigitSpeedDial.name(), oneDigitSpeedDialV2);
		
		// This map will contain dial code as key and digital number as value
		Map<String, String> digitalNumbersMap = oneDigitSpeedDialing.getDigitalNumbers();
		
		if (MapUtils.isNotEmpty(digitalNumbersMap)) {
			StringBuilder dialCodeEntries = new StringBuilder();
			StringBuilder dnEntries = new StringBuilder();
			
			int start = 2;
			int end = 9;
			// looping through 2 to 9 as these dial code are fixed
			for (; start <= end; start++) {
				if (digitalNumbersMap.get(String.valueOf(start)) != null) {
					dialCodeEntries.append(start);
					dnEntries.append((digitalNumbersMap.get(String.valueOf(start)).trim()));
				}
				if (start != end) {
					dialCodeEntries.append("^");
					dnEntries.append("^");
				}
			}
			/*
		     * <DialCodesEntries>2^3^4^5^^7^^</DialCodesEntries>
		    *  <DNEntries>9102212000^9102212001^9102212002^9102212003^^411^^</DNEntries>
		    */
			oneDigitSpeedDialV2.setDialCodesEntries(dialCodeEntries.toString());
			oneDigitSpeedDialV2.setDNEntries(dnEntries.toString());
		}
		
		oneDigitSpeedDialV2.setPublicUID(formatPublicUID(bcp.getPhoneNumber()));
		oneDigitSpeedDialV2.setAssigned(oneDigitSpeedDialing.getIsSubscribed());
		
		// setting NgfsOnedigitspeeddialV2 to PlexViewRequestType
		plxViewTmplt.setOneDigitSpeedDial(oneDigitSpeedDialV2);
	}

    private String formatPublicUID(String phoneNumber) {
        return String.format("%s%s", "+1", phoneNumber);
    }

    private String decryptPin(String pin) {
        String decryptPin = pin;

        if (pin.startsWith(Crypt.CHA)) {
            decryptPin = Crypt.decrypt(pin);
        }

        return decryptPin;
    }

    public static Object getRingPattern(int ringPatternIndex) {
        switch (ringPatternIndex) {
            case 1:
                return NgfsSimultaneousringingV2.RingPattern1.Enum.forInt(NgfsSimultaneousringingV2.RingPattern1.INT_STANDARD);
            case 2:
                return NgfsSimultaneousringingV2.RingPattern2.Enum.forInt(NgfsSimultaneousringingV2.RingPattern2.INT_STANDARD);
            case 3:
                return NgfsSimultaneousringingV2.RingPattern3.Enum.forInt(NgfsSimultaneousringingV2.RingPattern3.INT_STANDARD);
            case 4:
                return NgfsSimultaneousringingV2.RingPattern4.Enum.forInt(NgfsSimultaneousringingV2.RingPattern4.INT_STANDARD);
            case 5:
                return NgfsSimultaneousringingV2.RingPattern5.Enum.forInt(NgfsSimultaneousringingV2.RingPattern5.INT_STANDARD);
            case 6:
                return NgfsSimultaneousringingV2.RingPattern6.Enum.forInt(NgfsSimultaneousringingV2.RingPattern6.INT_STANDARD);
            case 7:
                return NgfsSimultaneousringingV2.RingPattern7.Enum.forInt(NgfsSimultaneousringingV2.RingPattern7.INT_STANDARD);
            case 8:
                return NgfsSimultaneousringingV2.RingPattern8.Enum.forInt(NgfsSimultaneousringingV2.RingPattern8.INT_STANDARD);
            default:
                return NgfsSimultaneousringingV2.RingPattern9.Enum.forInt(NgfsSimultaneousringingV2.RingPattern9.INT_STANDARD);
        }
    }

    private String getNoAnsTimeOut(String numRings) {
        return String.valueOf(Integer.parseInt(numRings) * 6);
    }
}
