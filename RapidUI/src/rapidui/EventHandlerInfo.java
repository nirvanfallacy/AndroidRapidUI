package rapidui;

import java.lang.reflect.Method;

public class EventHandlerInfo {
	public Method method;
	public ArgumentMapper argMatcher;
	
	public EventHandlerInfo(Method method, ArgumentMapper argMatcher) {
		this.method = method;
		this.argMatcher = argMatcher;
	}
}
