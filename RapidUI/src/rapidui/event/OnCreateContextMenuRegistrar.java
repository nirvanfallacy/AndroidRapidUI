package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.ArgumentMapper;
import rapidui.annotation.event.OnCreateContextMenu;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

public class OnCreateContextMenuRegistrar extends SimpleEventRegistrar {
	private static Class<?>[] argsCreateContextMenu = new Class<?>[] { ContextMenu.class, View.class, ContextMenuInfo.class };
	
	@Override
	public int[] getTargetViewIds(Annotation annotation) {
		return ((OnCreateContextMenu) annotation).value();
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {

		final Method onCreateContextMenu = methods.get(OnCreateContextMenu.class);
		final ArgumentMapper amCreateContextMenu = new ArgumentMapper(argsCreateContextMenu, onCreateContextMenu);
		
		return new View.OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				
				try {
					onCreateContextMenu.setAccessible(true);
					onCreateContextMenu.invoke(instance, amCreateContextMenu.map(menu, v, menuInfo));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		};
	}

	@Override
	public void registerEventListener(Object target, Object dispatcher) {
		((View) target).setOnCreateContextMenuListener((View.OnCreateContextMenuListener) dispatcher);
	}
}
