package com.charter.provisioning.voice.commercial.alu.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CallForwardUnconditional {
	
	private Boolean isActivated;

    private Boolean isSubscribed;
    
    private String forwardDN;

}
