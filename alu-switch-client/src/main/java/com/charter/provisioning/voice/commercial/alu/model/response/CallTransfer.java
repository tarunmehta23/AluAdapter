package com.charter.provisioning.voice.commercial.alu.model.response;

import lombok.Data;

@Data
public class CallTransfer {

private boolean blind;

    private boolean consultation;

    private boolean localRefer;

    private int ctRingBackTimer;

    private boolean activated;

    private String publicUID;

    private boolean assigned;

    private boolean perPuid;
}
