package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.ValueCallback;
import rapidui.annotation.adapter.BindToRatingReadOnly;
import android.view.View;
import android.widget.RatingBar;

public class RatingReadOnlyViewBinder extends ViewBinder {
	@Override
	public void bindValue(View v, Object value) {
		((RatingBar) v).setIsIndicator((Boolean) value);
	}

	@Override
	public int[] getIds(Annotation annotation) {
		return ((BindToRatingReadOnly) annotation).value();
	}

	@Override
	public Object bindListener(View v, final ValueCallback<Object> listener) {
		return null;
	}

	@Override
	public void unbindListener(View v, Object boundResult) {
	}
}
