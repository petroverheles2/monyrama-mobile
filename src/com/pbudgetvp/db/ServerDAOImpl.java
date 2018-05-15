package com.monyrama.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.monyrama.log.MyLog;
import com.monyrama.model.Server;

class ServerDAOImpl extends PBVPDatabaseOpenHelper implements ServerDAO {	

	private final String ALIAS_COLUMN = "alias";
	private final String ADDRESS_COLUMN = "address";
	private final String PORT_COLUMN = "port";
	
	ServerDAOImpl(Context context) {
		super(context);
	}
	
	@Override
	public boolean createOrUpdate(Server server) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ALIAS_COLUMN, server.getAlias());
		values.put(ADDRESS_COLUMN, server.getAddress());
		values.put(PORT_COLUMN, server.getPort());
		values.put(COMMENT_COLUMN, server.getComment());
		if(server.getServerid() != null) {
			db.update(SERVER_TABLE, values, SERVERID_COLUMN + "=?",
					new String[]{server.getServerid().toString()});
		} else {
			db.insert(SERVER_TABLE, null, values);			
		}	
		db.close();
		return true;
	}

	@Override
	public boolean delete(Server server) {
		boolean success = true;
		SQLiteDatabase db = getWritableDatabase();
		   db.beginTransaction();
		   try {
			 db.delete(EXPENSE_PLAN_TABLE, SERVERID_COLUMN + "=?", new String[]{server.getServerid().toString()});
			 db.delete(ENVELOPE_TABLE, SERVERID_COLUMN + "=?", new String[]{server.getServerid().toString()});
			 db.delete(EXPENSE_TABLE, SERVERID_COLUMN + "=?", new String[]{server.getServerid().toString()});
			 db.delete(SERVER_TABLE, SERVERID_COLUMN + "=?",	new String[]{Integer.toString(server.getServerid())});
		     db.setTransactionSuccessful();
		   } catch (Exception e) {
			   MyLog.error("Error deleting server", e);
			   success = false;
		   } finally {
		     db.endTransaction();
		   }
		   
		   return success;
	}

	@Override
	public List<Server> getAll() {
		SQLiteDatabase db = getReadableDatabase();
		List<Server> resultList = new ArrayList<Server>();
		Cursor cursor = db.query(SERVER_TABLE, null, null, null, null, null, null);
		while(cursor.moveToNext()) {
			resultList.add(mapServer(cursor));
		}
		cursor.close();
		db.close();
		
		Collections.sort(resultList, new Comparator<Server>() {
			@Override
			public int compare(Server lhs, Server rhs) {
				return lhs.getAlias().compareToIgnoreCase(rhs.getAlias());
			}
		});
		
		return resultList;
	}
	
	@Override
	public Server getServer(Integer serverid) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(SERVER_TABLE, null, SERVERID_COLUMN + "=" + serverid, null, null, null, null);
		cursor.moveToFirst();
		Server server = mapServer(cursor);
		cursor.close();
		db.close();
		return server;
	}
	
	private Server mapServer(Cursor cursor) {
		Server server = new Server();
		server.setServerid(cursor.getInt(cursor.getColumnIndex(SERVERID_COLUMN)));
		server.setAlias(cursor.getString(cursor.getColumnIndex(ALIAS_COLUMN)));
		server.setAddress(cursor.getString(cursor.getColumnIndex(ADDRESS_COLUMN)));
		server.setPort(cursor.getInt(cursor.getColumnIndex(PORT_COLUMN)));
		server.setComment(cursor.getString(cursor.getColumnIndex(COMMENT_COLUMN)));
		return server;
	}

}
