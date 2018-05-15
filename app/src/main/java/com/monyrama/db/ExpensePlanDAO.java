package com.monyrama.db;

import java.util.List;

import com.monyrama.model.ExpensePlan;
import com.monyrama.model.Envelope;
import com.monyrama.model.Server;

public interface ExpensePlanDAO {
	public boolean updateRemainder(ExpensePlan expensePlan, Envelope item);

	public List<ExpensePlan> getExpensePlansByServer(Server server);

	public List<Envelope> getAllEnvelopesByExpensePlan(ExpensePlan expensePlan);

	public ExpensePlan findById(Long id);
}