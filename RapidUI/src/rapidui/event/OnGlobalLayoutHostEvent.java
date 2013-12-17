package rapidui.event;

import java.lang.annotation.Annotation;

import rapidui.RapidAspect;
import android.content.Context;

public class OnGlobalLayoutHostEvent extends HostEventInfo {
	@Override
	public Iterable<?> getTargetIds(Annotation annotation) {
		return null;
	}

	@Override
	public int getType() {
		return RapidAspect.HOST_EVENT_GLOBAL_LAYOUT;
	}

	@Override
	public Object parseId(Context context, String s) {
		return null;
	}
}
