package rapidui;

import android.support.v4.app.FragmentActivity;

class SupportActivityHost extends ActivityHost {
	public SupportActivityHost(FragmentActivity activity) {
		super(activity);
	}

	@Override
	protected Object getFragmentManager() {
		return ((FragmentActivity) activity).getSupportFragmentManager();
	}
}
