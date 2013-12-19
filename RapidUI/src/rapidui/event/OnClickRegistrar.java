package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.ArgumentMapper;
import rapidui.Host;
import rapidui.annotation.event.OnClick;
import android.view.View;

public class OnClickRegistrar extends SimpleEventRegistrar {
	private static Class<?>[] argsClick = new Class<?>[] { View.class };
	
	@Override
	public int[] getTargetViewIds(Annotation annotation) {
		return ((OnClick) annotation).value();
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {
		
		final Method onClick = methods.get(OnClick.class);
		final ArgumentMapper amClick = new ArgumentMapper(argsClick, onClick);
		
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					onClick.setAccessible(true);
					onClick.invoke(instance, amClick.match(v));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		};
	}

	@Override
	public void registerEventListener(Object target, Object dispatcher) {
		((View) target).setOnClickListener((View.OnClickListener) dispatcher);
	}

	@Override
	public Object getNonViewTarget(Host host) {
		return null;
	}
}
