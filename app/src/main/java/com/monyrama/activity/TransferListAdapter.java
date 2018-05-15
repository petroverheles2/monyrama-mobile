package com.monyrama.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.monyrama.R;
import com.monyrama.model.Transfer;
import com.monyrama.util.DateUtil;
import com.monyrama.util.SumLocalizer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class TransferListAdapter extends BaseAdapter implements Filterable {

	private Context context;

	private LayoutInflater inflater;
	private List<Transfer> transferList;

	public TransferListAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.transferList = new ArrayList<Transfer>();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View itemView = null;
		if(transferList.size() > 0) {
			Transfer transfer = transferList.get(position);
			itemView = inflater.inflate(R.layout.transfer_item, null);
			TextView fromSumTextView = (TextView)itemView.findViewById(R.id.fromSumTextView);
			TextView toSumTextView = (TextView)itemView.findViewById(R.id.toSumTextView);
			TextView dateTextView = (TextView)itemView.findViewById(R.id.dateTextView);
			TextView fromAccountTextView = (TextView)itemView.findViewById(R.id.fromAccountTextView);
			TextView toAccountTextView = (TextView)itemView.findViewById(R.id.toAccountTextView);
			ImageButton removeButton = (ImageButton)itemView.findViewById(R.id.removeButton);

			fromSumTextView.setText(SumLocalizer.localize(transfer.getFromSum().toPlainString() +
				 " " + transfer.getFromCurrencyCode()));
			if(transfer.getFromCurrencyCode().equals(transfer.getToCurrencyCode())) {
				toSumTextView.setVisibility(View.GONE);
			} else {
				toSumTextView.setText(SumLocalizer.localize(transfer.getToSum().toPlainString() +
						" " + transfer.getToCurrencyCode()));
			}

			fromAccountTextView.setText(context.getResources().getString(R.string.fromAccount) +
					" " + transfer.getFromAccount().getName());
			toAccountTextView.setText(context.getResources().getString(R.string.to_account) +
					" " + transfer.getToAccount().getName());
			
			Date expenseDate = transfer.getDate();
			
			String expenseDateLabel;
			if(DateUtil.isToday(expenseDate)) {
				expenseDateLabel = context.getResources().getString(R.string.today);
			} else if(DateUtil.isYesterday(expenseDate)) {
				expenseDateLabel = context.getResources().getString(R.string.yesterday);
			} else {
				expenseDateLabel = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(transfer.getDate());
			}
			expenseDateLabel = "  " + expenseDateLabel + "  "; //adding extra space
			dateTextView.setText(expenseDateLabel);
			
			removeButton.setTag(position);	
		}
		return itemView;
	}
		
	@Override
	public int getCount() {
		return transferList.size();
	}

	@Override
	public Object getItem(int position) {
		if(position > transferList.size() - 1) {
			return null;
		}		
		
		return transferList.get(position);
	}

	@Override
	public long getItemId(int position) {
		if(position > transferList.size() - 1) {
			return -1;
		}		
		
		return transferList.get(position).getTransferId();
	}

	@Override
	public Filter getFilter() {
		return null;
	}

	public void addAll(Collection<Transfer> all) {
		transferList.addAll(all);
		notifyDataSetChanged();
	}

	public void removeAll() {
		transferList.clear();
		notifyDataSetChanged();
	}
	
}
