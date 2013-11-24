package rapidui.resource;

import rapidui.annotation.ResourceType;
import android.content.Context;

public class DimensionLoader extends ResourceLoader {
	private static final String RESOURCE_TYPE = "dimen";
	
	@Override
	public Object load(Context context, String resType, int id,
			Class<?> fieldType) {
		
		if (resType.equals(RESOURCE_TYPE)) {
			if (fieldType.equals(Integer.TYPE) || fieldType.equals(Integer.class)) {
				return context.getResources().getDimensionPixelSize(id);
			} else {
				return context.getResources().getDimension(id);
			}
		} else {
			return null;
		}
	}

	@Override
	public Object load(Context context, String fieldName, Class<?> fieldType) {
		final boolean isFloat = fieldType.isAssignableFrom(Float.TYPE) ||
				fieldType.isAssignableFrom(Float.class);
		final boolean isInteger = fieldType.isAssignableFrom(Integer.TYPE) ||
				fieldType.isAssignableFrom(Integer.class);
		
		if (isFloat || isInteger) {
			final int id = findResourceId(context, fieldName, RESOURCE_TYPE);
			if (id != 0) {
				if (isFloat) {
					return context.getResources().getDimension(id);
				} else {
					return context.getResources().getDimensionPixelSize(id);
				}
			}
		}
		
		return null;
	}

	@Override
	public Object load(Context context, ResourceType resType, int id,
			String fieldName) {
		
		final boolean isDimension = (resType == ResourceType.DIMENSION);
		final boolean isDimensionOffset = (resType == ResourceType.DIMENSION_LOCATION);
		final boolean isDimensionSize = (resType == ResourceType.DIMENSION_SIZE);

		if (isDimension || isDimensionOffset || isDimensionSize) {
			if (id == 0) {
				id = findResourceId(context, fieldName, RESOURCE_TYPE);
			}
			if (id != 0) {
				if (isDimension) {
					return context.getResources().getDimension(id);
				} else if (isDimensionOffset) {
					return context.getResources().getDimensionPixelOffset(id);
				} else {
					return context.getResources().getDimensionPixelSize(id);
				}
			}
		}
		return null;
	}
}
