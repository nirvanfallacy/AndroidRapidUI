package rapidui;

import android.view.View;

public abstract class Host {
	public abstract View findViewById(int id);
	public abstract void setHasOptionsMenu(boolean hasMenu);
}
