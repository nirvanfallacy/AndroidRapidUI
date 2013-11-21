package rapidui.eventhandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.ViewFinder;
import android.util.SparseArray;
import android.view.View;

public abstract class EventHandlerRegistrar {
	public Object selectEventTarget(ViewFinder viewFinder, int id, SparseArray<View> viewMap) {
		View v = viewMap.get(id);
		if (v == null) {
			v = viewFinder.findViewById(id);
			viewMap.put(id, v);
		}
		
		return v;
	}
	
	public abstract void registerEventListener(Object target, Object instance, HashMap<Class<?>, Method> methodMap);
	public abstract int[] getTargetIds(Annotation annotation);
}
