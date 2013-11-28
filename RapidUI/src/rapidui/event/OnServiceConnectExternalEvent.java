package rapidui.event;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import android.content.Context;
import rapidui.Extension;
import rapidui.annotation.event.OnServiceConnect;

public class OnServiceConnectExternalEvent extends ExternalEventInfo {
	@Override
	public Iterable<?> getTargetIds(Annotation annotation) {
		return Arrays.asList(((OnServiceConnect) annotation).alias());
	}

	@Override
	public int getType() {
		return Extension.EXTERNAL_EVENT_SERVICE_CONNECT;
	}

	@Override
	public Object parseId(Context context, String s) {
		return s;
	}
}
