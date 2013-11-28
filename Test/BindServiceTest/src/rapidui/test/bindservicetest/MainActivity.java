package rapidui.test.bindservicetest;

import rapidui.RapidActivity;
import rapidui.annotation.BindService;
import rapidui.annotation.EventHandler;
import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import rapidui.annotation.event.OnServiceConnect;
import android.os.RemoteException;
import android.widget.TextView;

@Layout
public class MainActivity extends RapidActivity {
	@LayoutElement
	TextView textView;
	
	@BindService(alias="test", classType=TestService.class)
	ITestService service;
	
	@EventHandler
	void test_ServiceConnect(String alias) {
		try {
			textView.setText("Service connected. (Aliased as '" + alias + "')\n1 + 2 = " + service.sum(1, 2));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
