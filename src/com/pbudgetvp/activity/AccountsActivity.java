package com.monyrama.activity;

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.monyrama.R;
import com.monyrama.db.DAOFactory;
import com.monyrama.db.ExpenseDAO;
import com.monyrama.db.ExpensePlanDAO;
import com.monyrama.db.ServerDAO;
import com.monyrama.json.ServerData;
import com.monyrama.json.ServerDataParser;
import com.monyrama.log.MyLog;
import com.monyrama.model.Expense;
import com.monyrama.model.Envelope;
import com.monyrama.model.Server;
import com.monyrama.net.PBVPGateway;
import com.monyrama.net.PBVPGatewayResponse;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;
import com.monyrama.util.SumLocalizer;

public class AccountsActivity extends ActionBarActivity {
    
    private static final int ANIMATION_INTERVAL = 400;
	
	private ListView itemsListView;
	private ItemsListAdapter itemsListAdapter;
	private ListView expensesListView;
	private ExpensesListAdapter expensesListAdapter;
	//private LinearLayout adHolder;
	private TextView itemsTab;
	private TextView expensesTab;
	private ImageView topPanelControlButton;
	private LinearLayout firstSeparator;
	private LinearLayout tabsLayout;
	private LinearLayout secondSeparator;
	private LinearLayout itemsAndExpensesListLayout;
	private LinearLayout noItemsListInfoLayout;
	private LinearLayout noExpensesListInfoLayout;
	
	private ServerDAO serverDAO;
	private ExpensePlanDAO expensePlanDAO;
	private ExpenseDAO expenseDAO;
	
	private boolean itemsTabActive = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts);
        
		ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.accounts);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
        
        itemsTab = (TextView)findViewById(R.id.itemsTab);
        expensesTab = (TextView)findViewById(R.id.expensesTab);
        topPanelControlButton = (ImageView)findViewById(R.id.topPanelControlButton);
        firstSeparator = (LinearLayout)findViewById(R.id.firstSeparator);
        tabsLayout = (LinearLayout)findViewById(R.id.tabsLayout);
        secondSeparator = (LinearLayout)findViewById(R.id.secondSeparator);
        itemsAndExpensesListLayout = (LinearLayout)findViewById(R.id.itemsAndExpensesListLayout);
        itemsListView = (ListView)findViewById(R.id.itemsListView);
        itemsListAdapter = new ItemsListAdapter(this);        
        itemsListView.setAdapter(itemsListAdapter);
        expensesListView = (ListView)findViewById(R.id.expensesListView);
        expensesListAdapter = new ExpensesListAdapter(this);
        expensesListView.setAdapter(expensesListAdapter);        
        noItemsListInfoLayout = (LinearLayout)findViewById(R.id.noItemsListInfoLayout);
        noExpensesListInfoLayout = (LinearLayout)findViewById(R.id.noExpensesListInfoLayout);

        switchItemsTabActive();
        //Adding Ads
        // Create an ad.
        //adView = new AdView(this, AdSize.BANNER, "a150bdfc1de9b85");
        
        //adHolder = (LinearLayout)findViewById(R.id.adHolder);
        //adHolder.addView(adView);
        
        //Creating daos
        serverDAO = DAOFactory.getServerDAO(this); 
        expensePlanDAO = DAOFactory.getExpensePlanDAO(this);
        expenseDAO = DAOFactory.getExpenseDAO(this);
        
        //Fill up data on start up
