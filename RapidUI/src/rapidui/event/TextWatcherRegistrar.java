package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.Lifecycle;
import rapidui.annotation.event.OnAfterTextChanged;
import rapidui.annotation.event.OnBeforeTextChanged;
import rapidui.annotation.event.OnTextChanged;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class TextWatcherRegistrar extends UnregisterableEventRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		if (annotation instanceof OnTextChanged) {
			return ((OnTextChanged) annotation).value();
		} else if (annotation instanceof OnBeforeTextChanged) {
			return ((OnBeforeTextChanged) annotation).value();
		} else if (annotation instanceof OnAfterTextChanged) {
			return ((OnAfterTextChanged) annotation).value();
		}
		
		return new int[] {};
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {

		final Method onTextChanged = methods.get(OnTextChanged.class);
		final Method onBeforeTextChanged = methods.get(OnBeforeTextChanged.class);
		final Method onAfterTextChanged = methods.get(OnAfterTextChanged.class);
		
		return new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (onTextChanged != null) {
					try {
						onTextChanged.setAccessible(true);
						onTextChanged.invoke(instance, s, start, before, count);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
				if (onBeforeTextChanged != null) {
					try {
						onBeforeTextChanged.setAccessible(true);
						onBeforeTextChanged.invoke(instance, s, start, count, after);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (onAfterTextChanged != null) {
					try {
						onAfterTextChanged.setAccessible(true);
						onAfterTextChanged.invoke(instance, s);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	@Override
	public void registerEventListener(Object target, Object dispatcher) {
		((TextView) target).addTextChangedListener((TextWatcher) dispatcher);
	}
	
	@Override
	public void unregisterEventListener(Object target, Object dispatcher) {
		((TextView) target).removeTextChangedListener((TextWatcher) dispatcher);
	}

	@Override
	public Lifecycle getLifecycle(Annotation annotation) {
		if (annotation instanceof OnTextChanged) {
			return ((OnTextChanged) annotation).lifecycle();
		} else if (annotation instanceof OnBeforeTextChanged) {
			return ((OnBeforeTextChanged) annotation).lifecycle();
		} else if (annotation instanceof OnAfterTextChanged) {
			return ((OnAfterTextChanged) annotation).lifecycle();
		}
		
		return Lifecycle.CREATE;
	}
}
