package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.event.OnDrag;
import android.view.DragEvent;
import android.view.View;

public class OnDragRegistrar extends SimpleEventRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnDrag) annotation).value();
	}

	@Override
	public Object createEventDispatcher(final Object instance,
			HashMap<Class<?>, Method> methods) {

		final Method method = methods.get(OnDrag.class);
		return new View.OnDragListener() {
			@Override
			public boolean onDrag(View v, DragEvent event) {
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
		((View) target).setOnDragListener((View.OnDragListener) dispatcher);
	}
}
