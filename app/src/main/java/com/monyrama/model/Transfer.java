package com.monyrama.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;

public class Transfer {
	private Integer serverId;
	private Long transferId;
	private Account fromAccount;
	private BigDecimal fromSum;
	private Account toAccount;
	private BigDecimal toSum;
	private Date date;
	private String fromRawSum;
	private String toRawSum;
	private String fromCurrencyCode;
	private String toCurrencyCode;

	public Integer getServerId() {
		return serverId;
	}

	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}

	public Long getTransferId() {
		return transferId;
	}
	
	public void setTransferId(Long transferId) {
		this.transferId = transferId;
	}
	
	public BigDecimal getFromSum() {
		return fromSum;
	}
	
	public void setFromSum(BigDecimal fromSum) {
		this.fromSum = fromSum;
	}
	
	public BigDecimal getToSum() {
		return toSum;
	}
	
	public void setToSum(BigDecimal toSum) {
		this.toSum = toSum;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFromRawSum() {
		return fromRawSum;
	}

	public void setFromRawSum(String fromRawSum) {
		this.fromRawSum = fromRawSum;
	}

	public String getToRawSum() {
		return toRawSum;
	}

	public void setToRawSum(String toRawSum) {
		this.toRawSum = toRawSum;
	}

	public Account getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(Account fromAccount) {
		this.fromAccount = fromAccount;
	}

	public Account getToAccount() {
		return toAccount;
	}

	public void setToAccount(Account toAccount) {
		this.toAccount = toAccount;
	}

	public String getFromCurrencyCode() {
		return fromCurrencyCode;
	}

	public void setFromCurrencyCode(String fromCurrencyCode) {
		this.fromCurrencyCode = fromCurrencyCode;
	}

	public String getToCurrencyCode() {
		return toCurrencyCode;
	}

	public void setToCurrencyCode(String toCurrencyCode) {
		this.toCurrencyCode = toCurrencyCode;
	}

	public void setSumsFromRawSums() {
		fromSum = new BigDecimal(fromRawSum);
		toSum = new BigDecimal(toRawSum);
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("transferMobileId", transferId);
		json.put("fromAccountId", fromAccount.getAccountId());
		json.put("fromSum", fromSum);
		json.put("toAccountId", toAccount.getAccountId());
		json.put("toSum", toSum);
		json.put("date", Constants.JSON_DATE_FORMAT.format(date));
		return json;
	}
}
