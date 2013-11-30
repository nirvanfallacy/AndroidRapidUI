package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class SimpleEventRegistrar {
	public abstract int[] getTargetIds(Annotation annotation);
	public abstract Object createEventDispatcher(Object target, Object instance, HashMap<Class<?>, Method> methods);
	public abstract void registerEventListener(Object target, Object dispatcher);
}
