package com.charter.provisioning.voice.commercial.alu.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DisconnectReferral {

    private String newDN;

    private String publicUID;

}