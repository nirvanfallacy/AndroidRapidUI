package rapidui;

import android.app.Fragment;
import android.view.View;

class FragmentHost extends Host {
	private Fragment fragment;
	
	public FragmentHost(Fragment fragment) {
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

	@Override
	protected Object getFragmentManager() {
		return fragment.getFragmentManager();
	}
}
