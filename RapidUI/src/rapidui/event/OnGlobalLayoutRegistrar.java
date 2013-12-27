package rapidui.event;

import static rapidui.shortcut.Shortcuts.addOnGlobalLayoutListener;
import static rapidui.shortcut.Shortcuts.removeOnGlobalLayoutListener;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.Lifecycle;
import rapidui.annotation.event.OnGlobalLayout;
import android.view.View;
import android.view.ViewTreeObserver;

public class OnGlobalLayoutRegistrar extends UnregisterableEventRegistrar {
	private static WeakReference<OnGlobalLayoutRegistrar> instance;
	
	public static OnGlobalLayoutRegistrar getInstance() {
		OnGlobalLayoutRegistrar registrar = (instance == null ? null : instance.get());
		if (registrar == null) {
			registrar = new OnGlobalLayoutRegistrar();
			instance = new WeakReference<OnGlobalLayoutRegistrar>(registrar);
		}
		
		return registrar;
	}
	
	@Override
	public Lifecycle getLifecycle(Annotation annotation) {
		return ((OnGlobalLayout) annotation).lifecycle();
	}

	@Override
	public void unregisterEventListener(Object target, Object dispatcher) {
		removeOnGlobalLayoutListener((View) target, (ViewTreeObserver.OnGlobalLayoutListener) dispatcher);
	}

	@Override
	public int[] getTargetViewIds(Annotation annotation) {
		return null;
	}

	@Override
	public Object createEventDispatcher(final Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {

		return null;
	}

	@Override
	public void registerEventListener(Object target, Object dispatcher) {
		addOnGlobalLayoutListener((View) target, (ViewTreeObserver.OnGlobalLayoutListener) dispatcher);
	}
}
