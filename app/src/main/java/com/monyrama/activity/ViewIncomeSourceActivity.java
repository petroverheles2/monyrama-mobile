package com.monyrama.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.monyrama.R;
import com.monyrama.db.DAOFactory;
import com.monyrama.model.Currency;
import com.monyrama.model.IncomeSource;
import com.monyrama.util.SumLocalizer;

import junit.framework.Assert;

import java.math.BigDecimal;

public class ViewIncomeSourceActivity extends ActionBarActivity {
    
	private TextView incomeSourceTextView;
	private TextView currencyTextView;
	private TextView sumTextView;
	private TextView commentTextView;
	
	private IncomeSource incomeSource;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_income_source);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.income_source_info);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);

        incomeSourceTextView = (TextView)findViewById(R.id.incomeSourceValueLabel);
        currencyTextView = (TextView)findViewById(R.id.currencyValueLabel);
        sumTextView = (TextView)findViewById(R.id.sumValueLabel);
        commentTextView = (TextView)findViewById(R.id.commentValueLabel);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        
        Assert.assertNotNull(extras);
        incomeSource = (IncomeSource)extras.get(ExtraNames.SELECTED_INCOME_SOURCE.name());
        
        Assert.assertNotNull(incomeSource);
        
        incomeSourceTextView.setText(incomeSource.getName());
        Currency currency = DAOFactory.getCurrencyDAO(this).getCurrencyById(incomeSource.getCurrencyId());
        currencyTextView.setText(currency.getName() + "(" + currency.getCode() + ")");
        sumTextView.setText(SumLocalizer.localize(incomeSource.getSum().toPlainString()));
        if(incomeSource.getSum().compareTo(BigDecimal.ZERO) < 0) {
            sumTextView.setTextColor(getResources().getColor(R.color.red));
        }
        commentTextView.setText(incomeSource.getComment());
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
