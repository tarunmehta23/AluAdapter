package com.charter.provisioning.voice.commercial.alu.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NetworkException extends RuntimeException {

	public NetworkException(String msg) {
		super(msg);
	}

	public NetworkException(Throwable cause) {
		super(cause);
	}

	public NetworkException(String message, Throwable cause) {
		super(message, cause);
	}
}
