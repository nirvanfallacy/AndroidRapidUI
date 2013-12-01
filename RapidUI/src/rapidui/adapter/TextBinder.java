package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.annotation.adapter.BindToText;
import android.view.View;
import android.widget.TextView;

public class TextBinder extends ViewBinder {
	@Override
	public void bind(View v, Object value) {
		((TextView) v).setText(value.toString());
	}

	@Override
	public int getId(Annotation annotation) {
		return ((BindToText) annotation).value();
	}
}
