package rapidui;

import android.content.Context;
import android.view.View;

public class RapidObject {
	public static void init(Object o) {
		init(o, null);
	}
	
	public static void init(Object o, View v) {
		final Context context = (v == null ? null : v.getContext());
		final ObjectAspect oa = new ObjectAspect(context, o, new ViewContainer(v));
		oa.injectCommonThings();
		oa.injectViews();
	}
	
	public RapidObject() {
		init(this);
	}
}
