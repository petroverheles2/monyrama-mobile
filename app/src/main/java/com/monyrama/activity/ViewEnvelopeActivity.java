package com.monyrama.activity;

import java.math.BigDecimal;

import junit.framework.Assert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.monyrama.R;
import com.monyrama.db.DAOFactory;
import com.monyrama.model.Currency;
import com.monyrama.model.ExpensePlan;
import com.monyrama.model.Envelope;
import com.monyrama.util.SumLocalizer;

public class ViewEnvelopeActivity extends ActionBarActivity {
    
	private TextView expensePlanTextView;
	private TextView currencyTextView;
	private TextView itemNameTextView;
	private TextView categoryTextView;
	private TextView sumTextView;	
	private TextView spentTextView;
	private TextView remainderTextView;
	private TextView commentTextView;
	
	private ExpensePlan expensePlan;
	private Envelope item;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_envelope);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.envelope_info);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
        
        expensePlanTextView = (TextView)findViewById(R.id.expensePlanValueLabel);
        currencyTextView = (TextView)findViewById(R.id.currencyValueLabel);
        itemNameTextView = (TextView)findViewById(R.id.itemNameValueLabel);
        categoryTextView = (TextView)findViewById(R.id.categoryValueLabel);
        sumTextView = (TextView)findViewById(R.id.sumValueLabel);
        spentTextView = (TextView)findViewById(R.id.spentValueLabel);
        remainderTextView = (TextView)findViewById(R.id.remainderValueLabel);        
        commentTextView = (TextView)findViewById(R.id.commentValueLabel);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        
        Assert.assertNotNull(extras);
        item = (Envelope)extras.get(ExtraNames.SELECTED_ITEM.name());
        expensePlan = (ExpensePlan)extras.get(ExtraNames.SELECTED_EXPENSE_PLAN.name());  
        
        Assert.assertNotNull(item);
        Assert.assertNotNull(expensePlan);
        
        expensePlanTextView.setText(expensePlan.getName());
        Currency currency = DAOFactory.getCurrencyDAO(this).getCurrencyById(expensePlan.getCurrencyId());
        currencyTextView.setText(currency.getName() + "(" + currency.getCode() + ")");
        itemNameTextView.setText(item.getName());
        categoryTextView.setText(item.getCategoryname());
        sumTextView.setText(SumLocalizer.localize(item.getSum().toPlainString()));
        BigDecimal spent = item.getSum().subtract(item.getRemainder());
        spentTextView.setText(SumLocalizer.localize(spent.toPlainString()));
        if(item.isExpensesSumExceeded()) {
        	spentTextView.setTextColor(getResources().getColor(R.color.red));
        	remainderTextView.setTextColor(getResources().getColor(R.color.red));
        }
        remainderTextView.setText(SumLocalizer.localize(item.getRemainder().toPlainString()));
        commentTextView.setText(item.getComment());            
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
