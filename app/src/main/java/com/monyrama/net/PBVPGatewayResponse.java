package com.monyrama.net;

public class PBVPGatewayResponse {

	public static int OK = 0;
	public static int ERROR = 1;
	
	private int responseCode;
	private String payload;
	private String message;
		
	private PBVPGatewayResponse(int responseCode, String payload, String message) {
		super();
		this.responseCode = responseCode;
		this.payload = payload;
		this.message = message;
	}
	
	static PBVPGatewayResponse createSuccessResponse(String payload) {
		return new PBVPGatewayResponse(OK, payload, null);
	}
	
	static PBVPGatewayResponse createErrorResponse(String message) {
		return new PBVPGatewayResponse(ERROR, null, message);
	}	

	public int getResponseCode() {
		return responseCode;
	}
	
	public String getPayload() {
		return payload;
	}
	
	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "PBVPResponse [responseCode=" + responseCode + ", payload="
				+ payload + "]";
	}
}
