package rapidui.adapter;

import java.lang.reflect.Field;

import rapidui.Canceler;
import android.view.View;

public class FieldDigger extends DataDigger {
	private Field field;
	
	public FieldDigger(Field field, ViewBinder binder) {
		super(binder);
		this.field = field;
	}

	@Override
	public void dig(Object instance, View v, Canceler canceler) {
		field.setAccessible(true);
		try {
			final Object value = field.get(instance);
			bind(v, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
}
