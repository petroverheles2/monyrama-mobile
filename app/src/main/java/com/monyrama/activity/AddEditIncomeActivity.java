package com.monyrama.activity;

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
import com.monyrama.db.IncomeDAO;
import com.monyrama.model.Account;
import com.monyrama.model.Income;
import com.monyrama.model.IncomeSource;
import com.monyrama.model.Server;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;
import com.monyrama.validator.Validator;
import com.monyrama.validator.ValidatorFactory;

import junit.framework.Assert;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by petroverheles on 7/10/16.
 */
public class AddEditIncomeActivity extends FragmentActivity {
    private InputMethodManager inputManager;

    private static EditText dateField;
    private TextView incomeSourceTextView;
    private EditText sumField;
    private AutoCompleteTextView commentsField;
    private Spinner accountSpinner;
    private ArrayAdapter<Account> accountAdapter;

    private IncomeSource incomeSource;
    private Income income;

    private BigDecimal previousSum;

    private static Date incomeDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_income);

        inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        incomeSourceTextView = (TextView)findViewById(R.id.incomeSourceValueField);
        sumField = (EditText)findViewById(R.id.sumField);
        commentsField = (AutoCompleteTextView)findViewById(R.id.commentField);
        commentsField.setThreshold(1);
        dateField = (EditText)findViewById(R.id.incomeDate);
        accountSpinner = (Spinner)findViewById(R.id.fromAccountSpinner);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        Assert.assertNotNull(extras);

        incomeSource = (IncomeSource)extras.get(ExtraNames.SELECTED_INCOME_SOURCE.name());

        Assert.assertNotNull(incomeSource);

        refreshAccountsList();

        incomeSourceTextView.setText(incomeSource.getName());

        income = (Income)extras.get(ExtraNames.SELECTED_INCOME.name());
        if(income != null) {
            TextView titleView = (TextView)findViewById(R.id.dialogTitle);
            titleView.setText(R.string.editIncome);

            sumField.setText(income.getSum().toPlainString());
            commentsField.setText(income.getComment());

            previousSum = income.getSum();
            incomeDate = income.getDate();
        } else {
            incomeDate = new Date();
        }

        dateField.setText(DateFormat.getDateInstance().format(incomeDate));

        commentsField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    inputManager.showSoftInput(v, 0);
                }
            }
        });
        dateField.setText(DateFormat.getDateInstance().format(incomeDate));

        CommentAdapter adapter = new CommentAdapter(this, DAOFactory.getIncomesSavedCommentDAO(this).getAll());
        commentsField.setAdapter(adapter);
    }

    private void refreshAccountsList() {
        AccountDAO accountDAO = DAOFactory.getAccountDAO(this);
        List<Account> accountList = accountDAO.getAccountsByServerAndCurrency(getCurrentServer().getServerid(), incomeSource.getCurrencyId());

        accountAdapter = new ArrayAdapter<Account>(this, R.layout.spinner_item, accountList);
        accountSpinner.setAdapter(accountAdapter);
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
            incomeDate = c.getTime();
            dateField.setText(DateFormat.getDateInstance().format(incomeDate));
        }
    }


    public void dateClicked(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void saveClicked(View view) {
        populateIncomeFromFields();

        Validator validator = ValidatorFactory.createIncomeValidator(income, this);
        if(validator.validate()) {
            income.setSumFromRawSum();
            IncomeDAO incomeDAO = DAOFactory.getIncomeDAO(this);
            Account account = (Account)accountSpinner.getSelectedItem();
            incomeDAO.createOrUpdateIncome(income, previousSum, account);

            DAOFactory.getIncomesSavedCommentDAO(this).saveIfNew(income.getComment());

            setResult(RESULT_OK);
            Toast.makeText(this, R.string.income_saved, Toast.LENGTH_SHORT).show();
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

    private void populateIncomeFromFields() {
        if(income == null) {
            income = new Income();
        }

        income.setRawSum((sumField.getText().toString().trim().replace(',', '.'))); //replacing , with dot for ru and uk locales
        income.setDate(incomeDate);
        income.setAccount(((Account)accountSpinner.getSelectedItem()));
        income.setServerid(getCurrentServer().getServerid());
        income.setIncomeSourceId(incomeSource.getIncomeSourceId());
        income.setComment(commentsField.getText().toString().trim());
    }
}
