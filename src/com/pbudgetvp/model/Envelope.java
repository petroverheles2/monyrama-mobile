package com.monyrama.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

public class Envelope extends BaseFinancialEntity implements Serializable {
	private Long itemid;
	private Long expensplanid;
	private String name;
	private String categoryname;
	private BigDecimal remainder;
	
	public Envelope() {		
	}
	
	public Envelope(Server server, ExpensePlan expensePlan, JSONObject json) throws JSONException {
		setServerid(server.getServerid());
		setExpensplanid(expensePlan.getExpenseplanid());
		setItemid(json.getLong("id"));
		setName(json.getString("name"));
		setCategoryname(json.getString("category"));
		setSum(new BigDecimal(json.getDouble("sum")));
		setRemainder(new BigDecimal(json.getDouble("remainder")));
		setComment(json.getString("comment"));
	}
	
	public Long getItemid() {
		return itemid;
	}
	
	public void setItemid(Long itemid) {
		this.itemid = itemid;
	}
	
	public Long getExpensplanid() {
		return expensplanid;
	}
	
	public void setExpensplanid(Long expensplanid) {
		this.expensplanid = expensplanid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCategoryname() {
		return categoryname;
	}
	
	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
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
				+ ((expensplanid == null) ? 0 : expensplanid.hashCode());
		result = prime * result + ((itemid == null) ? 0 : itemid.hashCode());
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
		Envelope other = (Envelope) obj;
		if (expensplanid == null) {
			if (other.expensplanid != null)
				return false;
		} else if (!expensplanid.equals(other.expensplanid))
			return false;
		if (itemid == null) {
			if (other.itemid != null)
				return false;
		} else if (!itemid.equals(other.itemid))
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
		String visibleName;
		if(name == null || name.trim().equals("")) {
			visibleName = categoryname;
		} else {
			visibleName = name;
		}
		
		return visibleName;
	}
	
	
}
