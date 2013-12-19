package rapidui.adapter;

import java.lang.reflect.InvocationTargetException;

import rapidui.Cancelable;
import android.os.Looper;
import android.view.View;

public class AsyncMethodDataBinder extends MethodDataBinder {
	public AsyncMethodDataBinder(ViewBinder binder) {
		super(binder);
	}

	public AsyncMethodDataBinder(MethodDataBinder methodBinder) {
		super(methodBinder);
	}
	
	@Override
	public void bind(final Object instance, final View v, final AsyncJob job) {
		// Bind value
		
		final AsyncResult getterCallback = new AsyncResult() {
			@Override
			public void done(final Object result) {
				publish(result, true);
			}

			@Override
			public void progress(Object data) {
				publish(data, false);
			}
			
			private void publish(final Object data, final boolean finished) {
				final Runnable r = new Runnable() {
					@Override
					public void run() {
						if (job.isDone()) return;
						
						viewBinder.bindValue(v, data);
						if (finished) {
							job.removeFromJobList();
						}
					}
				};
				
				if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
					r.run();
				} else {
					v.post(r);
				}
			}

			@Override
			public void done() {
				job.removeFromJobList();
			}

			@Override
			public void setCancelable(Cancelable c) {
				job.setCancelable(c);
			}
		};
		
		getter.setAccessible(true);
		try {
			getter.invoke(instance, getterCallback);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		// Bind listener

		bindListener(v, instance);
	}

	@Override
	public boolean isAsync() {
		return true;
	}
	
	@Override
	public Object getValue(Object instance) throws Exception {
		return null;
	}
}
