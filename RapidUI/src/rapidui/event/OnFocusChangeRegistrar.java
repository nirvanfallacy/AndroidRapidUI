package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.event.OnFocusChange;
import android.view.View;

public class OnFocusChangeRegistrar extends SimpleEventRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnFocusChange) annotation).value();
	}

	@Override
	public Object createEventDispatcher(final Object instance,
			HashMap<Class<?>, Method> methods) {
		
		final Method method = methods.get(OnFocusChange.class);
		
		return new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				try {
					method.setAccessible(true);
					method.invoke(instance, v, hasFocus);
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
		((View) target).setOnFocusChangeListener((View.OnFocusChangeListener) dispatcher);
	}
}
