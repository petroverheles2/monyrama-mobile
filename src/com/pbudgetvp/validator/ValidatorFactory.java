package com.monyrama.validator;

import android.content.Context;

import com.monyrama.model.Expense;
import com.monyrama.model.Server;

public class ValidatorFactory {

	private ValidatorFactory() {}
	
	public static Validator createServerValidator(Server server, Context context) {
		return new ServerValidator(server, context);
	}
	
	public static Validator createExpenseValidator(Expense expense, Context context) {
		return new ExpenseValidator(expense, context);
	}
}
