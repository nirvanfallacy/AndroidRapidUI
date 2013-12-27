package rapidui.event;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import android.content.Context;
import rapidui.ObjectAspect;
import rapidui.annotation.event.OnServiceConnect;

public class OnServiceConnectHostEvent extends HostEventInfo {
	@Override
	public Iterable<?> getTargetIds(Annotation annotation) {
		return Arrays.asList(((OnServiceConnect) annotation).alias());
	}

	@Override
	public int getType() {
		return ObjectAspect.HOST_EVENT_SERVICE_CONNECT;
	}

	@Override
	public Object parseId(Context context, String s) {
		return s;
	}
}
