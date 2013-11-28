package rapidui.event;

import java.lang.annotation.Annotation;

public abstract class ExternalEventInfo {
	public abstract int getType();
	public abstract int[] getTargetIds(Annotation annotation);
}
