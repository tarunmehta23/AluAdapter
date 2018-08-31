package com.charter.provisioning.voice.commercial.alu.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SimultaneousRing {

    private Boolean isActivated;

    private Boolean isSubscribed;

    private List<String> puidNums;

}
