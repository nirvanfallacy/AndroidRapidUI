package rapidui.event;

import java.lang.annotation.Annotation;

import rapidui.ObjectAspect;
import android.content.Context;

public class OnUncaughtExceptionHostEvent extends HostEventInfo {
	@Override
	public Iterable<?> getTargetIds(Annotation annotation) {
		return null;
	}

	@Override
	public int getType() {
		return ObjectAspect.HOST_EVENT_UNCAUGHT_EXCEPTION;
	}

	@Override
	public Object parseId(Context context, String s) {
		return null;
	}
}
