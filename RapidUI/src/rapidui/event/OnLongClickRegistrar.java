package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.event.OnLongClick;
import android.view.View;

public class OnLongClickRegistrar extends SimpleEventRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnLongClick) annotation).value();
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {

		final Method method = methods.get(OnLongClick.class);
		
		return new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				try {
					method.setAccessible(true);
					return (Boolean) method.invoke(instance, v);
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
		((View) target).setOnLongClickListener((View.OnLongClickListener) dispatcher);
	}
}
