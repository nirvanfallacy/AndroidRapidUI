package rapidui.resource;

import rapidui.annotation.ResourceType;
import android.content.Context;

public class IntegerLoader extends ResourceLoader {
	private static final String RESOURCE_TYPE = "integer";
	
	@Override
	public Object load(Context context, String resType, int id,
			Class<?> fieldType) {
		
		if (resType.equals(RESOURCE_TYPE)) {
			return context.getResources().getInteger(id);
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
				return context.getResources().getInteger(id);
			}
		}
		return null;
	}

	@Override
	public Object load(Context context, ResourceType resType, int id,
			String fieldName) {

		if (resType == ResourceType.INTEGER) {
			if (id == 0) {
				id = findResourceId(context, fieldName, RESOURCE_TYPE);
			}
			if (id != 0) {
				return context.getResources().getInteger(id);
			}
		}
		return null;
	}
}
