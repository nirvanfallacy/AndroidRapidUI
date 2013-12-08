package rapidui;

import rapidui.annotation.Lifecycle;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class RapidListActivity extends android.app.ListActivity {
	private ActivityExtension ext = new ActivityExtension(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ext.setCurrentLifecycle(Lifecycle.CREATE);
		ext.injectCommonThings();
		ext.injectActivity();
		
		if (savedInstanceState != null) {
			ext.restoreInstanceStates(savedInstanceState);
		}
		
		ext.registerReceivers(Lifecycle.CREATE);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		ext.saveInstanceStates(outState);
	}
	
	@Override
	protected void onDestroy() {
		ext.unbindServices();
		ext.unregisterListeners(Lifecycle.CREATE);
		ext.unregisterReceivers(Lifecycle.CREATE);
		super.onDestroy();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		ext.setCurrentLifecycle(Lifecycle.START);
		ext.registerListeners(Lifecycle.START);
		ext.registerReceivers(Lifecycle.START);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		ext.setCurrentLifecycle(Lifecycle.CREATE);
		ext.unregisterListeners(Lifecycle.START);
		ext.unregisterReceivers(Lifecycle.START);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		ext.setCurrentLifecycle(Lifecycle.RESUME);
		ext.registerListeners(Lifecycle.RESUME);
		ext.registerReceivers(Lifecycle.RESUME);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		ext.setCurrentLifecycle(Lifecycle.START);
		ext.unregisterListeners(Lifecycle.RESUME);
		ext.unregisterReceivers(Lifecycle.RESUME);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		ext.unregisterAllListeners();
		super.setContentView(layoutResID);
		ext.injectViews();
		ext.registerListenersToCurrentLifecycle();
	}
	
	@Override
	public void setContentView(View view) {
		ext.unregisterAllListeners();
		super.setContentView(view);
		ext.injectViews();
		ext.registerListenersToCurrentLifecycle();
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		ext.unregisterAllListeners();
		super.setContentView(view, params);
		ext.injectViews();
		ext.registerListenersToCurrentLifecycle();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ext.injectOptionsMenu(getMenuInflater(), menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return (ext.onOptionsItemSelected(item) ? true : super.onOptionsItemSelected(item));
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		ext.collect();
	}
}
