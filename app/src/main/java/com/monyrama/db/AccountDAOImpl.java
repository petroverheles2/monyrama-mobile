package com.monyrama.db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.monyrama.model.Account;

public class AccountDAOImpl extends PBVPDatabaseOpenHelper implements AccountDAO {

	AccountDAOImpl(Context context) {
		super(context);
	}

	@Override
	public List<Account> getAccountsByServer(Integer serverid) {
		return getAccounts(SERVERID_COLUMN + "=?", new String[]{serverid.toString()});
	}

	@Override
	public List<Account> getAccountsByServerAndCurrency(Integer serverId, Long currencyId) {
		return getAccounts(SERVERID_COLUMN + "=? AND " + CURRENCYID_COLUMN + "=?",
				new String[]{serverId.toString(), currencyId.toString()});
	}

	private List<Account> getAccounts(String filterStructure, String[] filterValues) {
		SQLiteDatabase db = getReadableDatabase();
		List<Account> resultList = new ArrayList<Account>();
		Cursor cursor = db.query(ACCOUNT_TABLE, null, filterStructure, filterValues, null, null, null);
		while(cursor.moveToNext()) {
			resultList.add(mapAccount(cursor));
		}
		cursor.close();
		db.close();
		return resultList;
	}

	private Account mapAccount(Cursor cursor) {
		Account account = new Account();
		account.setAccountId(cursor.getLong(cursor.getColumnIndex(ACCOUNTID_COLUMN)));
		account.setServerid(cursor.getInt(cursor.getColumnIndex(SERVERID_COLUMN)));
		account.setName(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));
		account.setCurrencyId(cursor.getLong(cursor.getColumnIndex(CURRENCYID_COLUMN)));
		account.setComment(cursor.getString(cursor.getColumnIndex(COMMENT_COLUMN)));
		account.setSum(new BigDecimal(cursor.getInt(cursor.getColumnIndex(SUM_COLUMN))).divide(new BigDecimal("100")));
		return account;
	}

}
