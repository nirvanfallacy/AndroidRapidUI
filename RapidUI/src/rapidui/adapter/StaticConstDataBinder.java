package rapidui.adapter;

import java.lang.reflect.Field;

import rapidui.Cancelable;
import android.view.View;

public class StaticConstDataBinder extends DataBinder {
	private Object value;
	
	public StaticConstDataBinder(Field field, ViewBinder binder) {
		super(binder);

		field.setAccessible(true);
		try {
			value = field.get(null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void bind(final Object instance, final View v, final Runnable callback, final Cancelable canceler) {
		try {
			viewBinder.bindValue(v, value);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public Object getValue(Object instance) throws Exception {
		return value;
	}
}
