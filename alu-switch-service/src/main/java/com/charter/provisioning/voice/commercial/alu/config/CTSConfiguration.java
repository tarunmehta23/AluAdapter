package com.charter.provisioning.voice.commercial.alu.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@Builder
@XStreamAlias("CTSConfiguration")
public class CTSConfiguration {

    @XStreamAsAttribute
    private int id;

    private String switchName;

    private String fsdbName;

    private String topologyId;

    private String status;

    private String provisionControllers;
}
