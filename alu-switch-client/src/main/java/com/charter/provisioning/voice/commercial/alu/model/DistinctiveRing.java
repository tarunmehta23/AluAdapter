package com.charter.provisioning.voice.commercial.alu.model;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DistinctiveRing {
	private Map<RingPattern, String> ringPatterns;
}
