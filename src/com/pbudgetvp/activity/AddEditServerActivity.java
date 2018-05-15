package com.monyrama.activity;

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
import android.widget.EditText;
import android.widget.Toast;

import com.monyrama.R;
import com.monyrama.db.DAOFactory;
import com.monyrama.db.ServerDAO;
import com.monyrama.log.MyLog;
import com.monyrama.model.Server;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;
import com.monyrama.validator.Validator;
import com.monyrama.validator.ValidatorFactory;

public class AddEditServerActivity extends ActionBarActivity {
	private EditText aliasField;
	private EditText addressField;
	private EditText portField;
	private EditText commentField;
	
	private Server server;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_server);
        
		ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.add_server);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
        
        aliasField = (EditText)findViewById(R.id.aliasField);
        addressField = (EditText)findViewById(R.id.addressField);
        portField = (EditText)findViewById(R.id.portField);
        commentField = (EditText)findViewById(R.id.commentField);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
        	server = (Server)extras.get(ExtraNames.SELECTED_SERVER.name());
        	if(server != null) {      		
        		getSupportActionBar().setTitle(R.string.editServer);
        		
        		aliasField.setText(server.getAlias());
        		addressField.setText(server.getAddress());
        		portField.setText(server.getPort().toString());
        		commentField.setText(server.getComment());
        	}
        }
        
		if(PrefsManager.getBoolean(this, PrefKeys.SHOW_NEW_SERVER_TIPS, true)) {
			showTipsDialog();
		}
    }
        
    private void populateServerFromFields() {
		if(server == null) {
			server = new Server();
		}
		server.setAlias(aliasField.getText().toString().trim());
		server.setAddress(addressField.getText().toString().trim());
		try {
			server.setPort(Integer.valueOf(portField.getText().toString().trim()));	
		} catch (Exception e) {
			MyLog.debug("Exception creating integer from string", e);
		}
		server.setComment(commentField.getText().toString().trim());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.new_server_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		    // Respond to the action bar's Up/Home button
		    case android.R.id.home:
		        super.onBackPressed();
		        break;
			case R.id.menu_new_server_save:
				saveClicked();
				break;
			case R.id.menu_new_server_tips:
				showTipsDialog();
				break;
			default:			
				break;
		}
		return super.onOptionsItemSelected(item);
	}	
	
	private void showTipsDialog() {
		TipsDialogUtility.showTipsDialog(this, R.string.add_server_tip, PrefKeys.SHOW_NEW_SERVER_TIPS);
	}	
	
    //------------------- CLICK HANDLERS ----------------------
    public void saveClicked() {
    	populateServerFromFields();
		Validator validator = ValidatorFactory.createServerValidator(server, this);
		if(validator.validate()) {
			ServerDAO serverDAO = DAOFactory.getServerDAO(this);
			serverDAO.createOrUpdate(server);
			setResult(RESULT_OK);
			Toast.makeText(this, R.string.server_saved, Toast.LENGTH_SHORT).show();
			finish();
		} else {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);				
			dialogBuilder.setMessage(validator.getMessage());				
			dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
			    	dialog.dismiss();
			    }
			});
			AlertDialog alert = dialogBuilder.create();
			alert.show();
		}
    }
    
	public void cancelClicked(View view) {
    	super.onBackPressed();
    }
}
