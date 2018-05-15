package com.monyrama.validator;

import android.content.Context;

import com.monyrama.R;
import com.monyrama.model.Expense;
import com.monyrama.util.StringUtil;

final class ExpenseValidator extends AbstractValidator {

	private Expense expense;
	
	private Context context;
	
	private final String SUM_REG_EXP = "[0-9]{0,63}(\\.[0-9]{1,2}){0,1}";
	
	ExpenseValidator(Expense expense, Context context) {
		this.expense = expense;
		this.context = context;
	}
	
	@Override
	public boolean validate() {
		if(StringUtil.emptyString(expense.getRawSum())) {
			setMessage(context.getString(R.string.empty_sum));
			return false;
		}
		
		if(!expense.getRawSum().matches(SUM_REG_EXP)) {
			setMessage(context.getString(R.string.invalid_sum));
			return false;
		}

		return true;
	}

}
