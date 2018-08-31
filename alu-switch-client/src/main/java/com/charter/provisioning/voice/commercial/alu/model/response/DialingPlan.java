package com.charter.provisioning.voice.commercial.alu.model.response;

import lombok.Data;

@Data
public class DialingPlan {

    private String prefixandFeatureCode;

    private String e164NormAndCodeConv;

    private String callBarringLocal;

    private String esrn1;

    private String esrn2;

    private String esrn3;

    private String esrn4;

    private String esrn5;

    private String privateDialingPlan;

    private String publicUID;

    private boolean assigned;

    private boolean perPuid;

}
