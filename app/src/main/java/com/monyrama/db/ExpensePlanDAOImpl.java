package com.monyrama.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.monyrama.log.MyLog;
import com.monyrama.model.ExpensePlan;
import com.monyrama.model.Envelope;
import com.monyrama.model.Server;

class ExpensePlanDAOImpl extends PBVPDatabaseOpenHelper implements
		ExpensePlanDAO {

	ExpensePlanDAOImpl(Context context) {
		super(context);
	}

	@Override
	public ExpensePlan findById(Long id) {
		ExpensePlan expensePlan = null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(EXPENSE_PLAN_TABLE, null, EXPENSEPLANID_COLUMN + "=?", new String[]{id.toString()}, null, null, null);
		if(cursor.moveToFirst()) {
			expensePlan = mapExpensePlan(cursor);
		}

		return expensePlan;
	}

	@Override
	public List<ExpensePlan> getExpensePlansByServer(Server server) {
		SQLiteDatabase db = getReadableDatabase();
		List<ExpensePlan> resultList = new ArrayList<ExpensePlan>();
		Cursor cursor = db.query(EXPENSE_PLAN_TABLE, null, SERVERID_COLUMN + "=?", new String[]{server.getServerid().toString()}, null, null, null);
		while(cursor.moveToNext()) {
			resultList.add(mapExpensePlan(cursor));
		}
		cursor.close();
		db.close();
		return resultList;
	}

	@Override
	public List<Envelope> getAllEnvelopesByExpensePlan(ExpensePlan expensePlan) {
		SQLiteDatabase db = getReadableDatabase();
		List<Envelope> resultList = new ArrayList<Envelope>();
		if(expensePlan != null) {
			Cursor cursor = db.query(ENVELOPE_TABLE, null,
					EXPENSEPLANID_COLUMN + "=? AND " + SERVERID_COLUMN + "=?",
					new String[]{expensePlan.getExpenseplanid().toString(), expensePlan.getServerid().toString()},
					null, null, null);
			while(cursor.moveToNext()) {
				resultList.add(mapEnvelope(cursor));
			}
			cursor.close();
			db.close();
		}
	
		return resultList;
	}
	
	@Override
	public boolean updateRemainder(ExpensePlan expensePlan, Envelope item) {
		boolean success = true;
	    SQLiteDatabase db = getWritableDatabase();
		
	    db.beginTransaction();
	    try {
			ContentValues itemValues = new ContentValues();
			itemValues.put(REMAINDER_COLUMN, item.getRemainder().toString());
		    db.update(ENVELOPE_TABLE, itemValues, ENVELOPEID_COLUMN + "=?",
						new String[]{item.getItemid().toString()});
		    
			ContentValues expensePlanValues = new ContentValues();
			expensePlanValues.put(REMAINDER_COLUMN, expensePlan.getRemainder().toString());
			db.update(EXPENSE_PLAN_TABLE, expensePlanValues, EXPENSEPLANID_COLUMN + "=?",
						new String[]{expensePlan.getExpenseplanid().toString()});
		    
	        db.setTransactionSuccessful();
	    } catch (Exception e) {
	    	success = false;
		    MyLog.error("Error updating remainder", e);		    
	    } finally {
	        db.endTransaction();
	    }
	    
		db.close();
		return success;
	}

	private ExpensePlan mapExpensePlan(Cursor cursor) {
		ExpensePlan expensePlan = new ExpensePlan();
		expensePlan.setExpenseplanid(cursor.getLong(cursor.getColumnIndex(EXPENSEPLANID_COLUMN)));
		expensePlan.setServerid(cursor.getInt(cursor.getColumnIndex(SERVERID_COLUMN)));
		expensePlan.setName(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));
		expensePlan.setCurrencyId(cursor.getLong(cursor.getColumnIndex(CURRENCYID_COLUMN)));
		expensePlan.setComment(cursor.getString(cursor.getColumnIndex(COMMENT_COLUMN)));
		expensePlan.setSum(toMoney(cursor.getInt(cursor.getColumnIndex(SUM_COLUMN))));
		expensePlan.setRemainder(toMoney(cursor.getInt(cursor.getColumnIndex(REMAINDER_COLUMN))));
		return expensePlan;
	}

	private Envelope mapEnvelope(Cursor cursor) {
		Envelope item = new Envelope();
		item.setItemid(cursor.getLong(cursor.getColumnIndex(ENVELOPEID_COLUMN)));
		item.setExpensplanid(cursor.getLong(cursor.getColumnIndex(EXPENSEPLANID_COLUMN)));
		item.setServerid(cursor.getInt(cursor.getColumnIndex(SERVERID_COLUMN)));
		item.setName(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));
		item.setCategoryname(cursor.getString(cursor.getColumnIndex(CATEGORYNAME_COLUMN)));
		item.setSum(toMoney(cursor.getInt(cursor.getColumnIndex(SUM_COLUMN))));
		item.setRemainder(toMoney(cursor.getInt(cursor.getColumnIndex(REMAINDER_COLUMN))));
		item.setComment(cursor.getString(cursor.getColumnIndex(COMMENT_COLUMN)));
		return item;
	}
}
