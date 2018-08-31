package com.charter.provisioning.voice.commercial.alu.helper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.alu.plexwebapi.api.PlexViewRequestType;
import com.charter.provisioning.voice.commercial.alu.config.CustomEnumConverter;
import com.charter.provisioning.voice.commercial.alu.config.ServiceConfig;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.util.MockObjectCreator;

@RunWith(MockitoJUnitRunner.class)
public class AlaCarteFeaturesHelperTest {

    @InjectMocks
    private AlaCarteFeaturesHelper alaCarteFeaturesHelper;

    private ServiceConfig ctsServiceConfig = new ServiceConfig();

    private CustomEnumConverter enumConverter = new CustomEnumConverter();

    @Before
    public void setup() throws Exception {
        ctsServiceConfig.setProvisioningAttributes(MockObjectCreator.createProvisioningAttributes());
        ctsServiceConfig.setEnumConverter(enumConverter);

        ReflectionTestUtils.setField(alaCarteFeaturesHelper, "maxSequentialRings" , 6, int.class);
        ReflectionTestUtils.setField(alaCarteFeaturesHelper, "maxSimultaneousRings", 9, int.class);
        ReflectionTestUtils.setField(alaCarteFeaturesHelper, "maxMultipleRings", 6, int.class);
        ReflectionTestUtils.setField(alaCarteFeaturesHelper, "ctsServiceConfig", ctsServiceConfig, ServiceConfig.class);
        ReflectionTestUtils.setField(alaCarteFeaturesHelper, "comInterfaceEnabled", "false", String.class);
        ReflectionTestUtils.setField(alaCarteFeaturesHelper, "maxNumberOfRings", 10, int.class);
    }

    @Test
    public void populateAlaCarteFeatures_ValidInputSequentialRinging_ExpectsValidResponse() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestTypeWithSequentialRing("J.", "Doe", "+1314-222-3548");
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("J.", "Doe", "+1314-222-3548", false);

