package com.monyrama.db;

import android.content.Context;

public class DAOFactory {
	private static ServerDAO serverDAO;
	private static GenericDao genericDao;
	private static CurrencyDAO currencyDAO;
	private static AccountDAO accountDAO;
	private static ExpensePlanDAO expensePlanDAO;
	private static ExpenseDAO expenseDAO;
	private static SavedCommentDAO savedCommentDAO;

	public static ServerDAO getServerDAO(Context context) {
		if(serverDAO == null) {
			serverDAO = new ServerDAOImpl(context);	
		}		
		return serverDAO;
	}
	
	public static CurrencyDAO getCurrencyDAO(Context context) {
		if(currencyDAO == null) {
			currencyDAO = new CurrencyDAOImpl(context);
		}
		return currencyDAO;
	}
	
	public static GenericDao getGenericDAO(Context context) {
		if(genericDao == null) {
			genericDao = new GenericDaoImpl(context);	
		}		
		return genericDao;
	}
	
	public static AccountDAO getAccountDAO(Context context) {
		if(accountDAO == null) {
			accountDAO = new AccountDAOImpl(context);	
		}		
		return accountDAO;
	}
	
	public static ExpensePlanDAO getExpensePlanDAO(Context context) {
		if(expensePlanDAO == null) {
			expensePlanDAO = new ExpensePlanDAOImpl(context);	
		}		
		return expensePlanDAO;
	}
	
	public static ExpenseDAO getExpenseDAO(Context context) {
		if(expenseDAO == null) {
			expenseDAO = new ExpenseDAOImpl(context);	
		}		
		return expenseDAO;
	}
	
	public static SavedCommentDAO getSavedCommentsDAO(Context context) {
		if(savedCommentDAO == null) {
			savedCommentDAO = new SavedCommentDAOImpl(context);	
		}		
		return savedCommentDAO;
	}	
}
