package com.charter.provisioning.voice.commercial.alu.model.response;

import lombok.Data;

@Data
public class CallingLineId {

    private String callingLineIdRestriction;

    private boolean clirEditAllowed;

    private boolean callingLineIdPresentation;

    private boolean callingNamePresentation;

    private boolean restrictionOverride;

    private boolean connectedLinePresentation;

    private boolean connectedLineRestriction;

    private boolean connectedLineRestrictionOverride;

    private int callingNumScreen;

    private int connectedNumScreen;

    private boolean pdpExtensionDisplay;

    private boolean colrEditAllowed;

    private String origLineIdRestrictionLevel;

    private boolean oipEditAllowed;

    private boolean blockPerCallOverride;

    private boolean suppressCLIPonCallWaiting;

    private String publicUID;

    private boolean assigned;

    private boolean perPuid;

    private String queCalNameSer;

    private boolean typ2CanSer;

    private int calNameSerUrl;

    private boolean companyNameQuery;

    private boolean origCallingNameQuery;

}
