package com.charter.provisioning.voice.commercial.alu.exceptions;

import com.alu.plexwebapi.api.PlexViewResponseType;

public class LogonException extends ProvisioningServiceException {

	public LogonException(String message) {
		super(message);
	}

	public LogonException(String message, PlexViewResponseType response)
	{
		super(message, response);
	}

}
