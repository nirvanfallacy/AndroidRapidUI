package rapidui.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rapidui.Canceler;
import rapidui.ValueCallback;
import android.view.View;

public class MethodDigger extends DataDigger {
	protected Method getter;
	protected Method setter;
	
	public MethodDigger(MethodDigger digger) {
		super(digger.binder);
		this.getter = digger.getter;
		this.setter = digger.setter;
	}
	
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
	public void dig(Object instance, View v, ValueCallback<Object> listener,
			Canceler canceler) {

		// Bind value
		
		getter.setAccessible(true);
		try {
			final Object value = getter.invoke(instance);
			binder.bindValue(v, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		// Bind listener

		if (listener != null) {
			registerListener(v, listener);
		}
	}
}
