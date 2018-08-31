package com.charter.provisioning.voice.commercial.alu.helper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.alu.plexwebapi.api.PlexViewRequestType;
import com.charter.provisioning.voice.commercial.alu.config.CTSSwitchConfiguration;
import com.charter.provisioning.voice.commercial.alu.util.MockObjectCreator;


@RunWith(MockitoJUnitRunner.class)
public class PersonalComManagerHelperTest {

    @InjectMocks
    private PersonalComManagerHelper personalComManagerHelper;

    @Mock
    private CTSSwitchConfiguration ctsSwitchConfig;

    @Before
    public void setup() throws Exception {
        ReflectionTestUtils.setField(personalComManagerHelper, "enterpriseId", "TWC", String.class);
        ReflectionTestUtils.setField(personalComManagerHelper, "pcmSwitchName", "TWC_PCM_ENG", String.class);
        ReflectionTestUtils.setField(personalComManagerHelper, "password", "default", String.class);
        ReflectionTestUtils.setField(personalComManagerHelper, "partitionedId", "3", String.class);
        ReflectionTestUtils.setField(personalComManagerHelper, "templateId", "default", String.class);
    }

    @Test
    public void buildCreatePCMSubscriberRequest_ValidInput_ExpectsSuccessfulResponse() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestType("J.", "Doe", "+17031111111^+17032221111", false);
        when(ctsSwitchConfig.getCTSSwitchConfiguration(MockObjectCreator.CTS_SWITCH_NAME)).thenReturn(MockObjectCreator.createCTSConfiguration(true));

        PlexViewRequestType response = personalComManagerHelper.buildCreatePCMSubscriberRequest(MockObjectCreator.createBusinessClassPhone(true,  "J. Doe"), MockObjectCreator.CORRELATION_ID);

        assertThat(expectedResponse.toString(), is(response.toString()));
    }

    @Test
    public void buildCreatePCMSubscriberRequest_NoDisplayName_ExpectsSuccessfulResponseNoFirstNameLastName() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestType(null, null, "+17031111111^+17032221111", false);
        when(ctsSwitchConfig.getCTSSwitchConfiguration(MockObjectCreator.CTS_SWITCH_NAME)).thenReturn(MockObjectCreator.createCTSConfiguration(true));

        PlexViewRequestType response = personalComManagerHelper.buildCreatePCMSubscriberRequest(MockObjectCreator.createBusinessClassPhone(true, null), MockObjectCreator.CORRELATION_ID);

        assertThat(expectedResponse.toString(), is(response.toString()));
    }

    @Test
    public void buildCreatePCMSubscriberRequest_NoLastName_ExpectsSuccessfulResponseFirstNameAsLastName() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestType("J.", "J.", "+1314-222-3548", false);
        when(ctsSwitchConfig.getCTSSwitchConfiguration(MockObjectCreator.CTS_SWITCH_NAME)).thenReturn(MockObjectCreator.createCTSConfiguration(true));

        PlexViewRequestType response = personalComManagerHelper.buildCreatePCMSubscriberRequest(MockObjectCreator.createBusinessClassPhone(false,  "J. "), MockObjectCreator.CORRELATION_ID);

        assertThat(expectedResponse.toString(), is(response.toString()));
    }

    @Test
    public void buildCreatePCMSubscriberRequest_NoDistinctiveRingFeatures_ExpectsSuccessfulResponseFirstNameAsLastName() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestType("J.", "Doe", "+1314-222-3548", false);
        when(ctsSwitchConfig.getCTSSwitchConfiguration(MockObjectCreator.CTS_SWITCH_NAME)).thenReturn(MockObjectCreator.createCTSConfiguration(true));

        PlexViewRequestType response = personalComManagerHelper.buildCreatePCMSubscriberRequest(MockObjectCreator.createBusinessClassPhone(false,   "J. Doe"), MockObjectCreator.CORRELATION_ID);

        assertThat(expectedResponse.toString(), is(response.toString()));
    }
}