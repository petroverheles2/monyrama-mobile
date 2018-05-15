package com.monyrama.db;

import java.util.List;

import com.monyrama.model.Currency;

public interface CurrencyDAO {
	public Currency getCurrencyById(Long id);
	public List<Currency> getAllCurrenciesByServer(Integer serverid);
}
