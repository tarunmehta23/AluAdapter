package com.charter.provisioning.voice.commercial.alu.config;

import com.alu.plexwebapi.api.NgfsAutoattendantV2;
import com.alu.plexwebapi.api.NgfsCallforwardingbusyV2;
import com.alu.plexwebapi.api.NgfsCallforwardingnoansV2;
import com.alu.plexwebapi.api.NgfsCallforwardingvariV2;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Data
@EqualsAndHashCode
@XStreamAlias("ServiceConfig")
public class ServiceConfig {

	@Autowired
	private CustomEnumConverter enumConverter;

	private final String SERVICE_CONFIG_DEFAULT = "Default";

	@XStreamImplicit(itemFieldName = "prov-attribute")
	private List<ProvisioningAttribute> provisioningAttributes;

	public void populateCatalogDefaultValues(String propertiesType, String featureName, String catalogName,
			Object featureSchemaObj) throws ProvisioningServiceException {

		String provAttribKey = propertiesType + "_" + featureName;
		ProvisioningAttribute provAttribute = getProvAttribute(provAttribKey);

        populateCatalogDefaultValues(catalogName, featureSchemaObj, provAttribute);
    }

	private ProvisioningAttribute getProvAttribute(String provAttribKey) throws ProvisioningServiceException {
		if (provisioningAttributes == null) {
			throw new ProvisioningServiceException("Service Configuration is null");
		}

		Optional<ProvisioningAttribute> prov = provisioningAttributes.stream()
				.filter(provAttr -> provAttr.getName().equalsIgnoreCase(provAttribKey)).findFirst();

		if (prov.isPresent()) {
			return prov.get();
		} else {
			throw new ProvisioningServiceException(String.format("Service Configuration not present for  %s", provAttribKey));
		}
	}

	private void populateCatalogDefaultValues(String catalogName, Object featureSchemaObj,
                                              ProvisioningAttribute provAttribute) {
		Optional<com.charter.provisioning.voice.commercial.alu.config.CatalogObject> catalogObject = getCatalogObject(
				catalogName, provAttribute);
		if (catalogObject.isPresent()) {
			Map<String, String> featureDefaultValues;
			featureDefaultValues = getDefaultValues(catalogObject.get());

			try {
				getCustomeBeanUtils().populate(featureSchemaObj, featureDefaultValues);
			} catch (Exception e) {
				log.info("Could not set Default values for catalogName=" + catalogName + " Reason: " + e.getMessage());
			}

		} else {
			log.error("No CatalogObject found for catalogName=" + catalogName + " in ServiceConfig Definition file");
		}

	}

	private Optional<CatalogObject> getCatalogObject(String catalogName, ProvisioningAttribute provAttribute) {
		List<CatalogObject> catalogObjectList = provAttribute.getCatalogObj();
		return catalogObjectList.stream().filter(catalog -> catalog.getName().equalsIgnoreCase(catalogName)).findFirst();
   	}

	private Map<String, String> getDefaultValues(CatalogObject catalogObject) {

		return catalogObject.getCatalogProperties().stream()
				.filter(catalogPropObj -> catalogPropObj.getSource().equalsIgnoreCase(SERVICE_CONFIG_DEFAULT))
				.collect(Collectors.toMap(map -> getBeanUtilPropertyName(map.getName()),
                        CatalogProperty::getValue));
	}

	private String getBeanUtilPropertyName(String propertyName) {
		if (StringUtils.isNotEmpty(propertyName)) {
			// if first char is upper case and 2nd char is lower case or number
			// then uncapitalize
			if (propertyName.length() > 1 && Character.isUpperCase(propertyName.charAt(0))
					&& (Character.isLowerCase(propertyName.charAt(1)) || Character.isDigit(propertyName.charAt(1)))) {
				propertyName = StringUtils.uncapitalize(propertyName);
			}
		}

		return propertyName;
	}

	private BeanUtilsBean getCustomeBeanUtils() {
		ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
		convertUtilsBean.register(enumConverter, com.alu.plexwebapi.api.NgfsSubpartyV2.CsdFlavor.Enum.class);
        convertUtilsBean.register(enumConverter, com.alu.plexwebapi.api.NgfsSeqringingV2.Send181Mode.Enum.class);
        convertUtilsBean.register(enumConverter, NgfsAutoattendantV2.AutoAttendantLevel.Enum.class);
        convertUtilsBean.register(enumConverter, NgfsCallforwardingbusyV2.EditPermission.Enum.class);
        convertUtilsBean.register(enumConverter, NgfsCallforwardingbusyV2.ForwardToType.Enum.class);
        convertUtilsBean.register(enumConverter, NgfsCallforwardingvariV2.EditPermission.Enum.class);
        convertUtilsBean.register(enumConverter, NgfsCallforwardingvariV2.ForwardToType.Enum.class);
        convertUtilsBean.register(enumConverter, NgfsCallforwardingnoansV2.EditPermission.Enum.class);
        convertUtilsBean.register(enumConverter, NgfsCallforwardingnoansV2.ForwardToType.Enum.class);
        convertUtilsBean.register(enumConverter, NgfsCallforwardingnoansV2.Send181Mode.Enum.class);

        return new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());
	}

}
