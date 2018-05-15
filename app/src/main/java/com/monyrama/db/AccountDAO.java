package com.monyrama.db;

import java.util.List;

import com.monyrama.model.Account;

public interface AccountDAO {
	public abstract List<Account> getAccountsByServer(Integer serverid);

	public abstract List<Account> getAccountsByServerAndCurrency(Integer serverId, Long currencyId);
}
