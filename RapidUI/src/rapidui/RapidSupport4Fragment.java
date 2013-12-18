package rapidui;

import java.util.concurrent.Executor;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class RapidSupport4Fragment extends Fragment {
	private FragmentAspect aspect;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		aspect = new FragmentAspect(activity, this, new SupportFragmentHost(this));
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		aspect.setCurrentLifecycle(Lifecycle.CREATE);
		aspect.injectCommonThings();

		if (savedInstanceState != null) {
			aspect.restoreInstanceStates(savedInstanceState);
		}

		aspect.registerReceivers(Lifecycle.CREATE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return aspect.injectFragment(inflater, container);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		aspect.injectViews();
		aspect.registerListenersToCurrentLifecycle();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		aspect.saveInstanceStates(outState);
	}
	
	@Override
	public void onDestroy() {
		aspect.unbindServices();
		aspect.unregisterListeners(Lifecycle.CREATE);
		aspect.unregisterReceivers(Lifecycle.CREATE);
		aspect.cancelTasks(TaskLifecycle.CANCEL_ON_DESTROY);
		super.onDestroy();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		aspect.setCurrentLifecycle(Lifecycle.START);
		aspect.registerListeners(Lifecycle.START);
		aspect.registerReceivers(Lifecycle.START);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		aspect.setCurrentLifecycle(Lifecycle.CREATE);
		aspect.unregisterListeners(Lifecycle.START);
		aspect.unregisterReceivers(Lifecycle.START);
		aspect.cancelTasks(TaskLifecycle.CANCEL_ON_STOP);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		aspect.setCurrentLifecycle(Lifecycle.RESUME);
		aspect.registerListeners(Lifecycle.RESUME);
		aspect.registerReceivers(Lifecycle.RESUME);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		aspect.setCurrentLifecycle(Lifecycle.START);
		aspect.unregisterListeners(Lifecycle.RESUME);
		aspect.unregisterReceivers(Lifecycle.RESUME);
		aspect.cancelTasks(TaskLifecycle.CANCEL_ON_PAUSE);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		aspect.injectOptionsMenu(inflater, menu);
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

	public void executeSingleton(String name, RapidTask<?> task, Object... params) {
		aspect.executeSingleton(TaskLifecycle.CANCEL_ON_DESTROY, name, RapidTask.sDefaultExecutor, task, params);
	}
	
	public void executeSingleton(String name, Executor exec,
			RapidTask<?> task, Object... params) {
		
		aspect.executeSingleton(TaskLifecycle.CANCEL_ON_DESTROY, name, exec, task, params);
	}

	public void executeSingleton(final TaskLifecycle lifecycle, final String name,
			final RapidTask<?> task, Object... params) {
		
		aspect.executeSingleton(lifecycle, name, RapidTask.sDefaultExecutor, task, params);
	}
	
	public void executeSingleton(final TaskLifecycle lifecycle, final String name, Executor exec,
			final RapidTask<?> task, Object... params) {

		aspect.executeSingleton(lifecycle, name, exec, task, params);
	}

	public void execute(final RapidTask<?> task, Object... params) {
		aspect.execute(TaskLifecycle.CANCEL_ON_DESTROY, RapidTask.sDefaultExecutor, task, params);
	}
	
	public void execute(Executor exec,
			final RapidTask<?> task, Object... params) {
		
		aspect.execute(TaskLifecycle.CANCEL_ON_DESTROY, exec, task, params);
	}

	public void execute(final TaskLifecycle lifecycle, 
			final RapidTask<?> task, Object... params) {
		
		aspect.execute(lifecycle, RapidTask.sDefaultExecutor, task, params);
	}
	
	public void execute(final TaskLifecycle lifecycle, Executor exec,
			final RapidTask<?> task, Object... params) {
		
		aspect.execute(lifecycle, exec, task, params);
	}
	
	public void cancelSingletonTask(String name) {
		aspect.cancelSingletonTask(name);
	}

	public void toast(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}
	
	public void toast(String text, int duration) {
		Toast.makeText(getActivity(), text, duration).show();
	}
	
	public void toastLong(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
	}
}
