package com.charter.provisioning.voice.commercial.alu.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@XStreamAlias("catalog-property")
public class CatalogProperty {
	@XStreamAsAttribute
	private String name;

	@XStreamAsAttribute
	private String value;

	@XStreamAsAttribute
	private String source;
	
}
