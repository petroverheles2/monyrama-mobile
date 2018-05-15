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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monyrama.R;
import com.monyrama.model.Server;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;
import com.monyrama.util.StringUtil;

class ServersListAdapter extends BaseAdapter implements Filterable {

	private LayoutInflater inflater;
	private List<Server> serversList;
	private Context context;
	
	public ServersListAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
		this.serversList = new ArrayList<Server>();
		this.context = context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View itemView = null;
		if(serversList.size() > 0) {
			Server server = serversList.get(position);
			itemView = inflater.inflate(R.layout.server_item, null);			
			TextView aliasView = (TextView)itemView.findViewById(R.id.itemTextView);			
			TextView commentsView = (TextView)itemView.findViewById(R.id.commentsTextView);
			ImageButton removeButton = (ImageButton)itemView.findViewById(R.id.removeButton);
			ImageButton setCurrentServerButton = (ImageButton)itemView.findViewById(R.id.setCurrentServerButton);
			LinearLayout serverInfoLayout = (LinearLayout)itemView.findViewById(R.id.serverInfoLayout);
			
			aliasView.setText(server.getAlias());
			
			if(StringUtil.emptyString(server.getComment())) {
				commentsView.setVisibility(View.GONE);
			} else {
				commentsView.setText(server.getComment());
			}
			
			serverInfoLayout.setTag(position);
			setCurrentServerButton.setTag(position);
			removeButton.setTag(position);
			
			Integer currentServerId = PrefsManager.getInt(context, PrefKeys.CURRENT_SERVER_ID, -1);
			
			if(currentServerId.equals(server.getServerid())) {
				setCurrentServerButton.setImageResource(R.drawable.tick);
			}
			
		}
		return itemView;
	}
		
	@Override
	public int getCount() {
		return serversList.size();
	}

	@Override
	public Object getItem(int position) {
		return serversList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return serversList.get(position).getServerid();
	}

	@Override
	public Filter getFilter() {
		return null;
	}

	public void add(Server t) {
		serversList.add(t);
		notifyDataSetChanged();
	}

	public void remove(Server t) {
		serversList.remove(t);
	}

	public void addAll(Collection<Server> all) {
		serversList.addAll(all);
		notifyDataSetChanged();
	}

	public void removeAll() {
		serversList.clear();
		notifyDataSetChanged();
	}
	
}
