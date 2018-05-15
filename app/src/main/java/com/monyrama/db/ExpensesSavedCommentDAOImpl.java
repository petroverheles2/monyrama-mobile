package com.monyrama.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.monyrama.log.MyLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class ExpensesSavedCommentDAOImpl extends PBVPDatabaseOpenHelper implements ExpensesSavedCommentDAO {

	private static final int MAX_SAVED_COMMENTS_NUMBER = 100;

	ExpensesSavedCommentDAOImpl(Context context) {
		super(context);
	}

	@Override
	public void saveIfNew(String comment) {
		if(comment == null || comment.trim().equals("")) {
			return;
		}
		
		SQLiteDatabase db = getWritableDatabase();

		Cursor cursor = db.query(EXPENSES_SAVED_COMMENTS, new String[]{COMMENT_ID_COLUMN, COMMENT_COLUMN}, COMMENT_COLUMN + "=?", new String[]{comment}, null, null, null);
		if(!cursor.moveToFirst()) {			
			
			//Save new value
			ContentValues values = new ContentValues();
			values.put(COMMENT_COLUMN, comment);
			values.put(TS_COLUMN, new Date().getTime());
			db.insert(EXPENSES_SAVED_COMMENTS, null, values);
		} else {
			//Update timestamp of comment
			Integer commentid = cursor.getInt(cursor.getColumnIndex(COMMENT_ID_COLUMN));
			ContentValues values = new ContentValues();
			values.put(TS_COLUMN, new Date().getTime());
			db.update(EXPENSES_SAVED_COMMENTS, values, COMMENT_ID_COLUMN + "=?", new String[]{commentid.toString()});
		}
		
		cursor.close();
		
		db.close();
	}

	@Override
	public List<String> getAll() {
		SQLiteDatabase db = getWritableDatabase();
		
		//Clear unused comments if there's too much records
		Cursor countCursor = db.rawQuery("SELECT count(*) FROM " + EXPENSES_SAVED_COMMENTS, null);
		countCursor.moveToFirst();
		int count = countCursor.getInt(0);
		countCursor.close();
		if(count > MAX_SAVED_COMMENTS_NUMBER) {
			long thirtyDaysInMillis = 30L /*days in months*/ * 24L /*hours in day*/ * 60L /*minutes in hour*/ * 60L /*seconds in minute*/ * 1000L /*milliseconds in second*/;
			//long thirtyDaysInMillis = 10L * 60L * 1000L;
			int rows = db.delete(EXPENSES_SAVED_COMMENTS, TS_COLUMN + " < ?", new String[]{Long.toString(new Date().getTime() - thirtyDaysInMillis)});
			MyLog.debug(rows + " of unused saved comments deleted");
		}

		//Get saved comments
		Cursor cursor = db.query(EXPENSES_SAVED_COMMENTS, new String[]{COMMENT_COLUMN}, null, null, null, null, null);

		List<String> savedCommentsList = new ArrayList<String>();
		
		while(cursor.moveToNext()) {		
			String comment = cursor.getString(cursor.getColumnIndex(COMMENT_COLUMN));
			savedCommentsList.add(comment);
		}
		
		cursor.close();
		db.close();
		
		Collections.sort(savedCommentsList);
		
		return savedCommentsList;
	}
}
