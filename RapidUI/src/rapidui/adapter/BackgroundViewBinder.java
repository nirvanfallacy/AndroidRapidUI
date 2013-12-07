package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.ValueCallback;
import rapidui.annotation.adapter.BindToBackground;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

public class BackgroundViewBinder extends ViewBinder {
	@Override
	public int[] getIds(Annotation annotation) {
		return ((BindToBackground) annotation).value();
	}

	@Override
	public void bindValue(View v, Object value) {
		if (value == null || value instanceof Drawable) {
			v.setBackgroundDrawable((Drawable) value);
		} else if (value instanceof Bitmap) {
			v.setBackgroundDrawable(new BitmapDrawable(v.getResources(), (Bitmap) value));
		}
	}

	@Override
	public Object bindListener(View v, ValueCallback<Object> listener) {
		return null;
	}

	@Override
	public void unbindListener(View v, Object boundResult) {
	}
}
