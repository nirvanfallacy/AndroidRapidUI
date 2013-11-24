package rapidui.resource;

import rapidui.annotation.ResourceType;
import android.content.Context;

public class ColorLoader extends ResourceLoader {
	private static final String RESOURCE_TYPE = "color";
	
	@Override
	public Object load(Context context, String resType, int id,
			Class<?> fieldType) {
		
		if (resType.equals(RESOURCE_TYPE)) {
			return context.getResources().getColor(id);
		} else {
			return null;
		}
	}

	@Override
	public Object load(Context context, String fieldName, Class<?> fieldType) {
		if (fieldType.isAssignableFrom(Integer.TYPE) ||
				fieldType.isAssignableFrom(Integer.class)) {
			
			final int id = findResourceId(context, fieldName, RESOURCE_TYPE);
			if (id != 0) {
				return context.getResources().getColor(id);
			}
		}
		return null;
	}

	@Override
	public Object load(Context context, ResourceType resType, int id,
			String fieldName) {

		if (resType == ResourceType.COLOR) {
			if (id == 0) {
				id = findResourceId(context, fieldName, RESOURCE_TYPE);
			}
			if (id != 0) {
				return context.getResources().getColor(id);
			}
		}
		return null;
	}
}
