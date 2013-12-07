package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.ValueCallback;
import rapidui.annotation.adapter.BindToProgressMax;
import android.view.View;
import android.widget.ProgressBar;

public class ProgressMaxViewBinder extends ViewBinder {
	@Override
	public void bindValue(View v, Object value) {
		((ProgressBar) v).setMax((Integer) value);
	}

	@Override
	public int[] getIds(Annotation annotation) {
		return ((BindToProgressMax) annotation).value();
	}

	@Override
	public Object bindListener(View v, final ValueCallback<Object> listener) {
		return null;
	}

	@Override
	public void unbindListener(View v, Object boundResult) {
	}
}
