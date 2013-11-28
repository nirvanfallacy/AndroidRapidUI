package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.event.OnClick;
import android.view.View;

public class OnClickRegistrar extends SimpleEventRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnClick) annotation).value();
	}

	@Override
	public Object createEventDispatcher(final Object instance,
			HashMap<Class<?>, Method> methods) {
		
		final Method onClickMethod = methods.get(OnClick.class);
		
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					onClickMethod.setAccessible(true);
					onClickMethod.invoke(instance, v);
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
		((View) target).setOnClickListener((View.OnClickListener) dispatcher);
	}
}
