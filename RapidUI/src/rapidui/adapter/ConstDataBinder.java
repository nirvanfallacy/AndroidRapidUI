package rapidui.adapter;

import java.lang.reflect.Field;

import android.view.View;

public class ConstDataBinder extends DataBinder {
	protected Field field;
	
	public ConstDataBinder(Field field, ViewBinder binder) {
		super(binder);
		this.field = field;
	}

	@Override
	public void bind(final Object instance, final View v, final AsyncJob job) {
		try {
			viewBinder.bindValue(v, getValue(instance));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public Object getValue(Object instance) throws Exception {
		field.setAccessible(true);
		return field.get(instance);
	}
}
