package com.charter.provisioning.voice.commercial.alu.session;

import com.alu.plexwebapi.api.PlexViewRequestType;
import com.charter.provisioning.voice.commercial.alu.exceptions.LogonException;
import com.charter.provisioning.voice.commercial.alu.exceptions.OMCPSessionInvalidOrSessionTimeoutException;
import com.charter.provisioning.voice.commercial.alu.helper.NetworkServiceHelper;
import com.charter.provisioning.voice.commercial.alu.util.MockObjectCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionRequestHelperTest {

    @InjectMocks
    private SessionRequestHelper sessionRequestHelper;

    @Mock
    private NetworkServiceHelper networkServiceHelper;

    @Test
    public void getLoginSessionId_ValidInput_ExpectsSessionId() throws Exception {
        when(networkServiceHelper.retrieveSessionIdFromNetwork(any(PlexViewRequestType.class), any(Map.class))).thenReturn(MockObjectCreator.createPlexViewResponseType(null, true, "SUCCESS"));

        String sessionId = sessionRequestHelper.getLoginSessionId(MockObjectCreator.CORRELATION_ID, MockObjectCreator.USER_NAME, MockObjectCreator.PASSWORD, null);

        assertThat(sessionId, is(MockObjectCreator.SESSION_ID));
    }

    @Test
    public void getLoginSessionId_FailureCodes_ExpectsLogonException() throws Exception {
        when(networkServiceHelper.retrieveSessionIdFromNetwork(any(PlexViewRequestType.class), any(Map.class))).thenReturn(MockObjectCreator.createPlexViewResponseType(SessionRequestHelper.SESSION_INVALID, true, "SUCCESS"));

        try {
            sessionRequestHelper.getLoginSessionId(MockObjectCreator.CORRELATION_ID, MockObjectCreator.USER_NAME, MockObjectCreator.PASSWORD, null);
            fail("test case should throw a LogonException.");
        }
        catch (LogonException le){
            assertThat(le.getMessage(), is("30001=Invalid session."));
        }
    }

    @Test
    public void logoff_SessionIdPopulated_ExpectSuccessLogoff() throws Exception {
        when(networkServiceHelper.terminateExistingSessionIdFromNetwork(any(PlexViewRequestType.class), any(Map.class))).thenReturn(MockObjectCreator.createPlexViewResponseType(null, false, "SUCCESS"));

        boolean logoffSuccess = sessionRequestHelper.logoff(MockObjectCreator.CORRELATION_ID, MockObjectCreator.SESSION_ID, null);

        assertThat(logoffSuccess, is(true));
    }

    @Test
    public void logoff_SessionIdNotPopulated_ExpectSuccessLogoff() throws Exception {
        when(networkServiceHelper.terminateExistingSessionIdFromNetwork(any(PlexViewRequestType.class), any(Map.class))).thenReturn(MockObjectCreator.createPlexViewResponseType(null, false, "SUCCESS"));

        boolean logoffSuccess = sessionRequestHelper.logoff(MockObjectCreator.CORRELATION_ID, null, null);

        assertThat(logoffSuccess, is(true));
    }

    @Test
    public void validate_SessionInvalid_ExpectsOMCPSessionInvalidOrSessionTimeoutException() throws Exception {
        try {
            sessionRequestHelper.validate(MockObjectCreator.createPlexViewResponseType(SessionRequestHelper.SESSION_INVALID, true, "SUCCESS"));
            fail("test case should throw a LogonException.");
        }
        catch (OMCPSessionInvalidOrSessionTimeoutException ex){
            assertThat(ex.getMessage(), is("30001=Invalid session."));
        }
    }

    @Test
    public void validate_SessionTimedOut_ExpectsOMCPSessionInvalidOrSessionTimeoutException() throws Exception {
        try {
            sessionRequestHelper.validate(MockObjectCreator.createPlexViewResponseType(SessionRequestHelper.SESSION_TIMED_OUT, true, "SUCCESS"));
            fail("test case should throw a LogonException.");
        }
        catch (OMCPSessionInvalidOrSessionTimeoutException ex){
            assertThat(ex.getMessage(), is("30002=Session timeout."));
        }
    }

}