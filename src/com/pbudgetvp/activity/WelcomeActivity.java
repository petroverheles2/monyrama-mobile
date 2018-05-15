package com.monyrama.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;

import com.monyrama.R;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;

public class WelcomeActivity extends Activity {

	private boolean welcomeStart = false;
	
	private WebView welcomeView;
	private CheckBox dontShowBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		Intent intent = getIntent();
		welcomeStart = intent.getBooleanExtra(ExtraNames.WELCOME_START.name(), false);
		
		welcomeView = (WebView)findViewById(R.id.welcomeView);
		dontShowBox = (CheckBox)findViewById(R.id.dontShowBox);
		
		welcomeView.setBackgroundColor(Color.BLACK);
		welcomeView.loadDataWithBaseURL(null, getResources().getString(R.string.welcomehtml), "text/html", "UTF-8", null);
		//welcomeView.loadData(getResources().getString(R.string.welcomehtml), "text/html", "UTF-8");
		
		if(!welcomeStart) {
			findViewById(R.id.bottomPanelLayout).setVisibility(View.GONE);
		}
	}
	
	public void goOn(View view) {
		if(welcomeStart) {
			startActivity(new Intent(this, ExpensesPlansActivity.class));
		}
		finish();
	}
	
	public void dontShowClicked(View view) {
		PrefsManager.saveBoolean(this, PrefKeys.SHOW_WELCOME, !dontShowBox.isChecked());
	}

}
