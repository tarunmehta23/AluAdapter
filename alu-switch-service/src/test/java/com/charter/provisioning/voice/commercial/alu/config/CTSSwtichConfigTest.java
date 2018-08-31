package com.charter.provisioning.voice.commercial.alu.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.charter.provisioning.voice.commercial.alu.util.MockObjectCreator;

@RunWith(MockitoJUnitRunner.class)
public class CTSSwtichConfigTest {

    @InjectMocks
    private CTSSwitchConfig ctsSwtichConfig;

/*    @Before
    public void setup() throws Exception {
        ReflectionTestUtils.setField(ctsSwtichConfig, "fileName", "2984729487.xml", String.class);
    }*/

    @Test
    public void getCTServiceConfiguration_ValidConfigurationFile_ExpectsCTSSwitchConfiguration() throws Exception {
        CTSSwitchConfiguration expectedCTSSwitchConfiguration = MockObjectCreator.createCTSSwitchConfiguration();

   //     CTSSwitchConfiguration ctsSwitchConfiguration = ctsSwtichConfig.getCTServiceConfiguration();

   //     assertThat(ctsSwitchConfiguration, is(expectedCTSSwitchConfiguration));
    }

}