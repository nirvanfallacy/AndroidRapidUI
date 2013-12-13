package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.ValueCallback;
import rapidui.annotation.adapter.BindToBottomImage;
import rapidui.annotation.adapter.BindToLeftImage;
import rapidui.annotation.adapter.BindToRightImage;
import rapidui.annotation.adapter.BindToTopImage;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

public class CompoundImageViewBinder extends ViewBinder {
	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_TOP = 1;
	public static final int DIRECTION_RIGHT = 2;
	public static final int DIRECTION_BOTTOM = 3;
	
	private int direction;
	
	public CompoundImageViewBinder(int direction) {
		this.direction = direction;
	}
	
	@Override
	public int[] getIds(Annotation annotation) {
		switch (direction) {
		case DIRECTION_LEFT: return ((BindToLeftImage) annotation).value();
		case DIRECTION_TOP: return ((BindToTopImage) annotation).value();
		case DIRECTION_RIGHT: return ((BindToRightImage) annotation).value();
		case DIRECTION_BOTTOM: return ((BindToBottomImage) annotation).value();
		default: return null;
		}
	}

	@Override
	public void bindValue(View v, Object value) {
		final Drawable[] drawables = ((TextView) v).getCompoundDrawables();
		
		if (value == null || value instanceof Drawable) {
			drawables[direction] = (Drawable) value;
		} else if (value instanceof Bitmap) {
			drawables[direction] = new BitmapDrawable(v.getResources(), (Bitmap) value);
		}
		
		((TextView) v).setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
	}

	@Override
	public Object bindListener(View v, ValueCallback<Object> listener) {
		return null;
	}

	@Override
	public void unbindListener(View v, Object boundResult) {
	}
}
