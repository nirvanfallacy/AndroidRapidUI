package rapidui.event;

import java.lang.reflect.Method;

public class UnregisterableCustomEventInfo extends
		CustomEventInfo {
	
	private Method remover;

	protected UnregisterableCustomEventInfo(Method adder, Method remover,
			Class<?> listenerType) {
		super(adder, listenerType);
		this.remover = remover;
	}

	public Method getRemover() {
		return remover;
	}
}
