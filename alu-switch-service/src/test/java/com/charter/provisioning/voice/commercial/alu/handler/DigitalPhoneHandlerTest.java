package com.charter.provisioning.voice.commercial.alu.handler;

import com.alu.plexwebapi.api.PlexViewRequestType;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.helper.AlaCarteFeaturesHelper;
import com.charter.provisioning.voice.commercial.alu.helper.DigitalPhoneRequestHelper;
import com.charter.provisioning.voice.commercial.alu.helper.PersonalComManagerHelper;
import com.charter.provisioning.voice.commercial.alu.rollback.async.TransactionRollBackImpl;
import com.charter.provisioning.voice.commercial.alu.handler.DigitalPhoneHandler;
import com.charter.provisioning.voice.commercial.alu.handler.NetworkHandler;
import com.charter.provisioning.voice.commercial.alu.util.MockObjectCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DigitalPhoneHandlerTest {

    @InjectMocks
    private DigitalPhoneHandler digitalPhoneHandler;

    @Mock
    private DigitalPhoneRequestHelper digitalPhoneRequestHelper;

    @Mock
    private NetworkHandler networkService;

    @Mock
    private PersonalComManagerHelper personalComManagerHelper;

    @Mock
    private AlaCarteFeaturesHelper alaCarteFeaturesHelper;

    @Mock
    private TransactionRollBackImpl transactionRollBackImpl;

    @Mock
    private ObjectMapper objectMapper;

    @Before
    public void setup() throws Exception {
        ReflectionTestUtils.setField(digitalPhoneHandler, "subscriberIdMaxLength", "27", String.class);
        ReflectionTestUtils.setField(digitalPhoneHandler, "imsDomain", "ims.eng.rr.com", String.class);
    }


    @Test
    public void retrieveDigitalPhoneSubscriber_NonAlphaNumericSubscriberId_ExpectsProvisioningServiceException() throws Exception {
        try {
            digitalPhoneHandler.retrieveDigitalPhoneSubscriber("2lkjf--slkfj", MockObjectCreator.CORRELATION_ID);
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("Subscriber ID must be alphanumeric and between 1 and 27 characters."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void retrieveDigitalPhoneSubscriber_SubscriberIdExceedsMaxLength_ExpectsProvisioningServiceException() throws Exception {
        try {
            digitalPhoneHandler.retrieveDigitalPhoneSubscriber("1sl9iy8ikwe345fertsqwe34r5t6", MockObjectCreator.CORRELATION_ID);
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("Subscriber ID must be alphanumeric and between 1 and 27 characters."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void retrieveDigitalPhoneSubscriber_CTSSubscriberDoesNotExist_ExpectsProvisioningServiceException() throws Exception {
        PlexViewRequestType ctsQueryResponse = PlexViewRequestType.Factory.newInstance();

        when(digitalPhoneRequestHelper.buildQueryCTSSubscriberRequest(MockObjectCreator.SUBSCRIBER_ID, MockObjectCreator.CORRELATION_ID)).thenReturn(ctsQueryResponse);
        when(networkService.provisionNetwork(ctsQueryResponse)).thenReturn(null);
        when(digitalPhoneRequestHelper.verifyCTSSubscriber(null)).thenReturn(false);

        try {
            digitalPhoneHandler.retrieveDigitalPhoneSubscriber(MockObjectCreator.SUBSCRIBER_ID, MockObjectCreator.CORRELATION_ID);
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("Digital Phone Subscriber does not exists in ALU."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.NOT_FOUND));
        }
    }

    @Test
    public void retrieveDigitalPhoneSubscriber_DigitalPhoneDoesNotSubscriberExists_ExpectsProvisioningServiceException() throws Exception {
        PlexViewRequestType ctsQueryResponse = PlexViewRequestType.Factory.newInstance();

        when(digitalPhoneRequestHelper.buildQueryCTSSubscriberRequest(MockObjectCreator.SUBSCRIBER_ID, MockObjectCreator.CORRELATION_ID)).thenReturn(ctsQueryResponse);
        when(networkService.provisionNetwork(ctsQueryResponse)).thenThrow(new ProvisioningServiceException("not found on any FSDB."));

        try {
            digitalPhoneHandler.retrieveDigitalPhoneSubscriber(MockObjectCreator.SUBSCRIBER_ID, MockObjectCreator.CORRELATION_ID);
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("Digital Phone not found."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.NOT_FOUND));
        }
    }

    @Test
    public void retrieveDigitalPhoneSubscriber_NetworkException_ExpectsProvisioningServiceException() throws Exception {
        PlexViewRequestType ctsQueryResponse = PlexViewRequestType.Factory.newInstance();

        when(digitalPhoneRequestHelper.buildQueryCTSSubscriberRequest(MockObjectCreator.SUBSCRIBER_ID, MockObjectCreator.CORRELATION_ID)).thenReturn(ctsQueryResponse);
        when(networkService.provisionNetwork(ctsQueryResponse)).thenThrow(new ProvisioningServiceException("issues connecting to switch"));

        try {
            digitalPhoneHandler.retrieveDigitalPhoneSubscriber(MockObjectCreator.SUBSCRIBER_ID, MockObjectCreator.CORRELATION_ID);
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("Exception while retrieving Digital Phone."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

}