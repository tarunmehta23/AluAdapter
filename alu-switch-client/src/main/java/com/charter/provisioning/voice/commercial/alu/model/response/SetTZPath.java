package com.charter.provisioning.voice.commercial.alu.model.response;

import lombok.Builder;
import lombok.Data;

@Data
public class SetTZPath {

    private String tzPath;

    private boolean assigned;

    private boolean perPuid;

    private String publicUID;

}
