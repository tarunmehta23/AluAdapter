package com.charter.provisioning.voice.commercial.alu.model;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SequentialRinging {

    private Boolean isActivated;

    private Boolean isSubscribed;
	
	private Map<String, String> sequentialRing;

}
