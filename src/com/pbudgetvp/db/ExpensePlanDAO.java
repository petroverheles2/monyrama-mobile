package com.monyrama.db;

import java.util.List;

import com.monyrama.model.ExpensePlan;
import com.monyrama.model.Envelope;
import com.monyrama.model.Server;

public interface ExpensePlanDAO {
	public abstract boolean updateRemainder(ExpensePlan expensePlan, Envelope item);
	public abstract List<ExpensePlan> getExpensePlansByServer(Server server);
	public abstract List<Envelope> getAllItemsByExpensePlan(ExpensePlan expensePlan);
}
