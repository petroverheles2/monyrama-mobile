package com.monyrama.db;

import java.util.List;

import com.monyrama.model.Server;

public interface ServerDAO {	
	public abstract boolean createOrUpdate(Server server);
	public abstract boolean delete(Server server);
	public abstract List<Server> getAll();	
	public abstract Server getServer(Integer serverid);
}
