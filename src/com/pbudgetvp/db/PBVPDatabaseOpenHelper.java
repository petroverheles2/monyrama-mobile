package com.monyrama.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

abstract class PBVPDatabaseOpenHelper extends SQLiteOpenHelper {	
	private final static String DB_NAME = "pbvp.db";
	private final static int DB_VERSION = 1;	
	
	protected final static String SERVER_TABLE = "server";
	protected final static String EXPENSE_PLAN_TABLE = "expenseplan";
	protected final static String ENVELOPE_TABLE = "envelope";
	protected final static String EXPENSE_TABLE = "expense";
	protected final static String CURRENCY_TABLE = "currency";
	protected final static String INCOME_SOURCE_TABLE = "incomesource";
	protected final static String INCOME_TABLE = "income";
	protected final static String ACCOUNT_TABLE = "account";
	protected final static String TRANSFER_TABLE = "transfer";
	protected final static String SAVED_COMMENTS = "saved_comments";
	
	protected final static String SERVERID_COLUMN = "serverid";
	
	protected final static String EXPENSEPLANID_COLUMN = "expenseplanid";
	protected final static String ENVELOPEID_COLUMN = "envelopeid";
	protected final static String COMMENT_COLUMN = "comment";
	protected final static String NAME_COLUMN = "name";
	protected final static String CATEGORYNAME_COLUMN = "categoryname";
	protected final static String SUM_COLUMN = "sum";
	protected final static String REMAINDER_COLUMN = "remainder";
	protected final static String TS_COLUMN = "ts";
	protected final static String COMMENT_ID_COLUMN = "commentid";
	protected final static String CURRENCYID_COLUMN = "currencyid";	
	protected final static String ACCOUNTID_COLUMN = "accountid";	
	protected final static String INCOMESOURCEID_COLUMN = "incomesourceid";	
	
	protected final static String CODE_COLUMN = "code";	
	protected final static String EXCHANGERATE_COLUMN = "exchangerate";	
	protected final static String MAIN_COLUMN = "main";	

	PBVPDatabaseOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		final String CREATE_SERVER_TABLE = "CREATE TABLE [server] " +
				"([serverid] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT," +
				"[alias] VARCHAR(100)  NULL, " +
				"[address] VARCHAR(100)  NOT NULL, " +
				"[port] INTEGER  NOT NULL, " +
				"[comment] TEXT  NULL)";
		
		final String CREATE_CURRENCY_TABLE = "CREATE TABLE [currency] " +
				"([currencyid] INTEGER NOT NULL," +
				"[name] VARCHAR(100)  NULL, " +
				"[code] CHAR(3)  NOT NULL, " +
				"[exchangerate] VARCHAR(100) NOT NULL, " +
				"[main] INTEGER NOT NULL, " +
				"[serverid] INTEGER  NOT NULL)";		

		final String CREATE_ACCOUNT_TABLE = "CREATE TABLE [account] " +
				"([accountid] INTEGER  NOT NULL, " +
				"[name] VARCHAR(100)  NOT NULL, " +
				"[currencyid] VARCHAR(100)  NOT NULL, " +
				"[sum] INTEGER  NOT NULL, " +
				"[comment] TEXT  NULL, " +
				"[serverid] INTEGER  NOT NULL)";		
		
		final String CREATE_EXPENSEPLAN_TABLE = "CREATE TABLE [expenseplan] " +
				"([expenseplanid] INTEGER  NOT NULL, " +
				"[name] VARCHAR(100)  NOT NULL, " +
				"[currencyid] INTEGER  NOT NULL, " +
				"[sum] INTEGER  NOT NULL, " +
				"[remainder] VARCHAR(100) NOT NULL, " +
				"[comment] TEXT  NULL, " +
				"[serverid] INTEGER  NOT NULL)";
		
		final String CREATE_ENVELOPE_TABLE = "CREATE TABLE [envelope] " +
				"([envelopeid] INTEGER  NOT NULL, " +
				"[expenseplanid] INTEGER  NOT NULL, " +
				"[name] VARCHAR(100)  NULL, " +
				"[categoryname] VARCHAR(100)  NOT NULL, " +
				"[sum] INTEGER NOT NULL, " +
				"[remainder] VARCHAR(100) NOT NULL, " +
				"[comment] TEXT  NULL, " +
				"[serverid] INTEGER  NOT NULL)";
		
		final String CREATE_EXPENSE_TABLE = "CREATE TABLE [expense] " +
				"([expenseid] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, " +
				"[envelopeid] INTEGER  NOT NULL, " +
				"[expenseplanid] INTEGER  NOT NULL, " +
				"[sum] INTEGER  NOT NULL, " +
				"[accountid] INTEGER NOT NULL," +
				"[ts] DATETIME NOT NULL, " +
				"[comment] TEXT  NULL, " +
				"[serverid] INTEGER  NOT NULL)";

		final String CREATE_INCOME_SOURCE_TABLE = "CREATE TABLE [incomesource] " +
				"([incomesourceid] INTEGER  NOT NULL, " +
				"[name] VARCHAR(100)  NOT NULL, " +
				"[currencyid] INTEGER  NOT NULL, " +
				"[sum] INTEGER  NOT NULL, " +
				"[comment] TEXT  NULL, " +
				"[serverid] INTEGER  NOT NULL)";
		
		final String CREATE_INCOME_TABLE = "CREATE TABLE [income] " +
				"([incomeid] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, " +
				"[incomesourceid] INTEGER  NOT NULL, " +
				"[sum] INTEGER  NOT NULL, " +
				"[accountid] INTEGER NOT NULL," +
				"[ts] DATETIME NOT NULL, " +
				"[comment] TEXT  NULL, " +
				"[serverid] INTEGER  NOT NULL)";
		
		final String CREATE_TRANSFER_TABLE = "CREATE TABLE [transfer] " +
				"([transferid] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, " +
				"[from_sum] INTEGER  NOT NULL, " +				
				"[from_accountid] INTEGER NOT NULL," +
				"[to_sum] INTEGER NOT NULL, " +				
				"[to_accountid] INTEGER NOT NULL," +
				"[ts] DATETIME NOT NULL, " +
				"[comment] TEXT  NULL, " +
				"[serverid] INTEGER  NOT NULL)";
		
		final String CREATE_SAVED_COMMENTS_TABLE = "CREATE TABLE [saved_comments] " +
				"([commentid] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, " +
				"[comment] TEXT NOT NULL UNIQUE, " +
				"[ts] DATETIME NOT NULL)";
		
	    db.beginTransaction();		   
	    try {
			db.execSQL(CREATE_SERVER_TABLE);
			db.execSQL(CREATE_CURRENCY_TABLE);
			db.execSQL(CREATE_ACCOUNT_TABLE);
			db.execSQL(CREATE_EXPENSEPLAN_TABLE);
			db.execSQL(CREATE_ENVELOPE_TABLE);
			db.execSQL(CREATE_EXPENSE_TABLE);
			db.execSQL(CREATE_INCOME_SOURCE_TABLE);
			db.execSQL(CREATE_INCOME_TABLE);
			db.execSQL(CREATE_TRANSFER_TABLE);
			db.execSQL(CREATE_SAVED_COMMENTS_TABLE);
		    db.setTransactionSuccessful();
	    } finally {
		    db.endTransaction();
	    }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}	
}
