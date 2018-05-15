package com.monyrama.activity;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.Assert;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.monyrama.R;
import com.monyrama.activity.sumkeyboard.NextKeyListener;
import com.monyrama.activity.sumkeyboard.SumKeyboardView;
import com.monyrama.db.DAOFactory;
import com.monyrama.db.ExpenseDAO;
import com.monyrama.db.ExpensePlanDAO;
import com.monyrama.model.Expense;
import com.monyrama.model.ExpensePlan;
import com.monyrama.model.Envelope;
import com.monyrama.util.SumLocalizer;
import com.monyrama.validator.Validator;
import com.monyrama.validator.ValidatorFactory;

public class AddEditExpenseActivity extends Activity {
	private InputMethodManager inputManager;
	
	private TextView expensePlanTextView;
	private TextView itemTextView;
	private TextView categoryTextView;
	private EditText sumField;
	private AutoCompleteTextView commentsField;
	private DatePicker datePicker;
	
	private SumKeyboardView sumKeyboardView;
	
	private Expense expense;
	private ExpensePlan expensePlan;
	private Envelope item;
	
	private BigDecimal previousSum; //need this to recalculate item remainder if we edit expense sum
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_expense);
        
		inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        
        expensePlanTextView = (TextView)findViewById(R.id.expensesPlanValueField);
        itemTextView = (TextView)findViewById(R.id.itemValueField);
        categoryTextView = (TextView)findViewById(R.id.categoryValueField);
        sumField = (EditText)findViewById(R.id.sumField);
        commentsField = (AutoCompleteTextView)findViewById(R.id.commentField);
        commentsField.setThreshold(1);
        datePicker = (DatePicker)findViewById(R.id.expenseDate);
        
        ExpenseCommentAdapter adapter = new ExpenseCommentAdapter(this, DAOFactory.getSavedCommentsDAO(this).getAll());
        commentsField.setAdapter(adapter);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        
        Assert.assertNotNull(extras);
        item = (Envelope)extras.get(ExtraNames.SELECTED_ITEM.name());
        expensePlan = (ExpensePlan)extras.get(ExtraNames.SELECTED_EXPENSE_PLAN.name());  
        
        Assert.assertNotNull(item);
        Assert.assertNotNull(expensePlan);
        
        expensePlanTextView.setText(expensePlan.getName());
        itemTextView.setText(item.toString());
        categoryTextView.setText(item.getCategoryname());
        
       	expense = (Expense)extras.get(ExtraNames.SELECTED_EXPENSE.name());
       	if(expense != null) {
       		TextView titleView = (TextView)findViewById(R.id.addEditExpenseTitleView);
       		titleView.setText(R.string.editExpense);
       		
       		sumField.setText(SumLocalizer.localize(expense.getSum().toPlainString()));
       		commentsField.setText(expense.getComment());
       		datePicker.init(expense.getDate().getYear() + 1900, expense.getDate().getMonth(), expense.getDate().getDate(), null);
       		
       		previousSum = expense.getSum();
       	}
       	
	    sumField.setInputType(InputType.TYPE_NULL);
	    sumField.setSelection(sumField.getText() != null ? sumField.getText().length() : 0);
			
		sumField.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {	    
					inputManager.hideSoftInputFromWindow(AddEditExpenseActivity.this.getCurrentFocus().getWindowToken(), 0);
					showSumKeyboard();
				} else {
					hideSumKeyboard();
				}
			}
		});

		sumKeyboardView = (SumKeyboardView) findViewById(R.id.keyboard_view);
		sumKeyboardView.setTargetActivity(this);
		sumKeyboardView.setNextKeyListener(new NextKeyListener() {			
			@Override
			public void nextKeyPressed() {
				commentsField.requestFocus();
			}
		});
		
		commentsField.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {			
				if(hasFocus) {
					inputManager.showSoftInput(v, 0);
				}
			}
		});
    }
	
	private void populateExpenseFromFields() {
		if(expense == null) {
			expense = new Expense();
		}
		
		expense.setServerid(item.getServerid());
		expense.setItem(item);
		expense.setExpenseplanid(item.getExpensplanid());
		expense.setSum(new BigDecimal(sumField.getText().toString().trim().replace(',', '.'))); //replacing , with dot for ru and uk locales
		expense.setComment(commentsField.getText().toString().trim());
		expense.setDate(new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth()));
	}
	
	public void saveClicked(View view) {
		populateExpenseFromFields();
		Validator validator = ValidatorFactory.createExpenseValidator(expense, this);
		if(validator.validate()) {
			ExpenseDAO expenseDAO = DAOFactory.getExpenseDAO(this);
			expenseDAO.createOrUpdateExpense(expense);
			
			boolean isNewExpense = expense.getExpenseid() == null;
			if(isNewExpense) { //New expense, we need to recalculate the expense plan and item remainder				
				BigDecimal newItemRemainder = item.getRemainder().subtract(expense.getSum());				
				item.setRemainder(newItemRemainder);								
				
				BigDecimal newExpensePlanRemainder = expensePlan.getRemainder().subtract(expense.getSum());
				expensePlan.setRemainder(newExpensePlanRemainder);
				
				ExpensePlanDAO expensePlanDAO = DAOFactory.getExpensePlanDAO(this);
				expensePlanDAO.updateRemainder(expensePlan, item);
			} else {
				boolean sumChanged = !previousSum.equals(expense.getSum());
				if(sumChanged) { //Expense sum changed, we need to recalculate the expense plan and item remainder
					BigDecimal previousAndNewSumDiff = previousSum.subtract(expense.getSum());
					
					BigDecimal newItemRemainder = item.getRemainder().add(previousAndNewSumDiff);
					item.setRemainder(newItemRemainder);
					
					BigDecimal newExpensePlanRemainder = expensePlan.getRemainder().add(previousAndNewSumDiff);
					expensePlan.setRemainder(newExpensePlanRemainder);
										
					ExpensePlanDAO expensePlanDAO = DAOFactory.getExpensePlanDAO(this);
					expensePlanDAO.updateRemainder(expensePlan, item);
				}
			}
			
			DAOFactory.getSavedCommentsDAO(this).saveIfNew(expense.getComment());
			
			setResult(RESULT_OK);
			Toast.makeText(this, R.string.expense_saved, Toast.LENGTH_SHORT).show();
			finish();
		} else {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);				
			dialogBuilder.setMessage(validator.getMessage());				
			dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
			    	dialog.dismiss();
			    }
			});
			AlertDialog alert = dialogBuilder.create();
			alert.show();
		}		
	}
	
	public void cancelClicked(View view) {
    	super.onBackPressed();
    }	
	
	private void showSumKeyboard() {		
		if (sumKeyboardView.getVisibility() == View.GONE) {
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.show_from_bottom);
			sumKeyboardView.showWithAnimation(animation);	
		}
	}
	
	private void hideSumKeyboard() {
		if (sumKeyboardView.getVisibility() == View.VISIBLE) {
			if(inputManager.isActive()) {
				sumKeyboardView.setVisibility(View.GONE);
			} else {
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.hide_to_bottom);
				sumKeyboardView.hideWithAnimation(animation);					
			}
		}
	}

	@Override
	public void onBackPressed() {
		if(sumKeyboardView.getVisibility() == View.VISIBLE) {
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.hide_to_bottom);
			sumKeyboardView.hideWithAnimation(animation);
		} else {
			super.onBackPressed();	
		}		
	}
	
	
}
