package rapidui;

import android.view.View;

abstract class Host {
	public abstract View findViewById(int id);
	public abstract void setHasOptionsMenu(boolean hasMenu);
}
