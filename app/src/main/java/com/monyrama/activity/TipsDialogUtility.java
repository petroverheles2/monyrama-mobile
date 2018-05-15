package com.monyrama.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.monyrama.R;
import com.monyrama.prefs.PrefKeys;
import com.monyrama.prefs.PrefsManager;

public class TipsDialogUtility {
	public static void showTipsDialog(final Context context, final int stringId, final PrefKeys prefKey) {
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.tip_dialog);
		dialog.setTitle(R.string.tips);
		
		WebView tipContentView = (WebView)dialog.findViewById(R.id.tipContentView);
		tipContentView.setBackgroundColor(Color.BLACK);
		tipContentView.loadDataWithBaseURL(null, context.getResources().getString(stringId), "text/html", "UTF-8", null);
		
		CheckBox gotItBox = (CheckBox)dialog.findViewById(R.id.gotItBtn);
		gotItBox.setChecked(!PrefsManager.getBoolean(context, prefKey, true));
		gotItBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PrefsManager.saveBoolean(context, prefKey, !isChecked);				
			}
		});
		
		Button dialogButton = (Button) dialog.findViewById(R.id.closeBtn);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
}