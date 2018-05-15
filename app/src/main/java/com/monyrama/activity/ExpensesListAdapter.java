package com.monyrama.activity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monyrama.R;
import com.monyrama.model.Expense;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;
import com.monyrama.util.DateUtil;
import com.monyrama.util.StringUtil;
import com.monyrama.util.SumLocalizer;

class ExpensesListAdapter extends BaseAdapter implements Filterable {

	private Context context;
	
	private LayoutInflater inflater;
	private List<Expense> expensesList;
	
	public ExpensesListAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.expensesList = new ArrayList<Expense>();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View itemView = null;
		if(expensesList.size() > 0) {
			Expense expense = expensesList.get(position);
			itemView = inflater.inflate(R.layout.expense_item, null);	
			TextView sumTextView = (TextView)itemView.findViewById(R.id.sumTextView);
			TextView itemTextView = (TextView)itemView.findViewById(R.id.itemTextView);
			TextView dateTextView = (TextView)itemView.findViewById(R.id.dateTextView);
			TextView expenseidTextView = (TextView)itemView.findViewById(R.id.expenseidTextView);
			TextView fromAccountTextView = (TextView)itemView.findViewById(R.id.fromAccountTextView);
			TextView commentsTextView = (TextView)itemView.findViewById(R.id.commentsTextView);
			ImageButton removeButton = (ImageButton)itemView.findViewById(R.id.removeButton);
			LinearLayout expenseInfoLayout = (LinearLayout)itemView.findViewById(R.id.expenseInfoLayout);
			
			sumTextView.setText(SumLocalizer.localize(expense.getSum().toPlainString()));
			itemTextView.setText(expense.getEnvelope().toString());
			fromAccountTextView.setText(expense.getAccount().getName());
			if(StringUtil.emptyString(expense.getComment())) {
				commentsTextView.setVisibility(View.GONE);
			} else  {
				commentsTextView.setText(expense.getComment());	
			}			
			
			Date expenseDate = expense.getDate();
			
			String expenseDateLabel;
			if(DateUtil.isToday(expenseDate)) {
				expenseDateLabel = context.getResources().getString(R.string.today);
			} else if(DateUtil.isYesterday(expenseDate)) {
				expenseDateLabel = context.getResources().getString(R.string.yesterday);
			} else {
				expenseDateLabel = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(expense.getDate());
			}
			expenseDateLabel = "  " + expenseDateLabel + "  "; //adding extra space
			dateTextView.setText(expenseDateLabel);
						
			if(PrefsManager.getBoolean(context, PrefKeys.SHOW_EXPENSE_IDS, false)) {								
				expenseidTextView.setVisibility(View.VISIBLE);
				expenseidTextView.setText("  ID: " + expense.getExpenseid() + "  ");				
			} else {
				expenseidTextView.setVisibility(View.GONE);
			}
			
			removeButton.setTag(position);	
			expenseInfoLayout.setTag(position);
		}
		return itemView;
	}
		
	@Override
	public int getCount() {
		return expensesList.size();
	}

	@Override
	public Object getItem(int position) {
		if(position > expensesList.size() - 1) {
			return null;
		}		
		
		return expensesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		if(position > expensesList.size() - 1) {
			return -1;
		}		
		
		return expensesList.get(position).getExpenseid();
	}

	@Override
	public Filter getFilter() {
		return null;
	}

	public void addAll(Collection<Expense> all) {
		expensesList.addAll(all);
		notifyDataSetChanged();
	}

	public void removeAll() {
		expensesList.clear();
		notifyDataSetChanged();
	}
	
}
