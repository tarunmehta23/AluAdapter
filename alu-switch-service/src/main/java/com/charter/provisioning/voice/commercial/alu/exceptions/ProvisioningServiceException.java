package com.charter.provisioning.voice.commercial.alu.exceptions;

import com.alu.plexwebapi.api.PlexViewResponseType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Setter
@Getter
public class ProvisioningServiceException extends RuntimeException {

	private HttpStatus httpStatus;

    private String ErrorDescription;

    public ProvisioningServiceException(String msg, PlexViewResponseType errorResponse){
        super(msg);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.ErrorDescription = errorResponse.getFailureReason();
    }

    public ProvisioningServiceException(String message) {
        super(message);
    }

    public ProvisioningServiceException(HttpStatus httpStatus,String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ProvisioningServiceException(Throwable cause) {
        super(cause);
    }

    public ProvisioningServiceException(String message, Throwable cause) {
        super(message, cause);

        if(cause instanceof ProvisioningServiceException){
            ProvisioningServiceException ae = (ProvisioningServiceException) cause;
            this.httpStatus = ae.getHttpStatus();
            this.ErrorDescription = ae.getErrorDescription();
        }
    }
}
