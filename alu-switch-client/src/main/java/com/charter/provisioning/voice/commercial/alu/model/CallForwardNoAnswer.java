package com.charter.provisioning.voice.commercial.alu.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CallForwardNoAnswer {
	
	private Boolean isActivated;

    private Boolean isSubscribed;
    
    private String forwardDN;
    
    private String numRings;

}