//        List<Server> allServers = serverDAO.getAll();
//        if(allServers.size() > 0) {
//        	serverAdapter = new ArrayAdapter<Server>(this, R.layout.spinner_item, allServers);
//        	serverSpinner.setAdapter(serverAdapter);        	    	  
//        }
    }

	private void switchItemsTabActive() {			
		itemsTabActive = true;
		itemsTab.setBackgroundResource(R.drawable.tab_active);
		expensesTab.setBackgroundResource(R.drawable.tab_passive);		
	}
	
	private void switchExpensesTabActive() {
		itemsTabActive = false;
		expensesTab.setBackgroundResource(R.drawable.tab_active);
		itemsTab.setBackgroundResource(R.drawable.tab_passive);	
	}
    
    @Override
	protected void onResume() {
		super.onResume();		
		
//		List<Server> allServers = serverDAO.getAll();
//        if(allServers.size() == 0) {
//        	Intent serverActivityIntent = new Intent(this, AddEditServerActivity.class);
//        	startActivityForResult(serverActivityIntent, ADD_SERVER_REQUEST_CODE);
//        } else {        	
//        	//Show tips dialog if needed
//			if(PrefsManager.getBoolean(this, PrefKeys.SHOW_MAIN_SCREEN_TIPS, true)) {
//				showTipsDialog();
//			}
//        }
        
        // Create an ad request.
        //AdRequest adRequest = new AdRequest();
        // Fill out ad request.

        //adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
        // Start loading the ad in the background.
        //adView.loadAd(adRequest);
				
	}
    
	private void showTipsDialog() {
		TipsDialogUtility.showTipsDialog(this, R.string.main_screen_tip, PrefKeys.SHOW_MAIN_SCREEN_TIPS);
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if(requestCode == ADD_EDIT_EXPENSE_REQUEST_CODE && resultCode == RESULT_OK) {			
//			refreshExpensesList();
//			refreshItemsList();
//			
//			//Update remainder in expense plan that is in spinner
//			ExpensePlan selectedExpensePlan = (ExpensePlan)expensePlanSpinner.getSelectedItem();
//			Server selectedServer = (Server)serverSpinner.getSelectedItem();
//			List<ExpensePlan> expensesPlansByServer = expensePlanDAO.getExpensePlansByServer(selectedServer);
//			for(ExpensePlan expensePlan : expensesPlansByServer) {
//				if(expensePlan.equals(selectedExpensePlan)) {
//					selectedExpensePlan.setRemainder(expensePlan.getRemainder());
//					break;
//				}
//			}
//			
//			
//		} else if((requestCode == ADD_SERVER_REQUEST_CODE && resultCode == RESULT_OK) || requestCode == SERVERS_REQUEST_CODE) {
//			List<Server> allServers = serverDAO.getAll();
//        	serverAdapter = new ArrayAdapter<Server>(this, R.layout.spinner_item, allServers);
//        	serverSpinner.setAdapter(serverAdapter);				
//		} else {
//			if(serverDAO.getAll().size() == 0) {
//				finish();
//			}
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.accounts, menu);
		return super.onCreateOptionsMenu(menu);
	}
    
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		    // Respond to the action bar's Up/Home button
		    case android.R.id.home:
		        super.onBackPressed();
		        break;
			case R.id.menu_accounts_tips:
				showTipsDialog();
				break;
			case R.id.menu_accounts_servers:
				Intent serversActivityIntent = new Intent(this, ServersActivity.class);
				startActivity(serversActivityIntent);
				break;		
			default:			
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void exchangeData() {
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getResources().getString(R.string.fetching_data));
		progressDialog.setCancelable(false);
		progressDialog.show();
		
		AsyncTask<Void, Integer, ExchangeActionResult> fetchTask = new AsyncTask<Void, Integer, ExchangeActionResult>() {			
			@Override
			protected ExchangeActionResult doInBackground(Void... params) {
				//Server server = (Server)serverSpinner.getSelectedItem();
				
//				List<Expense> expensesByServer = expenseDAO.getAllExpensesByServer(server);
//				if(expensesByServer.size() > 0) {
//					//Creating json expenses array for sending to the server
//					JSONArray expensesJsonArray = new JSONArray();
//					for(Expense expense : expensesByServer) {
//						try {
//							expensesJsonArray.put(expense.toJSON());
//						} catch (JSONException e) {
//							MyLog.error("Error creating expenses json array", e);
//							ExchangeActionResult failResult = new ExchangeActionResult(false, getResources().getString(R.string.unexpected_error));
//							progressDialog.dismiss();
//							return failResult;
//						}
//					}
//
//					//Sending expneses json to server
//					PBVPGatewayResponse respForSendExpenses = PBVPGateway.getInst(AccountsActivity.this).sendExpenses(server, expensesJsonArray.toString());
//					if(respForSendExpenses.getResponseCode() == PBVPGatewayResponse.ERROR) {
//						ExchangeActionResult failResult = new ExchangeActionResult(false, respForSendExpenses.getMessage());
//						progressDialog.dismiss();
//						return failResult;
//					}	
//					
//					//Cleaning expenses after successful transfer
//					expenseDAO.deleteExpensesByServer(server);
//				}
											
				//Getting updated data from the server
//				PBVPGatewayResponse respForGetData = PBVPGateway.getInst(AccountsActivity.this).getDataFromServer(server);
//				if(respForGetData.getResponseCode() == PBVPGatewayResponse.ERROR) {
//					ExchangeActionResult failResult = new ExchangeActionResult(false, respForGetData.getMessage());
//					progressDialog.dismiss();
//					return failResult;
//				}
//						
//				//MyLog.debug(respForGetExpensePlans.toString());
//				
//				//Parsing expenses plan json from server
//				List<ExpensePlan> expensePlans = new ArrayList<ExpensePlan>();
//				List<ExpensePlanItem> items = new ArrayList<ExpensePlanItem>();
//				String expensesPlansJSON = respForGetData.getPayload();
//				JSONArray openPlansJsonArray;
//				try {
//					openPlansJsonArray = new JSONArray(expensesPlansJSON);
//					for(int planIndex = 0; planIndex < openPlansJsonArray.length(); planIndex++) {
//						JSONObject openPlanJson = (JSONObject)openPlansJsonArray.get(planIndex);
//						ExpensePlan expensePlan = new ExpensePlan(server, openPlanJson);
//						expensePlans.add(expensePlan);
//						JSONArray itemsJsonArray = openPlanJson.getJSONArray("items");
//						for(int itemIndex = 0; itemIndex < itemsJsonArray.length(); itemIndex++) {
//							JSONObject itemJson = (JSONObject)itemsJsonArray.get(itemIndex);
//							ExpensePlanItem item = new ExpensePlanItem(server, expensePlan, itemJson);
//							items.add(item);
//						}
//					}					
//				} catch (JSONException e) {
//					MyLog.error("Error handling expenses plans json", e);
//					ExchangeActionResult failResult = new ExchangeActionResult(false, getResources().getString(R.string.unexpected_error));
//					progressDialog.dismiss();
//					return failResult;
//				}
//				
//				//Cleaning data before save updated data from server
//				expensePlanDAO.deleteExpensePlansByServer(server);
//				
//				//Saving Updated Expense Plans to DB
//				boolean success = expensePlanDAO.saveExpensePlansAndItems(expensePlans, items);
//				if(!success) {
//					ExchangeActionResult failResult = new ExchangeActionResult(false, getResources().getString(R.string.unexpected_error));
//					progressDialog.dismiss();
//					return failResult;
//				}
				
//				PBVPGatewayResponse respForGetData = PBVPGateway.getInst(AccountsActivity.this).getDataFromServer(server);
//				if(respForGetData.getResponseCode() == PBVPGatewayResponse.ERROR) {
//					ExchangeActionResult failResult = new ExchangeActionResult(false, respForGetData.getMessage());
//					progressDialog.dismiss();
//					return failResult;
//				}				
				
//				MyLog.debug(respForGetData.toString());
//				
//				final ServerData serverData;
//				try {
//					serverData = ServerDataParser.parse(server, respForGetData.getPayload());
//				} catch (JSONException e) {
//					ExchangeActionResult failResult = new ExchangeActionResult(false, e.getMessage());
//					progressDialog.dismiss();
//					return failResult;
//				}
				
				//All steps went well, so creating success result
				ExchangeActionResult successResult = new ExchangeActionResult(true, getResources().getString(R.string.data_exchange_success));
				progressDialog.dismiss();															
				return successResult;
			}

			@Override
			protected void onPostExecute(ExchangeActionResult exchangeActionResult) {
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AccountsActivity.this);				
				dialogBuilder.setMessage(exchangeActionResult.getMessage());				
				dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {
				    	dialog.dismiss();
				    }
				});
				AlertDialog alert = dialogBuilder.create();
				alert.show();
				
				if(exchangeActionResult.isSuccess()) {
					//refreshExpensePlanList();
					//refreshItemsList();					
				}
			}						
		};
		
		fetchTask.execute();				
	}

	private class ServerSelectionListener implements AdapterView.OnItemSelectedListener {
		
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
//			refreshExpensePlanList();
//			refreshItemsList();
//			refreshExpensesList();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}		
		
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
	
	//--------------- Click Handlers ------------------------------------
	public void itemsTabClicked(View view) {
		switchItemsTabActive();
		//refreshItemsList();
	}
	
	public void expensesTabClicked(View view) {
		switchExpensesTabActive();
		//refreshExpensesList();
	}
	
	public void expenseRemovedClicked(View view) {
		Integer position = (Integer)view.getTag();
		final Expense selectedExpense = (Expense)expensesListAdapter.getItem(position);
				
		AlertDialog alert;
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.think_please);
		dialogBuilder.setMessage(getResources().getString(R.string.question_remove_expense)
				+ " \"" + SumLocalizer.localize(selectedExpense.getSum().toString()) + "\"?");

		dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        expenseDAO.delete(selectedExpense);
		        
		        //We also need to update expense plan and item remainder
		        Envelope item = selectedExpense.getItem();
		        BigDecimal newItemRemainder = item.getRemainder().add(selectedExpense.getSum());
				item.setRemainder(newItemRemainder);
				
				//ExpensePlan expensePlan = (ExpensePlan)expensePlanSpinner.getSelectedItem();
				//String newExpensePlanRemainder = CalcUtil.addDoubleStrings(expensePlan.getRemainder(), selectedExpense.getSum());
				//expensePlan.setRemainder(newExpensePlanRemainder);
				
				//expensePlanDAO.updateRemainder(expensePlan, item);
		        
		        //refreshExpensesList();
		        dialog.dismiss();
		        Toast.makeText(AccountsActivity.this, R.string.expense_removed, Toast.LENGTH_SHORT).show();
		    }
		});

		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {		     
		        dialog.dismiss();
		    }
		});

		alert = dialogBuilder.create();
		alert.show();
	}
	
	public void exchangeDataClicked(View view) {
		exchangeData();
	}
	
	public void topPanelControlClicked(View view) {
		final LinearLayout topPanelLayout = (LinearLayout)findViewById(R.id.topPanelLayout);		
		if(topPanelLayout.getVisibility() == View.VISIBLE) {
            TranslateAnimation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f);
            slide.setDuration(ANIMATION_INTERVAL);           
            topPanelLayout.startAnimation(slide);
            firstSeparator.startAnimation(slide);
            tabsLayout.startAnimation(slide);
            secondSeparator.startAnimation(slide);
            itemsAndExpensesListLayout.startAnimation(slide);
            
            slide.setAnimationListener(new Animation.AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {}
				
				@Override
				public void onAnimationRepeat(Animation animation) {}
				
				@Override
				public void onAnimationEnd(Animation arg0) {
					topPanelLayout.setVisibility(View.GONE);
				}
			});
            
			topPanelControlButton.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
		} else {
            TranslateAnimation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0);
            slide.setDuration(ANIMATION_INTERVAL);
            topPanelLayout.setVisibility(View.VISIBLE);   
            topPanelLayout.startAnimation(slide);
            firstSeparator.startAnimation(slide);
            tabsLayout.startAnimation(slide);
            secondSeparator.startAnimation(slide);
            itemsAndExpensesListLayout.startAnimation(slide);                       
			topPanelControlButton.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
		}
	}
}