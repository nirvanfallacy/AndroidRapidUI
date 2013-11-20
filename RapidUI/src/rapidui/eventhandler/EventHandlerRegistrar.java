package rapidui.eventhandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import rapidui.Injector;
import rapidui.ViewFinder;
import android.util.SparseArray;
import android.view.View;

public abstract class EventHandlerRegistrar {
	public static final int EVENT_TYPE_DEFAULT = 0;
	public static final int EVENT_TYPE_EXTERNAL = 1;
	
	public static final int TARGET_TYPE_DEFAULT = 0;
	public static final int TARGET_TYPE_ACTIVITY = 1;
	
	public Object selectEventTarget(ViewFinder viewFinder, int id, SparseArray<View> viewMap) {
		final View v = viewMap.get(id);
		return (v != null ? v : viewFinder.findViewById(id));
	}
	
	public int getEventType() {
		return EVENT_TYPE_DEFAULT;
	}
	
	public int getTargetType() {
		return TARGET_TYPE_DEFAULT;
	}
	
	public void registerEventListener(Object target, Object instance, Method method) {
	}

	public void registerEventListener(Injector injector, int id, Method method) {
	}

	public abstract int[] getTargetIds(Annotation annotation);
}
