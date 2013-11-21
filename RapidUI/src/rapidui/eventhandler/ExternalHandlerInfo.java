package rapidui.eventhandler;

import java.lang.annotation.Annotation;

public abstract class ExternalHandlerInfo {
	public abstract int getType();
	public abstract int[] getTargetIds(Annotation annotation);
}
