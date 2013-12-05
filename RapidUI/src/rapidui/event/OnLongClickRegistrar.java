package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.ArgumentMapper;
import rapidui.annotation.event.OnLongClick;
import android.view.View;

public class OnLongClickRegistrar extends SimpleEventRegistrar {
	private static Class<?>[] argsLongClick = new Class<?>[] { View.class };
	
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnLongClick) annotation).value();
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {

		final Method onLongClick = methods.get(OnLongClick.class);
		final ArgumentMapper amLongClick = new ArgumentMapper(argsLongClick, onLongClick);
		
		return new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				try {
					onLongClick.setAccessible(true);
					return (Boolean) onLongClick.invoke(instance, amLongClick.match(v));
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
