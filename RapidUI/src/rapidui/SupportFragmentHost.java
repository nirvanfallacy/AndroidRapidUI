package rapidui;

import android.support.v4.app.Fragment;
import android.view.View;

public class SupportFragmentHost extends Host {
	private Fragment fragment;
	
	public SupportFragmentHost(Fragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public View findViewById(int id) {
		return fragment.getView().findViewById(id);
	}

	@Override
	public void setHasOptionsMenu(boolean hasMenu) {
		fragment.setHasOptionsMenu(hasMenu);
	}
}
