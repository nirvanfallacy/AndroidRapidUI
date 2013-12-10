package rapidui;

import android.support.v4.app.Fragment;
import android.view.View;

public class SupportFragmentViewFinder extends Host {
	private Fragment fragment;
	
	public SupportFragmentViewFinder(Fragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public View findViewById(int id) {
		return fragment.getView().findViewById(id);
	}
}
