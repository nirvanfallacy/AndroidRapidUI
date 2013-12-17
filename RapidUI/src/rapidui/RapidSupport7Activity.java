package rapidui;

import java.util.concurrent.Executor;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class RapidSupport7Activity extends ActionBarActivity {
	private ActivityAspect aspect;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		aspect = new ActivityAspect(this);
		aspect.setCurrentLifecycle(Lifecycle.CREATE);
		aspect.injectCommonThings();
		aspect.injectActivity();
		
		if (savedInstanceState != null) {
			aspect.restoreInstanceStates(savedInstanceState);
		}
		
		aspect.registerReceivers(Lifecycle.CREATE);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		aspect.saveInstanceStates(outState);
	}
	
	@Override
	protected void onDestroy() {
		aspect.unbindServices();
		aspect.unregisterListeners(Lifecycle.CREATE);
		aspect.unregisterReceivers(Lifecycle.CREATE);
		aspect.cancelTasks(TaskLifecycle.CANCEL_ON_DESTROY);
		super.onDestroy();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		aspect.setCurrentLifecycle(Lifecycle.START);
		aspect.registerListeners(Lifecycle.START);
		aspect.registerReceivers(Lifecycle.START);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		aspect.setCurrentLifecycle(Lifecycle.CREATE);
		aspect.unregisterListeners(Lifecycle.START);
		aspect.unregisterReceivers(Lifecycle.START);
		aspect.cancelTasks(TaskLifecycle.CANCEL_ON_STOP);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		aspect.setCurrentLifecycle(Lifecycle.RESUME);
		aspect.registerListeners(Lifecycle.RESUME);
		aspect.registerReceivers(Lifecycle.RESUME);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		aspect.setCurrentLifecycle(Lifecycle.START);
		aspect.unregisterListeners(Lifecycle.RESUME);
		aspect.unregisterReceivers(Lifecycle.RESUME);
		aspect.cancelTasks(TaskLifecycle.CANCEL_ON_PAUSE);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		aspect.unregisterAllListeners();
		super.setContentView(layoutResID);
		aspect.setCustomTitleBarId();
		aspect.injectViews();
		aspect.registerListenersToCurrentLifecycle();
	}
	
	@Override
	public void setContentView(View view) {
		aspect.unregisterAllListeners();
		super.setContentView(view);
		aspect.setCustomTitleBarId();
		aspect.injectViews();
		aspect.registerListenersToCurrentLifecycle();
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		aspect.unregisterAllListeners();
		super.setContentView(view, params);
		aspect.setCustomTitleBarId();
		aspect.injectViews();
		aspect.registerListenersToCurrentLifecycle();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		aspect.injectOptionsMenu(getMenuInflater(), menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return (aspect.onOptionsItemSelected(item) ? true : super.onOptionsItemSelected(item));
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		aspect.collect();
	}

	@SuppressWarnings("unchecked")
	public <Progress> void executeSingleton(String name, RapidTask<Progress, ?> task, Progress... params) {
		aspect.executeSingleton(TaskLifecycle.CANCEL_ON_DESTROY, name, RapidTask.sDefaultExecutor, task, params);
	}
	
	@SuppressWarnings("unchecked")
	public <Progress> void executeSingleton(String name, Executor exec,
			RapidTask<Progress, ?> task, Progress... params) {
		
		aspect.executeSingleton(TaskLifecycle.CANCEL_ON_DESTROY, name, exec, task, params);
	}

	@SuppressWarnings("unchecked")
	public <Progress> void executeSingleton(final TaskLifecycle lifecycle, final String name,
			final RapidTask<Progress, ?> task, Progress... params) {
		
		aspect.executeSingleton(lifecycle, name, RapidTask.sDefaultExecutor, task, params);
	}
	
	@SuppressWarnings("unchecked")
	public <Progress> void executeSingleton(final TaskLifecycle lifecycle, final String name, Executor exec,
			final RapidTask<Progress, ?> task, Progress... params) {

		aspect.executeSingleton(lifecycle, name, exec, task, params);
	}

	@SuppressWarnings("unchecked")
	public <Progress> void execute(final RapidTask<Progress, ?> task, Progress... params) {
		aspect.execute(TaskLifecycle.CANCEL_ON_DESTROY, RapidTask.sDefaultExecutor, task, params);
	}
	
	@SuppressWarnings("unchecked")
	public <Progress> void execute(Executor exec,
			final RapidTask<Progress, ?> task, Progress... params) {
		
		aspect.execute(TaskLifecycle.CANCEL_ON_DESTROY, exec, task, params);
	}

	@SuppressWarnings("unchecked")
	public <Progress> void execute(final TaskLifecycle lifecycle, 
			final RapidTask<Progress, ?> task, Progress... params) {
		
		aspect.execute(lifecycle, RapidTask.sDefaultExecutor, task, params);
	}
	
	@SuppressWarnings("unchecked")
	public <Progress> void execute(final TaskLifecycle lifecycle, Executor exec,
			final RapidTask<Progress, ?> task, Progress... params) {
		
		aspect.execute(lifecycle, exec, task, params);
	}
	
	public void cancelSingletonTask(String name) {
		aspect.cancelSingletonTask(name);
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
