package rapidui.eventhandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class EventHandlerRegistrar {
	public abstract int[] getTargetIds(Annotation annotation);
	public abstract Object createEventDispatcher(final Object instance, HashMap<Class<?>, Method> methods);
	public abstract void registerEventListener(Object target, Object dispatcher);
}
