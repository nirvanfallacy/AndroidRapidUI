package rapidui;

import java.util.concurrent.Executor;

import rapidui.annotation.Lifecycle;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class RapidFragment extends Fragment {
	private FragmentExtension ext;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ext = new FragmentExtension(activity, this, new FragmentHost(this));
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ext.setCurrentLifecycle(Lifecycle.CREATE);
		ext.injectCommonThings();

		if (savedInstanceState != null) {
			ext.restoreInstanceStates(savedInstanceState);
		}

		ext.registerReceivers(Lifecycle.CREATE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return ext.injectFragment(inflater, container);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ext.injectViews();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		ext.saveInstanceStates(outState);
	}
	
	@Override
	public void onDestroy() {
		ext.unbindServices();
		ext.unregisterListeners(Lifecycle.CREATE);
		ext.unregisterReceivers(Lifecycle.CREATE);
		ext.cancelTasks(TaskLifecycle.CANCEL_ON_DESTROY);
		super.onDestroy();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		ext.setCurrentLifecycle(Lifecycle.START);
		ext.registerListeners(Lifecycle.START);
		ext.registerReceivers(Lifecycle.START);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		ext.setCurrentLifecycle(Lifecycle.CREATE);
		ext.unregisterListeners(Lifecycle.START);
		ext.unregisterReceivers(Lifecycle.START);
		ext.cancelTasks(TaskLifecycle.CANCEL_ON_STOP);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ext.setCurrentLifecycle(Lifecycle.RESUME);
		ext.registerListeners(Lifecycle.RESUME);
		ext.registerReceivers(Lifecycle.RESUME);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		ext.setCurrentLifecycle(Lifecycle.START);
		ext.unregisterListeners(Lifecycle.RESUME);
		ext.unregisterReceivers(Lifecycle.RESUME);
		ext.cancelTasks(TaskLifecycle.CANCEL_ON_PAUSE);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		ext.injectOptionsMenu(inflater, menu);
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
}
