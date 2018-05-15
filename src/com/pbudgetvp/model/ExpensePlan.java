package com.monyrama.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

public class ExpensePlan extends BaseFinancialEntity implements Serializable {
	private Long expenseplanid;
	private String name;
	private Long currencyId;
	private BigDecimal remainder;

	public ExpensePlan() {
		
	}
	
	public ExpensePlan(Server server, JSONObject json) throws JSONException {
		setServerid(server.getServerid());
		setExpenseplanid(json.getLong("id"));
		setName(json.getString("name"));
		setCurrencyId(json.getLong("currencyId"));
		setSum(new BigDecimal(json.getDouble("sum")));
		setRemainder(new BigDecimal(json.getDouble("remainder")));
	}	
	
	public Long getExpenseplanid() {
		return expenseplanid;
	}
	
	public void setExpenseplanid(Long expenseplanid) {
		this.expenseplanid = expenseplanid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public BigDecimal getRemainder() {
		return remainder;
	}

	public void setRemainder(BigDecimal remainder) {
		this.remainder = remainder;
	}	
	
	public boolean isExpensesSumExceeded() {
		return getRemainder().doubleValue() < 0;
	}	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expenseplanid == null) ? 0 : expenseplanid.hashCode());
		result = prime * result
				+ ((getServerid() == null) ? 0 : getServerid().hashCode());
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
		ExpensePlan other = (ExpensePlan) obj;
		if (expenseplanid == null) {
			if (other.expenseplanid != null)
				return false;
		} else if (!expenseplanid.equals(other.expenseplanid))
			return false;
		if (getServerid() == null) {
			if (other.getServerid() != null)
				return false;
		} else if (!getServerid().equals(other.getServerid()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return name;
	}	
}
