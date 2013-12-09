package rapidui;

import rapidui.annotation.Layout;
import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

public abstract class FragmentExtensionBase extends HostExtension {
	public FragmentExtensionBase(Activity activity, Object memberContainer, ViewFinder viewFinder) {
		super(activity, memberContainer, viewFinder);
	}
	
	public View injectFragment(ViewGroup parentView) {
		final Resources res = activity.getResources();
		
		View contentView = null;
		
		Class<?> cls = activity.getClass();
		
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
				
				contentView = activity.getLayoutInflater().inflate(id, parentView, false);
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
