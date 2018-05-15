package com.monyrama.activity;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.monyrama.R;
import com.monyrama.db.AccountDAO;
import com.monyrama.db.DAOFactory;
import com.monyrama.db.ExpenseDAO;
import com.monyrama.model.Account;
import com.monyrama.model.Expense;
import com.monyrama.model.ExpensePlan;
import com.monyrama.model.Envelope;
import com.monyrama.model.Server;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;
import com.monyrama.validator.Validator;
import com.monyrama.validator.ValidatorFactory;

public class AddEditExpenseActivity extends FragmentActivity {
	private InputMethodManager inputManager;
	
	private TextView expensePlanTextView;
	private TextView itemTextView;
	private TextView categoryTextView;
	private EditText sumField;
	private static EditText dateField;
	private AutoCompleteTextView commentsField;
	private Spinner accountSpinner;
	private ArrayAdapter<Account> accountAdapter;

	private Expense expense;
	private ExpensePlan expensePlan;
	private Envelope item;
	
	private BigDecimal previousSum; //need this to recalculate item remainder if we edit expense sum

	private static Date expenseDate;
	
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
		dateField = (EditText)findViewById(R.id.expenseDate);
		accountSpinner = (Spinner)findViewById(R.id.fromAccountSpinner);

		CommentAdapter adapter = new CommentAdapter(this, DAOFactory.getExpensesSavedCommentsDAO(this).getAll());
        commentsField.setAdapter(adapter);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        
        Assert.assertNotNull(extras);
        item = (Envelope)extras.get(ExtraNames.SELECTED_ITEM.name());
        expensePlan = (ExpensePlan)extras.get(ExtraNames.SELECTED_EXPENSE_PLAN.name());  
        
        Assert.assertNotNull(item);
        Assert.assertNotNull(expensePlan);

		refreshAccountsList();

		expensePlanTextView.setText(expensePlan.getName());
        itemTextView.setText(item.toString());
        categoryTextView.setText(item.getCategoryname());
        
       	expense = (Expense)extras.get(ExtraNames.SELECTED_EXPENSE.name());
       	if(expense != null) {
       		TextView titleView = (TextView)findViewById(R.id.addEditExpenseTitleView);
       		titleView.setText(R.string.editExpense);
       		
       		sumField.setText(expense.getSum().toPlainString());
       		commentsField.setText(expense.getComment());

       		previousSum = expense.getSum();
			expenseDate = expense.getDate();
       	} else {
			expenseDate = new Date();
		}

		dateField.setText(DateFormat.getDateInstance().format(expenseDate));

		commentsField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					inputManager.showSoftInput(v, 0);
				}
			}
		});
    }

	private void refreshAccountsList() {
		AccountDAO accountDAO = DAOFactory.getAccountDAO(this);
		List<Account> accountList = accountDAO.getAccountsByServerAndCurrency(getCurrentServer().getServerid(), expensePlan.getCurrencyId());

		accountAdapter = new ArrayAdapter<Account>(this, R.layout.spinner_item, accountList);
		accountSpinner.setAdapter(accountAdapter);

		long lastSelectedAccountId = PrefsManager.getLong(this, PrefKeys.LAST_SELECTED_ACCOUNT_ID, -1);
		if(lastSelectedAccountId != -1) {
			for(int i = 0; i < accountList.size(); i++) {
				if(accountList.get(i).getAccountId().equals(lastSelectedAccountId)) {
					accountSpinner.setSelection(i);
				}
			}
		}
	}
	
	private void populateExpenseFromFields() {
		if(expense == null) {
			expense = new Expense();
		}
		
		expense.setServerid(item.getServerid());
		expense.setEnvelope(item);
		expense.setExpenseplanid(item.getExpensplanid());
		expense.setRawSum((sumField.getText().toString().trim().replace(',', '.'))); //replacing , with dot for ru and uk locales
		expense.setComment(commentsField.getText().toString().trim());
		expense.setAccount(((Account) accountSpinner.getSelectedItem()));
		expense.setDate(expenseDate);
	}
	
	public void saveClicked(View view) {
		populateExpenseFromFields();
		Validator validator = ValidatorFactory.createExpenseValidator(expense, this);
		if(validator.validate()) {
			expense.setSumFromRawSum();
			ExpenseDAO expenseDAO = DAOFactory.getExpenseDAO(this);
			Account account = (Account)accountSpinner.getSelectedItem();
			expenseDAO.createOrUpdateExpense(expense, previousSum, expensePlan, item, account);
			
			DAOFactory.getExpensesSavedCommentsDAO(this).saveIfNew(expense.getComment());
			
			setResult(RESULT_OK);
			Toast.makeText(this, R.string.expense_saved, Toast.LENGTH_SHORT).show();

			PrefsManager.saveLong(this, PrefKeys.LAST_SELECTED_ACCOUNT_ID, expense.getAccount().getAccountId());

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

	private Server getCurrentServer() {
		Integer serverid = PrefsManager.getInt(this, PrefKeys.CURRENT_SERVER_ID, -1);
		if(serverid == -1) {
			return null;
		}
		Server server = DAOFactory.getServerDAO(this).getServer(serverid);
		return server;
	}

	public static class DatePickerFragment extends DialogFragment
			implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			final Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month);
			c.set(Calendar.DAY_OF_MONTH, day);
			expenseDate = c.getTime();
			dateField.setText(DateFormat.getDateInstance().format(expenseDate));
		}
	}

	public void dateClicked(View view) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}
}
