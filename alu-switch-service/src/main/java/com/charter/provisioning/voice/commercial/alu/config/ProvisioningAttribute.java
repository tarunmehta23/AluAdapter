package com.charter.provisioning.voice.commercial.alu.config;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@XStreamAlias("prov-attribute")
public class ProvisioningAttribute{
	@XStreamAsAttribute
	private String name;
    
	@XStreamAsAttribute
	private String type;

	@XStreamAsAttribute
	private String label;
	
	@XStreamImplicit(itemFieldName = "catalog-object")
	private List<CatalogObject> catalogObj;

}
