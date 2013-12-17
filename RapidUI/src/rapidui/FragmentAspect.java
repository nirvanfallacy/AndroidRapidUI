package rapidui;

import rapidui.annotation.Layout;
import rapidui.annotation.OptionsMenu;
import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class FragmentAspect extends RapidAspect {
	public FragmentAspect(Activity activity, Object memberContainer, Host host) {
		super(activity, memberContainer, host);
	}
	
	public View injectFragment(LayoutInflater inflater, ViewGroup parentView) {
		final Resources res = activity.getResources();
		
		View contentView = null;
		
		Class<?> cls = memberContainer.getClass();
		
		while (cls != null && !isRapidClass(cls)) {
			// Layout
			
			final Layout layout = cls.getAnnotation(Layout.class);
			if (contentView == null && layout != null) {
				int id = layout.value();
				if (id == 0) {
					final String packageName = activity.getPackageName();
					
					String name = cls.getSimpleName();
					if (name.length() > 8 && name.endsWith("Fragment")) {
						name = "fragment_" + ResourceUtils.toLowerUnderscored(name.substring(0, name.length() - 8));
					} else {
						name = ResourceUtils.toLowerUnderscored(name);
					}
	
					id = res.getIdentifier(name, "layout", packageName);
				}
				
				contentView = inflater.inflate(id, parentView, false);
			}
			
			// OptionsMenu
			
			final OptionsMenu optionsMenu = cls.getAnnotation(OptionsMenu.class);
			if (optionsMenu != null) {
				host.setHasOptionsMenu(true);
			}
			
			cls = cls.getSuperclass();
		}
		
		return contentView;
	}

	@Override
	protected String getHostNamePostFix() {
		return "Fragment";
	}
}
