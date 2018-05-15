package com.monyrama.activity;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.monyrama.R;
import com.monyrama.dataexchange.DataExchanger;
import com.monyrama.db.DAOFactory;
import com.monyrama.model.Account;
import com.monyrama.model.Server;
import com.monyrama.model.Transfer;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;

import java.util.List;

public class AccountsActivity extends ActionBarActivity {
    
    private static final int ANIMATION_INTERVAL = 400;
	private static final int ADD_TRANSFER_REQUEST_CODE = 1;

	private ListView accountsListView;
	private AccountListAdapter accountListAdapter;
	private TransferListAdapter transferListAdapter;
	private ListView transferListView;
	private TextView accountsTab;
	private TextView transfersTab;
	private ImageView topPanelControlButton;
	private LinearLayout firstSeparator;
	private LinearLayout tabsLayout;
	private LinearLayout secondSeparator;
	private LinearLayout transfersAndAccountsListLayout;
	private LinearLayout noAccountsListInfoLayout;
	private LinearLayout noTransfersListInfoLayout;

	private boolean accountsTabActive = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts);
        
		ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.accounts);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
        
        accountsTab = (TextView)findViewById(R.id.itemsTab);
        transfersTab = (TextView)findViewById(R.id.expensesTab);
        topPanelControlButton = (ImageView)findViewById(R.id.topPanelControlButton);
        firstSeparator = (LinearLayout)findViewById(R.id.firstSeparator);
        tabsLayout = (LinearLayout)findViewById(R.id.tabsLayout);
        secondSeparator = (LinearLayout)findViewById(R.id.secondSeparator);
		transfersAndAccountsListLayout = (LinearLayout)findViewById(R.id.transfersAndAccountsListLayout);
        accountsListView = (ListView)findViewById(R.id.accountsListView);
        accountListAdapter = new AccountListAdapter(this);
        accountsListView.setAdapter(accountListAdapter);
		transferListView = (ListView)findViewById(R.id.transfersListView);
        transferListAdapter = new TransferListAdapter(this);
		transferListView.setAdapter(transferListAdapter);
        noAccountsListInfoLayout = (LinearLayout)findViewById(R.id.noAccountsListInfoLayout);
        noTransfersListInfoLayout = (LinearLayout)findViewById(R.id.noTransfersListInfoLayout);

        switchAccountsTabActive();
        
        //Fill up data on start up
		refreshAccountsList();
    }

	private void refreshAccountsList() {
		Integer serverid = PrefsManager.getInt(this, PrefKeys.CURRENT_SERVER_ID, -1);
		if(serverid != -1) {
			List<Account> accounts = DAOFactory.getAccountDAO(this).getAccountsByServer(serverid);
			if(accounts.size() > 0) {
				accountListAdapter.removeAll();
				accountListAdapter.addAll(accounts);
			}
		}

		if(accountsTabActive) {
			if(accountListAdapter.getCount() == 0) {
				accountsListView.setVisibility(View.GONE);
				noAccountsListInfoLayout.setVisibility(View.VISIBLE);
			} else {
				accountsListView.setVisibility(View.VISIBLE);
				noAccountsListInfoLayout.setVisibility(View.GONE);
			}

			transferListView.setVisibility(View.GONE);
			noTransfersListInfoLayout.setVisibility(View.GONE);
		}
	}

	private void refreshTransfersList() {
		transferListAdapter.removeAll();

		List<Transfer> transfers = DAOFactory.getTransferDAO(this).getTransfersByServer(getCurrentServer());
		if(transfers.size() > 0) {
			transferListAdapter.addAll(transfers);
		}

		if(!accountsTabActive) {
			if(transferListAdapter.getCount() == 0) {
				transferListView.setVisibility(View.GONE);
				noTransfersListInfoLayout.setVisibility(View.VISIBLE);
			} else {
				transferListView.setVisibility(View.VISIBLE);
				noTransfersListInfoLayout.setVisibility(View.GONE);
			}

			accountsListView.setVisibility(View.GONE);
			noAccountsListInfoLayout.setVisibility(View.GONE);
		}
	}

	private void switchAccountsTabActive() {
		accountsTabActive = true;
		accountsTab.setBackgroundResource(R.drawable.tab_active);
		transfersTab.setBackgroundResource(R.drawable.tab_passive);
	}
	
	private void switchTransfersTabActive() {
		accountsTabActive = false;
		transfersTab.setBackgroundResource(R.drawable.tab_active);
		accountsTab.setBackgroundResource(R.drawable.tab_passive);
	}
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshAccountsList();
		refreshTransfersList();
	}
    
	private void showTipsDialog() {
		TipsDialogUtility.showTipsDialog(this, R.string.main_screen_tip, PrefKeys.SHOW_MAIN_SCREEN_TIPS);
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == ADD_TRANSFER_REQUEST_CODE && resultCode == RESULT_OK) {
			refreshAccountsList();
			refreshTransfersList();
		}
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
			case R.id.menu_accounts_expenses:
				Intent expensesActivityIntent = new Intent(this, ExpensesPlansActivity.class);
				startActivity(expensesActivityIntent);
				break;
			case R.id.menu_accounts_incomes:
				Intent incomesActivityIntent = new Intent(this, IncomesActivity.class);
				startActivity(incomesActivityIntent);
				break;
			case R.id.menu_accounts_servers:
				Intent serversActivityIntent = new Intent(this, ServersActivity.class);
				startActivity(serversActivityIntent);
				break;
			case R.id.menu_accounts_currencies:
				Intent currenciesActivityIntent = new Intent(this, CurrenciesActivity.class);
				startActivity(currenciesActivityIntent);
				break;
			case R.id.menu_accounts_tips:
				showTipsDialog();
				break;
			case R.id.menu_accounts_data_exchange:
				exchangeData();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void exchangeData() {
		DataExchanger dataExchanger = new DataExchanger(this);
		dataExchanger.exchangeData(new DataExchanger.SuccessCallback() {

			@Override
			public void execute() {
				refreshAccountsList();
				refreshTransfersList();
			}
		});
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
		switchAccountsTabActive();
		refreshAccountsList();
	}
	
	public void expensesTabClicked(View view) {
		switchTransfersTabActive();
		refreshTransfersList();
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
            transfersAndAccountsListLayout.startAnimation(slide);
            
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
            transfersAndAccountsListLayout.startAnimation(slide);
			topPanelControlButton.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
		}
	}

	public void viewAccountClicked(View view) {
		int position = (Integer)view.getTag();
		Account account = (Account)accountListAdapter.getItem(position);

		Intent viewAccountIntent = new Intent(AccountsActivity.this, ViewAccountActivity.class);
		viewAccountIntent.putExtra(ExtraNames.SELECTED_ACCOUNT.name(), account);
		startActivity(viewAccountIntent);
	}

	public void addTransferClicked(View button) {
		Intent newTransferIntent = new Intent(AccountsActivity.this, AddTransferActivity.class);
		startActivityForResult(newTransferIntent, ADD_TRANSFER_REQUEST_CODE);
	}

	public void removeTransferClicked(View view) {
		int position = (Integer)view.getTag();
		final Transfer transfer = (Transfer)transferListAdapter.getItem(position);

		AlertDialog alert;
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.think_please);
		dialogBuilder.setMessage(getResources().getString(R.string.question_remove_transfer) + "?");

		dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				DAOFactory.getTransferDAO(AccountsActivity.this).delete(transfer);

				dialog.dismiss();
				Toast.makeText(AccountsActivity.this, R.string.transfer_removed, Toast.LENGTH_SHORT).show();

				refreshTransfersList();
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

	private Server getCurrentServer() {
		Integer serverid = PrefsManager.getInt(this, PrefKeys.CURRENT_SERVER_ID, -1);
		if(serverid == -1) {
			return null;
		}
		Server server = DAOFactory.getServerDAO(this).getServer(serverid);
		return server;
	}


}