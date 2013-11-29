package rapidui.test.basictest.globallayout;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import rapidui.RapidActivity;
import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import rapidui.annotation.event.OnGlobalLayout;

@Layout
public class GlobalLayoutTestActivity extends RapidActivity {
	@LayoutElement Button button;
	@LayoutElement TextView text1;
	@LayoutElement TextView text2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		text1.setText("Button width before global layout = " + button.getWidth());
	}
	
	@OnGlobalLayout
	void onGlobalLayout() {
		text2.setText("Button width after global layout = " + button.getWidth());
	}
}