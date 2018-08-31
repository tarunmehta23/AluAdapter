package com.charter.provisioning.voice.commercial.alu.config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.util.MockObjectCreator;

@RunWith(MockitoJUnitRunner.class)
public class CTSSwitchConfigurationTest {

    @InjectMocks
    private CTSSwitchConfiguration ctsSwitchConfiguration;

    private final List<CTSConfiguration> ctsConfigurations = MockObjectCreator.createCTSConfigurations(true, false);

    @Before
    public void setup(){
        ctsSwitchConfiguration.setCtsConfigurations(ctsConfigurations);
    }

    @Test
    public void getCTSSwitchConfiguration_ValidSwitchName_Expects_CTSConfiguration() throws ProvisioningServiceException {
        CTSConfiguration ctsConfiguration = ctsSwitchConfiguration.getCTSSwitchConfiguration("cts00");

        assertThat(ctsConfiguration, is(ctsConfigurations.get(0)));
    }

    @Test
    public void getCTSSwitchConfiguration_InvalidSwitchName_Expects_CTSConfiguration() throws ProvisioningServiceException {
        try{
            ctsSwitchConfiguration.getCTSSwitchConfiguration("cts05");
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is(String.format("Invalid CTS switch name: %s", "cts05")));
        }
    }
}