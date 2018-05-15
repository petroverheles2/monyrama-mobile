package com.monyrama.activity;

import java.util.List;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.monyrama.R;
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
        
        Integer serverid = PrefsManager.getInt(this, PrefKeys.CURRENT_SERVER_ID, -1);
        if(serverid != -1) {
            List<Currency> currencies = DAOFactory.getCurrencyDAO(this).getAllCurrenciesByServer(serverid);
            if(currencies.size() > 0) {
            	listAdapter.addAll(currencies);
            }
        }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		    // Respond to the action bar's Up/Home button
		    case android.R.id.home:
		        super.onBackPressed();
		        break;	
			default:			
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}