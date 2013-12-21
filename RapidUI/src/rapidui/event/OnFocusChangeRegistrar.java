package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.ArgumentMapper;
import rapidui.annotation.event.OnFocusChange;
import android.view.View;

public class OnFocusChangeRegistrar extends SimpleEventRegistrar {
	private static Class<?>[] argsFocusChange = new Class<?>[] { View.class, Boolean.TYPE };
	
	@Override
	public int[] getTargetViewIds(Annotation annotation) {
		return ((OnFocusChange) annotation).value();
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {
		
		final Method onFocus = methods.get(OnFocusChange.class);
		final ArgumentMapper amFocus = new ArgumentMapper(argsFocusChange, onFocus);
		
		return new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				try {
					onFocus.setAccessible(true);
					onFocus.invoke(instance, v, amFocus.map(hasFocus));
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
