package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.ArgumentMapper;
import rapidui.annotation.event.OnKey;
import android.view.KeyEvent;
import android.view.View;

public class OnKeyRegistrar extends SimpleEventRegistrar {
	private static Class<?>[] argsKey = new Class<?>[] { View.class, Integer.TYPE, KeyEvent.class };
	
	@Override
	public int[] getTargetViewIds(Annotation annotation) {
		return ((OnKey) annotation).value();
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {

		final Method onKey = methods.get(OnKey.class);
		final ArgumentMapper amKey = new ArgumentMapper(argsKey, onKey);
		
		return new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				try {
					onKey.setAccessible(true);
					return (Boolean) onKey.invoke(instance, amKey.map(v, keyCode, event));
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
