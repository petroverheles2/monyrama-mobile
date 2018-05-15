package com.monyrama.db;

import java.util.List;

public interface IncomesSavedCommentDAO {
	public void saveIfNew(String comment);
	public List<String> getAll();
}