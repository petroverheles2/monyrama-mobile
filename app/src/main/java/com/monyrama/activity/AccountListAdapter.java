package com.monyrama.activity;

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
import com.monyrama.model.Account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class AccountListAdapter extends BaseAdapter implements Filterable {

	private Context context;

	private LayoutInflater inflater;
	private List<Account> accountList;

	public AccountListAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.accountList = new ArrayList<Account>();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View itemView = null;
		if(accountList.size() > 0) {
			Account item = accountList.get(position);
			itemView = inflater.inflate(R.layout.account_item, null);
			LinearLayout statusStick = (LinearLayout)itemView.findViewById(R.id.statusStick);
			LinearLayout itemInfoLayout = (LinearLayout)itemView.findViewById(R.id.itemInfoLayout);
			TextView nameTextView = (TextView)itemView.findViewById(R.id.nameTextView);
			TextView sumTextView = (TextView)itemView.findViewById(R.id.accountSumTextView);
			ImageButton viewItemButton = (ImageButton)itemView.findViewById(R.id.viewItemButton);
			
			itemInfoLayout.setTag(position);
			viewItemButton.setTag(position);
			nameTextView.setText(item.getName());
			sumTextView.setText(item.getSum().toPlainString());
			
			if(item.getSum().compareTo(BigDecimal.ZERO) < 0) {
				statusStick.setBackgroundColor(context.getResources().getColor(R.color.red));
			}
		}
		return itemView;
	}
		
	@Override
	public int getCount() {
		return accountList.size();
	}

	@Override
	public Object getItem(int position) {
		return accountList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return accountList.get(position).getAccountId();
	}

	@Override
	public Filter getFilter() {
		return null;
	}

	public void addAll(Collection<Account> all) {
		accountList.addAll(all);
		Collections.sort(accountList, new Comparator<Account>() {
			@Override
			public int compare(Account leftItem, Account rightItem) {
				return leftItem.toString().compareToIgnoreCase(rightItem.toString());
			}
		});
		notifyDataSetChanged();
	}

	public void removeAll() {
		accountList.clear();
		notifyDataSetChanged();
	}
	
}
