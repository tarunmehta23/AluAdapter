package com.charter.provisioning.voice.commercial.alu.exceptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    private int statusCode;

    private String statusText;

    private String message;

}