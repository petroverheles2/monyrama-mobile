package com.monyrama.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.monyrama.R;
import com.monyrama.dataexchange.DataExchanger;
import com.monyrama.db.DAOFactory;
import com.monyrama.model.Currency;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;

public class CurrenciesActivity extends ActionBarActivity {
    	
	private ListView itemsListView;
	private CurrenciesListAdapter listAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currencies);
        
		ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.currencies);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);

        itemsListView = (ListView)findViewById(R.id.itemsListView);
        listAdapter = new CurrenciesListAdapter(this);        
        itemsListView.setAdapter(listAdapter);

        refreshCurrenciesList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.currencies, menu);
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.menu_currencies_accounts:
                Intent accountsActivityIntent = new Intent(this, AccountsActivity.class);
                startActivity(accountsActivityIntent);
                break;
            case R.id.menu_currencies_expenses:
                Intent expensesActivityIntent = new Intent(this, ExpensesPlansActivity.class);
                startActivity(expensesActivityIntent);
                break;
            case R.id.menu_currencies_incomes:
                Intent incomesActivityIntent = new Intent(this, IncomesActivity.class);
                startActivity(incomesActivityIntent);
                break;
            case R.id.menu_currencies_servers:
                Intent serversActivityIntent = new Intent(this, ServersActivity.class);
                startActivity(serversActivityIntent);
                break;
            case R.id.menu_currencies_tips:
                //showTipsDialog();
                break;
            case R.id.menu_currencies_data_exchange:
                exchangeData();
                break;
            default:
                break;
		}
		return super.onOptionsItemSelected(item);
	}

    private void refreshCurrenciesList() {
        listAdapter.removeAll();
        Integer serverid = PrefsManager.getInt(this, PrefKeys.CURRENT_SERVER_ID, -1);
        if(serverid != -1) {
            List<Currency> currencies = DAOFactory.getCurrencyDAO(this).getAllCurrenciesByServer(serverid);
            if(currencies.size() > 0) {
                listAdapter.addAll(currencies);
            }
        }
    }

    private void exchangeData() {
        DataExchanger dataExchanger = new DataExchanger(this);
        dataExchanger.exchangeData(new DataExchanger.SuccessCallback() {

            @Override
            public void execute() {
                refreshCurrenciesList();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCurrenciesList();
    }
}