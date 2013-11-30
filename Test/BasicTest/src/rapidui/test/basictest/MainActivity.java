package rapidui.test.basictest;

import rapidui.RapidActivity;
import rapidui.annotation.EventHandler;
import rapidui.annotation.Layout;
import rapidui.test.basictest.bindservice.BindServiceTestActivity;
import rapidui.test.basictest.font.FontTestActivity;
import rapidui.test.basictest.globallayout.GlobalLayoutTestActivity;
import rapidui.test.basictest.instancestate.InstanceStateTestActivity;
import rapidui.test.basictest.receiver.ReceiverTestActivity;
import android.content.Intent;
import android.view.View;

@Layout
public class MainActivity extends RapidActivity {
	@EventHandler
	void buttonBindServiceTest_Click(View v) {
		final Intent intent = new Intent(this, BindServiceTestActivity.class);
		startActivity(intent);
	}
	
	@EventHandler
	void buttonInstanceStateTest_Click(View v) {
		final Intent intent = new Intent(this, InstanceStateTestActivity.class);
		startActivity(intent);
	}
	
	@EventHandler
	void buttonReceiverTest_Click(View v) {
		final Intent intent = new Intent(this, ReceiverTestActivity.class);
		startActivity(intent);
	}
	
	@EventHandler
	void buttonGlobalLayoutTest_Click(View v) {
		final Intent intent = new Intent(this, GlobalLayoutTestActivity.class);
		startActivity(intent);
	}
	
	@EventHandler
	void buttonFontTest_Click(View v) {
		final Intent intent = new Intent(this, FontTestActivity.class);
		startActivity(intent);
	}
}
