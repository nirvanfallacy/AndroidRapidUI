package rapidui.resource;

import rapidui.annotation.ResourceType;
import android.content.Context;

public abstract class ResourceLoader {
	public abstract Object load(Context context, String resType, int id, Class<?> fieldType);
	public abstract Object load(Context context, String fieldName, Class<?> fieldType);
	public abstract Object load(Context context, ResourceType resType, int id, String fieldName);
}
