package rapidui;

import android.view.View;

public class ViewContainer extends ViewFinder {
	private View v;
	
	public ViewContainer(View v) {
		this.v = v;
	}
	
	public void setView(View v) {
		this.v = v;
	}

	@Override
	protected View findViewById(int id) {
		if (v == null) {
			return null;
		} else {
			return v.findViewById(id);
		}
	}

	@Override
	protected View getSuperView() {
		return v;
	}
}
