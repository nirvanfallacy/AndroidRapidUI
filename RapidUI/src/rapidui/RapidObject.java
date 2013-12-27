package rapidui;

import android.content.Context;
import android.view.View;

public class RapidObject {
	public static void init(Context context, Object o) {
		init(context, o, null);
	}
	
	public static void init(Object o) {
		init(null, o, null);
	}
	
	public static void init(Object o, View v) {
		final Context context = (v == null ? null : v.getContext());
		init(context, o, v);
	}
	
	public static void init(Context context, Object o, View v) {
		final ObjectAspect oa = new ObjectAspect(context, o, new ViewContainer(v));
		oa.injectCommonThings();
		oa.injectViews();
	}	
	
	private Context context;
	
	public RapidObject() {
		init(this);
	}
	
	public RapidObject(Context context) {
		this.context = context;
		init(context, this);
	}
	
	public Context getContext() {
		return context;
	}
}
