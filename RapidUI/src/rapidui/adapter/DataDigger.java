package rapidui.adapter;

import android.view.View;

public abstract class DataDigger {
	private ViewBinder binder;
	private int id;
	
	protected DataDigger(ViewBinder binder) {
		this.binder = binder;
	}
	
	protected void bind(View v, Object value) {
		binder.bind(v, value);
	}
	
	public abstract void dig(Object instance, View v);

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
