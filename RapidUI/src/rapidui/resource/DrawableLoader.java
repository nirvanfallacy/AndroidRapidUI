package rapidui.resource;

import rapidui.ResourceUtils;
import rapidui.annotation.ResourceType;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class DrawableLoader extends ResourceLoader {
	private static final String RESOURCE_TYPE = "drawable";
	
	@Override
	public Object load(Context context, String resType, int id, Class<?> fieldType) {
		if (resType.equals(RESOURCE_TYPE)) {
			return context.getResources().getDrawable(id);
		} else {
			return null;
		}
	}

	@Override
	public Object load(Context context, String fieldName, Class<?> fieldType) {
		if (fieldType.isAssignableFrom(Drawable.class)) {
			final int id = ResourceUtils.findResourceId(context, fieldName, RESOURCE_TYPE);
			if (id != 0) {
				return context.getResources().getDrawable(id);
			}
		}
		return null;
	}

	@Override
	public Object load(Context context, ResourceType resType, int id,
			String fieldName) {

		if (resType == ResourceType.DRAWABLE) {
			if (id == 0) {
				id = ResourceUtils.findResourceId(context, fieldName, RESOURCE_TYPE);
			}
			if (id != 0) {
				return context.getResources().getDrawable(id);
			}
		}
		return null;
	}
}
