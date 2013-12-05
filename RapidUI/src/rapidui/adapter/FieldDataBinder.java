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
		try {
			viewBinder.bindValue(v, getValue(instance));
		} catch (Exception e) {
			e.printStackTrace();
			return;
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
	public Object getValue(Object instance) throws Exception {
		field.setAccessible(true);
		return field.get(instance);
	}
}
