package com.monyrama.dataexchange;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.monyrama.R;
import com.monyrama.db.DAOFactory;
import com.monyrama.json.ServerData;
import com.monyrama.json.ServerDataParser;
import com.monyrama.log.MyLog;
import com.monyrama.model.Server;
import com.monyrama.net.PBVPGateway;
import com.monyrama.net.PBVPGatewayResponse;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;

public class DataExchanger {
	
	private Context context;
	
	public DataExchanger(Context context) {
		this.context = context;
	}
	
	public void exchangeData(final SuccessCallback successCallback) {
		final Server server = getCurrentServer();
		
		if(server == null) {
			return;
		}
		
		final ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(context.getResources().getString(R.string.fetching_data));
		progressDialog.setCancelable(false);
		progressDialog.show();
		
		AsyncTask<Void, Integer, ExchangeActionResult> fetchTask = new AsyncTask<Void, Integer, ExchangeActionResult>() {			
			@Override
			protected ExchangeActionResult doInBackground(Void... params) {															
				//Getting updated data from the server
				PBVPGatewayResponse respForGetData = PBVPGateway.getInst(context).getDataFromServer(server);
				if(respForGetData.getResponseCode() == PBVPGatewayResponse.ERROR) {
					ExchangeActionResult failResult = new ExchangeActionResult(false, respForGetData.getMessage());
					progressDialog.dismiss();
					return failResult;
				}
						
				MyLog.debug(respForGetData.toString());
				
				Integer serverId = PrefsManager.getInt(context, PrefKeys.CURRENT_SERVER_ID, -1);
				if(serverId == -1) {
					ExchangeActionResult failResult = new ExchangeActionResult(false, "No server");
					return failResult;
				}
				ServerData serverData;
				try {
					serverData = ServerDataParser.parse(server, respForGetData.getPayload());
				} catch (JSONException e) {
					ExchangeActionResult failResult = new ExchangeActionResult(false, e.getMessage());
					return failResult;
				}
				
				//Cleaning data before save updated data from server
				DAOFactory.getGenericDAO(context).deleteDataByServer(server);

				//Saving data from server
				DAOFactory.getGenericDAO(context).saveDataFromServer(serverData);

				//All steps went well, so creating success result
				ExchangeActionResult successResult = new ExchangeActionResult(true, context.getResources().getString(R.string.data_exchange_success));
				progressDialog.dismiss();															
				return successResult;
			}

			@Override
			protected void onPostExecute(ExchangeActionResult exchangeActionResult) {
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);				
				dialogBuilder.setMessage(exchangeActionResult.getMessage());				
				dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {
				    	dialog.dismiss();
				    }
				});
				AlertDialog alert = dialogBuilder.create();
				alert.show();
				
				if(exchangeActionResult.isSuccess()) {
					successCallback.execute();					
				}
			}						
		};
		
		fetchTask.execute();				
	}
	
	private Server getCurrentServer() {
		Integer serverid = PrefsManager.getInt(context, PrefKeys.CURRENT_SERVER_ID, -1);
		if(serverid == -1) {
			return null;
		}
		Server server = DAOFactory.getServerDAO(context).getServer(serverid);
		return server;
	}
	
	private class ExchangeActionResult {				
		private boolean success;
		private String message;
		
		public ExchangeActionResult(boolean success, String message) {
			super();
			this.success = success;
			this.message = message;
		}

		public boolean isSuccess() {
			return success;
		}

		public String getMessage() {
			return message;
		}				
	}
	
	public static interface SuccessCallback {
		public void execute();
	}
}
