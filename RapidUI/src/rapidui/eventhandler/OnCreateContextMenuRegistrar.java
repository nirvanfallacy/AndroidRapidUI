package rapidui.eventhandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.eventhandler.OnCreateContextMenu;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

public class OnCreateContextMenuRegistrar extends EventHandlerRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnCreateContextMenu) annotation).value();
	}

	@Override
	public void registerEventListener(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {
		
		final Method method = methods.get(OnCreateContextMenu.class);
		
		((View) target).setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				
				try {
					method.setAccessible(true);
					method.invoke(instance, menu, v, menuInfo);
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
