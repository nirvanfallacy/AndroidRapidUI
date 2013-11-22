package rapidui.eventhandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.eventhandler.OnFocusChange;
import android.view.View;

public class OnFocusChangeRegistrar extends EventHandlerRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnFocusChange) annotation).value();
	}

	@Override
	public void registerEventListener(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {
		
		final Method method = methods.get(OnFocusChange.class);

		((View) target).setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
		});
	}
}
