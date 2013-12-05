package rapidui.adapter;

import java.lang.reflect.Field;

import rapidui.Cancelable;
import rapidui.ValueCallback;
import android.view.View;

public class FieldDataBinder extends DataBinder {
	private Field field;
	
	public FieldDataBinder(Field field, ViewBinder binder) {
		super(binder);
		this.field = field;
	}

	@Override
	public void bind(final Object instance, final View v, final Runnable callback, final Cancelable canceler) {
		field.setAccessible(true);
		try {
			final Object value = field.get(instance);
			viewBinder.bindValue(v, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
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

	@Override
	public Object getValue(Object instance) {
		return null;
	}
}
