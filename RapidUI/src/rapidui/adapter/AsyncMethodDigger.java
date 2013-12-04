package rapidui.adapter;

import java.lang.reflect.InvocationTargetException;

import rapidui.ValueCallback;
import rapidui.Canceler;
import android.view.View;

public class AsyncMethodDigger extends MethodDigger {
	public AsyncMethodDigger(ViewBinder binder) {
		super(binder);
	}

	public AsyncMethodDigger(MethodDigger digger) {
		super(digger);
	}
	
	@Override
	public void dig(Object instance, View v, ValueCallback<Object> listener,
			Canceler canceler) {
		// TODO Auto-generated method stub
		super.dig(instance, v, listener, canceler);
	}

	@Override
	public boolean isAsync() {
		return true;
	}
}
