package com.monyrama.db;

import com.monyrama.model.Account;
import com.monyrama.model.Income;
import com.monyrama.model.IncomeSource;
import com.monyrama.model.Server;

import java.math.BigDecimal;
import java.util.List;

public interface IncomeDAO {
	public boolean createOrUpdateIncome(Income income, BigDecimal previousSum, Account account);
	public boolean delete(Income income);
	public boolean deleteIncomesByServer(Server server);
	public List<Income> getAllIncomesByIncomeSource(IncomeSource incomeSource);
	public List<Income> getAllIncomesByServer(Server server);
}
