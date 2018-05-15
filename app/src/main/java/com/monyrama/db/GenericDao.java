package com.monyrama.db;

import com.monyrama.json.ServerData;
import com.monyrama.model.Server;

public interface GenericDao {
	public boolean deleteDataByServer(Server server);
	public boolean saveDataFromServer(ServerData serverData);
}
