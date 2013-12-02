package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.annotation.adapter.BindToImage;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

public class ImageBinder extends ViewBinder {
	@Override
	public void bind(View v, Object value) {
		if (value instanceof Bitmap) {
			((ImageView) v).setImageBitmap((Bitmap) value);
		} else if (value instanceof Drawable) {
			((ImageView) v).setImageDrawable((Drawable) value);
		}
	}

	@Override
	public int getId(Annotation annotation) {
		return ((BindToImage) annotation).value();
	}
}
