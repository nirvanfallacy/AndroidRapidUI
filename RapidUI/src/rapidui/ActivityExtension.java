package rapidui;

import rapidui.annotation.FullScreen;
import rapidui.annotation.Layout;
import rapidui.annotation.TitleBar;
import rapidui.annotation.TitleBarType;
import android.app.Activity;
import android.content.res.Resources;
import android.view.Window;
import android.view.WindowManager;

public class ActivityExtension extends HostExtension {
	public ActivityExtension(Activity activity) {
		super(activity, activity, new ActivityHost(activity));
	}
	
	public void injectActivity() {
		final Resources res = activity.getResources();
		final Window w = activity.getWindow();
		
		boolean contentViewSet = false;
		
		Class<?> cls = activity.getClass();
		
		while (cls != null && !isRapidClass(cls)) {
			// NoTitleBar
			
			final TitleBar titleBar = cls.getAnnotation(TitleBar.class);
			if (titleBar != null) {
				final TitleBarType type = titleBar.value();
				if (type == TitleBarType.NONE) {
					activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
				} else if (type == TitleBarType.CUSTOM) {
					activity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
				}
			}
			
			// Layout
			
			final Layout layout = cls.getAnnotation(Layout.class);
			if (!contentViewSet && layout != null) {
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
				
				activity.setContentView(id);
				contentViewSet = true;
			}
			
			// Fullscreen
			
			if (cls.isAnnotationPresent(FullScreen.class)) {
				w.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
			
			// Set layout id when the title bar is set to be customized.
			
			if (titleBar != null && titleBar.value() == TitleBarType.CUSTOM) {
				w.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, titleBar.id());
			}
			
			cls = cls.getSuperclass();
		}
	}

	@Override
	protected String getHostNamePostFix() {
		return "Activity";
	}
}
