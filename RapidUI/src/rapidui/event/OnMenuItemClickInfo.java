package rapidui.event;

import java.lang.annotation.Annotation;

import rapidui.Injector;
import rapidui.annotation.event.OnMenuItemClick;

public class OnMenuItemClickInfo extends ExternalEventInfo {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnMenuItemClick) annotation).value();
	}

	@Override
	public int getType() {
		return Injector.EXTERNAL_HANDLER_MENU_ITEM_CLICK;
	}
}
