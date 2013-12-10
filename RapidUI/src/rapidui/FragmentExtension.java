package rapidui;

import android.app.Fragment;

public class FragmentExtension extends FragmentExtensionBase {
	public FragmentExtension(Fragment fragment) {
		super(fragment.getActivity(), fragment, new FragmentHost(fragment));
	}
}
