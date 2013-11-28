package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.event.OnKey;
import android.view.KeyEvent;
import android.view.View;

public class OnKeyRegistrar extends SimpleEventRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnKey) annotation).value();
	}

	@Override
	public Object createEventDispatcher(final Object instance,
			HashMap<Class<?>, Method> methods) {

		final Method method = methods.get(OnKey.class);
		return new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				try {
					method.setAccessible(true);
					return (Boolean) method.invoke(instance, v, keyCode, event);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				
				return false;
			}
		};
	}

	@Override
	public void registerEventListener(Object target, Object dispatcher) {
		((View) target).setOnKeyListener((View.OnKeyListener) dispatcher);
	}
}
