package rapidui.adapter;

import android.view.View;
import android.widget.TextView;

public class TextBinder extends ViewBinder {
	@Override
	public void bind(View v, Object value) {
		((TextView) v).setText(value.toString());
	}
}
