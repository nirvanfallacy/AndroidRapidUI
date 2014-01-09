package rapidui;

import android.support.v4.app.Fragment;
import android.view.View;

class SupportFragmentHost extends Host {
	private Fragment fragment;
	
	public SupportFragmentHost(Fragment fragment) {
		this.fragment = fragment;
	}

	@Override
	protected View findViewById(int id) {
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

	@Override
	protected View getSuperView() {
		return fragment.getActivity().getWindow().getDecorView();
	}
}
