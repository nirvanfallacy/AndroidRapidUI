package rapidui.resource;

import rapidui.ResourceUtils;
import rapidui.annotation.ResourceType;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationLoader extends ResourceLoader {
	private static final String RESOURCE_TYPE = "anim";
	
	@Override
	public Object load(Context context, String resType, int id,
			Class<?> fieldType) {
		
		if (resType.equals(RESOURCE_TYPE)) {
			return AnimationUtils.loadAnimation(context, id);
		} else {
			return null;
		}
	}

	@Override
	public Object load(Context context, String fieldName, Class<?> fieldType) {
		if (fieldType.isAssignableFrom(Animation.class)) {
			final int id = ResourceUtils.findResourceId(context, fieldName, RESOURCE_TYPE);
			if (id != 0) {
				return AnimationUtils.loadAnimation(context, id);
			}
		}
		return null;
	}

	@Override
	public Object load(Context context, ResourceType resType, int id,
			String fieldName) {
		
		if (resType == ResourceType.ANIMATION) {
			if (id == 0) {
				id = ResourceUtils.findResourceId(context, fieldName, RESOURCE_TYPE);
			}
			if (id != 0) {
				return AnimationUtils.loadAnimation(context, id);
			}
		}
		return null;
	}
}
