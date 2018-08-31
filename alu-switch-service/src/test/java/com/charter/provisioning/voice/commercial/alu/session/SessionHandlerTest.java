package com.charter.provisioning.voice.commercial.alu.session;

import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.util.MockObjectCreator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionHandlerTest {

    @InjectMocks
    private SessionHandler sessionHandler;

    @Mock
    private SessionRequestHelper sessionHelper;

    @Before
    public void setup() throws Exception {
        ReflectionTestUtils.setField(sessionHandler, "omcpUserName" , MockObjectCreator.USER_NAME, String.class);
        ReflectionTestUtils.setField(sessionHandler, "omcpPassword", MockObjectCreator.PASSWORD, String.class);
    }

    @Test
    public void reestablishSession_ValidInput_ExpectsSessionId() throws Exception {
        when(sessionHelper.logoff(MockObjectCreator.CORRELATION_ID, null, new HashMap<>())).thenReturn(true);
        when(sessionHelper.getLoginSessionId(MockObjectCreator.CORRELATION_ID, MockObjectCreator.USER_NAME, MockObjectCreator.PASSWORD, new HashMap<>())).thenReturn(MockObjectCreator.SESSION_ID);

        String sessionId = sessionHandler.reestablishSession(MockObjectCreator.CORRELATION_ID);

        assertThat(sessionId, is(MockObjectCreator.SESSION_ID));
    }

    @Test
    public void reestablishSession_TerminateSessionError_ExpectsSessionId() throws Exception {
        when(sessionHelper.logoff(MockObjectCreator.CORRELATION_ID, null, new HashMap<>())).thenThrow(new ProvisioningServiceException("Exception logging off."));
        when(sessionHelper.getLoginSessionId(MockObjectCreator.CORRELATION_ID, MockObjectCreator.USER_NAME, MockObjectCreator.PASSWORD, new HashMap<>())).thenReturn(MockObjectCreator.SESSION_ID);

        String sessionId = sessionHandler.reestablishSession(MockObjectCreator.CORRELATION_ID);

        assertThat(sessionId, is(MockObjectCreator.SESSION_ID));
    }

    @Test
    public void logout_ValidRequest_ExpectsNoResponse() throws Exception {
        when(sessionHelper.logoff(MockObjectCreator.CORRELATION_ID, MockObjectCreator.SESSION_ID, new HashMap<>())).thenThrow(new ProvisioningServiceException("Exception logging off."));

        sessionHandler.logout();

        verify(sessionHelper, times(1)).logoff("OMC-P_LOGOUT_ON_ADAPTER_SHUTDOWN", null, new HashMap<>());
    }


    @Test
    public void terminateSession_LogoffError_ExpectsNoResponse() throws Exception {
        when(sessionHelper.logoff(MockObjectCreator.CORRELATION_ID, MockObjectCreator.SESSION_ID, new HashMap<>())).thenThrow(new ProvisioningServiceException("Exception logging off."));

        sessionHandler.terminateSession(MockObjectCreator.CORRELATION_ID, MockObjectCreator.SESSION_ID);

        verify(sessionHelper, times(1)).logoff(MockObjectCreator.CORRELATION_ID, MockObjectCreator.SESSION_ID, new HashMap<>());
    }

    @Test
    public void terminateSession_Successful_ExpectsNoResponse() throws Exception {
        when(sessionHelper.logoff(MockObjectCreator.CORRELATION_ID, null, new HashMap<>())).thenReturn(true);

        sessionHandler.terminateSession(MockObjectCreator.CORRELATION_ID, MockObjectCreator.SESSION_ID);

        verify(sessionHelper, times(1)).logoff(MockObjectCreator.CORRELATION_ID, MockObjectCreator.SESSION_ID, new HashMap<>());
    }

    @Test
    public void establishSession() throws Exception {
        ReflectionTestUtils.setField(sessionHandler, "sessionMap" , MockObjectCreator.createSessionMap(), Map.class);

        when(sessionHelper.logoff(MockObjectCreator.CORRELATION_ID, null, new HashMap<>())).thenReturn(true);
        when(sessionHelper.getLoginSessionId(MockObjectCreator.CORRELATION_ID, MockObjectCreator.USER_NAME, MockObjectCreator.PASSWORD, new HashMap<>())).thenReturn(MockObjectCreator.SESSION_ID);

        String sessionId = sessionHandler.establishSession(MockObjectCreator.CORRELATION_ID);

        assertThat(sessionId, is(MockObjectCreator.SESSION_ID));
    }

}