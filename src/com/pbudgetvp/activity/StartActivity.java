package com.monyrama.activity;

import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(PrefsManager.getBoolean(this, PrefKeys.SHOW_WELCOME, true)) {
			Intent welcomeActivityIntent = new Intent(this, WelcomeActivity.class);
			welcomeActivityIntent.putExtra(ExtraNames.WELCOME_START.name(), true);		
			startActivity(welcomeActivityIntent);			
		} else {
			startActivity(new Intent(this, ExpensesPlansActivity.class));
		}
		
		finish();
	}

}
