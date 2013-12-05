package rapidui.adapter;

import java.util.WeakHashMap;

import rapidui.Cancelable;
import rapidui.ValueCallback;
import android.view.View;

public abstract class DataBinder {
	protected ViewBinder viewBinder;
	protected WeakHashMap<View, Object> listenerList;

	private int id;
	
	protected DataBinder(ViewBinder viewBinder) {
		this.viewBinder = viewBinder;
	}
	
	public abstract void bind(Object instance, View v, Runnable callback, Cancelable canceler);
	public abstract Object getValue(Object instance);

	public void unbind(View v) {
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
		final Object result = viewBinder.bindListener(v, listener);
		if (result != null) {
			if (listenerList == null) {
				listenerList = new WeakHashMap<View, Object>();
			}
			listenerList.put(v, result);
		}
	}
	
	protected void unregisterListeners(View v) {
		if (listenerList == null) return;
		
		final Object boundResult = listenerList.remove(v);
		if (boundResult != null) {
			viewBinder.unbindListener(v, boundResult);
		}
	}
}
