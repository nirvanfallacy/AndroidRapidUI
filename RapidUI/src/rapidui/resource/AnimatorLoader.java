package rapidui.resource;

import rapidui.annotation.ResourceType;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.os.Build;

public class AnimatorLoader extends ResourceLoader {
	private static final String RESOURCE_TYPE = "animator";
	
	@Override
	public Object load(Context context, String resType, int id,
			Class<?> fieldType) {
		
		if (resType.equals(RESOURCE_TYPE)) {
			return AnimatorInflater.loadAnimator(context, id);
		} else {
			return null;
		}
	}

	@Override
	public Object load(Context context, String fieldName, Class<?> fieldType) {
		if (Build.VERSION.SDK_INT >= 11 && fieldType.isAssignableFrom(Animator.class)) {
			final int id = findResourceId(context, fieldName, RESOURCE_TYPE);
			if (id != 0) {
				return AnimatorInflater.loadAnimator(context, id);
			}
		}
		return null;
	}

	@Override
	public Object load(Context context, ResourceType resType, int id,
			String fieldName) {

		if (Build.VERSION.SDK_INT >= 11 && resType == ResourceType.ANIMATOR) {
			if (id == 0) {
				id = findResourceId(context, fieldName, RESOURCE_TYPE);
			}
			if (id != 0) {
				return AnimatorInflater.loadAnimator(context, id);
			}
		}
		return null;
	}
}
