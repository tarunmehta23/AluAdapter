package com.charter.provisioning.voice.commercial.alu.controller;

import com.charter.provisioning.voice.commercial.alu.exceptions.ErrorResponse;
import com.charter.provisioning.voice.commercial.alu.handler.DigitalPhoneHandler;
import com.charter.provisioning.voice.commercial.alu.util.MockObjectCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DigitalPhoneControllerTest {

    @InjectMocks
    private DigitalPhoneController digitalPhoneController;

    @Mock
    private DigitalPhoneHandler digitalPhoneService;


    @Test
    public void getDigitalPhone_ValidInput_ExpectsHttpCreated() throws Exception {
        when(digitalPhoneService.createDigitalPhone(MockObjectCreator.createBusinessClassPhoneWithPersonalAnsweringService(), MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.generateCreateDigitalPhoneResponse());

        ResponseEntity responseEntity = digitalPhoneController.getDigitalPhone(MockObjectCreator.SUBSCRIBER_ID, MockObjectCreator.CORRELATION_ID);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void createDigitalPhone_ValidInput_ExpectsHttpCreated() throws Exception {
        when(digitalPhoneService.createDigitalPhone(MockObjectCreator.createBusinessClassPhoneWithPersonalAnsweringService(), MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.generateCreateDigitalPhoneResponse());

        ResponseEntity responseEntity = digitalPhoneController.createDigitalPhone(MockObjectCreator.createBusinessClassPhoneWithPersonalAnsweringService(), MockObjectCreator.CORRELATION_ID);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(responseEntity.getBody(), is(MockObjectCreator.generateCreateDigitalPhoneResponse()));
    }

    @Test
    public void deleteCTSDigitalPhone_ValidInput_ExpectsHttpCreated() throws Exception {
        doNothing().when(digitalPhoneService).deleteDigitalPhoneSubscriber(MockObjectCreator.PHONE_NUMBER, MockObjectCreator.CORRELATION_ID);

        ResponseEntity<Void> responseEntity = digitalPhoneController.deleteDigitalPhone(MockObjectCreator.PHONE_NUMBER, MockObjectCreator.CORRELATION_ID);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void testExceptionHandler_Exception_ExpectsResponseEntity() {
        String errorMessage = "SimultaneousRing: activated was not populated, requires a value of true or false.";
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);

        ErrorResponse errorResponse = digitalPhoneController.handleException(MockObjectCreator.getHttpServletResponse(), ex);

        assertThat(errorResponse.getMessage(),
                is(String.format("%s %s", HttpStatus.INTERNAL_SERVER_ERROR, errorMessage)));
    }

}