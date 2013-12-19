package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.ArgumentMapper;
import rapidui.annotation.event.OnScroll;
import rapidui.annotation.event.OnScrollStateChange;
import android.widget.AbsListView;
import android.widget.NumberPicker;

public class OnScrollRegistrar extends SimpleEventRegistrar {
	private static Class<?>[] argsScroll_AbsListView = new Class<?>[] { AbsListView.class, Integer.TYPE, Integer.TYPE, Integer.TYPE };
	private static Class<?>[] argsScrollStateChanged_AbsListView = new Class<?>[] { AbsListView.class, Integer.TYPE };
	private static Class<?>[] argsScrollStateChanged_NumberPicker = new Class<?>[] { NumberPicker.class, Integer.TYPE };
	
	@Override
	public int[] getTargetViewIds(Annotation annotation) {
		if (annotation instanceof OnScroll) {
			return ((OnScroll) annotation).value();
		} else if (annotation instanceof OnScrollStateChange) {
			return ((OnScrollStateChange) annotation).value();
		} else {
			return null;
		}
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {
		
		if (instance instanceof AbsListView) {
			final Method onScroll = methods.get(OnScroll.class);
			final Method onScrollStateChanged = methods.get(OnScrollStateChange.class);
			
			final ArgumentMapper amScroll = new ArgumentMapper(argsScroll_AbsListView, onScroll);
			final ArgumentMapper amScrollStateChanged = new ArgumentMapper(argsScrollStateChanged_AbsListView, onScrollStateChanged);
			
			return new AbsListView.OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if (onScrollStateChanged == null) return;
					
					try {
						onScrollStateChanged.setAccessible(true);
						onScrollStateChanged.invoke(instance, amScrollStateChanged.match(view, scrollState));
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
						onScroll.invoke(instance, amScroll.match(view, firstVisibleItem, visibleItemCount, totalItemCount));
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
			final Method onScrollStateChanged = methods.get(OnScrollStateChange.class);
			final ArgumentMapper amScrollStateChanged = new ArgumentMapper(argsScrollStateChanged_NumberPicker, onScrollStateChanged);
			
			return new NumberPicker.OnScrollListener() {
				@Override
				public void onScrollStateChange(NumberPicker view, int scrollState) {
					if (onScrollStateChanged == null) return;
					
					onScrollStateChanged.setAccessible(true);
					try {
						onScrollStateChanged.invoke(instance, amScrollStateChanged.match(view, scrollState));
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
