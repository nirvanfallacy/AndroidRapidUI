package rapidui.eventhandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.eventhandler.OnKey;
import android.view.KeyEvent;
import android.view.View;

public class OnKeyRegistrar extends EventHandlerRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnKey) annotation).value();
	}

	@Override
	public void registerEventListener(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {
		
		final Method method = methods.get(OnKey.class);
		
		((View) target).setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				try {
					method.setAccessible(true);
					return (Boolean) method.invoke(instance, v, keyCode, event);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				
				return false;
			}
		});
	}
}
