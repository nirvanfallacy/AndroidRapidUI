package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.event.OnCheckedChange;
import android.widget.CompoundButton;

public class OnCheckedChangeRegistrar extends SimpleEventRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnCheckedChange) annotation).value();
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {
		
		final Method method = methods.get(OnCheckedChange.class);
		
		return new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				try {
					method.setAccessible(true);
					method.invoke(instance, buttonView, isChecked);
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
		((CompoundButton) target).setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) dispatcher);
	}
}
