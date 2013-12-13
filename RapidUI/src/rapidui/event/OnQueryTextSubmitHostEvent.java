package rapidui.event;

import java.lang.annotation.Annotation;

import rapidui.HostExtension;
import rapidui.annotation.event.OnQueryTextSubmit;
import rapidui.util.IntegerArrayIterable;

public class OnQueryTextSubmitHostEvent extends HostEventInfo {
	@Override
	public Iterable<?> getTargetIds(Annotation annotation) {
		return new IntegerArrayIterable(((OnQueryTextSubmit) annotation).value());
	}

	@Override
	public int getType() {
		return HostExtension.HOST_EVENT_QUERY_TEXT_SUBMIT;
	}
}
