package rapidui.test.basictest.receiver;

import java.util.Random;

import rapidui.RapidActivity;
import rapidui.annotation.Extra;
import rapidui.annotation.IntentAction;
import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import rapidui.annotation.Receiver;
import rapidui.annotation.event.OnClick;
import rapidui.test.basictest.R;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

@Layout
public class ReceiverTestActivity extends RapidActivity {
	private static final String ACTION_PLUS = "rapidui.test.receivertest.action.PLUS";
	private static final String ACTION_MINUS = "rapidui.test.receivertest.action.MINUS";
	
	@LayoutElement TextView textResult;
	
	@OnClick({R.id.button_test_plus, R.id.button_test_minus})
	void buttonClickHandler(View v) {
		final Random r = new Random();
		
		final Intent intent = new Intent(
				v.getId() == R.id.button_test_plus ? ACTION_PLUS : ACTION_MINUS);
		intent.putExtra("a", r.nextInt(10));
		intent.putExtra("b", r.nextInt(10));
		sendBroadcast(intent);
	}
	
	@Receiver({ACTION_PLUS, ACTION_MINUS})
	void receiver(
			@IntentAction String action,
			@Extra("a") int a,
			@Extra("b") int b) {
		
		String op;
		int result;
		
		if (action.equals(ACTION_PLUS)) {
			result = a + b;
			op = "+";
		} else if (action.equals(ACTION_MINUS)) {
			result = a - b;
			op = "-";
		} else {
			textResult.setText("");
			return;
		}

		textResult.setText(a + " " + op + " " + b + " = " + result);
	}
}
