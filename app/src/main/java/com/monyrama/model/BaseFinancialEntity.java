package com.monyrama.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class BaseFinancialEntity implements Serializable {
	private Integer serverid;
	private BigDecimal sum;
	private String rawSum;
	private String comment;
		
	public Integer getServerid() {
		return serverid;
	}
	
	public void setServerid(Integer serverid) {
		this.serverid = serverid;
	}
	
	public BigDecimal getSum() {
		return sum;
	}

	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	public void setSumFromRawSum() {
		sum = new BigDecimal(rawSum);
	}

	public String getRawSum() {
		return rawSum;
	}

	public void setRawSum(String rawSum) {
		this.rawSum = rawSum;
	}

	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}

}
 