package rapidui.event;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import android.content.Context;
import rapidui.Extension;
import rapidui.ResourceUtils;
import rapidui.annotation.event.OnMenuItemClick;

public class OnMenuItemClickHostEvent extends HostEventInfo {
	@Override
	public Iterable<?> getTargetIds(Annotation annotation) {
		return Arrays.asList(((OnMenuItemClick) annotation).value());
	}

	@Override
	public int getType() {
		return Extension.HOST_EVENT_MENU_ITEM_CLICK;
	}

	@Override
	public Object parseId(Context context, String s) {
		final int id = ResourceUtils.findResourceId(context, s, "id");
		return (id == 0 ? null : id);
	}
}
