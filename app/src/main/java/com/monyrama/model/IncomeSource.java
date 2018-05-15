package com.monyrama.model;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

public class IncomeSource extends BaseFinancialEntity {
	private Long incomeSourceId;
	private String name;
	private Long currencyId;

	public IncomeSource() {}

	public IncomeSource(Server server, JSONObject json) throws JSONException {
		setServerid(server.getServerid());
		setIncomeSourceId(json.getLong("id"));
		setCurrencyId(json.getLong("currencyId"));
		setName(json.getString("name"));
		setSum(new BigDecimal(json.getDouble("sum")));
	}	
	
	public Long getIncomeSourceId() {
		return incomeSourceId;
	}
	
	public void setIncomeSourceId(Long incomeSourceId) {
		this.incomeSourceId = incomeSourceId;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((incomeSourceId == null) ? 0 : incomeSourceId.hashCode());
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
		IncomeSource other = (IncomeSource) obj;
		if (incomeSourceId == null) {
			if (other.incomeSourceId != null)
				return false;
		} else if (!incomeSourceId.equals(other.incomeSourceId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

}
