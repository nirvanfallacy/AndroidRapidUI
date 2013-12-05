package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.ArgumentMapper;
import rapidui.annotation.event.OnCheckedChange;
import android.widget.CompoundButton;

public class OnCheckedChangeRegistrar extends SimpleEventRegistrar {
	private static Class<?>[] argsCheckedChange = new Class<?>[] { CompoundButton.class, Boolean.TYPE };
	
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnCheckedChange) annotation).value();
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {
		
		final Method onCheckedChange = methods.get(OnCheckedChange.class);
		final ArgumentMapper amCheckedChange = new ArgumentMapper(argsCheckedChange, onCheckedChange);
		
		return new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				try {
					onCheckedChange.setAccessible(true);
					onCheckedChange.invoke(instance, amCheckedChange.match(buttonView, isChecked));
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
