package com.monyrama.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.monyrama.R;
import com.monyrama.db.DAOFactory;
import com.monyrama.model.Account;
import com.monyrama.model.Currency;
import com.monyrama.util.SumLocalizer;

import junit.framework.Assert;

import java.math.BigDecimal;

public class ViewAccountActivity extends ActionBarActivity {
    
	private TextView accountTextView;
	private TextView currencyTextView;
	private TextView sumTextView;
	private TextView commentTextView;
	
	private Account account;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_account);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.account_details);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
        
        accountTextView = (TextView)findViewById(R.id.accountValueLabel);
        currencyTextView = (TextView)findViewById(R.id.currencyValueLabel);
        sumTextView = (TextView)findViewById(R.id.sumValueLabel);
        commentTextView = (TextView)findViewById(R.id.commentValueLabel);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        
        Assert.assertNotNull(extras);
        account = (Account)extras.get(ExtraNames.SELECTED_ACCOUNT.name());

        Assert.assertNotNull(account);

        accountTextView.setText(account.getName());
        Currency currency = DAOFactory.getCurrencyDAO(this).getCurrencyById(account.getCurrencyId());
        currencyTextView.setText(currency.getName() + "(" + currency.getCode() + ")");
        sumTextView.setText(SumLocalizer.localize(account.getSum().toPlainString()));
        if(account.getSum().compareTo(BigDecimal.ZERO) < 0) {
            sumTextView.setTextColor(getResources().getColor(R.color.red));
        }
        commentTextView.setText(account.getComment());
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
