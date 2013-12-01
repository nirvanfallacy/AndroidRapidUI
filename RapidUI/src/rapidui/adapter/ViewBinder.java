package rapidui.adapter;

import java.lang.annotation.Annotation;

import android.view.View;

public abstract class ViewBinder {
	public abstract int getId(Annotation annotation);
	public abstract void bind(View v, Object value);
}
