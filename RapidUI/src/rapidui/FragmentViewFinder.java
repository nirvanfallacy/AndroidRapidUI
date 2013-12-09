package rapidui;

import android.app.Fragment;
import android.view.View;

public class FragmentViewFinder extends ViewFinder {
	private Fragment fragment;
	
	public FragmentViewFinder(Fragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public View findViewById(int id) {
		return fragment.getView().findViewById(id);
	}
}
