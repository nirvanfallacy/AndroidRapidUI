package rapidui.eventhandler;

import java.lang.annotation.Annotation;

import rapidui.Injector;
import rapidui.annotation.eventhandler.OnMenuItemClick;

public class OnMenuItemClickInfo extends ExternalHandlerInfo {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		return ((OnMenuItemClick) annotation).value();
	}

	@Override
	public int getType() {
		return Injector.EXTERNAL_HANDLER_MENU_ITEM_CLICK;
	}
}
