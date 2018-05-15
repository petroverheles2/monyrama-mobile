package com.monyrama.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.monyrama.model.IncomeSource;
import com.monyrama.model.Server;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class IncomeSourceDAOImpl extends PBVPDatabaseOpenHelper implements
		IncomeSourceDAO {

	IncomeSourceDAOImpl(Context context) {
		super(context);
	}

	@Override
	public List<IncomeSource> getIncomeSourceByServer(Server server) {
		SQLiteDatabase db = getReadableDatabase();
		List<IncomeSource> resultList = new ArrayList<IncomeSource>();
		Cursor cursor = db.query(INCOME_SOURCE_TABLE, null, SERVERID_COLUMN + "=?", new String[]{server.getServerid().toString()}, null, null, null);
		while(cursor.moveToNext()) {
			resultList.add(mapIncomeSource(cursor));
		}
		cursor.close();
		db.close();
		return resultList;
	}

	@Override
	public IncomeSource findById(Long id) {
		IncomeSource incomeSource = null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(INCOME_SOURCE_TABLE, null, INCOMESOURCEID_COLUMN + "=?", new String[]{id.toString()}, null, null, null);
		if(cursor.moveToFirst()) {
			incomeSource = mapIncomeSource(cursor);
		}

		return incomeSource;
	}

	private IncomeSource mapIncomeSource(Cursor cursor) {
		IncomeSource incomeSource = new IncomeSource();
		incomeSource.setIncomeSourceId(cursor.getLong(cursor.getColumnIndex(INCOMESOURCEID_COLUMN)));
		incomeSource.setServerid(cursor.getInt(cursor.getColumnIndex(SERVERID_COLUMN)));
		incomeSource.setName(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));
		incomeSource.setCurrencyId(cursor.getLong(cursor.getColumnIndex(CURRENCYID_COLUMN)));
		incomeSource.setComment(cursor.getString(cursor.getColumnIndex(COMMENT_COLUMN)));
		incomeSource.setSum(new BigDecimal(cursor.getInt(cursor.getColumnIndex(SUM_COLUMN))).divide(new BigDecimal("100")));
		return incomeSource;
	}
}
