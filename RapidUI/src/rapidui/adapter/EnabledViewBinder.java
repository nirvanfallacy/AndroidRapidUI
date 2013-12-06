package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.ValueCallback;
import rapidui.annotation.adapter.BindToEnabled;
import android.view.View;

public class EnabledViewBinder extends ViewBinder {
	@Override
	public int[] getIds(Annotation annotation) {
		return ((BindToEnabled) annotation).value();
	}

	@Override
	public void bindValue(View v, Object value) {
		v.setEnabled((Boolean) value);
	}

	@Override
	public Object bindListener(View v, final ValueCallback<Object> listener) {
		return null;
	}

	@Override
	public void unbindListener(View v, Object boundResult) {
	}
}
