package rapidui.eventhandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class EventHandlerRegistrar {
	public abstract void registerEventListener(Object target, Object instance, HashMap<Class<?>, Method> methodMap);
	public abstract int[] getTargetIds(Annotation annotation);
}
