package com.monyrama.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.monyrama.log.MyLog;
import com.monyrama.model.Account;
import com.monyrama.model.Server;
import com.monyrama.model.Transfer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by petroverheles on 7/26/16.
 */
public class TransferDAOImpl extends PBVPDatabaseOpenHelper implements TransferDAO {
    private final static String TRANSFERID_COLUMN = "transferid";
    private final static String FROM_SUM_COLUMN = "from_sum";
    private final static String FROM_ACCOUNTID_COLUMN = "from_accountid";
    private final static String TO_SUM_COLUMN = "to_sum";
    private final static String TO_ACCOUNTID_COLUMN = "to_accountid";

    private final static String FROM_ACCOUNTID = "from_accountid";
    private final static String FROM_CURRENCYID = "from_currencyid";
    private final static String FROM_ACCOUNT_NAME = "from_account_name";
    private final static String FROM_ACCOUNT_SUM = "from_account_sum";
    private final static String FROM_ACCOUNT_COMMENT = "from_account_comment";

    private final static String TO_ACCOUNTID = "to_accountid";
    private final static String TO_CURRENCYID = "to_currencyid";
    private final static String TO_ACCOUNT_NAME = "to_account_name";
    private final static String TO_ACCOUNT_SUM = "to_account_sum";
    private final static String TO_ACCOUNT_COMMENT = "to_account_comment";

    private final static String SELECT_BY_SERVER = "SELECT "
            + " tr." + TRANSFERID_COLUMN + ", "
            //+ " tr." + FROM_ACCOUNTID_COLUMN + ", "
            + " tr." + FROM_SUM_COLUMN + ", "
            //+ " tr." + TO_ACCOUNTID_COLUMN + ", "
            + " tr." + TO_SUM_COLUMN + ", "
            + " tr." + SERVERID_COLUMN + ", "
            + " tr." + TS_COLUMN + ", "
            + " froma."  + ACCOUNTID_COLUMN + " AS " + FROM_ACCOUNTID + ", "
            + " froma."  + CURRENCYID_COLUMN + " AS " + FROM_CURRENCYID + ", "
            + " froma."  + NAME_COLUMN + " AS " + FROM_ACCOUNT_NAME + ", "
            + " froma."  + SUM_COLUMN + " AS " + FROM_ACCOUNT_SUM + ", "
            + " froma."  + COMMENT_COLUMN + " AS " + FROM_ACCOUNT_COMMENT + ", "
            + " toa."  + ACCOUNTID_COLUMN + " AS " + TO_ACCOUNTID + ", "
            + " toa."  + CURRENCYID_COLUMN + " AS " + TO_CURRENCYID + ", "
            + " toa."  + NAME_COLUMN + " AS " + TO_ACCOUNT_NAME + ", "
            + " toa."  + SUM_COLUMN + " AS " + TO_ACCOUNT_SUM + ", "
            + " toa."  + COMMENT_COLUMN + " AS " + TO_ACCOUNT_COMMENT
            + " FROM " + TRANSFER_TABLE + " tr"
            + " JOIN " + ACCOUNT_TABLE + " froma ON "
            + " (" + "tr." + FROM_ACCOUNTID_COLUMN + "=" + "froma." + ACCOUNTID_COLUMN + " AND "
            + " tr." + SERVERID_COLUMN + "=" + "froma." + SERVERID_COLUMN + ") "
            + " JOIN " + ACCOUNT_TABLE + " toa ON "
            + " (" + "tr." + TO_ACCOUNTID_COLUMN + "=" + "toa." + ACCOUNTID_COLUMN + " AND "
            + " tr." + SERVERID_COLUMN + "=" + "toa." + SERVERID_COLUMN + ") "
            + "WHERE tr." + SERVERID_COLUMN + "=?";

    TransferDAOImpl(Context context) {
        super(context);
    }

    @Override
    public boolean save(Transfer transfer) {
        SQLiteDatabase db = getWritableDatabase();
        boolean success = true;
        db.beginTransaction();
        try {
            ContentValues transferValues = new ContentValues();

            transferValues.put(FROM_ACCOUNTID_COLUMN, transfer.getFromAccount().getAccountId());
            transferValues.put(TO_ACCOUNTID_COLUMN, transfer.getToAccount().getAccountId());
            transferValues.put(FROM_SUM_COLUMN, toCentsString(transfer.getFromSum()));
            transferValues.put(TO_SUM_COLUMN, toCentsString(transfer.getToSum()));
            transferValues.put(TS_COLUMN, transfer.getDate().getTime());
            transferValues.put(SERVERID_COLUMN, transfer.getServerId());

            BigDecimal fromAccountNewSum = transfer.getFromAccount().getSum().subtract(transfer.getFromSum());
            BigDecimal toAccountNewSum = transfer.getToAccount().getSum().add(transfer.getToSum());

            db.insert(TRANSFER_TABLE, null, transferValues);

            ContentValues fromAccountValues = new ContentValues();
            fromAccountValues.put(SUM_COLUMN, toCentsString(fromAccountNewSum));
            db.update(ACCOUNT_TABLE, fromAccountValues, ACCOUNTID_COLUMN + "=?",
                    new String[]{transfer.getFromAccount().getAccountId().toString()});

            ContentValues toAccountValues = new ContentValues();
            toAccountValues.put(SUM_COLUMN, toCentsString(toAccountNewSum));
            db.update(ACCOUNT_TABLE, toAccountValues, ACCOUNTID_COLUMN + "=?",
                    new String[]{transfer.getToAccount().getAccountId().toString()});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            success = false;
            MyLog.error("Error saving Expense", e);
        } finally {
            db.endTransaction();
        }
        db.close();
        return success;
    }

