package com.monyrama.activity;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.monyrama.dataexchange.DataExchanger;
import com.monyrama.dataexchange.DataExchanger.SuccessCallback;
import com.monyrama.db.DAOFactory;
import com.monyrama.db.ExpenseDAO;
import com.monyrama.db.ExpensePlanDAO;
import com.monyrama.db.ServerDAO;
import com.monyrama.model.Expense;
import com.monyrama.model.ExpensePlan;
import com.monyrama.model.Envelope;
import com.monyrama.model.Server;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;
import com.monyrama.util.SumLocalizer;

public class ExpensesPlansActivity extends ActionBarActivity {
    
    private static final int ANIMATION_INTERVAL = 400;
	
	private static int ADD_SERVER_REQUEST_CODE = 1;
	private static int ADD_EDIT_EXPENSE_REQUEST_CODE = 2;
	private static int SERVERS_REQUEST_CODE = 3;
	//private AdView adView;
	private Spinner expensePlanSpinner;
	private SpinnerAdapter expensePlanAdapter;
	private ListView itemsListView;
	private EnvelopesListAdapter envelopesListAdapter;
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
        setContentView(R.layout.expenses_plans);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.expenses_plans);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);

        expensePlanSpinner = (Spinner)findViewById(R.id.expensePlanSpinner);
        expensePlanSpinner.setOnItemSelectedListener(new ExpensePlanSelectionListener());
        
        itemsTab = (TextView)findViewById(R.id.itemsTab);
        expensesTab = (TextView)findViewById(R.id.expensesTab);
        topPanelControlButton = (ImageView)findViewById(R.id.topPanelControlButton);
        firstSeparator = (LinearLayout)findViewById(R.id.firstSeparator);
        tabsLayout = (LinearLayout)findViewById(R.id.tabsLayout);
        secondSeparator = (LinearLayout)findViewById(R.id.secondSeparator);
        itemsAndExpensesListLayout = (LinearLayout)findViewById(R.id.itemsAndExpensesListLayout);
        itemsListView = (ListView)findViewById(R.id.itemsListView);
        envelopesListAdapter = new EnvelopesListAdapter(this);
        itemsListView.setAdapter(envelopesListAdapter);
        expensesListView = (ListView)findViewById(R.id.expensesListView);
        expensesListAdapter = new ExpensesListAdapter(this);
        expensesListView.setAdapter(expensesListAdapter);        
        noItemsListInfoLayout = (LinearLayout)findViewById(R.id.noItemsListInfoLayout);
        noExpensesListInfoLayout = (LinearLayout)findViewById(R.id.noExpensesListInfoLayout);
              
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
        List<Server> allServers = serverDAO.getAll();
        if(allServers.size() > 0) {       	    	
        	refreshExpensePlanList();    	
        	refreshItemsList();   
        }
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
		
		List<Server> allServers = serverDAO.getAll();
        if(allServers.size() == 0) {
        	Intent serverActivityIntent = new Intent(this, AddEditServerActivity.class);
        	startActivityForResult(serverActivityIntent, ADD_SERVER_REQUEST_CODE);
        } else {        	
        	//Show tips dialog if needed
			if(PrefsManager.getBoolean(this, PrefKeys.SHOW_MAIN_SCREEN_TIPS, true)) {
				showTipsDialog();
			}

			refreshExpensePlanList();
			refreshItemsList();
			refreshExpensesList();
        }
	}
    
	private void showTipsDialog() {
		TipsDialogUtility.showTipsDialog(this, R.string.main_screen_tip, PrefKeys.SHOW_MAIN_SCREEN_TIPS);
	}

    private void refreshExpensesList() {
    	expensesListAdapter.removeAll();
    	ExpensePlan expensePlan = (ExpensePlan)expensePlanSpinner.getSelectedItem();
    	expensesListAdapter.addAll(expenseDAO.getAllExpensesByExpensePlan(expensePlan));
    	
		if(!itemsTabActive) {				    	
			if(expensesListAdapter.getCount() == 0) {
				expensesListView.setVisibility(View.GONE);
				noExpensesListInfoLayout.setVisibility(View.VISIBLE);
			} else {
				expensesListView.setVisibility(View.VISIBLE);				
				noExpensesListInfoLayout.setVisibility(View.GONE);
			}
			
			itemsListView.setVisibility(View.GONE);
			noItemsListInfoLayout.setVisibility(View.GONE);
		}
    }
    
	private void refreshItemsList() {
		envelopesListAdapter.removeAll();
		ExpensePlan expensePlan = (ExpensePlan)expensePlanSpinner.getSelectedItem();
		envelopesListAdapter.addAll(expensePlanDAO.getAllEnvelopesByExpensePlan(expensePlan));
		
		if(itemsTabActive) {			
			if(envelopesListAdapter.getCount() == 0) {
				itemsListView.setVisibility(View.GONE);				
				noItemsListInfoLayout.setVisibility(View.VISIBLE);
			} else {
				itemsListView.setVisibility(View.VISIBLE);
				noItemsListInfoLayout.setVisibility(View.GONE);
			}
			
			expensesListView.setVisibility(View.GONE);
			noExpensesListInfoLayout.setVisibility(View.GONE);
		}		
	}

	private void refreshExpensePlanList() {
		Server server = getCurrentServer();
		expensePlanAdapter = new ArrayAdapter<ExpensePlan>(this, R.layout.spinner_item, expensePlanDAO.getExpensePlansByServer(server));
		expensePlanSpinner.setAdapter(expensePlanAdapter);
	}

	private Server getCurrentServer() {
		Integer serverid = PrefsManager.getInt(this, PrefKeys.CURRENT_SERVER_ID, -1);
		if(serverid == -1) {
			return null;
		}
		Server server = serverDAO.getServer(serverid);
		return server;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == ADD_EDIT_EXPENSE_REQUEST_CODE && resultCode == RESULT_OK) {			
			refreshExpensesList();
			refreshItemsList();
			
			//Update remainder in expense plan that is in spinner
			ExpensePlan selectedExpensePlan = (ExpensePlan)expensePlanSpinner.getSelectedItem();
			List<ExpensePlan> expensesPlansByServer = expensePlanDAO.getExpensePlansByServer(getCurrentServer());
			for(ExpensePlan expensePlan : expensesPlansByServer) {
				if(expensePlan.equals(selectedExpensePlan)) {
					selectedExpensePlan.setRemainder(expensePlan.getRemainder());
					break;
				}
			}						
		} else if((requestCode == ADD_SERVER_REQUEST_CODE && resultCode == RESULT_OK) || requestCode == SERVERS_REQUEST_CODE) {
			List<Server> allServers = serverDAO.getAll();
        	PrefsManager.saveInt(this, PrefKeys.CURRENT_SERVER_ID, allServers.get(0).getServerid());
			exchangeData();
		} else {
			if(serverDAO.getAll().size() == 0) {
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.expenses_plans, menu);
		return super.onCreateOptionsMenu(menu);
	}
    
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem showHideExpensesIdsItem = menu.findItem(R.id.menu_expenses_plans_show_hide_expense_ids);
		if(PrefsManager.getBoolean(this, PrefKeys.SHOW_EXPENSE_IDS, false)) {
			showHideExpensesIdsItem.setTitle(R.string.hide_expense_ids);
			showHideExpensesIdsItem.setTitleCondensed(getResources().getString(R.string.hide_expense_ids));
		} else {
			showHideExpensesIdsItem.setTitle(R.string.show_expense_ids);
			showHideExpensesIdsItem.setTitleCondensed(getResources().getString(R.string.show_expense_ids));			
		}
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
			case R.id.menu_expenses_plans_accounts:
				Intent accountsActivityIntent = new Intent(this, AccountsActivity.class);
				startActivity(accountsActivityIntent);
				break;
			case R.id.menu_expenses_plans_incomes:
				Intent incomesActivityIntent = new Intent(this, IncomesActivity.class);
				startActivity(incomesActivityIntent);
				break;
			case R.id.menu_expenses_plans_servers:
				Intent serversActivityIntent = new Intent(this, ServersActivity.class);
				startActivity(serversActivityIntent);
				break;
			case R.id.menu_expenses_plans_currencies:
				Intent currenciesActivityIntent = new Intent(this, CurrenciesActivity.class);
				startActivity(currenciesActivityIntent);
				break;				
			case R.id.menu_expenses_plans_tips:
				showTipsDialog();
				break;		
			case R.id.menu_expenses_plans_show_hide_expense_ids:
				boolean previousValue = PrefsManager.getBoolean(this, PrefKeys.SHOW_EXPENSE_IDS, false);
				PrefsManager.saveBoolean(this, PrefKeys.SHOW_EXPENSE_IDS, !previousValue);
				refreshExpensesList();
				break;
			case R.id.menu_expenses_plans_data_exchange:
				exchangeData();
				break;
			default:			
				break;
		}
		return super.onOptionsItemSelected(item);
	}
		
	private void exchangeData() {
		DataExchanger dataExchanger = new DataExchanger(this);
		dataExchanger.exchangeData(new SuccessCallback() {
			
			@Override
			public void execute() {
				refreshExpensePlanList();
				refreshItemsList();
			}
		});
	}

	private final class ExpensePlanSelectionListener implements
			AdapterView.OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			refreshItemsList();	
			refreshExpensesList();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}
	}
	
	//--------------- Click Handlers ------------------------------------
	public void itemsTabClicked(View view) {
		switchItemsTabActive();
		refreshItemsList();
	}
	
	public void expensesTabClicked(View view) {
		switchExpensesTabActive();
		refreshExpensesList();
	}
	
	public void expenseRemovedClicked(View view) {
		Integer position = (Integer)view.getTag();
		final Expense selectedExpense = (Expense)expensesListAdapter.getItem(position);
				
		AlertDialog alert;
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.think_please);
		dialogBuilder.setMessage(getResources().getString(R.string.question_remove_expense)
				+ " \"" + SumLocalizer.localize(selectedExpense.getSum().toPlainString()) + "\"?");

		dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        expenseDAO.delete(selectedExpense);

				ExpensePlan expensePlan = (ExpensePlan)expensePlanSpinner.getSelectedItem();
				ExpensePlan updatedExpensePlan = expensePlanDAO.findById(expensePlan.getExpenseplanid());
				expensePlan.setRemainder(updatedExpensePlan.getRemainder());

		        refreshExpensesList();
		        dialog.dismiss();
		        Toast.makeText(ExpensesPlansActivity.this, R.string.expense_removed, Toast.LENGTH_SHORT).show();
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
	
	public void expenseItemClicked(View view) {
		Integer position = (Integer)view.getTag();
		
		final Expense selectedExpense = (Expense)expensesListAdapter.getItem(position);
		ExpensePlan expensePlan = (ExpensePlan)expensePlanSpinner.getSelectedItem();
		
		Intent editExpenseIntent = new Intent(ExpensesPlansActivity.this, AddEditExpenseActivity.class);
		editExpenseIntent.putExtra(ExtraNames.SELECTED_EXPENSE.name(), selectedExpense);
		editExpenseIntent.putExtra(ExtraNames.SELECTED_EXPENSE_PLAN.name(), expensePlan);
		editExpenseIntent.putExtra(ExtraNames.SELECTED_ITEM.name(), selectedExpense.getEnvelope());
		startActivityForResult(editExpenseIntent, ADD_EDIT_EXPENSE_REQUEST_CODE);
		
	}
	
	public void itemItemClicked(View view) {
		int position = (Integer)view.getTag();
		Envelope item = (Envelope) envelopesListAdapter.getItem(position);
		ExpensePlan expensePlan = (ExpensePlan)expensePlanSpinner.getSelectedItem();
		
		Intent newExpenseIntent = new Intent(ExpensesPlansActivity.this, AddEditExpenseActivity.class);
		newExpenseIntent.putExtra(ExtraNames.SELECTED_EXPENSE_PLAN.name(), expensePlan);
		newExpenseIntent.putExtra(ExtraNames.SELECTED_ITEM.name(), item);
		startActivityForResult(newExpenseIntent, ADD_EDIT_EXPENSE_REQUEST_CODE);
	}
	
	public void viewItemClicked(View view) {
		int position = (Integer)view.getTag();
		Envelope item = (Envelope) envelopesListAdapter.getItem(position);
		ExpensePlan expensePlan = (ExpensePlan)expensePlanSpinner.getSelectedItem();
		
		Intent viewItemIntent = new Intent(ExpensesPlansActivity.this, ViewEnvelopeActivity.class);
		viewItemIntent.putExtra(ExtraNames.SELECTED_EXPENSE_PLAN.name(), expensePlan);
		viewItemIntent.putExtra(ExtraNames.SELECTED_ITEM.name(), item);
		startActivity(viewItemIntent);
	}
	
	public void viewExpensePlanClicked(View view) {
		ExpensePlan expensePlan = (ExpensePlan)expensePlanSpinner.getSelectedItem();
		
		if(expensePlan != null) {
			Intent viewItemIntent = new Intent(ExpensesPlansActivity.this, ViewExpensePlanActivity.class);
			viewItemIntent.putExtra(ExtraNames.SELECTED_EXPENSE_PLAN.name(), expensePlan);
			startActivity(viewItemIntent);				
		}
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