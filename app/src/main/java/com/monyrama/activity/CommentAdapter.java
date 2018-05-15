package com.monyrama.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.monyrama.R;

public class CommentAdapter extends BaseAdapter implements Filterable {
			
	private List<String> values;
	private List<String> filteredValues = new ArrayList<String>();
	
	private LayoutInflater inflater;
	
	private String lowerConstraint = "";
	
	public CommentAdapter(Context context, List<String> values) {
		this.values = values;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
             	
                if(constraint != null) {
                    lowerConstraint = constraint.toString().trim().toLowerCase();
                    filteredValues = new ArrayList<String>();
                    for(String value : values) {
                    	if(value.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                    		filteredValues.add(value);
                    	}
                    }                	
                }
                    
             	filterResults.values = filteredValues;
                filterResults.count = filteredValues.size();            

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if(results != null && results.count > 0) {
                	notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getCount() {
		return filteredValues.size();
	}

	@Override
	public Object getItem(int position) {
		return filteredValues.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(filteredValues.size() > 0) {
			convertView = inflater.inflate(R.layout.comment_autocomplete_item, null);
			String value = filteredValues.get(position);
			((TextView)convertView).setText(value);
			highlightConstraint(((TextView)convertView), value);
		}

		return convertView;
	}
	
	private void highlightConstraint(TextView textView, String text) {		
		String lowerText = text.toLowerCase();
		if(!lowerText.contains(lowerConstraint)) {
			textView.setText(text);
		} else {
			int startIndex = lowerText.indexOf(lowerConstraint);
			int endIndex = startIndex + lowerConstraint.length();

			String startText = text.substring(0, startIndex);
			String middleText = text.substring(startIndex, endIndex);
			String endText = text.substring(endIndex, text.length());
			
			String htmlString = startText + "<b><font color='blue'>" + middleText + "</font></b>" + endText;
			
			textView.setText(Html.fromHtml(htmlString), TextView.BufferType.SPANNABLE);			
		}
		
	}
}
