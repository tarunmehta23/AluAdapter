package com.charter.provisioning.voice.commercial.alu.controller;

import com.charter.provisioning.voice.commercial.alu.exceptions.ErrorResponse;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.model.BusinessClassPhone;
import com.charter.provisioning.voice.commercial.alu.model.CreateDigitalPhoneResponse;
import com.charter.provisioning.voice.commercial.alu.model.response.GetDigitalPhoneResponse;
import com.charter.provisioning.voice.commercial.alu.handler.DigitalPhoneHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/digital-phone")
@Api(value = "Digital Phone Service")
public class DigitalPhoneController {

    private final DigitalPhoneHandler digitalPhoneHandler;

    @Autowired
    public DigitalPhoneController(DigitalPhoneHandler digitalPhoneHandler) {
        this.digitalPhoneHandler = digitalPhoneHandler;
    }

    @ApiOperation(value = "Endpoint for retrieving Digital Phone Subscriber.", response = GetDigitalPhoneResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "OK"),
            @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "Bad Request"),
            @ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Not Found"),
            @ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = "Unauthorized"),
            @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal Server Error")
    })
    @GetMapping(produces = "application/json", value = "/{subscriberId}")
    public ResponseEntity<GetDigitalPhoneResponse> getDigitalPhone(@ApiParam(value = "Subscriber ID.", required = true)
                                           @PathVariable String subscriberId,
                                                                   @ApiParam(value = "Correlation ID.", required = true)
                                           @RequestHeader String correlationId) {
        log.debug("retrieving digital phone subscriber by subscriber id {}, correlation id {}.", subscriberId, correlationId);
        GetDigitalPhoneResponse getDigitalPhoneResponse = digitalPhoneHandler.retrieveDigitalPhoneSubscriber(subscriberId, correlationId);
        return new ResponseEntity<>(getDigitalPhoneResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Endpoint for creating digital phone.", response = CreateDigitalPhoneResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "OK"),
            @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "Bad Request"),
            @ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Not Found"),
            @ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = "Unauthorized"),
            @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal Server Error")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CreateDigitalPhoneResponse> createDigitalPhone(
            @ApiParam(value = "The business class phone attributes that will be used to create the digital phone.", required = true) @RequestBody BusinessClassPhone businessClassPhone,
            @ApiParam(value = "Correlation ID.", required = true) @RequestHeader String correlationId) {
        log.debug("creating digital phone for phone number: {}, request id: {}", businessClassPhone.getPhoneNumber(), correlationId);

        return new ResponseEntity<>(digitalPhoneHandler.createDigitalPhone(businessClassPhone, correlationId), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Endpoint for deleting digital phone.")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "OK"),
            @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "Bad Request"),
            @ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Not Found"),
            @ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = "Unauthorized"),
            @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal Server Error")
    })
    @DeleteMapping(produces = "application/json", value = "/{phoneNumber}")
    public ResponseEntity<Void> deleteDigitalPhone(@ApiParam(value = "The phone number used to query and delete Subscriber in CTS.", required = true) @PathVariable String phoneNumber,
                                             @ApiParam(value = "Correlation-ID.", required = true) @RequestHeader String correlationId) {
        log.debug("deleting a digital phone for phone number: {}", phoneNumber);

        digitalPhoneHandler.deleteDigitalPhoneSubscriber(phoneNumber, correlationId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler
    public ErrorResponse handleException(HttpServletResponse response, Throwable ex) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        if (ex instanceof ProvisioningServiceException) {
            response.setStatus(((ProvisioningServiceException) ex).getHttpStatus().value());
            return ErrorResponse.builder().message(ex.getMessage())
                    .statusCode(((ProvisioningServiceException) ex).getHttpStatus().value())
                    .statusText(((ProvisioningServiceException) ex).getHttpStatus().getReasonPhrase()).build();
        }
        return ErrorResponse.builder().message(ex.getMessage()).build();
    }

}