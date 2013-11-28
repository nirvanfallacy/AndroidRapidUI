package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.event.OnTouch;
import android.view.MotionEvent;
import android.view.View;

public class OnTouchRegistrar extends SimpleEventRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnTouch) annotation).value();
	}

	@Override
	public Object createEventDispatcher(final Object instance,
			HashMap<Class<?>, Method> methods) {

		final Method method = methods.get(OnTouch.class);
		
		return new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					method.setAccessible(true);
					return (Boolean) method.invoke(instance, v, event);
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
		((View) target).setOnTouchListener((View.OnTouchListener) dispatcher);
	}
}
