package rapidui.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.view.View;

public class MethodDigger extends DataDigger {
	private Method method;
	
	public MethodDigger(Method method, ViewBinder binder) {
		super(binder);
		this.method = method;
	}

	@Override
	public void dig(Object instance, View v) {
		method.setAccessible(true);
		try {
			final Object value = method.invoke(instance);
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
