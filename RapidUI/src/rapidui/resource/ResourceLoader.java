package rapidui.resource;

import rapidui.ResourceUtils;
import rapidui.annotation.ResourceType;
import android.content.Context;
import android.content.res.Resources;

public abstract class ResourceLoader {
	public abstract Object load(Context context, String resType, int id, Class<?> fieldType);
	public abstract Object load(Context context, String fieldName, Class<?> fieldType);
	public abstract Object load(Context context, ResourceType resType, int id, String fieldName);
	
	protected static int findResourceId(Context context, String name, String type) {
		final Resources res = context.getResources();
		final String packageName = context.getPackageName();
		
		int id = res.getIdentifier(ResourceUtils.toLowerUnderscored(name), type, packageName);
		if (id == 0) {
			id = res.getIdentifier(name, type, packageName);
		}
		
		return id;
	}
}
