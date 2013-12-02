package rapidui.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rapidui.AsyncCallback;
import rapidui.Canceler;
import android.view.View;

public class AsyncMethodDigger extends DataDigger {
	private Method method;
	
	public AsyncMethodDigger(Method method, ViewBinder binder) {
		super(binder);
		this.method = method;
	}

	@Override
	public void dig(Object instance, final View v, final Canceler canceler) {
		final AsyncCallback<?> callback = new AsyncCallback<Object>() {
			@Override
			public void callback(Object result) {
				bind(v, result);
			}
		};
		
		method.setAccessible(true);
		try {
			method.invoke(instance, callback);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isAsync() {
		return true;
	}
}