    @Override
    public boolean delete(Transfer transfer) {
        SQLiteDatabase db = getWritableDatabase();
        boolean success = true;
        db.beginTransaction();
        try {
            BigDecimal fromAccountNewSum = transfer.getFromAccount().getSum().add(transfer.getFromSum());
            BigDecimal toAccountNewSum = transfer.getToAccount().getSum().subtract(transfer.getToSum());

            db.delete(TRANSFER_TABLE, TRANSFERID_COLUMN + "=?", new String[]{transfer.getTransferId().toString()});

            ContentValues fromAccountValues = new ContentValues();
            fromAccountValues.put(SUM_COLUMN, toCentsString(fromAccountNewSum));
            db.update(ACCOUNT_TABLE, fromAccountValues, ACCOUNTID_COLUMN + "=?",
                    new String[]{transfer.getFromAccount().getAccountId().toString()});

            ContentValues toAccountValues = new ContentValues();
            toAccountValues.put(SUM_COLUMN, toCentsString(toAccountNewSum));
            db.update(ACCOUNT_TABLE, toAccountValues, ACCOUNTID_COLUMN + "=?",
                    new String[]{transfer.getToAccount().getAccountId().toString()});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            success = false;
            MyLog.error("Error saving Expense", e);
        } finally {
            db.endTransaction();
        }
        db.close();
        return success;    }

    @Override
    public List<Transfer> getTransfersByServer(Server server) {
        SQLiteDatabase db = getReadableDatabase();

        Map<Long, String> currencyCodes = new HashMap<>();

        Cursor currencyCursor = db.query(CURRENCY_TABLE, null, SERVERID_COLUMN + "=" + server.getServerid(), null, null, null, null);
        while(currencyCursor.moveToNext()) {
            currencyCodes.put(currencyCursor.getLong(currencyCursor.getColumnIndex(CURRENCYID_COLUMN)),
                    currencyCursor.getString(currencyCursor.getColumnIndex(CODE_COLUMN)));
        }
        currencyCursor.close();

        List<Transfer> resultList = new ArrayList<Transfer>();
        Cursor cursor = db.rawQuery(SELECT_BY_SERVER, new String[]{server.getServerid().toString()});
        while(cursor.moveToNext()) {
            resultList.add(mapTransfer(currencyCodes, cursor));
        }
        cursor.close();
        db.close();
        return resultList;
    }

    private Transfer mapTransfer(Map<Long, String> currencyCodes, Cursor cursor) {
        Transfer transfer = new Transfer();

        int serverId = cursor.getInt(cursor.getColumnIndex(SERVERID_COLUMN));

        transfer.setTransferId(cursor.getLong(cursor.getColumnIndex(TRANSFERID_COLUMN)));
        transfer.setFromSum(toMoney(cursor.getInt(cursor.getColumnIndex(FROM_SUM_COLUMN))));
        transfer.setToSum(toMoney(cursor.getInt(cursor.getColumnIndex(TO_SUM_COLUMN))));
        transfer.setDate(new Date(cursor.getLong(cursor.getColumnIndex(TS_COLUMN))));
        transfer.setServerId(serverId);

        Account fromAccount = new Account();
        fromAccount.setServerid(serverId);
        fromAccount.setAccountId(cursor.getLong(cursor.getColumnIndex(FROM_ACCOUNTID)));
        fromAccount.setName(cursor.getString(cursor.getColumnIndex(FROM_ACCOUNT_NAME)));
        fromAccount.setSum(toMoney(cursor.getInt(cursor.getColumnIndex(FROM_ACCOUNT_SUM))));
        fromAccount.setComment(cursor.getString(cursor.getColumnIndex(FROM_ACCOUNT_COMMENT)));

        transfer.setFromCurrencyCode(currencyCodes.get(cursor.getLong(cursor.getColumnIndex(FROM_CURRENCYID))));

        Account toAccount = new Account();
        toAccount.setServerid(serverId);
        toAccount.setAccountId(cursor.getLong(cursor.getColumnIndex(TO_ACCOUNTID)));
        toAccount.setName(cursor.getString(cursor.getColumnIndex(TO_ACCOUNT_NAME)));
        toAccount.setSum(toMoney(cursor.getInt(cursor.getColumnIndex(TO_ACCOUNT_SUM))));
        toAccount.setComment(cursor.getString(cursor.getColumnIndex(TO_ACCOUNT_COMMENT)));

        transfer.setToCurrencyCode(currencyCodes.get(cursor.getLong(cursor.getColumnIndex(TO_CURRENCYID))));

        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);

        return transfer;
    }
}
