package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.ArgumentMapper;
import rapidui.annotation.event.OnItemClick;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

public class OnItemClickRegistrar extends SimpleEventRegistrar {
	private static Class<?>[] argsItemClick = new Class<?>[] { AdapterView.class, View.class, Integer.TYPE, Long.TYPE };
	
	@Override
	public int[] getTargetViewIds(Annotation annotation) {
		return ((OnItemClick) annotation).value();
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {

		final Method onItemClick = methods.get(OnItemClick.class);
		final ArgumentMapper amItemClick = new ArgumentMapper(argsItemClick, onItemClick);
		
		return new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				try {
					onItemClick.setAccessible(true);
					onItemClick.invoke(instance, amItemClick.map(parent, view, position, id));
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
		((AbsListView) target).setOnItemClickListener((AdapterView.OnItemClickListener) dispatcher);
	}
}
