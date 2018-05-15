package com.monyrama.db;

import com.monyrama.model.IncomeSource;
import com.monyrama.model.Server;

import java.util.List;

public interface IncomeSourceDAO {
	public abstract List<IncomeSource> getIncomeSourceByServer(Server server);
	public IncomeSource findById(Long id);

}
