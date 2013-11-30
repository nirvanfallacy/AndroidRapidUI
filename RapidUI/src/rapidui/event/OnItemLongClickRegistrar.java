package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.event.OnItemLongClick;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

public class OnItemLongClickRegistrar extends SimpleEventRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnItemLongClick) annotation).value();
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {

		final Method onItemLongClick = methods.get(OnItemLongClick.class);
		
		if (onItemLongClick == null) {
			return null;
		} else {
			return new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view,
						int position, long id) {

					onItemLongClick.setAccessible(true);;
					try {
						return (Boolean) onItemLongClick.invoke(instance, parent, view, position, id);
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
	}

	@Override
	public void registerEventListener(Object target, Object dispatcher) {
		((AbsListView) target).setOnItemLongClickListener((AdapterView.OnItemLongClickListener) dispatcher);
	}
}
