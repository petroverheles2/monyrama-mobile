package com.monyrama.db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.monyrama.log.MyLog;
import com.monyrama.model.Expense;
import com.monyrama.model.ExpensePlan;
import com.monyrama.model.Envelope;
import com.monyrama.model.Server;

class ExpenseDAOImpl extends PBVPDatabaseOpenHelper implements ExpenseDAO {
	
	private final static String EXPENSEID_COLUMN = "expenseid";
	
	private final static String ITEM_SUM = "item_sum";
	private final static String ITEM_COMMENT = "item_comment";
	
	private final static String SELECT_BY_SERVER = "SELECT "
			+ " ex." + EXPENSEID_COLUMN + ", "
			+ " ex." + ENVELOPEID_COLUMN + ", "
			+ " ex." + EXPENSEPLANID_COLUMN + ", "
			+ " ex." + SUM_COLUMN + ", "
			+ " ex." + TS_COLUMN + ", "
			+ " ex." + COMMENT_COLUMN + ", "
			+ " ex." + SERVERID_COLUMN + ", "
			+ " it." + NAME_COLUMN + ", "
			+ " it." + CATEGORYNAME_COLUMN + ", " 
			+ " it." + SUM_COLUMN + " AS " + ITEM_SUM + ", "
			+ " it." + REMAINDER_COLUMN + ", "
			+ " it." + COMMENT_COLUMN + " AS " + ITEM_COMMENT + 			
			" FROM " + EXPENSE_TABLE + " ex"
			+ " JOIN " + ENVELOPE_TABLE + " it ON "
			+ " (" + "ex." + ENVELOPEID_COLUMN + "="+ "it." + ENVELOPEID_COLUMN + " AND "
			+ " ex." + SERVERID_COLUMN + "=" + "it." + SERVERID_COLUMN + ") "
			+ "WHERE ex." + SERVERID_COLUMN + "=?";	
	
	private final static String SELECT_BY_SERVER_AND_EXPENSE_PLAN = SELECT_BY_SERVER
			+ " AND ex." + EXPENSEPLANID_COLUMN + "=?"
			+ " ORDER BY ex." + TS_COLUMN + " desc"
			+ ", ex." + EXPENSEID_COLUMN + " desc";
	
	ExpenseDAOImpl(Context context) {
		super(context);
	}

	@Override
	public boolean createOrUpdateExpense(Expense expense) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ENVELOPEID_COLUMN, expense.getItem().getItemid());
		values.put(EXPENSEPLANID_COLUMN, expense.getExpenseplanid());
		values.put(SUM_COLUMN, expense.getSum().toString());
		values.put(TS_COLUMN, expense.getDate().getTime());
		values.put(COMMENT_COLUMN, expense.getComment());
		values.put(SERVERID_COLUMN, expense.getServerid());

		if(expense.getExpenseid() != null) {
			db.update(EXPENSE_TABLE, values, EXPENSEID_COLUMN + "=?",
					new String[]{expense.getExpenseid().toString()});
		} else {
			db.insert(EXPENSE_TABLE, null, values);			
		}	
		db.close();
		return true;
	}

	@Override
	public List<Expense> getAllExpensesByExpensePlan(ExpensePlan expensePlan) {
		SQLiteDatabase db = getReadableDatabase();
		List<Expense> resultList = new ArrayList<Expense>();
		if(expensePlan != null) {
			Cursor cursor = db.rawQuery(SELECT_BY_SERVER_AND_EXPENSE_PLAN, 
					new String[]{expensePlan.getServerid().toString(), expensePlan.getExpenseplanid().toString()});
			while(cursor.moveToNext()) {
				resultList.add(mapExpense(cursor));
			}
			cursor.close();
			db.close();
		}
	
		return resultList;
	}

	private Expense mapExpense(Cursor cursor) {
		Long itemid = cursor.getLong(cursor.getColumnIndex(ENVELOPEID_COLUMN));
		Long expenseplanid = cursor.getLong(cursor.getColumnIndex(EXPENSEPLANID_COLUMN));
		String itemName = cursor.getString(cursor.getColumnIndex(NAME_COLUMN));
		String itemCategoryname = cursor.getString(cursor.getColumnIndex(CATEGORYNAME_COLUMN));
		String itemSum = cursor.getString(cursor.getColumnIndex(ITEM_SUM));
		String itemRemainder = cursor.getString(cursor.getColumnIndex(REMAINDER_COLUMN));
		String itemComment = cursor.getString(cursor.getColumnIndex(ITEM_COMMENT));
		Integer serverid = cursor.getInt(cursor.getColumnIndex(SERVERID_COLUMN));
		Long expenseid = cursor.getLong(cursor.getColumnIndex(EXPENSEID_COLUMN));
		String expenseSum = cursor.getString(cursor.getColumnIndex(SUM_COLUMN));
		Date expenseDate = new Date(cursor.getLong(cursor.getColumnIndex(TS_COLUMN)));
		String expenseComment = cursor.getString(cursor.getColumnIndex(COMMENT_COLUMN));
		
		Envelope item = new Envelope();
	    item.setItemid(itemid);
		item.setExpensplanid(expenseplanid);
		item.setName(itemName);
		item.setCategoryname(itemCategoryname);
		item.setSum(new BigDecimal(itemSum));
		item.setRemainder(new BigDecimal(itemRemainder));
		item.setComment(itemComment);
		item.setServerid(serverid);
		
		Expense expense = new Expense();
		expense.setExpenseid(expenseid);
		expense.setExpenseplanid(expenseplanid);
		expense.setServerid(serverid);
		expense.setItem(item);
		expense.setSum(new BigDecimal(expenseSum));
		expense.setDate(expenseDate);
		expense.setComment(expenseComment);
		
		return expense;
	}

	@Override
	public boolean delete(Expense expense) {
		boolean success = true;
		SQLiteDatabase db = getWritableDatabase();
		   db.beginTransaction();
		   try {
			 db.delete(EXPENSE_TABLE, EXPENSEID_COLUMN + "=?", new String[]{expense.getExpenseid().toString()});
		     db.setTransactionSuccessful();
		   } catch (Exception e) {
			   MyLog.error("Error deleting expense", e);
			   success = false;
		   } finally {
		     db.endTransaction();
		   }
		   
		   return success;
	}

	@Override
	public List<Expense> getAllExpensesByServer(Server server) {
		SQLiteDatabase db = getReadableDatabase();
		List<Expense> resultList = new ArrayList<Expense>();
		if(server != null) {
			Cursor cursor = db.rawQuery(SELECT_BY_SERVER, 
					new String[]{server.getServerid().toString()});
			while(cursor.moveToNext()) {
				resultList.add(mapExpense(cursor));
			}
			cursor.close();
			db.close();
		}
	
		return resultList;
	}

	@Override
	public boolean deleteExpensesByServer(Server server) {
		boolean success = true;
		SQLiteDatabase db = getWritableDatabase();
		   db.beginTransaction();
		   try {
			 db.delete(EXPENSE_TABLE, SERVERID_COLUMN + "=?", new String[]{server.getServerid().toString()});
		     db.setTransactionSuccessful();
		   } catch (Exception e) {
			   MyLog.error("Error deleting Expense Plans and items", e);
			   success = false;
		   } finally {
		     db.endTransaction();
		   }
		   
		   return success;
	}

}
