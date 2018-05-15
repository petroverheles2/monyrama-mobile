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
import com.monyrama.db.IncomeDAO;
import com.monyrama.db.IncomeSourceDAO;
import com.monyrama.db.ServerDAO;
import com.monyrama.model.Income;
import com.monyrama.model.IncomeSource;
import com.monyrama.model.Server;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;
import com.monyrama.util.SumLocalizer;

public class IncomesActivity extends ActionBarActivity {
    
    private static final int ANIMATION_INTERVAL = 400;

    private static int ADD_EDIT_INCOME_REQUEST_CODE = 1;

    private Spinner incomeSourcesSpinner;
	private SpinnerAdapter incomeSourcesAdapter;
	private ListView incomesListView;
	private IncomesListAdapter incomeListAdapter;
	private TextView incomesTab;
	private ImageView topPanelControlButton;
	private LinearLayout firstSeparator;
	private LinearLayout tabsLayout;
	private LinearLayout secondSeparator;
	private LinearLayout incomesListLayout;
	private LinearLayout noIncomesListInfoLayout;

	private ServerDAO serverDAO;
	private IncomeSourceDAO incomeSourceDAO;
	private IncomeDAO incomeDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incomes);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.incomes);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);

        incomeSourcesSpinner = (Spinner)findViewById(R.id.incomeSourceSpinner);
        incomeSourcesSpinner.setOnItemSelectedListener(new IncomeSourceSelectionListener());
        
        incomesTab = (TextView)findViewById(R.id.incomesTab);
        topPanelControlButton = (ImageView)findViewById(R.id.topPanelControlButton);
        firstSeparator = (LinearLayout)findViewById(R.id.firstSeparator);
        tabsLayout = (LinearLayout)findViewById(R.id.tabsLayout);
        secondSeparator = (LinearLayout)findViewById(R.id.secondSeparator);
        incomesListLayout = (LinearLayout)findViewById(R.id.incomesListLayout);
        incomesListView = (ListView)findViewById(R.id.incomesListView);
        incomeListAdapter = new IncomesListAdapter(this);
        incomesListView.setAdapter(incomeListAdapter);
        noIncomesListInfoLayout = (LinearLayout)findViewById(R.id.noIncomesListInfoLayout);
        
        //Creating daos
        serverDAO = DAOFactory.getServerDAO(this); 
        incomeSourceDAO = DAOFactory.getIncomeSourceDAO(this);
        incomeDAO = DAOFactory.getIncomeDAO(this);
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshIncomeSourceList();
		refreshIncomesList();
	}
    
	private void showTipsDialog() {
		TipsDialogUtility.showTipsDialog(this, R.string.main_screen_tip, PrefKeys.SHOW_MAIN_SCREEN_TIPS);
	}

    private void refreshIncomesList() {
    	incomeListAdapter.removeAll();
    	IncomeSource incomeSource = (IncomeSource) incomeSourcesSpinner.getSelectedItem();
		incomeListAdapter.addAll(incomeDAO.getAllIncomesByIncomeSource(incomeSource));

        if(incomeListAdapter.getCount() == 0) {
            incomesListView.setVisibility(View.GONE);
            noIncomesListInfoLayout.setVisibility(View.VISIBLE);
        } else {
            incomesListView.setVisibility(View.VISIBLE);
            noIncomesListInfoLayout.setVisibility(View.GONE);
        }
    }

	private void refreshIncomeSourceList() {
		Server server = getCurrentServer();
		incomeSourcesAdapter = new ArrayAdapter<IncomeSource>(this, R.layout.spinner_item, incomeSourceDAO.getIncomeSourceByServer(server));
		incomeSourcesSpinner.setAdapter(incomeSourcesAdapter);
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.incomes, menu);
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
			case R.id.menu_incomes_accounts:
				Intent accountsActivityIntent = new Intent(this, AccountsActivity.class);
				startActivity(accountsActivityIntent);
				break;
			case R.id.menu_incomes_expenses:
				Intent expensesActivityIntent = new Intent(this, ExpensesPlansActivity.class);
				startActivity(expensesActivityIntent);
				break;
			case R.id.menu_incomes_servers:
				Intent serversActivityIntent = new Intent(this, ServersActivity.class);
				startActivity(serversActivityIntent);
				break;
			case R.id.menu_incomes_currencies:
				Intent currenciesActivityIntent = new Intent(this, CurrenciesActivity.class);
				startActivity(currenciesActivityIntent);
				break;
			case R.id.menu_incomes_tips:
				showTipsDialog();
				break;
			case R.id.menu_incomes_data_exchange:
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
				refreshIncomeSourceList();
				refreshIncomesList();
			}
		});
	}

	private final class IncomeSourceSelectionListener implements
			AdapterView.OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			refreshIncomesList();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}
	}
	
	//--------------- Click Handlers ------------------------------------
	public void viewIncomeSourceClicked(View view) {
		IncomeSource incomeSource = (IncomeSource) incomeSourcesSpinner.getSelectedItem();

		Intent viewIncomeSourceIntent = new Intent(IncomesActivity.this, ViewIncomeSourceActivity.class);
		viewIncomeSourceIntent.putExtra(ExtraNames.SELECTED_INCOME_SOURCE.name(), incomeSource);
		startActivity(viewIncomeSourceIntent);
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
            incomesListLayout.startAnimation(slide);
            
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
            incomesListLayout.startAnimation(slide);
			topPanelControlButton.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
		}
	}

	public void addIncomeClicked(View button) {
        Intent newIncomeIntent = new Intent(IncomesActivity.this, AddEditIncomeActivity.class);
        IncomeSource incomeSource = (IncomeSource)incomeSourcesSpinner.getSelectedItem();
        newIncomeIntent.putExtra(ExtraNames.SELECTED_INCOME_SOURCE.name(), incomeSource);
        startActivityForResult(newIncomeIntent, ADD_EDIT_INCOME_REQUEST_CODE);
	}

    public void incomeRemovedClicked(View view) {
        Integer position = (Integer)view.getTag();
        final Income selectedIncome = (Income)incomeListAdapter.getItem(position);

        AlertDialog alert;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.think_please);
        dialogBuilder.setMessage(getResources().getString(R.string.question_remove_income)
                + " \"" + SumLocalizer.localize(selectedIncome.getSum().toPlainString()) + "\"?");

        dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                incomeDAO.delete(selectedIncome);

                IncomeSource incomeSource = (IncomeSource)incomeSourcesSpinner.getSelectedItem();
                IncomeSource updatedIncomeSource = incomeSourceDAO.findById(incomeSource.getIncomeSourceId());
                incomeSource.setSum(updatedIncomeSource.getSum());

                refreshIncomesList();
                dialog.dismiss();
                Toast.makeText(IncomesActivity.this, R.string.income_removed, Toast.LENGTH_SHORT).show();
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

    public void incomeItemClicked(View view) {
        Integer position = (Integer)view.getTag();

        final Income selectedIncome = (Income)incomeListAdapter.getItem(position);
        IncomeSource incomeSource = (IncomeSource)incomeSourcesSpinner.getSelectedItem();

        Intent editExpenseIntent = new Intent(IncomesActivity.this, AddEditIncomeActivity.class);
        editExpenseIntent.putExtra(ExtraNames.SELECTED_INCOME.name(), selectedIncome);
        editExpenseIntent.putExtra(ExtraNames.SELECTED_INCOME_SOURCE.name(), incomeSource);
        startActivityForResult(editExpenseIntent, ADD_EDIT_INCOME_REQUEST_CODE);
    }
}