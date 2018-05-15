package com.monyrama.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.monyrama.R;
import com.monyrama.log.MyLog;
import com.monyrama.model.Server;
import com.monyrama.util.StringUtil;

public class PBVPGateway {
	private static final int PROTOCOL_VERSION = 2;
	
	private static final int SO_TIMEOUT = 10000;
	private static final int CONNECTION_TIMEOUT = 5000;
	
	private static PBVPGateway instance = new PBVPGateway();
	private Context context;
	
	private PBVPGateway() {
		
	}
	
	public static PBVPGateway getInst(Context context) {
		instance.context = context;
		return instance;
	}
	
	public PBVPGatewayResponse getDataFromServer(Server server) {
		PBVPGatewayResponse pbvpResponse;
		
		final HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
	    HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		
		StringBuilder urlBuilder = new StringBuilder();
		buildBaseUrl(server, urlBuilder);
		urlBuilder.append("/getData");
		
		MyLog.debug("Call url: " + urlBuilder.toString());
		
		HttpRequest httpRequest = new BasicHttpRequest("get", urlBuilder.toString());
		HttpUriRequest uriRequest;
		try {
			uriRequest = new RequestWrapper(httpRequest);
			HttpResponse response = httpClient.execute(uriRequest);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream input = response.getEntity().getContent();			
				String payload = IOUtils.toString(input);
				pbvpResponse = PBVPGatewayResponse.createSuccessResponse(payload);
			} else {
				String errorMessage = fetchErrorMessage(response);
				if(StringUtil.emptyString(errorMessage)) {
					errorMessage = context.getResources().getString(R.string.unexpected_response_from_server);
				}
				pbvpResponse = PBVPGatewayResponse.createErrorResponse(errorMessage);
			}
		} catch (Exception e) {
			MyLog.error("Exception while sending open plans request", e);
			String messageFormat = context.getResources().getString(R.string.failed_to_connect_to_server);
			String message = String.format(messageFormat, server.getAlias(), server.getAddress(), server.getPort());
			pbvpResponse = PBVPGatewayResponse.createErrorResponse(message);
		}		
		
		return pbvpResponse;
	}

	private String fetchErrorMessage(HttpResponse response) throws IOException,
			JSONException {
		InputStream errorInput = response.getEntity().getContent();			
		String errorPayload = IOUtils.toString(errorInput);
		JSONObject errorJson = new JSONObject(errorPayload);
		String errorMessage = errorJson.getString("errorText");
		return errorMessage;
	}
	
	public PBVPGatewayResponse sendDataToServer(Server server, String json) {
		PBVPGatewayResponse pbvpResponse;
		
		final HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
	    HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		
		StringBuilder urlBuilder = new StringBuilder();
		buildBaseUrl(server, urlBuilder);		
		urlBuilder.append("/sendData");
		
		MyLog.debug("Call url: " + urlBuilder.toString());
		
		HttpPost httpost = new HttpPost(urlBuilder.toString());		
		StringEntity se = null;
		try {
			se = new StringEntity(json, "UTF-8");
		} catch (UnsupportedEncodingException unexpectedEncodingException) {
			MyLog.error("Really strange that we have UnsupportedEncodingException here", unexpectedEncodingException);
			pbvpResponse = PBVPGatewayResponse.createErrorResponse(context.getString(R.string.unexpected_error));
		} 
		httpost.setEntity(se);

		try {			
			HttpResponse response = httpClient.execute(httpost);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				pbvpResponse = PBVPGatewayResponse.createSuccessResponse("");
			} else {
				String errorMessage = fetchErrorMessage(response);
				if(StringUtil.emptyString(errorMessage)) {
					errorMessage = context.getResources().getString(R.string.unexpected_response_from_server);
				}
				pbvpResponse = PBVPGatewayResponse.createErrorResponse(errorMessage);
			}
		} catch (Exception exception) {
			MyLog.error("Exception while sending data to server", exception);
			String messageFormat = context.getResources().getString(R.string.failed_to_connect_to_server);
			String message = String.format(messageFormat, server.getAlias(), server.getAddress(), server.getPort());
			pbvpResponse = PBVPGatewayResponse.createErrorResponse(message);
		}		
		
		return pbvpResponse;
	}

	private void buildBaseUrl(Server server, StringBuilder urlBuilder) {
		urlBuilder.append("http://");
		urlBuilder.append(server.getAddress());
		urlBuilder.append(":");
		urlBuilder.append(server.getPort());
		urlBuilder.append("/v");
		urlBuilder.append(PROTOCOL_VERSION);
	}
}
