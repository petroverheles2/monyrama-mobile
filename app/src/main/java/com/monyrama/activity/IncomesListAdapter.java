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
import com.monyrama.model.Income;
import com.monyrama.util.DateUtil;
import com.monyrama.util.StringUtil;
import com.monyrama.util.SumLocalizer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class IncomesListAdapter extends BaseAdapter implements Filterable {

	private Context context;

	private LayoutInflater inflater;
	private List<Income> itemsList;

	public IncomesListAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.itemsList = new ArrayList<Income>();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View itemView = null;
		if(itemsList.size() > 0) {
			Income item = itemsList.get(position);
			itemView = inflater.inflate(R.layout.income_item, null);
			LinearLayout itemInfoLayout = (LinearLayout)itemView.findViewById(R.id.incomeInfoLayout);
			TextView sumTextView = (TextView)itemView.findViewById(R.id.incomeSumTextView);
			TextView toAccountTextView = (TextView)itemView.findViewById(R.id.toAccountTextView);
			TextView commentTextView = (TextView)itemView.findViewById(R.id.incomeCommentsTextView);
            TextView dateTextView = (TextView)itemView.findViewById(R.id.dateTextView);
            TextView incomeIdTextView = (TextView)itemView.findViewById(R.id.incomeIdTextView);
            incomeIdTextView.setVisibility(View.GONE);
            ImageButton removeButton = (ImageButton)itemView.findViewById(R.id.removeButton);

            itemInfoLayout.setTag(position);
			sumTextView.setText(SumLocalizer.localize(item.getSum().toPlainString()));
			toAccountTextView.setText(item.getAccount().getName());
			commentTextView.setText(item.getComment());
            if(StringUtil.emptyString(item.getComment())) {
                commentTextView.setVisibility(View.GONE);
            } else  {
                commentTextView.setText(item.getComment());
            }

            Date incomeDate = item.getDate();

            String dateLabel;
            if(DateUtil.isToday(incomeDate)) {
                dateLabel = context.getResources().getString(R.string.today);
            } else if(DateUtil.isYesterday(incomeDate)) {
                dateLabel = context.getResources().getString(R.string.yesterday);
            } else {
                dateLabel = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(incomeDate.getDate());
            }
            dateLabel = "  " + dateLabel + "  "; //adding extra space
            dateTextView.setText(dateLabel);

            removeButton.setTag(position);
            itemInfoLayout.setTag(position);
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
		return itemsList.get(position).getIncomeId();
	}

	@Override
	public Filter getFilter() {
		return null;
	}

	public void addAll(Collection<Income> all) {
		itemsList.addAll(all);
		notifyDataSetChanged();
	}

	public void removeAll() {
		itemsList.clear();
		notifyDataSetChanged();
	}
	
}
