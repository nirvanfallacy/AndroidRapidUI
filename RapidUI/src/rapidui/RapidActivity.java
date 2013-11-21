package rapidui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class RapidActivity extends android.app.Activity {
	private ActivityInjector injector = new ActivityInjector(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		injector.injectActivity();
	}
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		injector.injectViews();
	}
	
	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		injector.injectViews();
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		injector.injectViews();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		injector.injectOptionsMenu(getMenuInflater(), menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		if (id != 0) {
			final Method method = injector.getMenuItemClickHandler(id);
			if (method != null) {
				try {
					method.setAccessible(true);
					return (Boolean) method.invoke(this, item);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
}
