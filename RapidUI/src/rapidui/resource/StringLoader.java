package rapidui.resource;

import rapidui.ResourceUtils;
import rapidui.annotation.ResourceType;
import android.content.Context;

public class StringLoader extends ResourceLoader {
	private static final String RESOURCE_TYPE = "string";
	
	@Override
	public Object load(Context context, String resType, int id,
			Class<?> fieldType) {
		
		if (resType.equals(RESOURCE_TYPE)) {
			return context.getResources().getString(id);
		} else {
			return null;
		}
	}

	@Override
	public Object load(Context context, String fieldName, Class<?> fieldType) {
		if (fieldType.isAssignableFrom(String.class)) {
			final int id = ResourceUtils.findResourceId(context, fieldName, RESOURCE_TYPE);
			if (id != 0) {
				return context.getResources().getString(id);
			}
		}
		return null;
	}

	@Override
	public Object load(Context context, ResourceType resType, int id,
			String fieldName) {

		if (resType == ResourceType.STRING) {
			if (id == 0) {
				id = ResourceUtils.findResourceId(context, fieldName, RESOURCE_TYPE);
			}
			if (id != 0) {
				return context.getResources().getString(id);
			}
		}
		return null;
	}
}
