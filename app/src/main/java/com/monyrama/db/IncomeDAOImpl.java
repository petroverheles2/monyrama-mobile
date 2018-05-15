package com.monyrama.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.monyrama.log.MyLog;
import com.monyrama.model.Account;
import com.monyrama.model.Income;
import com.monyrama.model.IncomeSource;
import com.monyrama.model.Server;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class IncomeDAOImpl extends PBVPDatabaseOpenHelper implements IncomeDAO {

	private final static String INCOMEID_COLUMN = "incomeid";

	private final static String SELECT_BY_SERVER = "SELECT "
			+ " income." + INCOMEID_COLUMN + ", "
			+ " income." + INCOMESOURCEID_COLUMN + ", "
			+ " income." + SUM_COLUMN + ", "
			+ " income." + TS_COLUMN + ", "
			+ " income." + COMMENT_COLUMN + ", "
			+ " income." + SERVERID_COLUMN + ", "
			+ " income." + ACCOUNTID_COLUMN + ", "
			+ " account."  + ACCOUNTID_COLUMN + ", "
			+ " account."  + CURRENCYID_COLUMN + ", "
			+ " account."  + NAME_COLUMN + " AS " + ACCOUNT_NAME + ", "
			+ " account."  + SUM_COLUMN + " AS " + ACCOUNT_SUM + ", "
			+ " account."  + COMMENT_COLUMN + " AS " + ACCOUNT_COMMENT
			+ " FROM " + INCOME_TABLE + " income"
            + " JOIN " + ACCOUNT_TABLE + " account ON "
            + " (" + "income." + ACCOUNTID_COLUMN + "=" + "account." + ACCOUNTID_COLUMN + " AND "
            + " income." + SERVERID_COLUMN + "=" + "account." + SERVERID_COLUMN + ") "
			+ " WHERE income." + SERVERID_COLUMN + "=?";

	private final static String SELECT_BY_SERVER_AND_INCOME_SOURCE = SELECT_BY_SERVER
			+ " AND income." + INCOMESOURCEID_COLUMN + "=?"
			+ " ORDER BY income." + TS_COLUMN + " desc"
			+ ", income." + INCOMESOURCEID_COLUMN + " desc";

	IncomeDAOImpl(Context context) {
		super(context);
	}

	@Override
	public boolean createOrUpdateIncome(Income income, BigDecimal previousSum, Account account) {
		SQLiteDatabase db = getWritableDatabase();
		boolean success = true;
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put(INCOMESOURCEID_COLUMN, income.getIncomeSourceId());
			values.put(SUM_COLUMN, toCentsString(income.getSum()));
			values.put(TS_COLUMN, income.getDate().getTime());
			values.put(COMMENT_COLUMN, income.getComment());
			values.put(SERVERID_COLUMN, income.getServerid());
			values.put(ACCOUNTID_COLUMN, income.getAccount().getAccountId());

			BigDecimal accountRemainder;
			if(income.getIncomeId() == null) {
				db.insert(INCOME_TABLE, null, values);

				accountRemainder = account.getSum().add(income.getSum());

			} else {
				db.update(INCOME_TABLE, values, INCOMEID_COLUMN + "=?",
						new String[]{income.getIncomeId().toString()});

				BigDecimal previousAndNewSumDiff = previousSum.subtract(income.getSum());
				accountRemainder = account.getSum().subtract(previousAndNewSumDiff);
			}

			ContentValues accountValues = new ContentValues();
			accountValues.put(SUM_COLUMN, toCentsString(accountRemainder));
			db.update(ACCOUNT_TABLE, accountValues, ACCOUNTID_COLUMN + "=?",
					new String[]{account.getAccountId().toString()});

			db.setTransactionSuccessful();
		} catch (Exception e) {
			success = false;
			MyLog.error("Error saving Income", e);
		} finally {
			db.endTransaction();
		}

		db.close();
		return success;
	}

	@Override
	public List<Income> getAllIncomesByIncomeSource(IncomeSource incomeSource) {
		SQLiteDatabase db = getReadableDatabase();
		List<Income> resultList = new ArrayList<Income>();
		if(incomeSource != null) {
			Cursor cursor = db.rawQuery(SELECT_BY_SERVER_AND_INCOME_SOURCE,
					new String[]{incomeSource.getServerid().toString(), incomeSource.getIncomeSourceId().toString()});
			while(cursor.moveToNext()) {
				resultList.add(mapIncome(cursor));
			}
			cursor.close();
			db.close();
		}
	
		return resultList;
	}

	private Income mapIncome(Cursor cursor) {
		Long incomeid = cursor.getLong(cursor.getColumnIndex(INCOMEID_COLUMN));
		Long incomeSourceId = cursor.getLong(cursor.getColumnIndex(INCOMESOURCEID_COLUMN));
		Long accountId = cursor.getLong(cursor.getColumnIndex(ACCOUNTID_COLUMN));
		Integer serverid = cursor.getInt(cursor.getColumnIndex(SERVERID_COLUMN));
		BigDecimal incomeSum = toMoney(cursor.getInt(cursor.getColumnIndex(SUM_COLUMN)));
		Date incomeDate = new Date(cursor.getLong(cursor.getColumnIndex(TS_COLUMN)));
		String incomeComment = cursor.getString(cursor.getColumnIndex(COMMENT_COLUMN));
		BigDecimal accountSum = toMoney(cursor.getInt(cursor.getColumnIndex(ACCOUNT_SUM)));
		String accountComment = cursor.getString(cursor.getColumnIndex(ACCOUNT_COMMENT));
		String accountName = cursor.getString(cursor.getColumnIndex(ACCOUNT_NAME));
		Long currencyId = cursor.getLong(cursor.getColumnIndex(CURRENCYID_COLUMN));

		Account account = new Account();
		account.setAccountId(accountId);
		account.setCurrencyId(currencyId);
		account.setSum(accountSum);
		account.setName(accountName);
		account.setComment(accountComment);
		account.setServerid(serverid);
		
		Income income = new Income();
		income.setIncomeId(incomeid);
		income.setIncomeSourceId(incomeSourceId);
		income.setAccount(account);
		income.setServerid(serverid);
		income.setSum(incomeSum);
		income.setDate(incomeDate);
		income.setComment(incomeComment);

		return income;
	}

    @Override
    public boolean delete(Income income) {
        boolean success = true;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(INCOME_TABLE, INCOMEID_COLUMN + "=?", new String[]{income.getIncomeId().toString()});
            //update account sum
            String incomeSumInCents = toCentsString(income.getSum());
            db.execSQL("UPDATE "
                            + ACCOUNT_TABLE
                            + " SET "
                            + SUM_COLUMN
                            + " = "
                            + SUM_COLUMN
                            + " - "
                            + incomeSumInCents
                            + " WHERE "
                            + ACCOUNTID_COLUMN
                            + " = "
                            + income.getAccount().getAccountId()
            );

            db.setTransactionSuccessful();
        } catch (Exception e) {
            MyLog.error("Error deleting income", e);
            success = false;
        } finally {
            db.endTransaction();
        }

        return success;
    }

	@Override
	public List<Income> getAllIncomesByServer(Server server) {
		SQLiteDatabase db = getReadableDatabase();
		List<Income> resultList = new ArrayList<Income>();
		if(server != null) {
			Cursor cursor = db.rawQuery(SELECT_BY_SERVER, 
					new String[]{server.getServerid().toString()});
			while(cursor.moveToNext()) {
				resultList.add(mapIncome(cursor));
			}
			cursor.close();
			db.close();
		}
	
		return resultList;
	}

	@Override
	public boolean deleteIncomesByServer(Server server) {
		boolean success = true;
		SQLiteDatabase db = getWritableDatabase();
		   db.beginTransaction();
		   try {
			 db.delete(INCOME_TABLE, SERVERID_COLUMN + "=?", new String[]{server.getServerid().toString()});
		     db.setTransactionSuccessful();
		   } catch (Exception e) {
			   MyLog.error("Error deleting incomes", e);
			   success = false;
		   } finally {
		     db.endTransaction();
		   }
		   
		   return success;
	}

}
