package rapidui.eventhandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rapidui.annotation.eventhandler.OnClick;
import android.view.View;

public class OnClickRegistrar extends EventHandlerRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnClick) annotation).value();
	}

	@Override
	public void registerEventListener(Object target,
			final Object instance, final Method method) {

		((View) target).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					method.invoke(instance, v);
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
