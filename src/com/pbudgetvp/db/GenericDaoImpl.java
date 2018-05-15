package com.monyrama.db;

import com.monyrama.json.ServerData;
import com.monyrama.log.MyLog;
import com.monyrama.model.Account;
import com.monyrama.model.Currency;
import com.monyrama.model.ExpensePlan;
import com.monyrama.model.Envelope;
import com.monyrama.model.IncomeSource;
import com.monyrama.model.Server;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class GenericDaoImpl extends PBVPDatabaseOpenHelper implements
		GenericDao {

	GenericDaoImpl(Context context) {
		super(context);
	}

	@Override
	public boolean deleteDataByServer(Server server) {
		boolean success = true;
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete(CURRENCY_TABLE, SERVERID_COLUMN + "=?",
					new String[] { server.getServerid().toString() });
			db.delete(ACCOUNT_TABLE, SERVERID_COLUMN + "=?",
					new String[] { server.getServerid().toString() });
			db.delete(TRANSFER_TABLE, SERVERID_COLUMN + "=?",
					new String[] { server.getServerid().toString() });			
			db.delete(INCOME_SOURCE_TABLE, SERVERID_COLUMN + "=?",
					new String[] { server.getServerid().toString() });
			db.delete(INCOME_TABLE, SERVERID_COLUMN + "=?",
					new String[] { server.getServerid().toString() });				
			db.delete(EXPENSE_PLAN_TABLE, SERVERID_COLUMN + "=?",
					new String[] { server.getServerid().toString() });
			db.delete(ENVELOPE_TABLE, SERVERID_COLUMN + "=?", new String[] { server
					.getServerid().toString() });
			db.delete(EXPENSE_TABLE, SERVERID_COLUMN + "=?",
					new String[] { server.getServerid().toString() });
			db.setTransactionSuccessful();
		} catch (Exception e) {
			MyLog.error("Error deleting data by server", e);
			success = false;
		} finally {
			db.endTransaction();
		}

		return success;
	}

	@Override
	public boolean saveDataFromServer(ServerData serverData) {
		boolean success = true;
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			//save currencies
			for(Currency currency : serverData.getCurrencies()) {
				db.insertOrThrow(CURRENCY_TABLE, null, mapCurrencyToContentValues(currency));
			}
			
			// save accounts
			for (Account account : serverData.getAccounts()) {
				db.insertOrThrow(ACCOUNT_TABLE, null, mapAccountToContentValues(account));
			}
			
			// save income sources
			for(IncomeSource incomeSource : serverData.getIncomeSources()) {
				db.insertOrThrow(INCOME_SOURCE_TABLE, null, mapIncomeSourceToContentValues(incomeSource));
			}
			
			// save expenses plans
			for(ExpensePlan expensePlan : serverData.getExpensePlans()) {			
				db.insertOrThrow(EXPENSE_PLAN_TABLE, null, mapExpensesPlanToContentValues(expensePlan));			
			}
			
			// save envelopes
			for(Envelope envelope : serverData.getEnvelopes()) {
				db.insertOrThrow(ENVELOPE_TABLE, null, mapEnvelopeToContentValues(envelope));			
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			MyLog.error("Error saving data", e);
			success = false;
		} finally {
			db.endTransaction();
		}

		return success;
	}

	private ContentValues mapCurrencyToContentValues(Currency currency) {
		ContentValues values = new ContentValues();
		values.put(CURRENCYID_COLUMN, currency.getCurrencyId());
		values.put(NAME_COLUMN, currency.getName());
		values.put(CODE_COLUMN, currency.getCode());
		values.put(MAIN_COLUMN, currency.getMain());
		values.put(EXCHANGERATE_COLUMN, currency.getExchangeRate().toString());
		values.put(SERVERID_COLUMN, currency.getServerid());
		return values;
	}

	private ContentValues mapIncomeSourceToContentValues(IncomeSource incomeSource) {
		ContentValues values = new ContentValues();
		values.put(INCOMESOURCEID_COLUMN, incomeSource.getIncomeSourceId());
		values.put(CURRENCYID_COLUMN, incomeSource.getCurrencyId());
		values.put(NAME_COLUMN, incomeSource.getName());
		values.put(SUM_COLUMN, incomeSource.getSum().toPlainString());
		values.put(COMMENT_COLUMN, incomeSource.getComment());
		values.put(SERVERID_COLUMN, incomeSource.getServerid());
		return values;
	}

	private ContentValues mapEnvelopeToContentValues(Envelope envelope) {
		ContentValues values = new ContentValues();
		values.put(ENVELOPEID_COLUMN, envelope.getItemid());
		values.put(EXPENSEPLANID_COLUMN, envelope.getExpensplanid());
		values.put(NAME_COLUMN, envelope.getName());
		values.put(CATEGORYNAME_COLUMN, envelope.getCategoryname());
		values.put(SUM_COLUMN, envelope.getSum().toPlainString());
		values.put(REMAINDER_COLUMN, envelope.getRemainder().toPlainString());
		values.put(COMMENT_COLUMN, envelope.getComment());
		values.put(SERVERID_COLUMN, envelope.getServerid());
		return values;
	}

	private ContentValues mapExpensesPlanToContentValues(ExpensePlan ep) {
		ContentValues values = new ContentValues();
		values.put(EXPENSEPLANID_COLUMN, ep.getExpenseplanid());
		values.put(NAME_COLUMN, ep.getName());
		values.put(CURRENCYID_COLUMN, ep.getCurrencyId());
		values.put(SUM_COLUMN, ep.getSum().toPlainString());
		values.put(REMAINDER_COLUMN, ep.getRemainder().toPlainString());
		values.put(COMMENT_COLUMN, ep.getComment());
		values.put(SERVERID_COLUMN, ep.getServerid());
		return values;
	}

	private ContentValues mapAccountToContentValues(Account account) {
		ContentValues values = new ContentValues();
		values.put(ACCOUNTID_COLUMN, account.getAccountId());
		values.put(NAME_COLUMN, account.getName());
		values.put(CURRENCYID_COLUMN, account.getCurrencyId());
		values.put(SUM_COLUMN, account.getSum().toPlainString());
		values.put(COMMENT_COLUMN, account.getComment());
		values.put(SERVERID_COLUMN, account.getServerid());
		return values;
	}

}
