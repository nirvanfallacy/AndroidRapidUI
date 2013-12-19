package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.Lifecycle;

public class CustomEventRegistrar extends UnregisterableEventRegistrar {
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
	public int[] getTargetViewIds(Annotation annotation) {
		return null;
	}

	@Override
	public Object createEventDispatcher(Object target, Object instance,
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
	private int hashCode;
	
	public CustomEventRegistrar(Method adder, Method remover) {
		this.adder = adder;
		this.remover = remover;
	}
	
	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = adder.hashCode() ^ remover.hashCode();
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !(o instanceof CustomEventRegistrar)) return false;
		
		final CustomEventRegistrar registrar = (CustomEventRegistrar) o;
		return adder.equals(registrar.adder) && remover.equals(registrar.remover);
	}
}
