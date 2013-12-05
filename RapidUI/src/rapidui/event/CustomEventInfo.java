package rapidui.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import rapidui.ArgumentMapper;

public class CustomEventInfo {
	private Method adder;
	private Class<?> listenerType;
	
	protected CustomEventInfo(Method adder, Class<?> listenerType) {
		this.adder = adder;
		this.listenerType = listenerType;
	}
	
	public Method getAdder() {
		return adder;
	}

	public Class<?> getListenerType() {
		return listenerType;
	}
	
	public Object createProxy(final Object memberContainer, final HashMap<String, Method> delegates) {
		final HashMap<Method, ArgumentMapper> argMatchers = new HashMap<Method, ArgumentMapper>();
		
		final InvocationHandler invocationHandler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {

				final String eventName;

				final String methodName = method.getName();
				if (methodName.startsWith("on")) {
					eventName = methodName.substring(2);
				} else {
					eventName = methodName;
				}
				
				final Method delegate = delegates.get(eventName);

				ArgumentMapper am = argMatchers.get(method);
				if (am == null) {
					am = new ArgumentMapper(method.getParameterTypes(), delegate);
					argMatchers.put(method, am);
				}
				
				if (delegate != null) {
					delegate.setAccessible(true);
					return delegate.invoke(memberContainer, am.match(args));
				} else {
					return method.invoke(proxy, args);
				}
			}
		};
		
		return Proxy.newProxyInstance(
				listenerType.getClassLoader(),
				new Class<?>[] { listenerType },
				invocationHandler);
	}

	public static CustomEventInfo create(Object target, String category) {
		final String setterName = "setOn" + category + "Listener";
		final String adderName = "addOn" + category + "Listener";
		final String removerName = "removeOn" + category + "Listener";
		
		Method adder = null;
		Method remover = null;
		Class<?> listenerTypeAdder = null;
		Class<?> listenerTypeRemover = null;

		Class<?> targetType = target.getClass();
		
		while (targetType != null && !targetType.equals(Object.class)) {
			for (Method method2: targetType.getDeclaredMethods()) {
				if (adder != null && remover != null) break;
				
				final String method2Name = method2.getName();
				final Class<?>[] paramTypes = method2.getParameterTypes();
				
				if (method2Name.equals(setterName) && paramTypes.length == 1) {
					return new CustomEventInfo(method2, paramTypes[0]);
				} else if (method2Name.equals(adderName) && paramTypes.length == 1) {
					adder = method2;
					listenerTypeAdder = paramTypes[0];
				} else if (method2Name.equals(removerName) && paramTypes.length == 1) {
					remover = method2;
					listenerTypeRemover = paramTypes[0];
				}
			}
			
			targetType = targetType.getSuperclass();
		}
		
		if (adder == null || remover == null ||
		    listenerTypeAdder == null || listenerTypeRemover == null ||
			!listenerTypeAdder.equals(listenerTypeRemover)) {
			
			return null;
		} else {
			return new UnregisterableCustomEventInfo(adder, remover, listenerTypeAdder);
		}
	}
}
