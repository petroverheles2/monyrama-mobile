package com.monyrama.db;

import java.util.List;

import com.monyrama.model.Expense;
import com.monyrama.model.ExpensePlan;
import com.monyrama.model.Server;

public interface ExpenseDAO {
	public boolean createOrUpdateExpense(Expense expense);
	public boolean delete(Expense expense);
	public boolean deleteExpensesByServer(Server server);
	public List<Expense> getAllExpensesByExpensePlan(ExpensePlan expensePlan);
	public List<Expense> getAllExpensesByServer(Server server);
}
