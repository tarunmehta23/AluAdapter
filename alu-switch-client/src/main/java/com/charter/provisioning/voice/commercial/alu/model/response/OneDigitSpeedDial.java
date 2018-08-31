package com.charter.provisioning.voice.commercial.alu.model.response;

import lombok.Data;

@Data
public class OneDigitSpeedDial {

    private String dialCodesEntries;

    private String dnEntries;

    private String publicUID;

    private boolean assigned;

    private boolean perPuid;

}
