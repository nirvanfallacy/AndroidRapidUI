package rapidui.test.basictest.receiver;

import java.util.Random;

import rapidui.RapidActivity;
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
	private static final String ACTION_BROADCAST_TEST = "rapidui.test.receivertest.action.BROADCAST_TEST";
	
	@LayoutElement TextView textResult;
	
	@OnClick({R.id.button_test_plus, R.id.button_test_minus})
	void buttonClickHandler(View v) {
		final Random r = new Random();
		
		final Intent intent = new Intent(ACTION_BROADCAST_TEST);
		intent.putExtra("operator", (v.getId() == R.id.button_test_plus ? "+" : "-"));
		intent.putExtra("a", r.nextInt(10));
		intent.putExtra("b", r.nextInt(10));
		sendBroadcast(intent);
	}
	
	@Receiver(action=ACTION_BROADCAST_TEST,
			  extra={"operator", "a", "b"})
	void receiver(String op, int a, int b) {
		int result;
		if (op.equals("+")) {
			result = a + b;
		} else if (op.equals("-")) {
			result = a - b;
		} else {
			textResult.setText("");
			return;
		}

		textResult.setText(a + " " + op + " " + b + " = " + result);
	}
}
