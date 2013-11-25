package rapidui.eventhandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.eventhandler.OnCheckedChange;
import android.widget.CompoundButton;

public class OnCheckedChangeRegistrar extends EventHandlerRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnCheckedChange) annotation).value();
	}

	@Override
	public void registerEventListener(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {
		
		final Method method = methods.get(OnCheckedChange.class);
		
		((CompoundButton) target).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
		});
	}
}
