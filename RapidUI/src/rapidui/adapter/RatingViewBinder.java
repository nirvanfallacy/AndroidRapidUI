package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.ValueCallback;
import rapidui.annotation.adapter.BindToRating;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class RatingViewBinder extends ViewBinder {
	@Override
	public void bindValue(View v, Object value) {
		((RatingBar) v).setRating((Float) value);
	}

	@Override
	public int[] getIds(Annotation annotation) {
		return ((BindToRating) annotation).value();
	}

	@Override
	public Object bindListener(View v, final ValueCallback<Object> listener) {
		if (v instanceof RatingBar) {
			final OnRatingBarChangeListener eventListener = new OnRatingBarChangeListener() {
				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating,
						boolean fromUser) {
					
					listener.onCallback(rating);
				}
			};

			((RatingBar) v).setOnRatingBarChangeListener(eventListener);
			
			return eventListener;
		} else {
			return null;
		}
	}

	@Override
	public void unbindListener(View v, Object boundResult) {
		if (v instanceof RatingBar) {
			((RatingBar) v).setOnRatingBarChangeListener(null);
		}
	}
}
