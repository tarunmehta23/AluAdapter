package com.charter.provisioning.voice.commercial.alu.helper;

import com.alu.plexwebapi.api.PlexViewRequestType;
import com.charter.provisioning.voice.commercial.alu.config.CTSSwitchConfiguration;
import com.charter.provisioning.voice.commercial.alu.config.OMCPCommands;
import com.charter.provisioning.voice.commercial.alu.config.ServiceConfig;
import com.charter.provisioning.voice.commercial.alu.enums.PlexViewResponseStatusCode;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.session.SessionRequestHelper;
import com.charter.provisioning.voice.commercial.alu.util.MockObjectCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DigitalPhoneRequestHelperTest {

    @Mock
    private ServiceConfig ctsServiceConfig;

    @Mock
    private AlaCarteFeaturesHelper alaCarteFeaturesHelper;

    @Mock
    private CTSSwitchConfiguration ctsSwitchConfig;

    @InjectMocks
    private DigitalPhoneRequestHelper digitalPhoneRequestHelper;

    @Test
    public void buildQueryCTSSubscriberRequest_ValidInput_ExpectsPlexViewRequestType() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestType(OMCPCommands.RTRV_NGFS_SUBSCRIBER_V2, null, "", MockObjectCreator.CORRELATION_ID);
        PlexViewRequestType plexViewRequestType = digitalPhoneRequestHelper.buildQueryCTSSubscriberRequest(MockObjectCreator.SUBSCRIBER_ID, MockObjectCreator.CORRELATION_ID);

        assertThat(plexViewRequestType.toString(), is(expectedResponse.toString()));
    }

    @Test
    public void verifyCTSSubscriber_ValidResponse_ExpectsTrue() throws Exception {
        boolean validCTSSubscriber = digitalPhoneRequestHelper.verifyCTSSubscriber(MockObjectCreator.createPlexViewResponseType(null, true, PlexViewResponseStatusCode.SUCCESS.statusCode()));

        assertThat(validCTSSubscriber, is(true));
    }

    @Test
    public void verifyCTSSubscriber_InvalidResponse_ExpectsFalse() throws Exception {
        boolean validCTSSubscriber = digitalPhoneRequestHelper.verifyCTSSubscriber(MockObjectCreator.createPlexViewResponseType(PlexViewResponseStatusCode.OBJECT_NOT_EXISTS.statusCode(), true, "FAILURE"));

        assertThat(validCTSSubscriber, is(false));
    }

    @Test
    public void verifyCTSSubscriber_InvalidResponse_ExpectsProvisioningServiceClientException() throws Exception {
        try {
            digitalPhoneRequestHelper.verifyCTSSubscriber(MockObjectCreator.createPlexViewResponseType(SessionRequestHelper.SESSION_INVALID, true, "FAILURE"));
            fail("test case should throw a ProvisioningServiceException.");
        } catch (ProvisioningServiceException pse) {
            assertThat(pse.getMessage(), is("CTS Query failed"));
            assertThat(pse.getHttpStatus(), is(HttpStatus.NOT_FOUND));
        }
    }

    @Test
    public void buildCreateCTSSubscriberWithDRefRequest() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestTypeDREF();

        when(ctsSwitchConfig.getCtsConfigurations()).thenReturn(MockObjectCreator.createCTSConfigurations(true, true));

        PlexViewRequestType plexViewRequestType = digitalPhoneRequestHelper.buildCreateCTSSubscriberWithDRefRequest(MockObjectCreator.createBusinessClassPhoneDREF(), MockObjectCreator.CORRELATION_ID);

        //assertThat(plexViewRequestType.toString(), is(expectedResponse.toString()));
    }

    @Test
    public void buildQueryCTSSubPartyRequest() throws Exception {
    }

    @Test
    public void buildDeleteCTSSubscriberRequest() throws Exception {
    }

    @Test
    public void buildCreateCTSSubscriberRequest() throws Exception {
    }

}