package com.monyrama.db;

import android.content.Context;

public class DAOFactory {
	private static ServerDAO serverDAO;
	private static GenericDao genericDao;
	private static CurrencyDAO currencyDAO;
	private static AccountDAO accountDAO;
	private static ExpensePlanDAO expensePlanDAO;
	private static ExpenseDAO expenseDAO;
	private static ExpensesSavedCommentDAO expensesSavedCommentDAO;
	private static IncomesSavedCommentDAO incomesSavedCommentDAO;
	private static IncomeSourceDAOImpl incomeSourceDAO;
	private static IncomeDAOImpl incomeDAO;
	private static TransferDAOImpl transferDAO;

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
	
	public static ExpensesSavedCommentDAO getExpensesSavedCommentsDAO(Context context) {
		if(expensesSavedCommentDAO == null) {
			expensesSavedCommentDAO = new ExpensesSavedCommentDAOImpl(context);
		}		
		return expensesSavedCommentDAO;
	}

	public static IncomesSavedCommentDAO getIncomesSavedCommentDAO(Context context) {
		if(incomesSavedCommentDAO == null) {
			incomesSavedCommentDAO = new IncomesSavedCommentDAOImpl(context);
		}
		return incomesSavedCommentDAO;
	}

	public static IncomeSourceDAO getIncomeSourceDAO(Context context) {
		if(incomeSourceDAO == null) {
			incomeSourceDAO = new IncomeSourceDAOImpl(context);
		}
		return incomeSourceDAO;
	}

	public static IncomeDAO getIncomeDAO(Context context) {
		if(incomeDAO == null) {
			incomeDAO = new IncomeDAOImpl(context);
		}
		return incomeDAO;
	}

	public static TransferDAO getTransferDAO(Context context) {
		if(transferDAO == null) {
			transferDAO = new TransferDAOImpl(context);
		}
		return transferDAO;
	}
}
