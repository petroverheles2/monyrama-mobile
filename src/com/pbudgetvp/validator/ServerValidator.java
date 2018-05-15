package com.monyrama.validator;

import android.content.Context;
import android.util.Patterns;

import com.monyrama.R;
import com.monyrama.model.Server;
import com.monyrama.util.StringUtil;

final class ServerValidator extends AbstractValidator {

	private Server server;
	
	private Context context;
	
	ServerValidator(Server server, Context context) {
		this.server = server;
		this.context = context;
	}
	
	@Override
	public boolean validate() {
		if(StringUtil.emptyString(server.getAlias())) {
			setMessage(context.getString(R.string.alias_empty));
			return false;
		}
		
		if(StringUtil.emptyString(server.getAddress())) {
			setMessage(context.getString(R.string.servername_or_ip_empty));
			return false;
		}
		
		if(!Patterns.IP_ADDRESS.matcher(server.getAddress()).matches()) {
			setMessage(context.getString(R.string.invalid_ip));
			return false;
		}
		
		if((server.getPort() == null)) {
			setMessage(context.getString(R.string.port_empty));
			return false;
		}
		
		if(server.getPort() < 1024 || server.getPort() > 99999) {
			setMessage(context.getString(R.string.port_number));
			return false;
		}

		return true;
		
	}

}
