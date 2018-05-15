package com.monyrama.validator;

import android.content.Context;

import com.monyrama.model.Expense;
import com.monyrama.model.Income;
import com.monyrama.model.Server;
import com.monyrama.model.Transfer;

public class ValidatorFactory {

	private ValidatorFactory() {}
	
	public static Validator createServerValidator(Server server, Context context) {
		return new ServerValidator(server, context);
	}

	public static Validator createCorrectServerValidator(Server server, Context context) {
		return new CorrectServerValidator(server, context);
	}
	
	public static Validator createExpenseValidator(Expense expense, Context context) {
		return new ExpenseValidator(expense, context);
	}

	public static Validator createIncomeValidator(Income income, Context context) {
		return new IncomeValidator(income, context);
	}

	public static Validator createTransferValidator(Transfer transfer, Context context) {
		return new TransferValidator(transfer, context);
	}
}
