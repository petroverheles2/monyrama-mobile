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
import com.monyrama.model.Account;
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
            + " ex." + ACCOUNTID_COLUMN + ", "
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
            + " it." + COMMENT_COLUMN + " AS " + ITEM_COMMENT + ", "
            + " a."  + ACCOUNTID_COLUMN + ", "
            + " a."  + CURRENCYID_COLUMN + ", "
            + " a."  + NAME_COLUMN + " AS " + ACCOUNT_NAME + ", "
            + " a."  + SUM_COLUMN + " AS " + ACCOUNT_SUM + ", "
            + " a."  + COMMENT_COLUMN + " AS " + ACCOUNT_COMMENT
            + " FROM " + EXPENSE_TABLE + " ex"
            + " JOIN " + ENVELOPE_TABLE + " it ON "
            + " (" + "ex." + ENVELOPEID_COLUMN + "=" + "it." + ENVELOPEID_COLUMN + " AND "
            + " ex." + SERVERID_COLUMN + "=" + "it." + SERVERID_COLUMN + ") "
            + " JOIN " + ACCOUNT_TABLE + " a ON "
            + " (" + "ex." + ACCOUNTID_COLUMN + "=" + "a." + ACCOUNTID_COLUMN + " AND "
            + " ex." + SERVERID_COLUMN + "=" + "a." + SERVERID_COLUMN + ") "
            + "WHERE ex." + SERVERID_COLUMN + "=?";

    private final static String SELECT_BY_SERVER_AND_EXPENSE_PLAN = SELECT_BY_SERVER
            + " AND ex." + EXPENSEPLANID_COLUMN + "=?"
            + " ORDER BY ex." + TS_COLUMN + " desc"
            + ", ex." + EXPENSEID_COLUMN + " desc";

    ExpenseDAOImpl(Context context) {
        super(context);
    }

    @Override
    public boolean createOrUpdateExpense(Expense expense, BigDecimal previousSum, ExpensePlan expensePlan, Envelope envelope, Account account) {
        SQLiteDatabase db = getWritableDatabase();
        boolean success = true;
        db.beginTransaction();
        try {
            ContentValues expenseValues = new ContentValues();
            expenseValues.put(ENVELOPEID_COLUMN, expense.getEnvelope().getItemid());
            expenseValues.put(EXPENSEPLANID_COLUMN, expense.getExpenseplanid());
            expenseValues.put(SUM_COLUMN, toCentsString(expense.getSum()));
            expenseValues.put(ACCOUNTID_COLUMN, expense.getAccount().getAccountId());
            expenseValues.put(TS_COLUMN, expense.getDate().getTime());
            expenseValues.put(COMMENT_COLUMN, expense.getComment());
            expenseValues.put(SERVERID_COLUMN, expense.getServerid());

            BigDecimal envelopeRemainder;
            BigDecimal expensePlanRemainder;
            BigDecimal accountRemainder;
            if (expense.getExpenseid() == null) { //new expense
                db.insert(EXPENSE_TABLE, null, expenseValues);

                envelopeRemainder = envelope.getRemainder().subtract(expense.getSum());
                expensePlanRemainder = expensePlan.getRemainder().subtract(expense.getSum());
                accountRemainder = account.getSum().subtract(expense.getSum());
            } else { //editing expense
                db.update(EXPENSE_TABLE, expenseValues, EXPENSEID_COLUMN + "=?",
                        new String[]{expense.getExpenseid().toString()});

                BigDecimal previousAndNewSumDiff = previousSum.subtract(expense.getSum());
                envelopeRemainder = envelope.getRemainder().add(previousAndNewSumDiff);
                expensePlanRemainder = expensePlan.getRemainder().add(previousAndNewSumDiff);
                accountRemainder = account.getSum().add(previousAndNewSumDiff);
            }

            ContentValues envelopeValues = new ContentValues();
            envelopeValues.put(REMAINDER_COLUMN, toCentsString(envelopeRemainder));
            db.update(ENVELOPE_TABLE, envelopeValues, ENVELOPEID_COLUMN + "=?",
                    new String[]{envelope.getItemid().toString()});

            ContentValues expensePlanValues = new ContentValues();
            expensePlanValues.put(REMAINDER_COLUMN, toCentsString(expensePlanRemainder));
            db.update(EXPENSE_PLAN_TABLE, expensePlanValues, EXPENSEPLANID_COLUMN + "=?",
                    new String[]{expensePlan.getExpenseplanid().toString()});

            ContentValues accountValues = new ContentValues();
            accountValues.put(SUM_COLUMN, toCentsString(accountRemainder));
            db.update(ACCOUNT_TABLE, accountValues, ACCOUNTID_COLUMN + "=?",
                    new String[]{account.getAccountId().toString()});

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
    public List<Expense> getAllExpensesByExpensePlan(ExpensePlan expensePlan) {
        SQLiteDatabase db = getReadableDatabase();
        List<Expense> resultList = new ArrayList<Expense>();
        if (expensePlan != null) {
            Cursor cursor = db.rawQuery(SELECT_BY_SERVER_AND_EXPENSE_PLAN,
                    new String[]{expensePlan.getServerid().toString(), expensePlan.getExpenseplanid().toString()});
            while (cursor.moveToNext()) {
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
        BigDecimal itemSum = toMoney(cursor.getInt(cursor.getColumnIndex(ITEM_SUM)));
        BigDecimal itemRemainder = toMoney(cursor.getInt(cursor.getColumnIndex(REMAINDER_COLUMN)));
        String itemComment = cursor.getString(cursor.getColumnIndex(ITEM_COMMENT));
        Integer serverid = cursor.getInt(cursor.getColumnIndex(SERVERID_COLUMN));
        Long expenseid = cursor.getLong(cursor.getColumnIndex(EXPENSEID_COLUMN));
        BigDecimal expenseSum = toMoney(cursor.getInt(cursor.getColumnIndex(SUM_COLUMN)));
        Date expenseDate = new Date(cursor.getLong(cursor.getColumnIndex(TS_COLUMN)));
        String expenseComment = cursor.getString(cursor.getColumnIndex(COMMENT_COLUMN));
        Long accountId = cursor.getLong(cursor.getColumnIndex(ACCOUNTID_COLUMN));
        Long currencyId = cursor.getLong(cursor.getColumnIndex(CURRENCYID_COLUMN));
        BigDecimal accountSum = toMoney(cursor.getInt(cursor.getColumnIndex(ACCOUNT_SUM)));
        String accountComment = cursor.getString(cursor.getColumnIndex(ACCOUNT_COMMENT));
        String accountName = cursor.getString(cursor.getColumnIndex(ACCOUNT_NAME));

        Envelope item = new Envelope();
        item.setItemid(itemid);
        item.setExpensplanid(expenseplanid);
        item.setName(itemName);
        item.setCategoryname(itemCategoryname);
        item.setSum(itemSum);
        item.setRemainder(itemRemainder);
        item.setComment(itemComment);
        item.setServerid(serverid);

        Account account = new Account();
        account.setAccountId(accountId);
        account.setCurrencyId(currencyId);
        account.setSum(accountSum);
        account.setName(accountName);
        account.setComment(accountComment);
        account.setServerid(serverid);

        Expense expense = new Expense();
        expense.setExpenseid(expenseid);
        expense.setExpenseplanid(expenseplanid);
        expense.setAccount(account);
        expense.setServerid(serverid);
        expense.setEnvelope(item);
        expense.setSum(expenseSum);
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

            //update account sum
            String expenseSumInCents = toCentsString(expense.getSum());
            db.execSQL("UPDATE "
                            + ACCOUNT_TABLE
                            + " SET "
                            + SUM_COLUMN
                            + " = "
                            + SUM_COLUMN
                            + " + "
                            + expenseSumInCents
                            + " WHERE "
                            + ACCOUNTID_COLUMN
                            + " = "
                            + expense.getAccount().getAccountId()
            );

            //update expense plan remainder
            db.execSQL("UPDATE "
                            + EXPENSE_PLAN_TABLE
                            + " SET "
                            + REMAINDER_COLUMN
                            + " = "
                            + REMAINDER_COLUMN
                            + " + "
                            + expenseSumInCents
                            + " WHERE "
                            + EXPENSEPLANID_COLUMN
                            + " = "
                            + expense.getExpenseplanid()
            );

            //update expense plan envelope remainder
            db.execSQL("UPDATE "
                            + ENVELOPE_TABLE
                            + " SET "
                            + REMAINDER_COLUMN
                            + " = "
                            + REMAINDER_COLUMN
                            + " + "
                            + expenseSumInCents
                            + " WHERE "
                            + ENVELOPEID_COLUMN
                            + " = "
                            + expense.getEnvelope().getItemid()
            );

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
        if (server != null) {
            Cursor cursor = db.rawQuery(SELECT_BY_SERVER,
                    new String[]{server.getServerid().toString()});
            while (cursor.moveToNext()) {
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
