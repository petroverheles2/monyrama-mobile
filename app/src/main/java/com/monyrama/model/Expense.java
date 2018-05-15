package com.monyrama.model;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Expense extends BaseFinancialEntity implements Serializable {
	private Long expenseid;
	private Long expenseplanid;
	private Account account;
	private Date date;
	private Envelope envelope;
	
	public Long getExpenseid() {
		return expenseid;
	}
	
	public void setExpenseid(Long expenseid) {
		this.expenseid = expenseid;
	}
	
	public Envelope getEnvelope() {
		return envelope;
	}

	public void setEnvelope(Envelope envelope) {
		this.envelope = envelope;
	}

	public Long getExpenseplanid() {
		return expenseplanid;
	}
	
	public void setExpenseplanid(Long expenseplanid) {
		this.expenseplanid = expenseplanid;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("expenseMobileId", expenseid);
		json.put("expenseplanid", expenseplanid);
		json.put("envelopeid", envelope.getItemid());
		json.put("accountid", account.getAccountId());
		json.put("sum", getSum());
		json.put("date", Constants.JSON_DATE_FORMAT.format(date));
		json.put("comment", getComment());
		return json;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expenseid == null) ? 0 : expenseid.hashCode());
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
		Expense other = (Expense) obj;
		if (expenseid == null) {
			if (other.expenseid != null)
				return false;
		} else if (!expenseid.equals(other.expenseid))
			return false;
		return true;
	}
	
}
