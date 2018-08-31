package com.charter.provisioning.voice.commercial.alu.model;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OneDigitSpeedDialing {

    private Boolean isSubscribed;
	
    // This map will contain dial code as key and digital number as value.
	private Map<String, String> digitalNumbers;

}
