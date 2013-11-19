package rapidui;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class Activity extends android.app.Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Injector.injectActivity(this);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		Injector.injectViews(this);
	}
	
	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		Injector.injectViews(this);
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		Injector.injectViews(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Injector.injectOptionsMenu(this, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
