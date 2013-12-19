package rapidui;

import java.util.List;

import rapidui.annotation.AddFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.util.SparseArray;
import android.view.View;

public abstract class Host {
	public abstract void setHasOptionsMenu(boolean hasMenu);
	
	protected abstract View findViewById(int id);
	protected abstract Object getFragmentManager();
	
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

	public void addFragments(List<AddFragment> list) {
		final Object fm = getFragmentManager();
		if (fm == null) return;

		if (Build.VERSION.SDK_INT >= 11) {
			final FragmentTransaction transaction = ((FragmentManager) fm).beginTransaction();
			for (AddFragment af: list) {
				final int container = af.container();

				String tag = af.tag();
				if (tag.length() == 0) tag = null;
				
				Fragment fragment;
				try {
					fragment = (Fragment) af.fragment().newInstance();
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				
				transaction.add(container, fragment, tag);
			}
			
			transaction.commit();
		} else {
			final android.support.v4.app.FragmentTransaction transaction =
					((android.support.v4.app.FragmentManager) fm).beginTransaction();
			
			for (AddFragment af: list) {
				final int container = af.container();
	
				String tag = af.tag();
				if (tag.length() == 0) tag = null;
				
				android.support.v4.app.Fragment fragment;
				try {
					fragment = (android.support.v4.app.Fragment) af.fragment().newInstance();
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				
				transaction.add(container, fragment, tag);
			}
			
			transaction.commit();
		}
	}
}
