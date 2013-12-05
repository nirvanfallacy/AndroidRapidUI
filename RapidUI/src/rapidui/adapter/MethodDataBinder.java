package rapidui.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rapidui.Cancelable;
import rapidui.ValueCallback;
import android.view.View;

public class MethodDataBinder extends DataBinder {
	protected Method getter;
	protected Method setter;
	
	public MethodDataBinder(MethodDataBinder methodBinder) {
		super(methodBinder.viewBinder);
		this.getter = (getter != null ? getter : methodBinder.getter);
		this.setter = (setter != null ? setter : methodBinder.setter);
	}
	
	public MethodDataBinder(ViewBinder binder) {
		super(binder);
	}
	
	public void setGetter(Method getter) {
		this.getter = getter;
	}
	
	public void setSetter(Method setter) {
		this.setter = setter;
	}

	@Override
	public void bind(final Object instance, View v, Runnable callback, Cancelable canceler) {
		// Bind value
		
		getter.setAccessible(true);
		try {
			final Object value = getter.invoke(instance);
			viewBinder.bindValue(v, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		// Bind listener
		
		bindListener(v, instance);
	}
	
	protected void bindListener(View v, final Object instance) {
		if (setter != null) {
			registerListener(v, new ValueCallback<Object>() {
				@Override
				public void onCallback(Object result) {
					setter.setAccessible(true);
					try {
						setter.invoke(instance, result);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public Object getValue(Object instance) {
		// TODO Auto-generated method stub
		return null;
	}
}
