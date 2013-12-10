package rapidui;

import android.app.Fragment;
import android.view.View;

public class FragmentHost extends Host {
	private Fragment fragment;
	
	public FragmentHost(Fragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public View findViewById(int id) {
		return fragment.getView().findViewById(id);
	}
}
