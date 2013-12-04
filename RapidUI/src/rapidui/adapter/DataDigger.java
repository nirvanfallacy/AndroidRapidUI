package rapidui.adapter;

import java.util.WeakHashMap;

import rapidui.Canceler;
import rapidui.ValueCallback;
import android.view.View;

public abstract class DataDigger {
	protected ViewBinder binder;
	protected WeakHashMap<View, Object> listenerList;

	private int id;
	
	protected DataDigger(ViewBinder binder) {
		this.binder = binder;
	}
	
	public abstract void dig(Object instance, View v,
			ValueCallback<Object> listener, Canceler canceler);

	public void undig(View v) {
		unregisterListeners(v);
	}
	
	public boolean isAsync() {
		return false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	protected void registerListener(View v, ValueCallback<Object> listener) {
		final Object result = binder.bindListener(v, listener);
		listenerList.put(v, result);
	}
	
	protected void unregisterListeners(View v) {
		final Object boundResult = listenerList.get(v);
		if (boundResult != null) {
			binder.unbindListener(v, boundResult);
			listenerList.remove(v);
		}
	}
}
