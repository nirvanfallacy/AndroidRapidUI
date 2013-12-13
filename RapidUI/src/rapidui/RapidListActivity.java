package rapidui;

import java.util.concurrent.Executor;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class RapidListActivity extends android.app.ListActivity {
	private ActivityExtension ext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ext = new ActivityExtension(this);
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
		ext.cancelTasks(TaskLifecycle.CANCEL_ON_DESTROY);
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
		ext.cancelTasks(TaskLifecycle.CANCEL_ON_STOP);
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
		ext.cancelTasks(TaskLifecycle.CANCEL_ON_PAUSE);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		ext.unregisterAllListeners();
		super.setContentView(layoutResID);
		ext.setCustomTitleBarId();
		ext.injectViews();
		ext.registerListenersToCurrentLifecycle();
	}
	
	@Override
	public void setContentView(View view) {
		ext.unregisterAllListeners();
		super.setContentView(view);
		ext.setCustomTitleBarId();
		ext.injectViews();
		ext.registerListenersToCurrentLifecycle();
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		ext.unregisterAllListeners();
		super.setContentView(view, params);
		ext.setCustomTitleBarId();
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

	@SuppressWarnings("unchecked")
	public <Progress> void executeSingleton(String name, RapidTask<Progress, ?> task, Progress... params) {
		ext.executeSingleton(TaskLifecycle.CANCEL_ON_DESTROY, name, RapidTask.sDefaultExecutor, task, params);
	}
	
	@SuppressWarnings("unchecked")
	public <Progress> void executeSingleton(String name, Executor exec,
			RapidTask<Progress, ?> task, Progress... params) {
		
		ext.executeSingleton(TaskLifecycle.CANCEL_ON_DESTROY, name, exec, task, params);
	}

	@SuppressWarnings("unchecked")
	public <Progress> void executeSingleton(final TaskLifecycle lifecycle, final String name,
			final RapidTask<Progress, ?> task, Progress... params) {
		
		ext.executeSingleton(lifecycle, name, RapidTask.sDefaultExecutor, task, params);
	}
	
	@SuppressWarnings("unchecked")
	public <Progress> void executeSingleton(final TaskLifecycle lifecycle, final String name, Executor exec,
			final RapidTask<Progress, ?> task, Progress... params) {

		ext.executeSingleton(lifecycle, name, exec, task, params);
	}

	@SuppressWarnings("unchecked")
	public <Progress> void execute(final RapidTask<Progress, ?> task, Progress... params) {
		ext.execute(TaskLifecycle.CANCEL_ON_DESTROY, RapidTask.sDefaultExecutor, task, params);
	}
	
	@SuppressWarnings("unchecked")
	public <Progress> void execute(Executor exec,
			final RapidTask<Progress, ?> task, Progress... params) {
		
		ext.execute(TaskLifecycle.CANCEL_ON_DESTROY, exec, task, params);
	}

	@SuppressWarnings("unchecked")
	public <Progress> void execute(final TaskLifecycle lifecycle, 
			final RapidTask<Progress, ?> task, Progress... params) {
		
		ext.execute(lifecycle, RapidTask.sDefaultExecutor, task, params);
	}
	
	@SuppressWarnings("unchecked")
	public <Progress> void execute(final TaskLifecycle lifecycle, Executor exec,
			final RapidTask<Progress, ?> task, Progress... params) {
		
		ext.execute(lifecycle, exec, task, params);
	}
	
	public void cancelSingletonTask(String name) {
		ext.cancelSingletonTask(name);
	}
	
	public void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	
	public void toast(String text, int duration) {
		Toast.makeText(this, text, duration).show();
	}
	
	public void toastLong(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
}
