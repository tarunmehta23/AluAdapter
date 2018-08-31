package com.charter.provisioning.voice.commercial.alu.config;

import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.List;

@Data
@XStreamAlias("CTSSwitchConfiguration")
public class CTSSwitchConfiguration {

    @XStreamImplicit(itemFieldName = "CTSConfiguration")
    private List<CTSConfiguration> ctsConfigurations;

    public CTSConfiguration getCTSSwitchConfiguration(String switchName) throws ProvisioningServiceException {
        for(CTSConfiguration ctsConfiguration: ctsConfigurations){
            if(ctsConfiguration.getSwitchName().equalsIgnoreCase(switchName)){
                return ctsConfiguration;
            }
        }

        throw new ProvisioningServiceException(String.format("Invalid CTS switch name: %s", switchName));
    }

}
