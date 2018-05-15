package com.monyrama.model;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

public class Account extends BaseFinancialEntity {
	private Long accountId;
	private Long currencyId;
	private String name;

	public Account() {}
	
	public Account(Server server, JSONObject json) throws JSONException {
		setServerid(server.getServerid());
		setAccountId(json.getLong("id"));
		setCurrencyId(json.getLong("currencyId"));
		setName(json.getString("name"));
		setSum(new BigDecimal(json.getDouble("sum")));
	}
	
	public Long getAccountId() {
		return accountId;
	}
	
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	
	public Long getCurrencyId() {
		return currencyId;
	}
	
	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accountId == null) ? 0 : accountId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (accountId == null) {
			if (other.accountId != null)
				return false;
		} else if (!accountId.equals(other.accountId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
	
}
