package com.charter.provisioning.voice.commercial.alu.config;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@XStreamAlias("catalog-object")
public class CatalogObject {
	@XStreamAsAttribute
	private String name;
	
	@XStreamImplicit(itemFieldName = "catalog-property")
	private List<CatalogProperty> catalogProperties;

}
