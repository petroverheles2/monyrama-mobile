package com.monyrama.db;

import java.util.List;

public interface ExpensesSavedCommentDAO {
	public void saveIfNew(String comment);
	public List<String> getAll();
}