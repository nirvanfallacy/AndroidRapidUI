package rapidui.test.basictest.bindservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class TestService extends Service {
	@Override
	public IBinder onBind(Intent arg0) {
		return stub;
	}

	private final ITestService.Stub stub = new ITestService.Stub() {
		@Override
		public int sum(int a, int b) throws RemoteException {
			return a + b;
		}
	};
}
