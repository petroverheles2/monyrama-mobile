package com.monyrama.db;

import com.monyrama.model.Server;
import com.monyrama.model.Transfer;

import java.util.List;

/**
 * Created by petroverheles on 7/26/16.
 */
public interface TransferDAO {
    public boolean save(Transfer transfer);
    public boolean delete(Transfer transfe);
    public List<Transfer> getTransfersByServer(Server server);
}
