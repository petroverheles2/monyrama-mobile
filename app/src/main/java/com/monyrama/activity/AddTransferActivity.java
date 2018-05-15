package com.monyrama.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.monyrama.R;
import com.monyrama.db.AccountDAO;
import com.monyrama.db.DAOFactory;
import com.monyrama.db.TransferDAO;
import com.monyrama.model.Account;
import com.monyrama.model.Server;
import com.monyrama.model.Transfer;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;
import com.monyrama.validator.Validator;
import com.monyrama.validator.ValidatorFactory;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddTransferActivity extends FragmentActivity {
	private EditText fromSumField;
	private EditText toSumField;
	private static EditText dateField;
	private Spinner fromAccountSpinner;
	private Spinner toAccountSpinner;

	private ArrayAdapter<Account> fromAccountAdapter;
	private ArrayAdapter<Account> toAccountAdapter;

	private static Date transferDate;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_transfer);

		fromSumField = (EditText)findViewById(R.id.fromSumField);
		toSumField = (EditText)findViewById(R.id.toSumField);

		fromAccountSpinner = (Spinner)findViewById(R.id.fromAccountSpinner);
		toAccountSpinner = (Spinner)findViewById(R.id.toAccountSpinner);

		toAccountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				copyToSum();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		fromAccountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				copyToSum();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		dateField = (EditText)findViewById(R.id.transferDate);

		refreshAccountsList();

		transferDate = new Date();

		dateField.setText(DateFormat.getDateInstance().format(transferDate));
    }

	private void copyToSum() {
		Account fromAccount = (Account) fromAccountSpinner.getSelectedItem();
		Account toAccount = (Account) toAccountSpinner.getSelectedItem();

		if(!fromAccount.getAccountId().equals(toAccount.getAccountId())
            && fromAccount.getCurrencyId().equals(toAccount.getCurrencyId())) {
            toSumField.setText(fromSumField.getText());
        }
	}

	private void refreshAccountsList() {
		AccountDAO accountDAO = DAOFactory.getAccountDAO(this);
		List<Account> accountList = accountDAO.getAccountsByServer(getCurrentServer().getServerid());

		toAccountAdapter = new ArrayAdapter<Account>(this, R.layout.spinner_item, accountList);
		toAccountSpinner.setAdapter(toAccountAdapter);

		fromAccountAdapter = new ArrayAdapter<Account>(this, R.layout.spinner_item, accountList);
		fromAccountSpinner.setAdapter(fromAccountAdapter);
	}
	
	private Transfer createTransferFromFields() {
		Transfer transfer = new Transfer();

		transfer.setServerId(getCurrentServer().getServerid());
		transfer.setFromAccount(((Account) fromAccountSpinner.getSelectedItem()));
		transfer.setToAccount(((Account) toAccountSpinner.getSelectedItem()));
		transfer.setFromRawSum(fromSumField.getText().toString().trim().replace(',', '.')); //replacing , with dot for ru and uk locales
		transfer.setToRawSum(toSumField.getText().toString().trim().replace(',', '.')); //replacing , with dot for ru and uk locales
		transfer.setDate(transferDate);

		return transfer;
	}
	
	public void saveClicked(View view) {
		Transfer transfer = createTransferFromFields();
		Validator validator = ValidatorFactory.createTransferValidator(transfer, this);
		if(validator.validate()) {
			transfer.setSumsFromRawSums();
			TransferDAO transferDAO = DAOFactory.getTransferDAO(this);
			transferDAO.save(transfer);
			setResult(RESULT_OK);
			Toast.makeText(this, R.string.transfer_saved, Toast.LENGTH_SHORT).show();
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
			transferDate = c.getTime();
			dateField.setText(DateFormat.getDateInstance().format(transferDate));
		}
	}

	public void dateClicked(View view) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}
}
