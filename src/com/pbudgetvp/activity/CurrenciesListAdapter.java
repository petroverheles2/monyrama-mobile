package com.monyrama.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.monyrama.R;
import com.monyrama.model.Currency;

class CurrenciesListAdapter extends BaseAdapter implements Filterable {

	private LayoutInflater inflater;
	private List<Currency> currenciesList;
	private Context context;
	
	public CurrenciesListAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
		this.currenciesList = new ArrayList<Currency>();
		this.context = context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View itemView = null;
		if(currenciesList.size() > 0) {
			Currency currency = currenciesList.get(position);
			itemView = inflater.inflate(R.layout.currency_item, null);			
			TextView nameCodeView = (TextView)itemView.findViewById(R.id.itemTextView);			
			TextView rateView = (TextView)itemView.findViewById(R.id.rateView);
			
			nameCodeView.setText(currency.getName() + " (" + currency.getCode() + ")");
			rateView.setText(currency.getExchangeRate().toString());			
		}
		return itemView;
	}
		
	@Override
	public int getCount() {
		return currenciesList.size();
	}

	@Override
	public Object getItem(int position) {
		return currenciesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return currenciesList.get(position).getCurrencyId();
	}

	@Override
	public Filter getFilter() {
		return null;
	}

	public void add(Currency t) {
		currenciesList.add(t);
		notifyDataSetChanged();
	}

	public void remove(Currency t) {
		currenciesList.remove(t);
	}

	public void addAll(Collection<Currency> all) {
		currenciesList.addAll(all);
		notifyDataSetChanged();
	}

	public void removeAll() {
		currenciesList.clear();
		notifyDataSetChanged();
	}
}
