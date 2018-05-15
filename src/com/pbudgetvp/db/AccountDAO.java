package com.monyrama.db;

import java.util.List;

import com.monyrama.model.Account;
import com.monyrama.model.Server;

public interface AccountDAO {
	public abstract List<Account> getAccountsByServer(Server server);
}
