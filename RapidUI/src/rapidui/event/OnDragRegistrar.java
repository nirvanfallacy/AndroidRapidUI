package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.ArgumentMapper;
import rapidui.annotation.event.OnDrag;
import android.view.DragEvent;
import android.view.View;

public class OnDragRegistrar extends SimpleEventRegistrar {
	private static Class<?>[] argsOnDrag = new Class<?>[] { View.class, DragEvent.class };
	
	@Override
	public int[] getTargetViewIds(Annotation annotation) {
		return ((OnDrag) annotation).value();
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {

		final Method onDrag = methods.get(OnDrag.class);
		final ArgumentMapper amDrag = new ArgumentMapper(argsOnDrag, onDrag);
		
		return new View.OnDragListener() {
			@Override
			public boolean onDrag(View v, DragEvent event) {
				try {
					onDrag.setAccessible(true);
					return (Boolean) onDrag.invoke(instance, amDrag.map(v, event));
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
