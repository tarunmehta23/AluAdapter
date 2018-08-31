package com.charter.provisioning.voice.commercial.alu.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateDigitalPhoneResponse {

    private String pcmSwitchName;

    private String ctsSwitchName;

    private String subscriberId;
}
