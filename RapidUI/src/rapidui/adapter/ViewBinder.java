package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.ValueCallback;
import android.view.View;

public abstract class ViewBinder {
	public abstract int getId(Annotation annotation);
	public abstract void bindValue(View v, Object value);
	public abstract Object bindListener(View v, ValueCallback<Object> listener);
	public abstract void unbindListener(View v, Object boundResult);
}
