package rapidui.adapter;

import java.lang.annotation.Annotation;

import rapidui.ValueCallback;
import rapidui.annotation.adapter.BindToProgress;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ProgressViewBinder extends ViewBinder {
	@Override
	public void bindValue(View v, Object value) {
		((ProgressBar) v).setProgress((Integer) value);
	}

	@Override
	public int[] getIds(Annotation annotation) {
		return ((BindToProgress) annotation).value();
	}

	@Override
	public Object bindListener(View v, final ValueCallback<Object> listener) {
		if (v instanceof SeekBar) {
			final OnSeekBarChangeListener eventListener = new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					
					listener.onCallback(progress);
				}
			};
			
			((SeekBar) v).setOnSeekBarChangeListener(eventListener);
			return eventListener;
		} else {
			return null;
		}
	}

	@Override
	public void unbindListener(View v, Object boundResult) {
		if (v instanceof SeekBar) {
			((SeekBar) v).setOnSeekBarChangeListener(null);
		}
	}
}
