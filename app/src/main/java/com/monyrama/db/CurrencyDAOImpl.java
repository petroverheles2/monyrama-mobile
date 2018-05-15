package com.monyrama.db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.monyrama.model.Currency;

public class CurrencyDAOImpl extends PBVPDatabaseOpenHelper implements CurrencyDAO {

	CurrencyDAOImpl(Context context) {
		super(context);
	}

	@Override
	public Currency getCurrencyById(Long id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(CURRENCY_TABLE, null, CURRENCYID_COLUMN + "=" + id, null, null, null, null);
		cursor.moveToFirst();
		Currency currency = mapCurrency(cursor);
		cursor.close();
		db.close();
		return currency;
	}
	
	@Override
	public List<Currency> getAllCurrenciesByServer(Integer serverid) {
		SQLiteDatabase db = getReadableDatabase();
		List<Currency> resultList = new ArrayList<Currency>();
		if(serverid != null) {
			Cursor cursor = db.query(CURRENCY_TABLE, null, SERVERID_COLUMN + "=" + serverid, null, null, null, null);
			while(cursor.moveToNext()) {
				resultList.add(mapCurrency(cursor));
			}
			cursor.close();
			db.close();
		}
	
		return resultList;
	}

	private Currency mapCurrency(Cursor cursor) {
		Currency currency = new Currency();
		currency.setCurrencyId(cursor.getLong(cursor.getColumnIndex(CURRENCYID_COLUMN)));
		currency.setCode(cursor.getString(cursor.getColumnIndex(CODE_COLUMN)));
		currency.setName(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));
		currency.setExchangeRate(new BigDecimal(cursor.getString(cursor.getColumnIndex(EXCHANGERATE_COLUMN))));
		currency.setMain(cursor.getInt(cursor.getColumnIndex(MAIN_COLUMN)) == 1 ? true : false);
		currency.setServerid(cursor.getInt(cursor.getColumnIndex(SERVERID_COLUMN)));
		return currency;
	}
}
