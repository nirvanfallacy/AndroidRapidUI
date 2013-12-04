package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.ValueCallback;
import rapidui.annotation.adapter.BindToText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TextBinder extends ViewBinder {
	@Override
	public void bindValue(View v, Object value) {
		((TextView) v).setText(value.toString());
	}

	@Override
	public int getId(Annotation annotation) {
		return ((BindToText) annotation).value();
	}

	@Override
	public Object bindListener(View v, final ValueCallback<Object> listener) {
		if (!(v instanceof EditText)) return null;
		
		final EditText edit = (EditText) v;
		final TextWatcher watcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				listener.onCallback(arg0.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
			}
		};
		
		edit.addTextChangedListener(watcher);
		
		return watcher;
	}

	@Override
	public void unbindListener(View v, Object boundResult) {
		if (!(v instanceof EditText)) return;

		final EditText edit = (EditText) v;
		edit.removeTextChangedListener((TextWatcher) boundResult);
	}
}
