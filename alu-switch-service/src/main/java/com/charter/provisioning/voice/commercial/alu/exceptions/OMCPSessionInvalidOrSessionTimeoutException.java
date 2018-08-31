package com.charter.provisioning.voice.commercial.alu.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OMCPSessionInvalidOrSessionTimeoutException extends ProvisioningServiceException {

	public OMCPSessionInvalidOrSessionTimeoutException(String message) {
        super(message);
    }

    public OMCPSessionInvalidOrSessionTimeoutException(Throwable t) {
        super(t);
    }

    public OMCPSessionInvalidOrSessionTimeoutException(String message, Throwable nestedException) {
        super(message, nestedException);
    }

}
