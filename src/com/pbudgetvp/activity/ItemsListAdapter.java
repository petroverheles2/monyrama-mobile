package com.monyrama.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import com.monyrama.model.Envelope;
import com.monyrama.util.StringUtil;

class ItemsListAdapter extends BaseAdapter implements Filterable {

	private Context context;
	
	private LayoutInflater inflater;
	private List<Envelope> itemsList;
	
	public ItemsListAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.itemsList = new ArrayList<Envelope>();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View itemView = null;
		if(itemsList.size() > 0) {
			Envelope item = itemsList.get(position);
			itemView = inflater.inflate(R.layout.item_item, null);
			LinearLayout statusStick = (LinearLayout)itemView.findViewById(R.id.statusStick);
			LinearLayout itemInfoLayout = (LinearLayout)itemView.findViewById(R.id.itemInfoLayout);
			TextView nameTextView = (TextView)itemView.findViewById(R.id.nameTextView);
			TextView categoryNameTextView = (TextView)itemView.findViewById(R.id.categoryNameTextView);
			ImageButton viewItemButton = (ImageButton)itemView.findViewById(R.id.viewItemButton);
			
			itemInfoLayout.setTag(position);
			viewItemButton.setTag(position);
			nameTextView.setText(StringUtil.emptyString(item.getName())
					? item.getCategoryname()
					: item.getName());
			categoryNameTextView.setText(item.getCategoryname());
			
			if(item.isExpensesSumExceeded()) {
				statusStick.setBackgroundColor(context.getResources().getColor(R.color.red));
			}
		}
		return itemView;
	}
		
	@Override
	public int getCount() {
		return itemsList.size();
	}

	@Override
	public Object getItem(int position) {
		return itemsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return itemsList.get(position).getItemid();
	}

	@Override
	public Filter getFilter() {
		return null;
	}

	public void addAll(Collection<Envelope> all) {
		itemsList.addAll(all);
		Collections.sort(itemsList, new Comparator<Envelope>() {
			@Override
			public int compare(Envelope leftItem, Envelope rightItem) {
				return leftItem.toString().compareToIgnoreCase(rightItem.toString());
			}
		});
		notifyDataSetChanged();
	}

	public void removeAll() {
		itemsList.clear();
		notifyDataSetChanged();
	}
	
}
