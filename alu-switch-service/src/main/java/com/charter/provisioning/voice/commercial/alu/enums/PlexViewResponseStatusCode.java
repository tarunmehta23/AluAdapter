package com.charter.provisioning.voice.commercial.alu.enums;

public enum PlexViewResponseStatusCode {

	SUCCESS("SUCCESS"),
	FAILURE("FAILURE"),
    SESSION_NOT_FOUND("Session not found"),
    NO_ERROR("0"),
	OBJECT_NOT_EXISTS("4"),
	PCM_OBJECT_NOT_EXISTS("30000"),
	BG_SHRT_CD_NOT_EXISTS("5"),
	PCM_OBJECT_NOT_EXISTS_TXT("Failed to get object with specified AID"),
	SWITCH_CONN_FAILURE("8"),
	WEB_API_FAILURE("30000"),
	SESSION_INVALID("30001"),
	SESSION_TIMED_OUT("30002"),
	REQUEST_TIMED_OUT("30003"),
	ERROR_DUPLICATE_EXISTS("20011");

	private String statusCode;

    PlexViewResponseStatusCode(String statusCode){
        this.statusCode = statusCode;
    }
	
    public String statusCode(){
        return statusCode;
    }
}
