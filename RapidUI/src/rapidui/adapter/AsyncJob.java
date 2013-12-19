package rapidui.adapter;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import rapidui.Cancelable;
import android.view.View;

public class AsyncJob {
	private View v;
	private Cancelable cancelable;
	private WeakHashMap<View, AsyncJob> jobList;
	private AtomicBoolean canceled;
	
	public AsyncJob(View v, WeakHashMap<View, AsyncJob> jobList) {
		this.v = v;
		this.jobList = jobList;
		this.canceled = new AtomicBoolean(false);
		
		synchronized (jobList) {
			jobList.put(v, this);
		}
	}
	
	public void setCancelable(Cancelable c) {
		if (this.cancelable != c) {
			this.cancelable = c;
			if (canceled.get()) {
				c.cancel();
			}
		}
	}
	
	public void cancel() {
		if (canceled.compareAndSet(false, true)) {
			if (cancelable != null) {
				cancelable.cancel();
			}
			removeFromJobList();
		}
	}

	public boolean isDone() {
		if (canceled.get()) {
			return true;
		} else {
			synchronized (jobList) {
				if (!jobList.containsKey(v)) return true;
			}
			return false;
		}
	}

	public void removeFromJobList() {
		synchronized (jobList) {
			jobList.remove(v);
		}
	}
}