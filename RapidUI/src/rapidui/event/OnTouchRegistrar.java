package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.ArgumentMapper;
import rapidui.annotation.event.OnTouch;
import android.view.MotionEvent;
import android.view.View;

public class OnTouchRegistrar extends SimpleEventRegistrar {
	private static Class<?>[] argsTouch = new Class<?>[] { View.class, MotionEvent.class };
	
	@Override
	public int[] getTargetViewIds(Annotation annotation) {
		return ((OnTouch) annotation).value();
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {

		final Method onTouch = methods.get(OnTouch.class);
		final ArgumentMapper amTouch = new ArgumentMapper(argsTouch, onTouch);
		
		return new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					onTouch.setAccessible(true);
					return (Boolean) onTouch.invoke(instance, amTouch.match(v, event));
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
