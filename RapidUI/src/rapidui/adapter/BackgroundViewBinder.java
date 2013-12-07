package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.ValueCallback;
import rapidui.annotation.adapter.BindToBackground;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

public class BackgroundViewBinder extends ViewBinder {
	@Override
	public int[] getIds(Annotation annotation) {
		return ((BindToBackground) annotation).value();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void bindValue(View v, Object value) {
		Drawable d = null;
		
		if (value == null || value instanceof Drawable) {
			d = (Drawable) value;
		} else if (value instanceof Bitmap) {
			d = new BitmapDrawable(v.getResources(), (Bitmap) value);
		}

		if (d != null) {
			if (Build.VERSION.SDK_INT >= 16) {
				v.setBackground(d);
			} else {
				v.setBackgroundDrawable(d);
			}
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
