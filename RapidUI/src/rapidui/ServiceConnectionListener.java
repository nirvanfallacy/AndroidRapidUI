package rapidui;

import android.os.IBinder;

public interface ServiceConnectionListener {
	void onServiceConnect(IBinder binder);
	void onServiceDisconnect(IBinder binder);
}
