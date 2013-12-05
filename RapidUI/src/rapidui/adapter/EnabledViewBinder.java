package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.ValueCallback;
import android.view.View;

public class EnabledViewBinder extends ViewBinder {
	@Override
	public void bindValue(View v, Object value) {
		v.setEnabled((Boolean) value);
	}

	@Override
	public int getId(Annotation annotation) {
		return 0;
	}

	@Override
	public Object bindListener(View v, final ValueCallback<Object> listener) {
		return null;
	}

	@Override
	public void unbindListener(View v, Object boundResult) {
	}
}
