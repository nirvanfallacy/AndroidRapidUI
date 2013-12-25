package rapidui.shortcut;

import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

public class ViewTreeObserverShortcut {
	public static void addOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
		v.getViewTreeObserver().addOnGlobalLayoutListener(listener);
	}
	
	@SuppressWarnings("deprecation")
	public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
		if (Build.VERSION.SDK_INT >= 16) {
			v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
		} else {
			v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
		}
	}
}
