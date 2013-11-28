package rapidui.event;

import java.lang.annotation.Annotation;

import rapidui.annotation.Lifecycle;

public abstract class UnregisterableEventRegistrar extends SimpleEventRegistrar {
	public abstract Lifecycle getLifecycle(Annotation annotation);
	public abstract void unregisterEventListener(Object target, Object dispatcher);
}
