package rapidui;

import rapidui.event.UnregisterableEventRegistrar;

class UnregisterableEventHandler {
	public UnregisterableEventRegistrar registrar;
	public Object target;
	public Object dispatcher;
	
	public UnregisterableEventHandler(
			UnregisterableEventRegistrar registrar, Object target,
			Object dispatcher) {
		this.registrar = registrar;
		this.target = target;
		this.dispatcher = dispatcher;
	}
}
