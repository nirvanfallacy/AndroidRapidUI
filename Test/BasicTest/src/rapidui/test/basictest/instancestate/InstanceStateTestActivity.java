package rapidui.test.basictest.instancestate;

import rapidui.RapidActivity;
import rapidui.annotation.EventHandler;
import rapidui.annotation.InstanceState;
import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * To test auto saving/restoring instance state, increase the value by clicking
 * buttons first. Then watch the value is not changed when you rotate the phone.
 */
@Layout
public class InstanceStateTestActivity extends RapidActivity {
	@InstanceState int value;
	
	@LayoutElement TextView textValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		value = 0;

		super.onCreate(savedInstanceState);
		
		renderValue();
	}
	
	@EventHandler
	void buttonIncrement_Click(View v) {
		++value;
		renderValue();
	}
	
	@EventHandler
	void buttonDecrement_Click(View v) {
		--value;
		renderValue();
	}
	
	private void renderValue() {
		textValue.setText(Integer.toString(value));
	}
}
