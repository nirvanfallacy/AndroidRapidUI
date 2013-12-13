package rapidui.event;

import java.lang.annotation.Annotation;

import rapidui.ResourceUtils;
import android.content.Context;

public abstract class HostEventInfo {
	public abstract int getType();
	public abstract Iterable<?> getTargetIds(Annotation annotation);
	
	public Object parseId(Context context, String s) {
		final int id = ResourceUtils.findResourceId(context, s, "id");
		return (id == 0 ? null : id);
	}
}
