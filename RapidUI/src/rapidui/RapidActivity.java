package rapidui;

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
		injector.injectCommonThings();
		injector.injectActivity();
		injector.registerReceiversOnCreate();
		
		if (savedInstanceState != null) {
			injector.restoreInstanceStates(savedInstanceState);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		injector.saveInstanceStates(outState);
	}
	
	@Override
	protected void onDestroy() {
		injector.unregisterReceiversOnDestroy();
		super.onDestroy();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		injector.registerReceiversOnStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		injector.unregisterReceiversOnStop();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		injector.registerReceiversOnResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		injector.unregisterReceiversOnPause();
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
		return (injector.onOptionsItemSelected(item) ? true : super.onOptionsItemSelected(item));
	}
}
