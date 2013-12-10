package rapidui;

import android.app.Activity;
import android.view.View;

public class ActivityHost extends Host {
	private Activity activity;
	
	public ActivityHost(Activity activity) {
		this.activity = activity;
	}

	@Override
	public View findViewById(int id) {
		return activity.findViewById(id);
	}
}
