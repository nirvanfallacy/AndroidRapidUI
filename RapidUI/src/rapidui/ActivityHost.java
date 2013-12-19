package rapidui;

import android.app.Activity;
import android.view.View;

class ActivityHost extends Host {
	protected Activity activity;
	
	public ActivityHost(Activity activity) {
		this.activity = activity;
	}

	@Override
	public View findViewById(int id) {
		return activity.findViewById(id);
	}

	@Override
	public void setHasOptionsMenu(boolean hasMenu) {
	}

	@Override
	protected Object getFragmentManager() {
		return activity.getFragmentManager();
	}
}
