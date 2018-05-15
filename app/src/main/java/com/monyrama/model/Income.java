package com.monyrama.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Income extends BaseFinancialEntity {
	private Long incomeId;
	private Account account;
	private Long incomeSourceId;
	private Date date;

	public Long getIncomeId() {
		return incomeId;
	}
	
	public void setIncomeId(Long incomeId) {
		this.incomeId = incomeId;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Long getIncomeSourceId() {
		return incomeSourceId;
	}

	public void setIncomeSourceId(Long incomeSourceId) {
		this.incomeSourceId = incomeSourceId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("incomeMobileId", incomeId);
		json.put("accountid", account.getAccountId());
		json.put("incomeSourceId", incomeSourceId);
		json.put("sum", getSum());
		json.put("date", Constants.JSON_DATE_FORMAT.format(date));
		json.put("comment", getComment());
		return json;
	}
		
}
