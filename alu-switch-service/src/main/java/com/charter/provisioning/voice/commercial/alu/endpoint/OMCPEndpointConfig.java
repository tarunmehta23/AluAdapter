package com.charter.provisioning.voice.commercial.alu.endpoint;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis2.transport.http.HTTPConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alu.plexwebapi.api.COMProvisioningServiceStub;
import com.charter.health.model.ServiceHealthConfig;
import com.charter.provisioning.voice.commercial.alu.config.CatalogObject;
import com.charter.provisioning.voice.commercial.alu.config.CatalogProperty;
import com.charter.provisioning.voice.commercial.alu.config.ProvisioningAttribute;
import com.charter.provisioning.voice.commercial.alu.config.ServiceConfig;
import com.thoughtworks.xstream.XStream;

@Configuration
public class OMCPEndpointConfig {
	
    @Value("${config.ctsServiceConfigName}")
    private String ctsServiceConfigFileName;	
    
    @Value("${config.pcmWebAPIServiceConfigName}")
    private String pcmServiceConfigFileName;	

    @Value("${OMCPSwitchConfiguration.PrimaryEndpointURL}")
    private String ctsPrimaryUrl;

    @Value("${OMCPSwitchConfiguration.SecondaryEndpointURL}")
    private String ctsSecondaryUrl;

    @Value("${OMCPSwitchConfiguration.UserId}")
    private String omcpUserName;

    @Value("${OMCPSwitchConfiguration.Password}")
    private String omcpPassword;
    
    @Value("${OMCPSwitchConfiguration.PlxTimeout}")
    private String plxTimeOut;
    
    @Value("${OMCPSwitchConfiguration.RetryWaitTime}")
    private String retryInterval;
    

    @Value("${OMCPSwitchConfiguration.ImsDomain}")
    private String imsDomain;


    @Bean(name = "primaryOmcpService")
    public COMProvisioningServiceStub comPrimaryProvisioningService() throws Exception {
        return  getCOMProvisioningServiceStub(ctsPrimaryUrl);
    }

    @Bean(name = "runtimeStub")
    public COMProvisioningServiceStub comRuntimeProvisioningService() throws Exception {
        return  getCOMProvisioningServiceStub(ctsPrimaryUrl);
    }

    @Bean(name = "secondaryOmcpService")
    public COMProvisioningServiceStub comSecondaryProvisioningService() throws Exception {
        return  getCOMProvisioningServiceStub(ctsSecondaryUrl);
    }


    @Bean(name = "ctsServiceConfig")
    public ServiceConfig getCTServiceConfiguration() throws FileNotFoundException {
        return getConfig(ctsServiceConfigFileName);

    }
    
    private COMProvisioningServiceStub getCOMProvisioningServiceStub(String targetEndpoint) throws Exception {
    	COMProvisioningServiceStub provisioningstub= new COMProvisioningServiceStub(targetEndpoint);
    	provisioningstub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, (new Integer(plxTimeOut)*1000)); 
    	return provisioningstub;    	
    }

    @Bean(name = "pcmFeatureConfig")
    public ServiceConfig getPCMFeatureConfiguration() throws FileNotFoundException {
        return getConfig(pcmServiceConfigFileName);
    }


    @Bean
    public Map<String, ServiceHealthConfig> dependentServiceMap() {
        Map<String, ServiceHealthConfig> map = new HashMap<>();
        map.put("OMCP ", new ServiceHealthConfig(ctsPrimaryUrl, omcpUserName, omcpPassword));
        return map;
    }

    private ServiceConfig getConfig(String fileName) throws FileNotFoundException {
          if(fileName == null){
            throw new FileNotFoundException(String.format("file not found for OMCPEndpointConfig:%s", fileName));
        }

        FileReader fileReader = new FileReader(fileName);
        XStream xstream = new XStream();
        xstream.processAnnotations(ServiceConfig.class);
        xstream.processAnnotations(ProvisioningAttribute.class);
        xstream.processAnnotations(CatalogObject.class);
        xstream.processAnnotations(CatalogProperty.class);
        return (ServiceConfig) xstream.fromXML(fileReader);
    }

}