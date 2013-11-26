package rapidui.eventhandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.Lifecycle;

public class CustomEventRegistrar extends UnregisterableEventHandlerRegistrar {
	@Override
	public Lifecycle getLifecycle(Annotation annotation) {
		return null;
	}

	@Override
	public void unregisterEventListener(Object target, Object dispatcher) {
		try {
			remover.invoke(target, dispatcher);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int[] getTargetIds(Annotation annotation) {
		return null;
	}

	@Override
	public Object createEventDispatcher(Object instance,
			HashMap<Class<?>, Method> methods) {
		return null;
	}

	@Override
	public void registerEventListener(Object target, Object dispatcher) {
		try {
			adder.invoke(target, dispatcher);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private Method adder;
	private Method remover;
	
	public CustomEventRegistrar(Method adder, Method remover) {
		this.adder = adder;
		this.remover = remover;
	}
}
