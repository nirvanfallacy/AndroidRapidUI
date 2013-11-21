package rapidui;

import android.view.View;

public class ViewViewFinder extends ViewFinder {
	private View view;
	
	public ViewViewFinder(View view) {
		this.view = view;
	}

	@Override
	public View findViewById(int id) {
		return view.findViewById(id);
	}
}
