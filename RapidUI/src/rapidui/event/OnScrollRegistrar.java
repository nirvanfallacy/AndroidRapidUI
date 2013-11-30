package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.annotation.event.OnScroll;
import rapidui.annotation.event.OnScrollStateChanged;
import android.widget.AbsListView;
import android.widget.NumberPicker;

public class OnScrollRegistrar extends SimpleEventRegistrar {
	@Override
	public int[] getTargetIds(Annotation annotation) {
		if (annotation instanceof OnScroll) {
			return ((OnScroll) annotation).value();
		} else if (annotation instanceof OnScrollStateChanged) {
			return ((OnScrollStateChanged) annotation).value();
		} else {
			return null;
		}
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {
		
		if (instance instanceof AbsListView) {
			final Method onScroll = methods.get(OnScroll.class);
			final Method onScrollStateChanged = methods.get(OnScrollStateChanged.class);
			
			return new AbsListView.OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if (onScrollStateChanged == null) return;
					
					try {
						onScrollStateChanged.setAccessible(true);
						onScrollStateChanged.invoke(instance, view, scrollState);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					
					if (onScroll == null) return;
					
					try {
						onScroll.setAccessible(true);
						onScroll.invoke(instance, view, firstVisibleItem, visibleItemCount, totalItemCount);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			};
		} else if (instance instanceof NumberPicker) {
			final Method onScrollStateChanged = methods.get(OnScrollStateChanged.class);
			
			return new NumberPicker.OnScrollListener() {
				@Override
				public void onScrollStateChange(NumberPicker view, int scrollState) {
					if (onScrollStateChanged == null) return;
					
					onScrollStateChanged.setAccessible(true);
					try {
						onScrollStateChanged.invoke(instance, view, scrollState);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			};
		} else {
			return null;
		}
	}

	@Override
	public void registerEventListener(Object target, Object dispatcher) {
		if (target instanceof AbsListView) {
			((AbsListView) target).setOnScrollListener((AbsListView.OnScrollListener) dispatcher);
		} else if (target instanceof NumberPicker) {
			((NumberPicker) target).setOnScrollListener((NumberPicker.OnScrollListener) dispatcher);
		}
	}
}
