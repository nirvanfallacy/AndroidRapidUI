package rapidui.event;

import java.lang.annotation.Annotation;

import rapidui.HostAspect;
import rapidui.annotation.event.OnQueryTextChange;
import rapidui.util.IntegerArrayIterable;

public class OnQueryTextChangeHostEvent extends HostEventInfo {
	@Override
	public Iterable<?> getTargetIds(Annotation annotation) {
		return new IntegerArrayIterable(((OnQueryTextChange) annotation).value());
	}

	@Override
	public int getType() {
		return HostAspect.HOST_EVENT_QUERY_TEXT_CHANGE;
	}
}
