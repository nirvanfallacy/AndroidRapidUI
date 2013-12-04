package rapidui.adapter;

import java.lang.reflect.Field;

import rapidui.Canceler;
import rapidui.ValueCallback;
import android.view.View;

public class FieldDigger extends DataDigger {
	private Field field;
	
	public FieldDigger(Field field, ViewBinder binder) {
		super(binder);
		this.field = field;
	}

	@Override
	public void dig(Object instance, View v, ValueCallback<Object> listener, Canceler canceler) {
		field.setAccessible(true);
		try {
			final Object value = field.get(instance);
			binder.bindValue(v, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		// Bind listener
		
		if (listener != null) {
			registerListener(v, listener);
		}
	}
}
