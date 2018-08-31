package com.charter.provisioning.voice.commercial.alu.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessClassPhone {

    private String phoneNumber;

    private String subscriberId;

    private String displayName; //cnamx`

    private String pps; //privacy?

    private String pic;
    
    private String timeZone;

    private String ipic;

    private String lpic;
    
    private DisconnectReferral disconnectReferral;
    
    private String basicCallLog;
    
    private String profile;
    
    private String pinService;
    
   
    private List<String> blockingCodes;
        /*<Value value="900" />
        <Value value="976" />
        <Value value="INT" />
        <Value value="0+" />
        <Value value="DA" />
        <Value value="CASUAL" />
        <Value value="411" />
        <Value value="NDA" />
        <Value value="IBR" />
        <Value value="OBR" />
        */
    private String dialPlanId;

    private DistinctiveRing distinctiveRingFeature;

    private SimultaneousRing simultaneousRingFeature;

    private SequentialRinging sequentialRingingFeature;

    private RemoteUser remoteUserFeature;

    private Boolean personalAttendantFeature;

    private MultipleRingPattern multipleRingPatternFeature;

    private Boolean suspendLineFeature;

    private String switchName;
    
    private CallForwardBusy callForwardBusy;
    
    private CallForwardUnconditional callForwardUnconditional;
    
    private CallTransfer callTransfer;
    
    private CallForwardNoAnswer callForwardNoAnswer;
    
    private OneDigitSpeedDialing oneDigitSpeedDialing;

}