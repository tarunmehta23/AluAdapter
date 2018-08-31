package com.charter.provisioning.voice.commercial.alu.util;

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
import com.alu.plexwebapi.api.NgfsSubpartyV2;
import com.alu.plexwebapi.api.NgfsWebportalV2;
import com.alu.plexwebapi.api.PlexViewRequestType;
import com.alu.plexwebapi.api.PlexViewResponseType;
import com.charter.provisioning.voice.commercial.alu.config.CTSConfiguration;
import com.charter.provisioning.voice.commercial.alu.config.CTSSwitchConfiguration;
import com.charter.provisioning.voice.commercial.alu.config.CatalogObject;
import com.charter.provisioning.voice.commercial.alu.config.CatalogProperty;
import com.charter.provisioning.voice.commercial.alu.config.OMCPCommands;
import com.charter.provisioning.voice.commercial.alu.config.ProvisioningAttribute;
import com.charter.provisioning.voice.commercial.alu.enums.BlockingCodes;
import com.charter.provisioning.voice.commercial.alu.helper.AlaCarteFeaturesHelper;
import com.charter.provisioning.voice.commercial.alu.model.BusinessClassPhone;
import com.charter.provisioning.voice.commercial.alu.model.CallForwardBusy;
import com.charter.provisioning.voice.commercial.alu.model.CallForwardNoAnswer;
import com.charter.provisioning.voice.commercial.alu.model.CallForwardUnconditional;
import com.charter.provisioning.voice.commercial.alu.model.CallTransfer;
import com.charter.provisioning.voice.commercial.alu.model.CreateDigitalPhoneResponse;
import com.charter.provisioning.voice.commercial.alu.model.DisconnectReferral;
import com.charter.provisioning.voice.commercial.alu.model.DistinctiveRing;
import com.charter.provisioning.voice.commercial.alu.model.OneDigitSpeedDialing;
import com.charter.provisioning.voice.commercial.alu.model.RemoteUser;
import com.charter.provisioning.voice.commercial.alu.model.RingPattern;
import com.charter.provisioning.voice.commercial.alu.model.SequentialRinging;
import com.charter.provisioning.voice.commercial.alu.model.SimultaneousRing;
import com.charter.provisioning.voice.commercial.alu.session.SessionRequestHelper;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockHttpServletResponse;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MockObjectCreator {

    public static final String CTS_SWITCH_NAME = "cts00";

    public static final String CORRELATION_ID = "0b767cdf-ec4a-403f-9c41-e71b703532a3";

    public static final String PHONE_NUMBER = "314-235-2882";

    public static final String USER_NAME = "jdoe";

    public static final String PASSWORD = "test123";

    private static final String PUBLIC_UID = "+1314-222-3548";

    private static final String PIN = "242";

    public static final String SESSION_ID = "testaccount:1092748";

    public static final String SUBSCRIBER_ID = "0160bd1ac699a58p";


    public static List<CTSConfiguration> createCTSConfigurations(boolean config1, boolean config2){
        List<CTSConfiguration> ctsConfigurations = new ArrayList<>();

        if(config1) {
            ctsConfigurations.add(createCTSConfiguration(true));
        }
        if(config2) {
            ctsConfigurations.add(createCTSConfiguration(false));
        }

        return ctsConfigurations;
    }

    public static CTSConfiguration createCTSConfiguration(boolean config1){
        return CTSConfiguration
                .builder()
                .fsdbName("fsdb1")
                .id(config1 ? 1 : 2)
                .status("Active")
                .topologyId(config1 ? "cts00_fsdb1" : "cts01_fsdb1")
                .switchName(config1 ? CTS_SWITCH_NAME : "cts01")
                .provisionControllers(config1 ? "false" : "true")
                .build();
    }

    public static CTSSwitchConfiguration createCTSSwitchConfiguration(){
        CTSSwitchConfiguration ctsSwitchConfiguration = new CTSSwitchConfiguration();
        ctsSwitchConfiguration.setCtsConfigurations(createCTSConfigurations(true, true));
        return ctsSwitchConfiguration;
    }

    public static BusinessClassPhone createBusinessClassPhone(boolean distinctiveRingFeatures, String displayName){
        return BusinessClassPhone
                .builder()
                .phoneNumber("314-222-3548")
                .displayName(displayName)
                .basicCallLog("inactive")
                .blockingCodes(createBlockingCodes())
                .dialPlanId("2000")
                .subscriberId("0160bd1ac699a58f")
                .switchName(CTS_SWITCH_NAME)
                .distinctiveRingFeature(distinctiveRingFeatures ? createDistinctiveRing(): null)
                .build();
    }

    public static BusinessClassPhone createBusinessClassPhoneWithSequentialRinging(Boolean sequentialRingMax, Boolean isActivated, Boolean isAssigned){
        BusinessClassPhone bcp = createBusinessClassPhone(false,  "J. Doe");
        bcp.setSequentialRingingFeature(createSequentialRinging(sequentialRingMax, isActivated, isAssigned));
        return bcp;
    }

    public static BusinessClassPhone createBusinessClassPhoneWithSimultaneousRing(Boolean maxSimultaneousRings, Boolean isAssigned, Boolean isActivated){
        BusinessClassPhone bcp = createBusinessClassPhone(false,  "J. Doe");
        bcp.setSimultaneousRingFeature(createSimultaneousRingFeature(maxSimultaneousRings, isAssigned, isActivated));
        return bcp;
    }

    public static DisconnectReferral createDisconnectReferral(){
        return DisconnectReferral
                .builder()
                .newDN("DN")
                .publicUID(MockObjectCreator.PUBLIC_UID)
                .build();
    }

    public static BusinessClassPhone createBusinessClassPhoneWithRemoteUserOffice(boolean includePin){
        BusinessClassPhone bcp = createBusinessClassPhone(false,  "J. Doe");
        bcp.setRemoteUserFeature(RemoteUser
                .builder()
                .pin(includePin ? PIN : null)
                .build());
        return bcp;
    }

    public static BusinessClassPhone createBusinessClassPhoneWithSuspendLine(){
        BusinessClassPhone bcp = createBusinessClassPhone(false,  "J. Doe");
        bcp.setSuspendLineFeature(true);
        return bcp;
    }

    public static BusinessClassPhone createBusinessClassPhoneWithPersonalAnsweringService(){
        BusinessClassPhone bcp = createBusinessClassPhone(false,  "J. Doe");
        bcp.setPersonalAttendantFeature(true);
        return bcp;
    }

    public static PlexViewRequestType createPlexViewRequestType(String firstName, String lastName, String callServerPublicIds, boolean includeSubParty){
        PlexViewRequestType plexViewRequestType = PlexViewRequestType.Factory.newInstance();

        plexViewRequestType.setCommand("ent-pcm-entuser");
        plexViewRequestType.setSwitchName("TWC_PCM_ENG");
        plexViewRequestType.setRequestId("0b767cdf-ec4a-403f-9c41-e71b703532a3");
        plexViewRequestType.setMaxRows(new BigInteger("-1"));

        plexViewRequestType.setPcmEntUserAid("TWC-0160bd1ac699a58f");
        plexViewRequestType.setUserId("0160bd1ac699a58f");
        plexViewRequestType.setCsServicePointID("0160bd1ac699a58f");
        plexViewRequestType.setPrimaryPublicId(PUBLIC_UID);
        plexViewRequestType.setCallServerPublicIds(callServerPublicIds);

        if(StringUtils.isNotEmpty(firstName)) {
            plexViewRequestType.setFirstName(firstName);
        }

        if(StringUtils.isNotEmpty(lastName)) {
            plexViewRequestType.setLastName(lastName);
        }

        plexViewRequestType.setGroupId("");
        plexViewRequestType.setCsPrivilegeLevel("1");
        plexViewRequestType.setIsAssigned(Boolean.toString(true));

        plexViewRequestType.setPassword("default");
        plexViewRequestType.setPartitionId("3");
        plexViewRequestType.setTemplateId("default");
        plexViewRequestType.setTopologyId("cts00_fsdb1");

        if(includeSubParty) {
            plexViewRequestType.setSubParty(createSubParty());
        }

        return plexViewRequestType;
    }

    public static PlexViewRequestType createPlexViewRequestTypeDREF(){
        PlexViewRequestType plxViewTmplt = PlexViewRequestType.Factory.newInstance();

        plxViewTmplt.setCommand(OMCPCommands.ENT_NGFS_SUBSCRIBER_V2);
        plxViewTmplt.setFsdb("fsdb1");
        plxViewTmplt.setSwitchName("cts00");
        plxViewTmplt.setRequestId(CORRELATION_ID);
        plxViewTmplt.setMaxRows(new BigInteger("-1"));

        return plxViewTmplt;
    }

    public static PlexViewRequestType createPlexViewRequestType(String command, String ctsSwitchName, String fsdbName, String requestId) {
        PlexViewRequestType plxViewTmplt = PlexViewRequestType.Factory.newInstance();

        plxViewTmplt.setCommand(command);

        if (fsdbName != null && StringUtils.isNotEmpty(fsdbName)) {
            plxViewTmplt.setFsdb(fsdbName);
        }

        if (ctsSwitchName != null && StringUtils.isNotEmpty(ctsSwitchName)) {
            plxViewTmplt.setSwitchName(ctsSwitchName);
        }

        plxViewTmplt.setRequestId(requestId);
        plxViewTmplt.setMaxRows(new BigInteger("-1"));
        plxViewTmplt.setAid(SUBSCRIBER_ID);

        return plxViewTmplt;
    }

    public static PlexViewRequestType createPlexViewRequestTypeWithSequentialRing(String firstName, String lastName, String callServerPublicIds){
        PlexViewRequestType plexViewRequestType = createPlexViewRequestType(firstName, lastName, callServerPublicIds, false);
        plexViewRequestType.setSeqRinging(createSequentialRinging());
        return plexViewRequestType;
    }

    public static PlexViewRequestType createPlexViewRequestTypeWithSimultaneousRinging(String firstName, String lastName, String callServerPublicIds){
        PlexViewRequestType plexViewRequestType = createPlexViewRequestType(firstName, lastName, callServerPublicIds, false);
        plexViewRequestType.setSimultaneousRinging(createSimultaneousRinging());
        return plexViewRequestType;
    }

    public static PlexViewRequestType createPlexViewRequestTypeWithRemoteUserOffice(String firstName, String lastName, String callServerPublicIds, boolean setPin){
        PlexViewRequestType plexViewRequestType = createPlexViewRequestType(firstName, lastName, callServerPublicIds, false);
        plexViewRequestType.setRemoteUser(createRemoteUserV2());
        plexViewRequestType.setPinService(createPinServiceV2(setPin));
        return plexViewRequestType;
    }

    public static PlexViewRequestType createPlexViewRequestTypeWithPersonalAnsweringService(String firstName, String lastName, String callServerPublicIds){
        PlexViewRequestType plexViewRequestType = createPlexViewRequestType(firstName, lastName, callServerPublicIds, false);
        plexViewRequestType.setCallTransfer(createCallTransferV2());
        plexViewRequestType.setAutoAttendant(createNgfsAutoAttendantV2());
        plexViewRequestType.setWebPortal(createNgfsWebPortalV2());
        return plexViewRequestType;
    }

    public static PlexViewRequestType createPlexViewRequestTypeWithSuspendLine(String firstName, String lastName, String callServerPublicIds){
        PlexViewRequestType plexViewRequestType = createPlexViewRequestType(firstName, lastName, callServerPublicIds, false);
        plexViewRequestType.setCallBlocking(createNgfsCallBlockingV2());
        plexViewRequestType.setCallBarring(createNgfsCallBarringV2());
        return plexViewRequestType;
    }

    private static NgfsSeqringingV2 createSequentialRinging(){
        NgfsSeqringingV2 ngfsSeqringingV2 = NgfsSeqringingV2.Factory.newInstance();
        ngfsSeqringingV2.setAssigned(true);
        ngfsSeqringingV2.setActivated(true);
        ngfsSeqringingV2.setPublicUID(PUBLIC_UID);
        ngfsSeqringingV2.setDefaultAnswerTimeout(30);
        ngfsSeqringingV2.setPlayAnnouncement(false);
        ngfsSeqringingV2.setSend181Mode(NgfsSeqringingV2.Send181Mode.TAS_181_NONE);
        ngfsSeqringingV2.setPerPuid(false);
        ngfsSeqringingV2.setRingingList("null^null^true^true^18^false`8163888343^STANDARD^true^true^30^false`8163888344^STANDARD^true^true^30^false");
        return ngfsSeqringingV2;
    }

    private static NgfsSimultaneousringingV2 createSimultaneousRinging(){
        NgfsSimultaneousringingV2 ngfsSimultaneousringingV2 = NgfsSimultaneousringingV2.Factory.newInstance();
        ngfsSimultaneousringingV2.setPublicUID(PUBLIC_UID);
        ngfsSimultaneousringingV2.setActivated(true);
        ngfsSimultaneousringingV2.setAssigned(true);
        long puidNum = 3142221231L;

        for(int i = 1; i <= 9; i++) {
            try {
                PropertyUtils.setProperty(ngfsSimultaneousringingV2, String.format("%s%s", "puidNum", i), String.valueOf(puidNum));
                PropertyUtils.setProperty(ngfsSimultaneousringingV2, String.format("%s%s", "ringPattern", i), AlaCarteFeaturesHelper.getRingPattern(i));
                PropertyUtils.setProperty(ngfsSimultaneousringingV2, String.format("%s%s", "familyFlag", i), true);
                puidNum++;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return ngfsSimultaneousringingV2;
    }

    private static NgfsRemoteuserV2 createRemoteUserV2(){
        NgfsRemoteuserV2 remoteUserV2 = NgfsRemoteuserV2.Factory.newInstance();
        remoteUserV2.setAssigned(true);
        remoteUserV2.setPublicUID(PUBLIC_UID);
        remoteUserV2.setCallsAlwd(true);
        remoteUserV2.setSubsequentAlwd(true);
        remoteUserV2.setPerPuid(true);
        return remoteUserV2;
    }

    private static NgfsPinserviceV2 createPinServiceV2(boolean setPin){
        NgfsPinserviceV2 pinServiceV2 = NgfsPinserviceV2.Factory.newInstance();
        pinServiceV2.setAssigned(true);
        pinServiceV2.setPublicUID(PUBLIC_UID);
        pinServiceV2.setPerPuid(false);
        pinServiceV2.setPin(setPin ? PIN : null);
        pinServiceV2.setPinFrozen(false);
        return pinServiceV2;
    }

    private static NgfsSubpartyV2 createSubParty(){
        NgfsSubpartyV2 ngfsSubPartyV2 = NgfsSubpartyV2.Factory.newInstance();
        ngfsSubPartyV2.setPin(PIN);
        return ngfsSubPartyV2;
    }

    private static NgfsCalltransferV2 createCallTransferV2(){
        NgfsCalltransferV2 callTransferV2 = NgfsCalltransferV2.Factory.newInstance();
        callTransferV2.setAssigned(true);
        callTransferV2.setActivated(true);
        callTransferV2.setPublicUID(PUBLIC_UID);
        callTransferV2.setBlind(true);
        callTransferV2.setPerPuid(false);
        return callTransferV2;
    }

    private static NgfsAutoattendantV2 createNgfsAutoAttendantV2(){
        NgfsAutoattendantV2 autoAttendantV2 = NgfsAutoattendantV2.Factory.newInstance();
        autoAttendantV2.setAssigned(true);
        autoAttendantV2.setPublicUID(PUBLIC_UID);
        return autoAttendantV2;
    }

    private static NgfsWebportalV2 createNgfsWebPortalV2(){
        NgfsWebportalV2 webPortal = NgfsWebportalV2.Factory.newInstance();
        webPortal.setPublicUID(PUBLIC_UID);
        webPortal.setPerPuid(false);
        return  webPortal;
    }

    private static NgfsCallbarringV2 createNgfsCallBarringV2(){
        NgfsCallbarringV2 ngfsCallBarringV2 = NgfsCallbarringV2.Factory.newInstance();
        ngfsCallBarringV2.setCallBarringAll(false);
        ngfsCallBarringV2.setPublicUID(PUBLIC_UID);
        return ngfsCallBarringV2;
    }

    private static NgfsCallblockingV2 createNgfsCallBlockingV2(){
        NgfsCallblockingV2 ngfsCallBlockingV2 = NgfsCallblockingV2.Factory.newInstance();
        ngfsCallBlockingV2.setAll(true);
        ngfsCallBlockingV2.setUserCtrlAll(false);
        ngfsCallBlockingV2.setActivated(true);
        ngfsCallBlockingV2.setPublicUID(PUBLIC_UID);
        ngfsCallBlockingV2.setAssigned(true);
        ngfsCallBlockingV2.setPerPuid(false);
        return ngfsCallBlockingV2;
    }

    private static List<String> createBlockingCodes(){
        List<String> blockingCodes = new ArrayList<>();
        blockingCodes.add(BlockingCodes.CODE_411.getCode());
        blockingCodes.add(BlockingCodes.CODE_CASUAL.getCode());
        blockingCodes.add(BlockingCodes.CODE_ZERO_PLUS.getCode());

        return blockingCodes;
    }

    private static Map<RingPattern, String> createRingPattern(){
        Map<RingPattern, String> ringPatterns = new HashMap<>();
        ringPatterns.put(RingPattern.STANDARD, "7031111111");
        ringPatterns.put(RingPattern.RINGPATTERN2, "7032221111");

        return ringPatterns;
    }

    private static DistinctiveRing createDistinctiveRing(){
        return DistinctiveRing
                .builder()
                .ringPatterns(createRingPattern())
                .build();
    }

    private static SequentialRinging createSequentialRinging(Boolean max, Boolean isActivated, Boolean isAssigned){
        Map<String, String> sequentialRingingList = new TreeMap<>();
        sequentialRingingList.put("314-222-3548", "3");
        sequentialRingingList.put("8163888343", null);
        sequentialRingingList.put("8163888344", "5");

        if(max){
            sequentialRingingList.put("8163888345", "2");
            sequentialRingingList.put("8163888346", "3");
            sequentialRingingList.put("8163888347", "3");
            sequentialRingingList.put("8163888348", "3");
        }

        return SequentialRinging
                .builder()
                .sequentialRing(sequentialRingingList)
                .isActivated(isActivated)
                .isSubscribed(isAssigned)
                .build();
    }

    private static SimultaneousRing createSimultaneousRingFeature(Boolean maxSimultaneousRings, Boolean isAssigned, Boolean isActivated){
        List<String> puidNums = new ArrayList<>();
        puidNums.add("3142221231");
        puidNums.add("3142221232");
        puidNums.add("3142221233");
        puidNums.add("3142221234");
        puidNums.add("3142221235");
        puidNums.add("3142221236");
        puidNums.add("3142221237");
        puidNums.add("3142221238");
        puidNums.add("3142221239");

        if(maxSimultaneousRings){
            puidNums.add("3142221240");
        }

        return SimultaneousRing.builder()
                .isActivated(isActivated)
                .isSubscribed(isAssigned)
                .puidNums(puidNums)
                .build();
    }

    public static List<ProvisioningAttribute> createProvisioningAttributes(){
        List<ProvisioningAttribute> provisioningAttributes = new ArrayList<>();

        provisioningAttributes.add(createProvisioningAttributesSequentialRing());
        provisioningAttributes.add(createProvisioningAttributesSimultaneousRing());
        provisioningAttributes.add(createRemoteUser());
        provisioningAttributes.add(createPinService());
        provisioningAttributes.add(createCallTransfer());
        provisioningAttributes.add(createAutoAttendant());
        provisioningAttributes.add(createCallBlocking());
        provisioningAttributes.add(createCallBarring());
        provisioningAttributes.add(createWebPortal());
        provisioningAttributes.add(createProvisioningAttributesCallForwardBusy());
        provisioningAttributes.add(createProvisioningAttributesCallForwardUnconditional());
        provisioningAttributes.add(createProvisioningAttributesCallForwardNoAns());
        provisioningAttributes.add(createProvisioningAttributesSingleDigitSpeedDial());
        provisioningAttributes.add(createProvisioningAttributesCallTransfer());

        return provisioningAttributes;
    }

    private static ProvisioningAttribute createProvisioningAttributesSimultaneousRing(){
        List<CatalogObject> catalogObjects = new ArrayList<>();
        List<CatalogProperty> catalogProperties = new ArrayList<>();
        CatalogObject catalogObject = createCatalogObject("SeqRinging");

        ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
        provisioningAttribute.setName("BCP.ManagedLine_SimultaneousRing");
        provisioningAttribute.setType("feature");
        provisioningAttribute.setLabel("SimultaneousRinging");

        catalogProperties.add(createCatalogProperty("PerPuid", "default", "false"));

        catalogObject.setCatalogProperties(catalogProperties);

        catalogObjects.add(catalogObject);
        provisioningAttribute.setCatalogObj(catalogObjects);
        return provisioningAttribute;
    }

    private static ProvisioningAttribute createProvisioningAttributesSequentialRing(){
        List<CatalogObject> catalogObjects = new ArrayList<>();
        List<CatalogProperty> catalogProperties = new ArrayList<>();
        CatalogObject catalogObject = createCatalogObject("SeqRinging");

        ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
        provisioningAttribute.setName("BCP.ManagedLine_SequentialRing");
        provisioningAttribute.setType("feature");
        provisioningAttribute.setLabel("Sequential Ring");

        catalogProperties.add(createCatalogProperty("RingingList", "request", "null^null^true^true^30^false"));
        catalogProperties.add(createCatalogProperty("ContinueOnBusy", "default", "true"));
        catalogProperties.add(createCatalogProperty("ContinueOnNoAnswer", "default", "true"));
        catalogProperties.add(createCatalogProperty("DefaultAnswerTimeout", "default", "30"));
        catalogProperties.add(createCatalogProperty("PlayAnnouncement", "default", "false"));
        catalogProperties.add(createCatalogProperty("Send181Mode", "default", "TAS_181_NONE"));
        catalogProperties.add(createCatalogProperty("Activated", "request", ""));
        catalogProperties.add(createCatalogProperty("PublicUID", "request", ""));
        catalogProperties.add(createCatalogProperty("Assigned", "request", ""));
        catalogProperties.add(createCatalogProperty("PerPuid", "default", "false"));

        catalogObject.setCatalogProperties(catalogProperties);

        catalogObjects.add(catalogObject);
        provisioningAttribute.setCatalogObj(catalogObjects);
        return provisioningAttribute;
    }

    private static ProvisioningAttribute createRemoteUser(){
        List<CatalogObject> catalogObjects = new ArrayList<>();
        List<CatalogProperty> catalogProperties = new ArrayList<>();
        CatalogObject catalogObject = createCatalogObject("RemoteUser");

        ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
        provisioningAttribute.setName("BCP.ManagedLine_RemoteOffice");
        provisioningAttribute.setType("feature");
        provisioningAttribute.setLabel("Remote Office");

        catalogProperties.add(createCatalogProperty("CallsAlwd", "default", "true"));
        catalogProperties.add(createCatalogProperty("SubsequentAlwd", "default", "true"));
        catalogProperties.add(createCatalogProperty("PerPuid", "default", "true"));

        catalogObject.setCatalogProperties(catalogProperties);

        catalogObjects.add(catalogObject);
        provisioningAttribute.setCatalogObj(catalogObjects);
        return provisioningAttribute;
    }

    private static ProvisioningAttribute createPinService(){
        List<CatalogObject> catalogObjects = new ArrayList<>();
        List<CatalogProperty> catalogProperties = new ArrayList<>();
        CatalogObject catalogObject = createCatalogObject("PinService");

        ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
        provisioningAttribute.setName("BCP.ManagedLine_PinService");
        provisioningAttribute.setType("feature");
        provisioningAttribute.setLabel("Pin Service");

        catalogProperties.add(createCatalogProperty("PerPuid", "default", "false"));
        catalogProperties.add(createCatalogProperty("PinFrozen", "default", "false"));

        catalogObject.setCatalogProperties(catalogProperties);

        catalogObjects.add(catalogObject);
        provisioningAttribute.setCatalogObj(catalogObjects);
        return provisioningAttribute;
    }

    private static ProvisioningAttribute createCallTransfer(){
        List<CatalogObject> catalogObjects = new ArrayList<>();
        List<CatalogProperty> catalogProperties = new ArrayList<>();
        CatalogObject catalogObject = createCatalogObject("CallTransfer");

        ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
        provisioningAttribute.setName("BCP.ManagedLine_PersonalAnswering");
        provisioningAttribute.setType("feature");
        provisioningAttribute.setLabel("Personal Answering Service");

        catalogProperties.add(createCatalogProperty("Blind", "default", "true"));
        catalogProperties.add(createCatalogProperty("Activated", "default", "true"));
        catalogProperties.add(createCatalogProperty("PerPuid", "default", "false"));

        catalogObject.setCatalogProperties(catalogProperties);

        catalogObjects.add(catalogObject);
        provisioningAttribute.setCatalogObj(catalogObjects);
        return provisioningAttribute;
    }

    private static ProvisioningAttribute createAutoAttendant(){
        List<CatalogObject> catalogObjects = new ArrayList<>();
        List<CatalogProperty> catalogProperties = new ArrayList<>();
        CatalogObject catalogObject = createCatalogObject("AutoAttendant");

        ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
        provisioningAttribute.setName("BCP.ManagedLine_PersonalAnswering");
        provisioningAttribute.setType("feature");
        provisioningAttribute.setLabel("Personal Answering Service");

        catalogProperties.add(createCatalogProperty("AutoAttendantLevel", "default", "2"));
        catalogProperties.add(createCatalogProperty("PerPuid", "default", "false"));

        catalogObject.setCatalogProperties(catalogProperties);

        catalogObjects.add(catalogObject);
        provisioningAttribute.setCatalogObj(catalogObjects);
        return provisioningAttribute;
    }

    private static ProvisioningAttribute createCallBlocking(){
        List<CatalogObject> catalogObjects = new ArrayList<>();
        List<CatalogProperty> catalogProperties = new ArrayList<>();
        CatalogObject catalogObject = createCatalogObject("CallBlocking");

        ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
        provisioningAttribute.setName("BCP.ManagedLine_SuspendedLine");
        provisioningAttribute.setType("feature");
        provisioningAttribute.setLabel("Line Suspension");

        catalogProperties.add(createCatalogProperty("All", "default", "true"));
        catalogProperties.add(createCatalogProperty("UserCtrlAll", "default", "false"));
        catalogProperties.add(createCatalogProperty("Activated", "default", "true"));
        catalogProperties.add(createCatalogProperty("Assigned", "default", "true"));
        catalogProperties.add(createCatalogProperty("PerPuid", "default", "false"));

        catalogObject.setCatalogProperties(catalogProperties);

        catalogObjects.add(catalogObject);
        provisioningAttribute.setCatalogObj(catalogObjects);
        return provisioningAttribute;
    }

    private static ProvisioningAttribute createCallBarring(){
        List<CatalogObject> catalogObjects = new ArrayList<>();
        List<CatalogProperty> catalogProperties = new ArrayList<>();
        CatalogObject catalogObject = createCatalogObject("CallBarring");

        ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
        provisioningAttribute.setName("BCP.ManagedLine_SuspendedLine");
        provisioningAttribute.setType("feature");
        provisioningAttribute.setLabel("Line Suspension");

        catalogProperties.add(createCatalogProperty("CallBarringAll", "default", "true"));
        catalogProperties.add(createCatalogProperty("Assigned", "default", "true"));
        catalogProperties.add(createCatalogProperty("PerPuid", "default", "false"));

        catalogObject.setCatalogProperties(catalogProperties);

        catalogObjects.add(catalogObject);
        provisioningAttribute.setCatalogObj(catalogObjects);
        return provisioningAttribute;
    }

    private static ProvisioningAttribute createWebPortal(){
        List<CatalogObject> catalogObjects = new ArrayList<>();
        List<CatalogProperty> catalogProperties = new ArrayList<>();
        CatalogObject catalogObject = createCatalogObject("CallBarring");

        ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
        provisioningAttribute.setName("BCP.ManagedLine_Default");
        provisioningAttribute.setType("feature");
        provisioningAttribute.setLabel("To allow PCM User for Admin/Portal actions");

        catalogProperties.add(createCatalogProperty("Assigned", "default", "true"));

        catalogObject.setCatalogProperties(catalogProperties);

        catalogObjects.add(catalogObject);
        provisioningAttribute.setCatalogObj(catalogObjects);
        return provisioningAttribute;
    }

    private static CatalogObject createCatalogObject(String catalogName){
        CatalogObject catalogObject = new CatalogObject();
        catalogObject.setName(catalogName);
        return catalogObject;
    }

    private static CatalogProperty createCatalogProperty(String name, String source, String value){
        CatalogProperty catalogProperty = new CatalogProperty();
        catalogProperty.setName(name);
        catalogProperty.setSource(source);
        catalogProperty.setValue(value);
        return catalogProperty;
    }

    public static PlexViewRequestType createLoginRequest(String uuid, String userName, String password) {
        PlexViewRequestType plexViewRequest = PlexViewRequestType.Factory.newInstance();
        plexViewRequest.setCommand(OMCPCommands.ACT_USER);
        plexViewRequest.setRequestId(uuid);
        plexViewRequest.setUserName(userName);
        plexViewRequest.setPassWord(password);

        return plexViewRequest;
    }

    public static PlexViewResponseType createPlexViewResponseType(String failureCode, boolean logon, String status) {
        PlexViewResponseType plexViewResponseType = PlexViewResponseType.Factory.newInstance();
        plexViewResponseType.setSessionId(SESSION_ID);
        plexViewResponseType.setCommand(logon ? OMCPCommands.ACT_USER : OMCPCommands.CANC_USER);
        plexViewResponseType.setRequestId(CORRELATION_ID);
        plexViewResponseType.setStatus(com.alu.plexwebapi.api.ResponseStatus.Enum.forString(status));

        if (StringUtils.isNotEmpty(failureCode)){
            plexViewResponseType.setFailureCode(new BigInteger(String.valueOf(failureCode)));
            plexViewResponseType.setFailureReason(failureCode.equalsIgnoreCase(SessionRequestHelper.SESSION_INVALID) ? "Invalid session.": "Session timeout.");
        }

        return plexViewResponseType;
    }

    public static CreateDigitalPhoneResponse generateCreateDigitalPhoneResponse(){
        return CreateDigitalPhoneResponse
                .builder()
                .pcmSwitchName("TWC_PMC_ENG")
                .ctsSwitchName("cts01")
                .subscriberId("0160bd1ac699a58p")
                .build();
    }

    public static Map<String, String> createSessionMap(){
        Map<String, String> sessionMap = new HashMap<>();
        sessionMap.put("SessionId", MockObjectCreator.SESSION_ID);
        return sessionMap;
    }
    
	// Call Forward Busy methods
	public static PlexViewRequestType createPlexViewRequestTypeWithCallForwardBusy(String firstName, String lastName,
			String callServerPublicIds) {
		PlexViewRequestType plexViewRequestType = createPlexViewRequestType(firstName, lastName, callServerPublicIds,
				false);
		plexViewRequestType.setCallForwardingBusy(createCallForwardBusyV2());
		return plexViewRequestType;
	}

	private static NgfsCallforwardingbusyV2 createCallForwardBusyV2() {
		NgfsCallforwardingbusyV2 callForwardingBusy = NgfsCallforwardingbusyV2.Factory.newInstance();

		callForwardingBusy.setForwardToType(NgfsCallforwardingbusyV2.ForwardToType.FORWARD_TO_DN);
		callForwardingBusy.setForwardToDN("3142223548");
		callForwardingBusy.setForwardToDNAllowed(true);
		callForwardingBusy.setEditPermission(NgfsCallforwardingbusyV2.EditPermission.EDIT_FULL);
		callForwardingBusy.setForwardVoiceCalls(true);
		callForwardingBusy.setForwardDataCalls(true);
		callForwardingBusy.setPlayAnnouncement(false);
		callForwardingBusy.setActivated(true);
		callForwardingBusy.setPublicUID(PUBLIC_UID);
		callForwardingBusy.setAssigned(true);
		callForwardingBusy.setPerPuid(false);

		return callForwardingBusy;
	}

	public static BusinessClassPhone createBusinessClassPhoneWithCallForwardingBusy(Boolean isActivate,
			Boolean isAssigned, String forwardDN) {
		BusinessClassPhone bcp = createBusinessClassPhone(false, "J. Doe");
		bcp.setCallForwardBusy(createCallForwardBusy(isActivate, isAssigned, forwardDN));
		return bcp;
	}

	private static CallForwardBusy createCallForwardBusy(Boolean isActivate, Boolean isAssigned, String forwardDN) {
		return CallForwardBusy.builder().forwardDN(forwardDN).isActivated(isActivate).isSubscribed(isAssigned).build();
	}

	private static ProvisioningAttribute createProvisioningAttributesCallForwardBusy() {
		List<CatalogObject> catalogObjects = new ArrayList<>();
		List<CatalogProperty> catalogProperties = new ArrayList<>();
		CatalogObject catalogObject = createCatalogObject("CallForwardingBusy");

		ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
		provisioningAttribute.setName("BCP.ManagedLine_CFB");
		provisioningAttribute.setType("feature");
		provisioningAttribute.setLabel("Call Forward Busy");

		catalogProperties.add(createCatalogProperty("ForwardToType", "default", "FORWARD_TO_DN"));
		catalogProperties.add(createCatalogProperty("ForwardToDN", "default", ""));
		catalogProperties.add(createCatalogProperty("ForwardToDNAllowed", "default", "true"));
		catalogProperties.add(createCatalogProperty("EditPermission", "default", "EDIT_FULL"));
		catalogProperties.add(createCatalogProperty("ForwardVoiceCalls", "default", "true"));
		catalogProperties.add(createCatalogProperty("ForwardDataCalls", "default", "true"));
		catalogProperties.add(createCatalogProperty("PlayAnnouncement", "default", "false"));
		catalogProperties.add(createCatalogProperty("Activated", "default", "false"));
		catalogProperties.add(createCatalogProperty("PublicUID", "request", ""));
		catalogProperties.add(createCatalogProperty("Assigned", "request", ""));
		catalogProperties.add(createCatalogProperty("PerPuid", "default", "false"));

		catalogObject.setCatalogProperties(catalogProperties);

		catalogObjects.add(catalogObject);
		provisioningAttribute.setCatalogObj(catalogObjects);
		return provisioningAttribute;
	}

	// Call Forward Unconditional
	public static PlexViewRequestType createPlexViewRequestTypeWithCallForwardUnconditional(String firstName,
			String lastName, String callServerPublicIds) {
		PlexViewRequestType plexViewRequestType = createPlexViewRequestType(firstName, lastName, callServerPublicIds,
				false);
		plexViewRequestType.setCallForwardingVari(createCallForwardVariV2());
		return plexViewRequestType;
	}

	private static NgfsCallforwardingvariV2 createCallForwardVariV2() {
		NgfsCallforwardingvariV2 callForwardingVari = NgfsCallforwardingvariV2.Factory.newInstance();

		callForwardingVari.setForwardToType(NgfsCallforwardingvariV2.ForwardToType.FORWARD_TO_DN);
		callForwardingVari.setForwardToDN("3142223548");
		callForwardingVari.setPingRing(true);
		callForwardingVari.setEditPermission(NgfsCallforwardingvariV2.EditPermission.EDIT_FULL);
		callForwardingVari.setForwardVoiceCalls(true);
		callForwardingVari.setForwardDataCalls(true);
		callForwardingVari.setReceiveNotify(false);
		callForwardingVari.setPlayAnnouncement(false);
		callForwardingVari.setActivated(true);
		callForwardingVari.setPublicUID(PUBLIC_UID);
		callForwardingVari.setAssigned(true);
		callForwardingVari.setPerPuid(false);

		return callForwardingVari;
	}

	public static BusinessClassPhone createBusinessClassPhoneWithCallForwardingUnconditional(Boolean isActivate,
			Boolean isAssigned, String forwardDN) {
		BusinessClassPhone bcp = createBusinessClassPhone(false, "J. Doe");
		bcp.setCallForwardUnconditional(createCallForwardUnconditional(isActivate, isAssigned, forwardDN));
		return bcp;
	}

	private static CallForwardUnconditional createCallForwardUnconditional(Boolean isActivate, Boolean isAssigned,
			String forwardDN) {
		return CallForwardUnconditional.builder().forwardDN(forwardDN).isActivated(isActivate).isSubscribed(isAssigned)
				.build();
	}

	private static ProvisioningAttribute createProvisioningAttributesCallForwardUnconditional() {
		List<CatalogObject> catalogObjects = new ArrayList<>();
		List<CatalogProperty> catalogProperties = new ArrayList<>();
		CatalogObject catalogObject = createCatalogObject("CallForwardingVari");

		ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
		provisioningAttribute.setName("BCP.ManagedLine_CFU");
		provisioningAttribute.setType("feature");
		provisioningAttribute.setLabel("Call Forward Unconditional");

		catalogProperties.add(createCatalogProperty("ForwardToType", "default", "FORWARD_TO_DN"));
		catalogProperties.add(createCatalogProperty("ForwardToDN", "default", ""));
		catalogProperties.add(createCatalogProperty("PingRing", "default", "true"));
		catalogProperties.add(createCatalogProperty("EditPermission", "default", "EDIT_FULL"));
		catalogProperties.add(createCatalogProperty("ForwardVoiceCalls", "default", "true"));
		catalogProperties.add(createCatalogProperty("ForwardDataCalls", "default", "true"));
		catalogProperties.add(createCatalogProperty("ReceiveNotify", "default", "false"));
		catalogProperties.add(createCatalogProperty("PlayAnnouncement", "default", "false"));
		catalogProperties.add(createCatalogProperty("Activated", "default", "false"));
		catalogProperties.add(createCatalogProperty("PublicUID", "request", ""));
		catalogProperties.add(createCatalogProperty("Assigned", "request", ""));
		catalogProperties.add(createCatalogProperty("PerPuid", "default", "false"));

		catalogObject.setCatalogProperties(catalogProperties);

		catalogObjects.add(catalogObject);
		provisioningAttribute.setCatalogObj(catalogObjects);
		return provisioningAttribute;
	}

	// Call Forward No Answer
	public static PlexViewRequestType createPlexViewRequestTypeWithCallForwardNoAns(String firstName, String lastName,
			String callServerPublicIds) {
		PlexViewRequestType plexViewRequestType = createPlexViewRequestType(firstName, lastName, callServerPublicIds,
				false);
		plexViewRequestType.setCallForwardingNoAns(createCallForwardNoAnsV2());
		return plexViewRequestType;
	}

	private static NgfsCallforwardingnoansV2 createCallForwardNoAnsV2() {
		NgfsCallforwardingnoansV2 callForwardingNoAns = NgfsCallforwardingnoansV2.Factory.newInstance();

		callForwardingNoAns.setForwardToType(NgfsCallforwardingnoansV2.ForwardToType.FORWARD_TO_DN);
		callForwardingNoAns.setForwardToDN("3142223548");
		callForwardingNoAns.setForwardToDNAllowed(true);
		callForwardingNoAns.setNoAnswerTimeout(Short.parseShort("30"));
		callForwardingNoAns.setEditPermission(NgfsCallforwardingnoansV2.EditPermission.EDIT_FULL);
		callForwardingNoAns.setForwardVoiceCalls(true);
		callForwardingNoAns.setForwardDataCalls(true);
		callForwardingNoAns.setReceiveNotify(false);
		callForwardingNoAns.setPinRequired(false);
		callForwardingNoAns.setSend181Mode(NgfsCallforwardingnoansV2.Send181Mode.TAS_181_NONE);
		callForwardingNoAns.setActivated(true);
		callForwardingNoAns.setPublicUID(PUBLIC_UID);
		callForwardingNoAns.setAssigned(true);
		callForwardingNoAns.setPerPuid(false);

		return callForwardingNoAns;
	}

	public static BusinessClassPhone createBusinessClassPhoneWithCallForwardingNoAns(Boolean isActivate,
			Boolean isAssigned, String forwardDN, String numRings) {
		BusinessClassPhone bcp = createBusinessClassPhone(false, "J. Doe");
		bcp.setCallForwardNoAnswer(createCallForwardNoAns(isActivate, isAssigned, forwardDN, numRings));
		return bcp;
	}

	private static CallForwardNoAnswer createCallForwardNoAns(Boolean isActivate, Boolean isAssigned,
			String forwardDN, String numRings) {
		return CallForwardNoAnswer.builder().forwardDN(forwardDN).isActivated(isActivate).isSubscribed(isAssigned)
				.numRings(numRings).build();
	}

	private static ProvisioningAttribute createProvisioningAttributesCallForwardNoAns() {
		List<CatalogObject> catalogObjects = new ArrayList<>();
		List<CatalogProperty> catalogProperties = new ArrayList<>();
		CatalogObject catalogObject = createCatalogObject("CallForwardingNoAns");

		ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
		provisioningAttribute.setName("BCP.ManagedLine_CFNA");
		provisioningAttribute.setType("feature");
		provisioningAttribute.setLabel("Call Forward No Answer");

		catalogProperties.add(createCatalogProperty("ForwardToType", "default", "FORWARD_TO_DN"));
		catalogProperties.add(createCatalogProperty("ForwardToDN", "default", ""));
		catalogProperties.add(createCatalogProperty("ForwardToDNAllowed", "default", "true"));
		catalogProperties.add(createCatalogProperty("NoAnswerTimeout", "default", "30"));
		catalogProperties.add(createCatalogProperty("EditPermission", "default", "EDIT_FULL"));
		catalogProperties.add(createCatalogProperty("ForwardVoiceCalls", "default", "true"));
		catalogProperties.add(createCatalogProperty("ForwardDataCalls", "default", "true"));
		catalogProperties.add(createCatalogProperty("ReceiveNotify", "default", "false"));
		catalogProperties.add(createCatalogProperty("PinRequired", "default", "false"));
		catalogProperties.add(createCatalogProperty("Send181Mode", "default", "TAS_181_NONE"));
		catalogProperties.add(createCatalogProperty("Activated", "default", "false"));
		catalogProperties.add(createCatalogProperty("PublicUID", "request", ""));
		catalogProperties.add(createCatalogProperty("Assigned", "request", ""));
		catalogProperties.add(createCatalogProperty("PerPuid", "default", "false"));

		catalogObject.setCatalogProperties(catalogProperties);

		catalogObjects.add(catalogObject);
		provisioningAttribute.setCatalogObj(catalogObjects);
		return provisioningAttribute;
	}

	// single digit speed dial
	public static PlexViewRequestType createPlexViewRequestTypeWithSingleDigitSpeedDial(String firstName,
			String lastName, String callServerPublicIds) {
		PlexViewRequestType plexViewRequestType = createPlexViewRequestType(firstName, lastName, callServerPublicIds,
				false);
		plexViewRequestType.setOneDigitSpeedDial(createOneDigitSpeedDialV2());
		return plexViewRequestType;
	}

	private static NgfsOnedigitspeeddialV2 createOneDigitSpeedDialV2() {
		NgfsOnedigitspeeddialV2 oneDigitSpeedDial = NgfsOnedigitspeeddialV2.Factory.newInstance();

		oneDigitSpeedDial.setDialCodesEntries("2^^4^^6^^^");
		oneDigitSpeedDial.setDNEntries("8123234567^^^^8123234568^^^");
		oneDigitSpeedDial.setPublicUID(PUBLIC_UID);
		oneDigitSpeedDial.setAssigned(true);
		oneDigitSpeedDial.setPerPuid(false);

		return oneDigitSpeedDial;
	}

	public static BusinessClassPhone createBusinessClassPhoneWithSingleDigitSpeedDial(Boolean isAssigned) {
		BusinessClassPhone bcp = createBusinessClassPhone(false, "J. Doe");
		bcp.setOneDigitSpeedDialing(createSingleDigitSpeedDial(isAssigned));
		return bcp;
	}

	private static Map<String, String> createDigitalNumbersMap() {
		Map<String, String> createDigitalNumbersMap = new HashMap<>();
		createDigitalNumbersMap.put("2", "8123234567");
		createDigitalNumbersMap.put("4", "");
		createDigitalNumbersMap.put("6", "8123234568");
		return createDigitalNumbersMap;
	}

	private static OneDigitSpeedDialing createSingleDigitSpeedDial(Boolean isAssigned) {
		return OneDigitSpeedDialing.builder().digitalNumbers(createDigitalNumbersMap()).isSubscribed(isAssigned)
				.build();
	}

	private static ProvisioningAttribute createProvisioningAttributesSingleDigitSpeedDial() {
		List<CatalogObject> catalogObjects = new ArrayList<>();
		List<CatalogProperty> catalogProperties = new ArrayList<>();
		CatalogObject catalogObject = createCatalogObject("OneDigitSpeedDial");

		ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
		provisioningAttribute.setName("BCP.ManagedLine_SC1D");
		provisioningAttribute.setType("feature");
		provisioningAttribute.setLabel("One Digit Speed Dial");

		catalogProperties.add(createCatalogProperty("DialCodeEntries", "default", ""));
		catalogProperties.add(createCatalogProperty("DNEntries", "default", ""));
		catalogProperties.add(createCatalogProperty("PublicUID", "request", ""));
		catalogProperties.add(createCatalogProperty("Assigned", "request", ""));
		catalogProperties.add(createCatalogProperty("PerPuid", "default", "false"));

		catalogObject.setCatalogProperties(catalogProperties);

		catalogObjects.add(catalogObject);
		provisioningAttribute.setCatalogObj(catalogObjects);
		return provisioningAttribute;
	}

	// call transfer
	public static PlexViewRequestType createPlexViewRequestTypeWithCallTransfer(String firstName,
			String lastName, String callServerPublicIds) {
		PlexViewRequestType plexViewRequestType = createPlexViewRequestType(firstName, lastName, callServerPublicIds,
				false);
		plexViewRequestType.setCallTransfer(createCalltransferV2());
		return plexViewRequestType;
	}

	private static NgfsCalltransferV2 createCalltransferV2() {
		NgfsCalltransferV2 callTransfer = NgfsCalltransferV2.Factory.newInstance();

		callTransfer.setBlind(true);
		callTransfer.setConsultation(true);
		callTransfer.setLocalRefer(true);
		callTransfer.setCTRingbackTimer(0);
		callTransfer.setActivated(true);
		callTransfer.setPublicUID(PUBLIC_UID);
		callTransfer.setAssigned(true);
		callTransfer.setPerPuid(false);

		return callTransfer;
	}

	public static BusinessClassPhone createBusinessClassPhoneWithCallTransfer(Boolean isAssigned) {
		BusinessClassPhone bcp = createBusinessClassPhone(false, "J. Doe");
		bcp.setCallTransfer(createCallTransfer(isAssigned));
		return bcp;
	}

	private static CallTransfer createCallTransfer(Boolean isAssigned) {
		return CallTransfer.builder().isSubscribed(isAssigned).build();
	}

	private static ProvisioningAttribute createProvisioningAttributesCallTransfer() {
		List<CatalogObject> catalogObjects = new ArrayList<>();
		List<CatalogProperty> catalogProperties = new ArrayList<>();
		CatalogObject catalogObject = createCatalogObject("CallTransfer");

		ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
		provisioningAttribute.setName("BCP.ManagedLine_CallTransfer");
		provisioningAttribute.setType("feature");
		provisioningAttribute.setLabel("Call Transfer");

		catalogProperties.add(createCatalogProperty("Blind", "default", "true"));
		catalogProperties.add(createCatalogProperty("Consultation", "default", "true"));
		catalogProperties.add(createCatalogProperty("LocalRefer", "default", "true"));
		catalogProperties.add(createCatalogProperty("CTRingbackTimer", "default", "0"));
		catalogProperties.add(createCatalogProperty("Activated", "default", "true"));
		catalogProperties.add(createCatalogProperty("PublicUID", "request", ""));
		catalogProperties.add(createCatalogProperty("Assigned", "request", ""));
		catalogProperties.add(createCatalogProperty("PerPuid", "default", "false"));

		catalogObject.setCatalogProperties(catalogProperties);

		catalogObjects.add(catalogObject);
		provisioningAttribute.setCatalogObj(catalogObjects);
		return provisioningAttribute;
	}

    public static MockHttpServletResponse getHttpServletResponse() {
        return new MockHttpServletResponse();
    }

    public static BusinessClassPhone createBusinessClassPhoneDREF(){
        BusinessClassPhone bcp = createBusinessClassPhone(false,  "J. Doe");
        bcp.setDisconnectReferral(createDisconnectReferral());
        return bcp;
    }

}
