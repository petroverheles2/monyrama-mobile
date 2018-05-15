package com.monyrama.model;

import java.math.BigDecimal;

public class Transfer {
	private Integer serverId;
	private Long transferId;
	private Long fromAccountId;
	private BigDecimal fromSum;
	private Long toAccountId;
	private BigDecimal toSum;
	
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
	
	public Long getFromAccountId() {
		return fromAccountId;
	}
	
	public void setFromAccountId(Long fromAccountId) {
		this.fromAccountId = fromAccountId;
	}
	
	public BigDecimal getFromSum() {
		return fromSum;
	}
	
	public void setFromSum(BigDecimal fromSum) {
		this.fromSum = fromSum;
	}
	
	public Long getToAccountId() {
		return toAccountId;
	}
	
	public void setToAccountId(Long toAccountId) {
		this.toAccountId = toAccountId;
	}
	
	public BigDecimal getToSum() {
		return toSum;
	}
	
	public void setToSum(BigDecimal toSum) {
		this.toSum = toSum;
	}
}
