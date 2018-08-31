package com.charter.provisioning.voice.commercial.alu.handler;

import com.alu.plexwebapi.api.PlexViewRequestType;
import com.alu.plexwebapi.api.PlexViewResponseType;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.helper.NetworkServiceHelper;
import com.charter.provisioning.voice.commercial.alu.handler.NetworkHandler;
import com.charter.provisioning.voice.commercial.alu.session.SessionHandler;
import com.charter.provisioning.voice.commercial.alu.session.SessionRequestHelper;
import com.charter.provisioning.voice.commercial.alu.util.MockObjectCreator;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NetworkHandlerTest {

    @InjectMocks
    private NetworkHandler networkHandler;

    @Mock
    private NetworkServiceHelper networkServiceHelper;

    @Mock
    private SessionHandler sessionHandler;

    @Mock
    private SessionRequestHelper sessionRequestHelper;


    @Test
    public void provisionNetwork_EstablishedSessionValidResponse_ExpectsSuccessfulResponse() throws Exception {
        PlexViewResponseType expectedPlexViewResponseType = MockObjectCreator.createPlexViewResponseType(null, false, "SUCCESS");
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("john", "doe", "8163888341", false);

        when(sessionHandler.getSessionMap()).thenReturn(MockObjectCreator.createSessionMap());
        when(networkServiceHelper.executeNetworkCall(plexViewRequestType, sessionHandler.getSessionMap())).thenReturn(expectedPlexViewResponseType);
        when(sessionHandler.getSessionHelper()).thenReturn(sessionRequestHelper);

        PlexViewResponseType plexViewResponseType = networkHandler.provisionNetwork(plexViewRequestType);

        assertThat(plexViewResponseType, is(expectedPlexViewResponseType));
    }

    @Test
    public void provisionNetwork_UnestablishedSessionValidResponse_ExpectsSuccessfulResponse() throws Exception {
        PlexViewResponseType expectedPlexViewResponseType = MockObjectCreator.createPlexViewResponseType(null, false, "SUCCESS");
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("john", "doe", "8163888341", false);

        when(sessionHandler.getSessionMap()).thenReturn(new HashMap<>());
        when(networkServiceHelper.executeNetworkCall(plexViewRequestType, sessionHandler.getSessionMap())).thenReturn(expectedPlexViewResponseType);
        when(sessionHandler.getSessionHelper()).thenReturn(sessionRequestHelper);

        PlexViewResponseType plexViewResponseType = networkHandler.provisionNetwork(plexViewRequestType);

        assertThat(plexViewResponseType, is(expectedPlexViewResponseType));
    }

    @Test
    @Ignore
    public void provisionNetwork_OMCPSessionInvalidOrSessionTimeoutException_ExpectsResubmitRequest() throws Exception {
        SessionRequestHelper sessionRequestHelper = new SessionRequestHelper(networkServiceHelper);
        SessionHandler sessionHandler = new SessionHandler(sessionRequestHelper);
        sessionHandler.setSessionMap(MockObjectCreator.createSessionMap());
        networkHandler = new NetworkHandler(networkServiceHelper, sessionHandler);

        PlexViewResponseType expectedPlexViewResponseType = MockObjectCreator.createPlexViewResponseType(null, false, "SUCCESS");
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("john", "doe", "8163888341", false);

        //when(networkServiceHelper.executeNetworkCall(plexViewRequestType, sessionHandler.getSessionMap())).thenReturn(expectedPlexViewResponseType);
        //doNothing().when(sessionHandler).voidSession();
        //when(sessionHandler.establishSession(MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.SESSION_ID);
        //when(sessionHandler.getSessionHelper().validate(expectedPlexViewResponseType)).thenThrow(new OMCPSessionInvalidOrSessionTimeoutException());
        //when(networkServiceHelper.executeNetworkCall(plexViewRequestType, MockObjectCreator.createSessionMap())).thenReturn(expectedPlexViewResponseType);

        PlexViewResponseType plexViewResponseType = networkHandler.provisionNetwork(plexViewRequestType);

        assertThat(plexViewResponseType, is(expectedPlexViewResponseType));
    }

    @Test
    public void provisionNetwork_ResponseWithErrorCodes_ExpectsProvisioningServiceException() throws Exception {
        PlexViewResponseType expectedPlexViewResponseType = MockObjectCreator.createPlexViewResponseType(SessionRequestHelper.SESSION_INVALID, true, "SUCCESS");
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("john", "doe", "8163888341", false);

        when(sessionHandler.getSessionMap()).thenReturn(MockObjectCreator.createSessionMap());
        when(networkServiceHelper.executeNetworkCall(plexViewRequestType, sessionHandler.getSessionMap())).thenReturn(expectedPlexViewResponseType);
        when(sessionHandler.getSessionHelper()).thenReturn(sessionRequestHelper);

        try {
            networkHandler.provisionNetwork(plexViewRequestType);
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch (ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("30001=Invalid session."));
        }
    }
}