        alaCarteFeaturesHelper.populateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSequentialRinging(false, true, true), plexViewRequestType, MockObjectCreator.CORRELATION_ID);

        assertThat(plexViewRequestType.toString(), is(expectedResponse.toString()));
    }

    @Test
    public void validateAlaCarteFeatures_MaxSequentialRingsExceeded_ExpectsProvisioningServiceException() throws Exception {
        try {
            alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSequentialRinging(true, true, true));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch (ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("Sequential Ring: More than allowed DN's provided in the request, allowed 6, actual 7."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void validateAlaCarteFeatures_SequentialRingsWithIsActivatedNull_ExpectsProvisioningServiceException() throws Exception {
        try {
            alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSequentialRinging(false, null, true));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch (ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("SequentialRing: activated was not populated, requires a value of true or false."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void validateAlaCarteFeatures_SequentialRingsWithIsSubscribedNull_ExpectsProvisioningServiceException() throws Exception {
        try {
            alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSequentialRinging(false, true, null));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch (ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("SequentialRing: subscribed was not populated, requires a value of true or false."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void validateAlaCarteFeatures_SequentialRingsWithIsSubscribedFalse_ExpectsProvisioningServiceException() throws Exception {
        try {
            alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSequentialRinging(false, false, false));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch (ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("SequentialRing: subscribed cannot be false, requires a value of true only."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void populateAlaCarteFeatures_ValidInputSimultaneousRinging_ExpectsValidResponse() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestTypeWithSimultaneousRinging("J.", "Doe", "+1314-222-3548");
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("J.", "Doe", "+1314-222-3548", false);

        alaCarteFeaturesHelper.populateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSimultaneousRing(false, true, true), plexViewRequestType, MockObjectCreator.CORRELATION_ID);

        assertThat(plexViewRequestType.toString(), is(expectedResponse.toString()));
    }

    @Test
    public void populateAlaCarteFeatures_MaxSimultaneousRings_ExpectsProvisioningServiceException() throws Exception {
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("J.", "Doe", "+1314-222-3548", false);

        try {
            alaCarteFeaturesHelper.populateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSimultaneousRing(true, true, true ), plexViewRequestType, MockObjectCreator.CORRELATION_ID);
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("Simultaneous Ring: Only 9 DNs allowed in the request, actual DNs 10"));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void validateAlaCarteFeatures_SimultaneousRingWithIsActivatedNull_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSimultaneousRing(false, false, null));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("SimultaneousRing: activated was not populated, requires a value of true or false."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void validateAlaCarteFeatures_SimultaneousRingWithIsSubscribedNull_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSimultaneousRing(false, null, false));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("SimultaneousRing: subscribed was not populated, requires a value of true or false."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void validateAlaCarteFeatures_SimultaneousRingWithIsSubscribedFalse_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSimultaneousRing(false, false, false));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("SimultaneousRing: subscribed cannot be false, requires a value of true only."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void validateAlaCarteFeatures_MaxSimultaneousRing_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSimultaneousRing(true, true, true));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("Simultaneous Ring: Only 9 DNs allowed in the request, actual DNs 10"));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void populateAlaCarteFeatures_RemoteUserOfficeValidInput_ExpectsValidResponse() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestTypeWithRemoteUserOffice("J.", "Doe", "+1314-222-3548", true);
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("J.", "Doe", "+1314-222-3548", false);

        alaCarteFeaturesHelper.populateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithRemoteUserOffice(true), plexViewRequestType, MockObjectCreator.CORRELATION_ID);

        assertThat(plexViewRequestType.toString(), is(expectedResponse.toString()));
    }

    @Test
    public void validateAlaCarteFeatures_EmptyPin_ExpectsProvisioningServiceException() throws Exception {
        try {
            alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithRemoteUserOffice(false));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CTS Remote User/Office: Pin is required."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void populateAlaCarteFeatures_PersonalAnsweringServiceValidInput_ExpectsProvisioningServiceException() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestTypeWithPersonalAnsweringService("J.", "Doe", "+1314-222-3548");
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("J.", "Doe", "+1314-222-3548", false);

        alaCarteFeaturesHelper.populateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithPersonalAnsweringService(), plexViewRequestType, MockObjectCreator.CORRELATION_ID);

        assertThat(plexViewRequestType.toString(), is(expectedResponse.toString()));
    }

    @Test
    public void populateAlaCarteFeatures_SuspendLineValidInput_ExpectsValidResponse() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestTypeWithSuspendLine("J.", "Doe", "+1314-222-3548");
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("J.", "Doe", "+1314-222-3548", false);

        alaCarteFeaturesHelper.populateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSuspendLine(), plexViewRequestType, MockObjectCreator.CORRELATION_ID);

        assertThat(plexViewRequestType.toString(), is(expectedResponse.toString()));
    }
    
    // Call Forward Busy 
    @Test
    public void populateAlaCarteFeatures_ValidInputCallForwardBusy_ExpectsValidResponse() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestTypeWithCallForwardBusy("J.", "Doe", "+1314-222-3548");
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("J.", "Doe", "+1314-222-3548", false);

        alaCarteFeaturesHelper.populateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingBusy(true, true, "3142223548"), plexViewRequestType, MockObjectCreator.CORRELATION_ID);

        assertThat(plexViewRequestType.toString(), is(expectedResponse.toString()));
    } 
    
    @Test
    public void validateAlaCarteFeatures_CallForwardBusyWithActiveTrueAndForwardNull_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingBusy(true, true, null));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardBusy: The forward property value has to be set in the request before it can be enabled."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    @Test
    public void validateAlaCarteFeatures_CallForwardBusyWithActivateNull_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingBusy(null, null, ""));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardBusy: activated was not populated, requires a value of true or false."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    @Test
    public void validateAlaCarteFeatures_CallForwardBusyWithAssignedNull_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingBusy(true, null, ""));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardBusy: subscribed was not populated, requires a value of true or false."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    @Test
    public void validateAlaCarteFeatures_CallForwardBusyWithIsAssignedFalse_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingBusy(false, false, ""));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardBusy: subscribed cannot be false, requires a value of true only."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    // Call Forward Unconditional 
    @Test
    public void populateAlaCarteFeatures_ValidInputCallForwardUnconditional_ExpectsValidResponse() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestTypeWithCallForwardUnconditional("J.", "Doe", "+1314-222-3548");
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("J.", "Doe", "+1314-222-3548", false);

        alaCarteFeaturesHelper.populateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingUnconditional(true, true, "3142223548"), plexViewRequestType, MockObjectCreator.CORRELATION_ID);

        assertThat(plexViewRequestType.toString(), is(expectedResponse.toString()));
    } 
    
    @Test
    public void validateAlaCarteFeatures_CallForwardUnconditionalWithActiveTrueAndForwardNull_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingUnconditional(true, true, null));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardUnconditional: The forward property value has to be set in the request before it can be enabled."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    @Test
    public void validateAlaCarteFeatures_CallForwardUnconditionalWithActivatedNull_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingUnconditional(null, false, ""));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardUnconditional: activated was not populated, requires a value of true or false."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    @Test
    public void validateAlaCarteFeatures_CallForwardUnconditionalWithAssignedNull_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingUnconditional(true, null, ""));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardUnconditional: subscribed was not populated, requires a value of true or false."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    @Test
    public void validateAlaCarteFeatures_CallForwardUnconditionalWithIsAssignedFalse_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingUnconditional(false, false, ""));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardUnconditional: subscribed cannot be false, requires a value of true only."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    // Call Forward No Answer
    @Test
    public void populateAlaCarteFeatures_ValidInputCallForwardNoAns_ExpectsValidResponse() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestTypeWithCallForwardNoAns("J.", "Doe", "+1314-222-3548");
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("J.", "Doe", "+1314-222-3548", false);

        alaCarteFeaturesHelper.populateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingNoAns(true, true, "3142223548","5"), plexViewRequestType, MockObjectCreator.CORRELATION_ID);

        assertThat(plexViewRequestType.toString(), is(expectedResponse.toString()));
    }
    
    @Test
    public void validateAlaCarteFeatures_CallForwardNoAnsWithActiveTrueAndForwardNull_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingNoAns(true, true, null,"5"));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardNoAnswer: The forward property value has to be set in the request before it can be enabled."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    @Test
    public void validateAlaCarteFeatures_CallForwardNoAnsWithActiveTrueAssignedTrueAndMaxRings_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingNoAns(true, true, "5123431212","11"));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardNoAnswer: Maximum value for Number of rings is 10."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    @Test
    public void validateAlaCarteFeatures_CallForwardNoAnsWithActiveTrueAssignedTrueAndMaxRingsInString_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingNoAns(true, true, "5123431212","hello"));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardNoAnswer: Number of rings should be numeric."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    @Test
    public void validateAlaCarteFeatures_CallForwardNoAnsWithActiveNull_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingNoAns(null, true, "5123431212","hello"));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardNoAnswer: activated was not populated, requires a value of true or false."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    @Test
    public void validateAlaCarteFeatures_CallForwardNoAnsWithAssignedNull_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingNoAns(true, null, "5123431212","hello"));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardNoAnswer: subscribed was not populated, requires a value of true or false."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    @Test
    public void validateAlaCarteFeatures_CallForwardNoAnsWithIsAssignedFalse_ExpectsProvisioningServiceException() throws Exception {
        try {
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallForwardingNoAns(false, false, "5123431212","hello"));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallForwardNoAnswer: subscribed cannot be false, requires a value of true only."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    // Single Digit Speed Dial
    @Test
    public void populateAlaCarteFeatures_ValidInputSingleDigitSpeedDial_ExpectsValidResponse() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestTypeWithSingleDigitSpeedDial("J.", "Doe", "+1314-222-3548");
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("J.", "Doe", "+1314-222-3548", false);

        alaCarteFeaturesHelper.populateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSingleDigitSpeedDial(true), plexViewRequestType, MockObjectCreator.CORRELATION_ID);

        assertThat(plexViewRequestType.toString(), is(expectedResponse.toString()));
    }
    
    @Test
    public void validateAlaCarteFeatures_InValidInputSingleDigitSpeedDial_ExpectsProvisioningServiceException() throws Exception {
        try {
        	
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSingleDigitSpeedDial(null));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("OneDigitSpeedDialing: subscribed was not populated, requires a value of true or false."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    @Test
    public void validateAlaCarteFeatures_SingleDigitSpeedDialWithIsAssignedFalse_ExpectsProvisioningServiceException() throws Exception {
        try {

        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithSingleDigitSpeedDial(false));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("OneDigitSpeedDialing: subscribed cannot be false, requires a value of true only."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

    // Call Transfer
    @Test
    public void populateAlaCarteFeatures_ValidInputCallTransfer_ExpectsValidResponse() throws Exception {
        PlexViewRequestType expectedResponse = MockObjectCreator.createPlexViewRequestTypeWithCallTransfer("J.", "Doe", "+1314-222-3548");
        PlexViewRequestType plexViewRequestType = MockObjectCreator.createPlexViewRequestType("J.", "Doe", "+1314-222-3548", false);

        alaCarteFeaturesHelper.populateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallTransfer(true), plexViewRequestType, MockObjectCreator.CORRELATION_ID);

        assertThat(plexViewRequestType.toString(), is(expectedResponse.toString()));
    }
    
    @Test
    public void validateAlaCarteFeatures_InValidInputCallTransfer_ExpectsProvisioningServiceException() throws Exception {
        try {
        	
        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallTransfer(null));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallTransfer: subscribed was not populated, requires a value of true or false."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }
    
    @Test
    public void validateAlaCarteFeatures_CallTransferWithIsAssignedFalse_ExpectsProvisioningServiceException() throws Exception {
        try {

        	alaCarteFeaturesHelper.validateAlaCarteFeatures(MockObjectCreator.createBusinessClassPhoneWithCallTransfer(false));
            fail("test case should throw a ProvisioningServiceException.");
        }
        catch(ProvisioningServiceException pse){
            assertThat(pse.getMessage(), is("CallTransfer: subscribed cannot be false, requires a value of true only."));
            assertThat(pse.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        }
    }

 
}