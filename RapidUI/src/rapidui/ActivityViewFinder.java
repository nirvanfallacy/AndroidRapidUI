package rapidui;

import android.app.Activity;
import android.view.View;

public class ActivityViewFinder extends ViewFinder {
	private Activity activity;
	
	public ActivityViewFinder(Activity activity) {
		this.activity = activity;
	}

	@Override
	public View findViewById(int id) {
		return activity.findViewById(id);
	}
}
