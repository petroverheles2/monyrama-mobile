package com.monyrama.validator;

import android.content.Context;

import com.monyrama.R;
import com.monyrama.model.Server;
import com.monyrama.util.StringUtil;

final class ServerValidator extends CorrectServerValidator {

	ServerValidator(Server server, Context context) {
		super(server, context);
	}

	@Override
	public boolean validate() {
		if(StringUtil.emptyString(server.getAlias())) {
			setMessage(context.getString(R.string.alias_empty));
			return false;
		}

		return addressAndPortValidation();
	}

}
