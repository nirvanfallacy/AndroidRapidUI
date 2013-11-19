package rapidui.eventhandler;

import android.view.View;

public class OnClickRegistrar extends EventHandlerRegistrar {
	@Override
	public void registerEventListener(View v, Object listener) {
		v.setOnClickListener((View.OnClickListener) listener);
	}
}
