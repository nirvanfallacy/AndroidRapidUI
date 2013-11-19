package rapidui;

import java.lang.reflect.Field;

import rapidui.annotation.FullScreen;
import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import rapidui.annotation.OptionsMenu;
import rapidui.annotation.TitleBar;
import rapidui.annotation.TitleBarType;
import android.app.Activity;
import android.content.res.Resources;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class Injector {
	public static void injectViews(Activity activity) {
		final Resources res = activity.getResources();
		final Class<?> activityClass = activity.getClass();
		
		// Inject fields
		
		for (Field field: activityClass.getDeclaredFields()) {
			final String fieldName = field.getName();
			
			final LayoutElement layoutElement = field.getAnnotation(LayoutElement.class);
			if (layoutElement != null) {
				int id = layoutElement.value();
				if (id == 0) {
					final String packageName = activity.getPackageName();
					
					id = res.getIdentifier(fieldName, "id", packageName);
					
					if (id == 0) {
						final String name = camelCaseToUnderlinedLowerCase(fieldName);
						id = res.getIdentifier(name, "id", packageName);
					}
				}
				
				field.setAccessible(true);
				try {
					field.set(activity, activity.findViewById(id));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static String camelCaseToUnderlinedLowerCase(String s) {
		final StringBuilder sb = new StringBuilder();
		
		boolean lastCharWasUpperCase = true;
		
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if (Character.isUpperCase(c)) {
				c = Character.toLowerCase(c);
				if (lastCharWasUpperCase) {
					sb.append(c);
				} else {
					sb.append('_').append(c);
				}
				lastCharWasUpperCase = true;
			} else {
				sb.append(c);
				lastCharWasUpperCase = false;
			}
		}
		
		return sb.toString();
	}
	
	public static void injectActivity(Activity activity) {
		final Resources res = activity.getResources();
		final Window w = activity.getWindow();
		final Class<?> activityClass = activity.getClass();
		
		// NoTitleBar
		
		final TitleBar titleBar = activityClass.getAnnotation(TitleBar.class);
		if (titleBar != null) {
			final TitleBarType type = titleBar.value();
			if (type == TitleBarType.NONE) {
				activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
			} else if (type == TitleBarType.CUSTOM) {
				activity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			}
		}
		
		// Layout
		
		final Layout layout = activityClass.getAnnotation(Layout.class);
		if (layout != null) {
			int id = layout.value();
			if (id == 0) {
				final String packageName = activity.getPackageName();
				
				String name = activityClass.getSimpleName();
				if (name.length() > 8 && name.endsWith("Activity")) {
					name = "activity_" + camelCaseToUnderlinedLowerCase(name.substring(0, name.length() - 8));
				} else {
					name = camelCaseToUnderlinedLowerCase(name);
				}

				id = res.getIdentifier(name, "layout", packageName);
			}
			
			activity.setContentView(id);
		}
		
		// Fullscreen
		
		if (activityClass.isAnnotationPresent(FullScreen.class)) {
			w.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		// Set layout id when the title bar is set to be customized.
		
		if (titleBar != null && titleBar.value() == TitleBarType.CUSTOM) {
			w.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, titleBar.id());
		}
	}

	public static void injectOptionsMenu(Activity activity, Menu menu) {
		final Resources res = activity.getResources();
		final Class<?> activityClass = activity.getClass();

		final OptionsMenu optionsMenu = activityClass.getAnnotation(OptionsMenu.class);
		if (optionsMenu != null) {
			int id = optionsMenu.value();
			if (id == 0) {
				final String packageName = activity.getPackageName();
				
				String name = activityClass.getSimpleName();
				if (name.length() > 8 && name.endsWith("Activity")) {
					name = camelCaseToUnderlinedLowerCase(name.substring(0, name.length() - 8));
				} else {
					name = camelCaseToUnderlinedLowerCase(name);
				}

				id = res.getIdentifier(name, "menu", packageName);
			}

			activity.getMenuInflater().inflate(id, menu);
		}
	}
}
