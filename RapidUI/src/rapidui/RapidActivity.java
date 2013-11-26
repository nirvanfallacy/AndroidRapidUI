package rapidui;

import rapidui.annotation.Lifecycle;
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
		injector.setCurrentLifecycle(Lifecycle.CREATE);
		injector.injectCommonThings();
		injector.injectActivity();
		
		if (savedInstanceState != null) {
			injector.restoreInstanceStates(savedInstanceState);
		}
		
		injector.registerReceivers(Lifecycle.CREATE);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		injector.saveInstanceStates(outState);
	}
	
	@Override
	protected void onDestroy() {
		injector.unregisterListeners(Lifecycle.CREATE);
		injector.unregisterReceivers(Lifecycle.CREATE);
		super.onDestroy();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		injector.setCurrentLifecycle(Lifecycle.START);
		injector.registerListeners(Lifecycle.START);
		injector.registerReceivers(Lifecycle.START);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		injector.setCurrentLifecycle(Lifecycle.CREATE);
		injector.unregisterListeners(Lifecycle.START);
		injector.unregisterReceivers(Lifecycle.START);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		injector.setCurrentLifecycle(Lifecycle.RESUME);
		injector.registerListeners(Lifecycle.RESUME);
		injector.registerReceivers(Lifecycle.RESUME);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		injector.setCurrentLifecycle(Lifecycle.START);
		injector.unregisterListeners(Lifecycle.RESUME);
		injector.unregisterReceivers(Lifecycle.RESUME);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		injector.unregisterAllListeners();
		super.setContentView(layoutResID);
		injector.injectViews();
		injector.registerListenersToCurrentLifecycle();
	}
	
	@Override
	public void setContentView(View view) {
		injector.unregisterAllListeners();
		super.setContentView(view);
		injector.injectViews();
		injector.registerListenersToCurrentLifecycle();
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		injector.unregisterAllListeners();
		super.setContentView(view, params);
		injector.injectViews();
		injector.registerListenersToCurrentLifecycle();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		injector.injectOptionsMenu(getMenuInflater(), menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return (injector.onOptionsItemSelected(item) ? true : super.onOptionsItemSelected(item));
	}
}
