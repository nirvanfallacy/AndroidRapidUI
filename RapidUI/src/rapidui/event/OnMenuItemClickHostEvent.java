package rapidui.event;

import java.lang.annotation.Annotation;

import rapidui.HostAspect;
import rapidui.annotation.event.OnMenuItemClick;
import rapidui.util.IntegerArrayIterable;

public class OnMenuItemClickHostEvent extends HostEventInfo {
	@Override
	public Iterable<?> getTargetIds(Annotation annotation) {
		return new IntegerArrayIterable(((OnMenuItemClick) annotation).value());
	}

	@Override
	public int getType() {
		return HostAspect.HOST_EVENT_MENU_ITEM_CLICK;
	}
}
