package com.monyrama.activity;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.monyrama.R;
import com.monyrama.db.DAOFactory;
import com.monyrama.db.ServerDAO;
import com.monyrama.model.Server;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;

public class ServersActivity extends ActionBarActivity {

	private ListView listView;
	private ServersListAdapter adapter;
	
	private ServerDAO serverDAO;
	
	private final static int NEW_EDIT_SERVER_REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.servers);
		
		ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.servers);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
		
		serverDAO = DAOFactory.getServerDAO(this);
		
		listView = (ListView)findViewById(R.id.serversListView);		
		adapter = new ServersListAdapter(this);
		adapter.addAll(serverDAO.getAll());
		
		listView.setAdapter(adapter);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.servers_menu, menu);
		return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        super.onBackPressed();
	        break;	    
	    case R.id.menu_servers_add_item:
			Intent newServerIntent = new Intent(this, AddEditServerActivity.class);
			startActivityForResult(newServerIntent, NEW_EDIT_SERVER_REQUEST_CODE);
			break;
	    }	
	    return super.onOptionsItemSelected(item);
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == NEW_EDIT_SERVER_REQUEST_CODE && resultCode == RESULT_OK) {
			reloadServersList();
		}
	}

	private void reloadServersList() {
		adapter.removeAll();
		List<Server> allServers = serverDAO.getAll();
		adapter.addAll(allServers);
		if(allServers.size() == 1) {
			PrefsManager.saveInt(this, PrefKeys.CURRENT_SERVER_ID, allServers.get(0).getServerid());
		}
	}
	
	//---------------- Click handlers --------------------------------------	
	public void serverItemClicked(View view) {
		Integer position = (Integer)view.getTag();
		Server selectedServer = (Server)adapter.getItem(position);
		Intent editServerIntent = new Intent(this, AddEditServerActivity.class);
		editServerIntent.putExtra(ExtraNames.SELECTED_SERVER.name(), selectedServer);
		startActivityForResult(editServerIntent, NEW_EDIT_SERVER_REQUEST_CODE);
	}
	
	public void setCurrentServerClicked(View view) {
		Integer position = (Integer)view.getTag();
		Server selectedServer = (Server)adapter.getItem(position);
		PrefsManager.saveInt(this, PrefKeys.CURRENT_SERVER_ID, selectedServer.getServerid());
		adapter.notifyDataSetChanged();
	}
	
	public void itemRemovedClicked(View view) {
		Integer position = (Integer)view.getTag();
		final Server selectedServer = (Server)adapter.getItem(position);
				
		AlertDialog alert;
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.think_please);
		dialogBuilder.setMessage(getResources().getString(R.string.question_remove_server)
				+ " \"" + selectedServer.getAlias() + "\"?");

		dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        serverDAO.delete(selectedServer);
		        reloadServersList();
		        dialog.dismiss();
		        Toast.makeText(ServersActivity.this, R.string.server_removed, Toast.LENGTH_SHORT).show();
		        if(adapter.getCount() == 0) {
		    		finish();
		        }
		    }
		});

		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {		     
		        dialog.dismiss();
		    }
		});

		alert = dialogBuilder.create();
		alert.show();
	}	
}
