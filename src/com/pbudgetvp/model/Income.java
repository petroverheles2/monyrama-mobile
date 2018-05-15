package com.monyrama.model;

public class Income extends BaseFinancialEntity {
	private Long incomeId;
	private Long accountId;
	
	public Long getIncomeId() {
		return incomeId;
	}
	
	public void setIncomeId(Long incomeId) {
		this.incomeId = incomeId;
	}
	
	public Long getAccountId() {
		return accountId;
	}
	
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
		
}
