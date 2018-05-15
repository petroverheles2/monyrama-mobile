package com.monyrama.dataexchange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.monyrama.R;
import com.monyrama.db.DAOFactory;
import com.monyrama.json.ServerData;
import com.monyrama.json.ServerDataParser;
import com.monyrama.log.MyLog;
import com.monyrama.model.Expense;
import com.monyrama.model.Income;
import com.monyrama.model.Server;
import com.monyrama.model.Transfer;
import com.monyrama.net.MonyramaGateway;
import com.monyrama.net.MonyramaGatewayResponse;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;
import com.monyrama.validator.Validator;
import com.monyrama.validator.ValidatorFactory;

import java.util.List;

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
				//Sending data to server
				List<Expense> expenses = DAOFactory.getExpenseDAO(context).getAllExpensesByServer(server);
				List<Income> incomes = DAOFactory.getIncomeDAO(context).getAllIncomesByServer(server);
				List<Transfer> transfers = DAOFactory.getTransferDAO(context).getTransfersByServer(server);

				JSONObject rootJson = new JSONObject();

				try {
					JSONArray expensesJsonArray = new JSONArray();
					for(Expense expense : expenses) {
						expensesJsonArray.put(expense.toJSON());
					}
					rootJson.put("expenses", expensesJsonArray);

					JSONArray incomesJsonArray = new JSONArray();
					for(Income income : incomes) {
						incomesJsonArray.put(income.toJSON());
					}
					rootJson.put("incomes", incomesJsonArray);

					JSONArray transfersJsonArray = new JSONArray();
					for(Transfer transfer : transfers) {
						transfersJsonArray.put(transfer.toJSON());
					}
					rootJson.put("transfers", transfersJsonArray);

				} catch (JSONException e) {
					ExchangeActionResult failResult = new ExchangeActionResult(false, e.getMessage(), -1);
					return failResult;
				}

				MonyramaGatewayResponse respForSendData = MonyramaGateway.getInst(context).sendDataToServer(server, rootJson.toString());

				if(respForSendData.getResponseCode() == MonyramaGatewayResponse.ERROR ||
						respForSendData.getResponseCode() == MonyramaGatewayResponse.CONNECTION_ERROR) {
					ExchangeActionResult failResult = new ExchangeActionResult(false, respForSendData.getMessage(), respForSendData.getResponseCode());
					progressDialog.dismiss();
					return failResult;
				}

				//Getting updated data from the server
				MonyramaGatewayResponse respForGetData = MonyramaGateway.getInst(context).getDataFromServer(server);
				if(respForGetData.getResponseCode() == MonyramaGatewayResponse.ERROR ||
						respForGetData.getResponseCode() == MonyramaGatewayResponse.CONNECTION_ERROR) {
					ExchangeActionResult failResult = new ExchangeActionResult(false, respForGetData.getMessage(), respForSendData.getResponseCode());
					progressDialog.dismiss();
					return failResult;
				}
						
				MyLog.debug(respForGetData.toString());
				
				Integer serverId = PrefsManager.getInt(context, PrefKeys.CURRENT_SERVER_ID, -1);
				if(serverId == -1) {
					ExchangeActionResult failResult = new ExchangeActionResult(false, "No server", -1);
					return failResult;
				}
				ServerData serverData;
				try {
					serverData = ServerDataParser.parse(server, respForGetData.getPayload());
				} catch (JSONException e) {
					ExchangeActionResult failResult = new ExchangeActionResult(false, e.getMessage(), -1);
					return failResult;
				}
				
				//Cleaning data before save updated data from server
				DAOFactory.getGenericDAO(context).deleteDataByServer(server);

				//Saving data from server
				DAOFactory.getGenericDAO(context).saveDataFromServer(serverData);
		
				//All steps went well, so creating success result
				ExchangeActionResult successResult = new ExchangeActionResult(true, context.getResources().getString(R.string.data_exchange_success), -1);
				progressDialog.dismiss();															
				return successResult;
			}

			@Override
			protected void onPostExecute(ExchangeActionResult exchangeActionResult) {
				if(exchangeActionResult.responseCode == MonyramaGatewayResponse.CONNECTION_ERROR) {
					final AlertDialog.Builder correctServerDialogBuilder = new AlertDialog.Builder(context);
					View view = View.inflate(context, R.layout.correct_server_dialog, null);
					final EditText addressField = (EditText)view.findViewById(R.id.addressField);
					addressField.setText(server.getAddress());
					final EditText portField = (EditText)view.findViewById(R.id.portField);
					portField.setText(Integer.toString(server.getPort()));
					final TextView messageView = (TextView)view.findViewById(R.id.messageView);
					messageView.setText(R.string.cant_connect_ip_port);

					correctServerDialogBuilder.setView(view);
					//correctServerDialogBuilder.setMessage(exchangeActionResult.message);
					correctServerDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Server updatedServer = new Server();
							updatedServer.setServerid(server.getServerid());
							updatedServer.setAlias(server.getAlias());
							updatedServer.setAddress(addressField.getText().toString().trim());
							try {
								updatedServer.setPort(Integer.valueOf(portField.getText().toString().trim()));
							} catch (Exception e) {
								MyLog.debug("Exception creating integer from string", e);
							}

							Validator updatedServerValidator = ValidatorFactory.createCorrectServerValidator(updatedServer, context);
							if (updatedServerValidator.validate()) {
								DAOFactory.getServerDAO(context).createOrUpdate(updatedServer);
								dialog.dismiss();
								exchangeData(successCallback);
							} else {
								AlertDialog.Builder mistakeDialogBuilder = new AlertDialog.Builder(context);
								mistakeDialogBuilder.setMessage(updatedServerValidator.getMessage());
								mistakeDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								});
								AlertDialog mistakeAlert = mistakeDialogBuilder.create();
								mistakeAlert.show();
							}
						}
					});
					correctServerDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					AlertDialog correctServerDialog = correctServerDialogBuilder.create();
					correctServerDialog.show();
					return;
				}

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
		private int responseCode;
		
		public ExchangeActionResult(boolean success, String message, int responseCode) {
			super();
			this.success = success;
			this.message = message;
			this.responseCode = responseCode;
		}

		public boolean isSuccess() {
			return success;
		}

		public String getMessage() {
			return message;
		}

		public int getResponseCode() {
			return responseCode;
		}
	}
	
	public static interface SuccessCallback {
		public void execute();
	}
}
