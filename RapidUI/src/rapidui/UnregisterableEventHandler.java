package rapidui;

import rapidui.eventhandler.UnregisterableEventHandlerRegistrar;

public class UnregisterableEventHandler {
	public UnregisterableEventHandlerRegistrar registrar;
	public Object target;
	public Object dispatcher;
	
	public UnregisterableEventHandler(
			UnregisterableEventHandlerRegistrar registrar, Object target,
			Object dispatcher) {
		this.registrar = registrar;
		this.target = target;
		this.dispatcher = dispatcher;
	}
}
