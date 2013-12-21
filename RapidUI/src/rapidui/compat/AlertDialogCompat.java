package rapidui.compat;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;

public class AlertDialogCompat {
	public static final int THEME_TRADITIONAL = 1;
	public static final int THEME_HOLO_DARK = 2;
	public static final int THEME_HOLO_LIGHT = 3;
	public static final int THEME_DEVICE_DEFAULT_DARK = 4;
	public static final int THEME_DEVICE_DEFAULT_LIGHT = 5;
	
	public static AlertDialog.Builder newBuilder(Context context, int theme) {
		if (Build.VERSION.SDK_INT >= 11) {
			return new AlertDialog.Builder(context, theme);
		} else {
			return new AlertDialog.Builder(context);
		}
	}
}
