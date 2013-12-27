package rapidui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class RapidView extends View {
	public static void init(View v) {
		final ObjectAspect oa = new ObjectAspect(v.getContext(), v, new ViewContainer(v));
		oa.injectCommonThings();
		oa.injectViews();
	}
	
	public RapidView(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
		init(this);
	}

	public RapidView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(this);
	}

	public RapidView(Context context) {
		super(context);
		init(this);
	}
}
