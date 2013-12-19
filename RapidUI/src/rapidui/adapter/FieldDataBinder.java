package rapidui.adapter;

import java.lang.reflect.Field;

import rapidui.ValueCallback;
import android.view.View;

public class FieldDataBinder extends ConstDataBinder {
	public FieldDataBinder(Field field, ViewBinder binder) {
		super(field, binder);
	}

	@Override
	public void bind(final Object instance, final View v, final AsyncJob job) {
		super.bind(instance, v, job);
		
		// Bind listener
		
		registerListener(v, new ValueCallback<Object>() {
			@Override
			public void onCallback(Object result) {
				field.setAccessible(true);
				try {
					field.set(instance, result);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
