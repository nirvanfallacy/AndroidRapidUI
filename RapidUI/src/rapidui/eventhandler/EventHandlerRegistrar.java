package rapidui.eventhandler;

import android.view.View;

public abstract class EventHandlerRegistrar {
	public abstract void registerEventListener(View v, Object listener);
	
	public void unregisterEventListener(View v, Object listener) {
	}
}
