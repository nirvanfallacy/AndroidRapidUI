package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.ValueCallback;
import rapidui.annotation.adapter.BindToChecked;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CheckViewBinder extends ViewBinder {
	@Override
	public int[] getIds(Annotation annotation) {
		return ((BindToChecked) annotation).value();
	}

	@Override
	public void bindValue(View v, Object value) {
		((CompoundButton) v).setChecked((Boolean) value);
	}

	@Override
	public Object bindListener(View v, final ValueCallback<Object> listener) {
		if (v instanceof CompoundButton) {
			final OnCheckedChangeListener eventListener = new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					listener.onCallback(isChecked);
				}
			};
			
			((CompoundButton) v).setOnCheckedChangeListener(eventListener);
			
			return eventListener;
		} else {
			return null;
		}
	}

	@Override
	public void unbindListener(View v, Object boundResult) {
		if (v instanceof CompoundButton) {
			((CompoundButton) v).setOnCheckedChangeListener(null);
		}
	}
}
