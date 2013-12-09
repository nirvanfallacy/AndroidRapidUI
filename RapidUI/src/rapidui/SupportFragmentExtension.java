package rapidui;

import android.support.v4.app.Fragment;

public class SupportFragmentExtension extends FragmentExtensionBase {
	public SupportFragmentExtension(Fragment fragment) {
		super(fragment.getActivity(), fragment, new SupportFragmentViewFinder(fragment));
	}
}
