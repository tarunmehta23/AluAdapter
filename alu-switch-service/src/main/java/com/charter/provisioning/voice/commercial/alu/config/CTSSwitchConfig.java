package com.charter.provisioning.voice.commercial.alu.config;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thoughtworks.xstream.XStream;

@Configuration
public class CTSSwitchConfig {
	
	@Value("${config.ctsSwitchConfigName}")
	private String ctsSwitchConfigFileName;	
	 

    @Bean(name="ctsSwitchConfig")
    public CTSSwitchConfiguration getCTServiceConfiguration() throws FileNotFoundException {
        return getConfig();
    }

    private CTSSwitchConfiguration getConfig() throws FileNotFoundException {
        if(ctsSwitchConfigFileName == null){
            throw new FileNotFoundException(String.format("file not found for ctsSwitchConfig:%s", ctsSwitchConfigFileName));
        }

        FileReader fileReader = new FileReader(ctsSwitchConfigFileName);
        XStream xstream = new XStream();
        xstream.processAnnotations(CTSSwitchConfiguration.class);
        xstream.processAnnotations(CTSConfiguration.class);

        return (CTSSwitchConfiguration) xstream.fromXML(fileReader);
    }
}
