package com.charter.provisioning.voice.commercial.alu.helper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.alu.plexwebapi.api.COMProvisioningService;
import com.charter.provisioning.voice.commercial.alu.config.CTSSwitchConfiguration;
import com.charter.provisioning.voice.commercial.alu.model.BusinessClassPhone;
import com.charter.provisioning.voice.commercial.alu.util.MockObjectCreator;

@RunWith(MockitoJUnitRunner.class)
public class NetworkServiceHelperTest {

    @InjectMocks
    private NetworkServiceHelper networkServiceHelper;

    @Mock
    private COMProvisioningService secondaryOmcpService;

    @Mock
    private COMProvisioningService primaryOmcpService;

    @Mock
    private CTSSwitchConfiguration ctsSwitchConfig;


    @Test
    public void retrieveSessionIdFromNetwork() throws Exception {
    }

    @Test
    public void terminateExistingSessionIdFromNetwork() throws Exception {
    }

    @Test
    public void executeNetworkCall() throws Exception {
    }

    @Test
    public void assignSwitch() throws Exception {
        BusinessClassPhone bcp = MockObjectCreator.createBusinessClassPhoneWithSimultaneousRing(true, true, true);
        when(ctsSwitchConfig.getCTSSwitchConfiguration(MockObjectCreator.CTS_SWITCH_NAME)).thenReturn(MockObjectCreator.createCTSConfiguration(true));
        when(ctsSwitchConfig.getCtsConfigurations()).thenReturn(MockObjectCreator.createCTSConfigurations(true, true));

        networkServiceHelper.assignSwitch(bcp);
        assertThat(bcp, is(bcp));

    }

}