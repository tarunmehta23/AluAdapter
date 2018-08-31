package com.charter.provisioning.voice.commercial.alu.model.response;

import lombok.Data;

@Data
public class GetDigitalPhoneResponse {

    private String alternateOtasRealm;

    private String servingSwitch;

    private String servingFSDB;

    private String servingTas;

    private SubParty subParty;

    private CallTransfer callTransfer;

    private CarrierSelection carrierSelection;

    private RemoteUser remoteUser;

    private CallBarring callBarring;

    private DialingPlan dialingPlan;

    private SimultaneousRinging simultaneousRinging;

    private SeqRinging seqRinging;

    private OneDigitSpeedDial oneDigitSpeedDial;

    private CallForwardingNoAns callForwardingNoAns;

    private CallForwardingVari callForwardingVari;

    private CallingLineId callingLineId;

    private PinService pinService;

    private CallBlocking callBlocking;

    private CallForwardingBusy callForwardingBusy;

    private SetTZPath setTZPath;

}
