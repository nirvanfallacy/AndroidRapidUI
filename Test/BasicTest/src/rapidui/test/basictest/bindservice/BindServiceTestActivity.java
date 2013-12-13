package rapidui.test.basictest.bindservice;

import rapidui.RapidActivity;
import rapidui.annotation.ConnectService;
import rapidui.annotation.EventHandler;
import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import android.os.RemoteException;
import android.widget.TextView;

@Layout
public class BindServiceTestActivity extends RapidActivity {
	@LayoutElement
	TextView textView;
	
	@ConnectService(alias="test", classType=TestService.class)
	ITestService service;
	
//	@OnServiceConnect(alias="test")
	@EventHandler
	void test_ServiceConnect(String alias) {
		try {
			textView.setText("Service connected. (Aliased as '" + alias + "')\n1 + 2 = " + service.sum(1, 2));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
