package rapidui;

import android.util.SparseArray;
import android.view.View;

public abstract class ViewFinder {
	protected abstract View findViewById(int id);
	protected abstract View getSuperView();

	private SparseArray<View> viewCache;
	
	public View findView(int id) {
		if (viewCache == null) {
			return findViewById(id);
		} else {
			View v = viewCache.get(id);
			if (v == null) {
				v = findViewById(id);
				viewCache.put(id, v);
			}
			return v;
		}
	}
	
	public void enableViewCache() {
		viewCache = new SparseArray<View>();
	}
	
	public void disableViewCache() {
		viewCache = null;
	}
}
