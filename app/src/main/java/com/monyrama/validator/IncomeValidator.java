package com.monyrama.validator;

import android.content.Context;

import com.monyrama.R;
import com.monyrama.model.Income;
import com.monyrama.util.StringUtil;

final class IncomeValidator extends AbstractValidator {

	private Income income;

	private Context context;

	private final String SUM_REG_EXP = "[0-9]{0,63}(\\.[0-9]{1,2}){0,1}";

	IncomeValidator(Income income, Context context) {
		this.income = income;
		this.context = context;
	}
	
	@Override
	public boolean validate() {
		if(StringUtil.emptyString(income.getRawSum())) {
			setMessage(context.getString(R.string.empty_sum));
			return false;
		}
		
		if(!income.getRawSum().matches(SUM_REG_EXP)) {
			setMessage(context.getString(R.string.invalid_sum));
			return false;
		}

		return true;
		
	}

}
