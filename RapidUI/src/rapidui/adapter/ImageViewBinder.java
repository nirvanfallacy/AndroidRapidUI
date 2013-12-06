package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.ValueCallback;
import rapidui.annotation.adapter.BindToImage;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

public class ImageViewBinder extends ViewBinder {
	@Override
	public int[] getIds(Annotation annotation) {
		return ((BindToImage) annotation).value();
	}

	@Override
	public void bindValue(View v, Object value) {
		if (value == null || value instanceof Drawable) {
			((ImageView) v).setImageDrawable((Drawable) value);
		} else if (value instanceof Bitmap) {
			((ImageView) v).setImageBitmap((Bitmap) value);
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
