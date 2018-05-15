package com.monyrama.activity.sumkeyboard;

import com.monyrama.R;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class SumKeyboardView extends KeyboardView implements OnKeyboardActionListener {

	private Activity targetActivity;
	
	private NextKeyListener nextKeyListener;

	public SumKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setKeyboard(new Keyboard(context, R.xml.keyboard));
		setOnKeyboardActionListener(this);
	}

	public void showWithAnimation(Animation animation) {
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				setVisibility(View.VISIBLE);
			}
		});
		
		startAnimation(animation);
	}
	
	public void hideWithAnimation(Animation animation) {
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				setVisibility(View.GONE);
			}
		});
		
		startAnimation(animation);
	}	
	
	@Override
	public void swipeUp() {}

	@Override
	public void swipeRight() {}

	@Override
	public void swipeLeft() {}

	@Override
	public void swipeDown() {}

	@Override
	public void onText(CharSequence text) {}

	@Override
	public void onRelease(int primaryCode) {}

	@Override
	public void onPress(int primaryCode) {}

	@Override
	public void onKey(int primaryCode, int[] keyCodes) {
		long eventTime = System.currentTimeMillis();
		KeyEvent event = new KeyEvent(eventTime, eventTime,
				KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0,
				KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);

		if(primaryCode == KeyEvent.KEYCODE_ENTER) {
			if(nextKeyListener != null) {
				nextKeyListener.nextKeyPressed();
			}
		}
		
		targetActivity.dispatchKeyEvent(event);
	}
	
	public void setTargetActivity(Activity targetActivity) {
		this.targetActivity = targetActivity;
	}
	
	public void setNextKeyListener(NextKeyListener nextKeyListener) {
		this.nextKeyListener = nextKeyListener;
	}	
}
