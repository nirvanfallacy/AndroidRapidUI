package rapidui.event;

import java.lang.annotation.Annotation;

import android.content.Context;

public abstract class HostEventInfo {
	public abstract int getType();
	public abstract Iterable<?> getTargetIds(Annotation annotation);
	public abstract Object parseId(Context context, String s);
}
