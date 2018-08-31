package com.charter.provisioning.voice.commercial.alu.model.response;

import lombok.Data;

@Data
public class PinService {

    private String pin;

    private boolean assigned;

    private String publicUID;

    private boolean perPuid;

    private boolean pinFrozen;

}
