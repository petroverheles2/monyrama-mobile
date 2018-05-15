package com.monyrama.model;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

public class Currency {
	private Integer serverid;
	private Long currencyId;
	private String name;
	private String code;
	private BigDecimal exchangeRate;
	private Boolean main;
	
	public Currency() {
		
	}
	
	public Currency(Server server, JSONObject json) throws JSONException {
		setServerid(server.getServerid());
		setCurrencyId(json.getLong("id"));
		setName(json.getString("name"));
		setCode(json.getString("code"));
		setExchangeRate(new BigDecimal(json.getString("exchangeRate")));
		setMain(json.getBoolean("main"));
	}
		
	public Integer getServerid() {
		return serverid;
	}

	public void setServerid(Integer serverid) {
		this.serverid = serverid;
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
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}
	
	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public Boolean getMain() {
		return main;
	}

	public void setMain(Boolean main) {
		this.main = main;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currencyId == null) ? 0 : currencyId.hashCode());
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
		Currency other = (Currency) obj;
		if (currencyId == null) {
			if (other.currencyId != null)
				return false;
		} else if (!currencyId.equals(other.currencyId))
			return false;
		return true;
	}
	
}
