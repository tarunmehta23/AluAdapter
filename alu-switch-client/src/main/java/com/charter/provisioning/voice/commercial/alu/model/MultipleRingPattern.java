package com.charter.provisioning.voice.commercial.alu.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class MultipleRingPattern {

    Map<String, RingPattern> ringPatterns;
}
