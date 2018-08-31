package com.charter.provisioning.voice.commercial.alu.model.response;

import lombok.Data;

@Data
public class RemoteUser {

    private boolean callsAlwd;

    private boolean subsequentAlwd;

    private String publicUID;

    private boolean assigned;

    private boolean perPuid;
}
