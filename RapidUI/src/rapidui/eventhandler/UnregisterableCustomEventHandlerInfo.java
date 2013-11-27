package rapidui.eventhandler;

import java.lang.reflect.Method;

public class UnregisterableCustomEventHandlerInfo extends
		CustomEventHandlerInfo {
	
	private Method remover;

	protected UnregisterableCustomEventHandlerInfo(Method adder, Method remover,
			Class<?> listenerType) {
		super(adder, listenerType);
		this.remover = remover;
	}

	public Method getRemover() {
		return remover;
	}
}
