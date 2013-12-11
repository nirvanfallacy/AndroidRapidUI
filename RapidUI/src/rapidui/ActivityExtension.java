package rapidui;

import rapidui.annotation.CustomTitleBar;
import rapidui.annotation.FullScreen;
import rapidui.annotation.Layout;
import rapidui.annotation.NoTitleBar;
import android.app.Activity;
import android.content.res.Resources;
import android.view.Window;
import android.view.WindowManager;

public class ActivityExtension extends HostExtension {
	private int customTitleBar;
	
	public ActivityExtension(Activity activity) {
		super(activity, activity, new ActivityHost(activity));
		customTitleBar = 0;
	}
	
	public void injectActivity() {
		final Resources res = activity.getResources();
		final Window w = activity.getWindow();
		
		boolean titleBar = false;
		int contentView = 0;
		
		Class<?> cls = activity.getClass();
		
		while (cls != null && !isRapidClass(cls)) {
			// NoTitleBar, CustomTitleBar
			
			if (!titleBar) {
				final CustomTitleBar ctb = cls.getAnnotation(CustomTitleBar.class);
				if (ctb != null) {
					activity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
					customTitleBar = ctb.value();
					titleBar = true;
				} else if (cls.isAnnotationPresent(NoTitleBar.class)) {
					activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
					titleBar = true;
				}
			}
			
			// Fullscreen
			
			if (cls.isAnnotationPresent(FullScreen.class)) {
				w.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}

			// Layout
			
			final Layout layout = cls.getAnnotation(Layout.class);
			if (contentView == 0 && layout != null) {
				int id = layout.value();
				if (id == 0) {
					final String packageName = activity.getPackageName();
					
					String name = cls.getSimpleName();
					if (name.length() > 8 && name.endsWith("Activity")) {
						name = "activity_" + ResourceUtils.toLowerUnderscored(name.substring(0, name.length() - 8));
					} else {
						name = ResourceUtils.toLowerUnderscored(name);
					}
	
					id = res.getIdentifier(name, "layout", packageName);
				}
				
				contentView = id;
			}
			
			cls = cls.getSuperclass();
		}
		
		if (contentView != 0) {
			activity.setContentView(contentView);
		}
	}

	@Override
	protected String getHostNamePostFix() {
		return "Activity";
	}
	
	public void setCustomTitleBarId() {
		// Set layout id when the title bar is set to be customized.
		
		if (customTitleBar != 0) {
			activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, customTitleBar);
		}
	}
}
