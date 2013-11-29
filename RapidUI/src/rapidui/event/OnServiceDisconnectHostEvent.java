package rapidui.event;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import android.content.Context;
import rapidui.Extension;
import rapidui.annotation.event.OnServiceDisconnect;

public class OnServiceDisconnectHostEvent extends HostEventInfo {
	@Override
	public Iterable<?> getTargetIds(Annotation annotation) {
		return Arrays.asList(((OnServiceDisconnect) annotation).alias());
	}

	@Override
	public int getType() {
		return Extension.EXTERNAL_EVENT_SERVICE_DISCONNECT;
	}

	@Override
	public Object parseId(Context context, String s) {
		return s;
	}
}
