package com.charter.provisioning.voice.commercial.alu.model.response;

import lombok.Data;

@Data
public class CarrierSelection {

    private int intraLataTollCarrier;

    private int longDistanceCarrier;

    private int preSubscribedCarrier3;

    private int preSubscribedCarrier4;

    private int originatingLata;

    private String originatingLocalArea;

    private boolean ignoreUserCarrier;

    private String publicUID;

    private boolean assigned;

    private boolean perPuid;
}
