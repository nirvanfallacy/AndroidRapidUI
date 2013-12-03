package rapidui.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rapidui.Canceler;
import android.view.View;

public class MethodDigger extends DataDigger {
	private Method getter;
	private Method setter;
	
	public MethodDigger(ViewBinder binder) {
		super(binder);
	}
	
	public void setGetter(Method getter) {
		this.getter = getter;
	}
	
	public void setSetter(Method setter) {
		this.setter = setter;
	}

	@Override
	public void dig(Object instance, View v, Canceler canceler) {
		getter.setAccessible(true);
		try {
			final Object value = getter.invoke(instance);
			bind(v, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
