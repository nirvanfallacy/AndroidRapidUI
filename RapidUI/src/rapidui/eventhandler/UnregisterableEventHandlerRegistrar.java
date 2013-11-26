package rapidui.eventhandler;

import java.lang.annotation.Annotation;

import rapidui.annotation.Lifecycle;

public abstract class UnregisterableEventHandlerRegistrar extends EventHandlerRegistrar {
	public abstract Lifecycle getLifecycle(Annotation annotation);
	public abstract void unregisterEventListener(Object target, Object dispatcher);
}
