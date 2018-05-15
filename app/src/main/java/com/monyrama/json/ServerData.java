package com.monyrama.json;

import java.util.Collection;
import java.util.Collections;

import com.monyrama.model.Account;
import com.monyrama.model.Currency;
import com.monyrama.model.ExpensePlan;
import com.monyrama.model.IncomeSource;
import com.monyrama.model.Envelope;

public class ServerData {
	private final Collection<Currency> currencies;
	private final Collection<ExpensePlan> expensePlans;
	private final Collection<Envelope> expensePlanItems;
	private final Collection<Account> accounts;
	private final Collection<IncomeSource> incomeSources;
	
	public ServerData(Collection<Currency> currencies,
							Collection<ExpensePlan> expensePlans,
							Collection<Envelope> expensePlanItems,
							Collection<Account> accounts,
							Collection<IncomeSource> incomeSources) {
		
		this.currencies = Collections.unmodifiableCollection(currencies);
		this.expensePlans = Collections.unmodifiableCollection(expensePlans);
		this.expensePlanItems = Collections.unmodifiableCollection(expensePlanItems);
		this.accounts = Collections.unmodifiableCollection(accounts);
		this.incomeSources = Collections.unmodifiableCollection(incomeSources);
	}

	public Collection<IncomeSource> getIncomeSources() {
		return incomeSources;
	}

	public Collection<Currency> getCurrencies() {
		return currencies;
	}

	public Collection<ExpensePlan> getExpensePlans() {
		return expensePlans;
	}

	public Collection<Envelope> getEnvelopes() {
		return expensePlanItems;
	}

	public Collection<Account> getAccounts() {
		return accounts;
	}
}
