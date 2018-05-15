package com.monyrama.net;

public class MonyramaGatewayResponse {

	public static int OK = 0;
	public static int ERROR = 1;
	public static int CONNECTION_ERROR = 2;
	
	private int responseCode;
	private String payload;
	private String message;
		
	private MonyramaGatewayResponse(int responseCode, String payload, String message) {
		super();
		this.responseCode = responseCode;
		this.payload = payload;
		this.message = message;
	}
	
	static MonyramaGatewayResponse createSuccessResponse(String payload) {
		return new MonyramaGatewayResponse(OK, payload, null);
	}
	
	static MonyramaGatewayResponse createErrorResponse(String message) {
		return new MonyramaGatewayResponse(ERROR, null, message);
	}

	static MonyramaGatewayResponse createConnectionErrorResponse(String message) {
		return new MonyramaGatewayResponse(CONNECTION_ERROR, null, message);
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
