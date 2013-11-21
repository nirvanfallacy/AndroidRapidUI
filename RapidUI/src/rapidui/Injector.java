package rapidui;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;

import rapidui.annotation.LayoutElement;
import rapidui.annotation.eventhandler.OnClick;
import rapidui.annotation.eventhandler.OnMenuItemClick;
import rapidui.eventhandler.EventHandlerRegistrar;
import rapidui.eventhandler.ExternalHandlerInfo;
import rapidui.eventhandler.OnClickRegistrar;
import rapidui.eventhandler.OnMenuItemClickInfo;
import rapidui.util.HashMap3;
import android.app.Activity;
import android.content.res.Resources;
import android.util.SparseArray;
import android.view.View;

public abstract class Injector {
	public static final int EXTERNAL_HANDLER_MENU_ITEM_CLICK = 0;
	
	private static HashMap<Class<? extends Annotation>, EventHandlerRegistrar> eventHandlerRegistrars =
			new HashMap<Class<? extends Annotation>, EventHandlerRegistrar>();
	private static HashMap<Class<? extends Annotation>, ExternalHandlerInfo> externalHandlerInfoList =
			new HashMap<Class<? extends Annotation>, ExternalHandlerInfo>();
	
	static {
		eventHandlerRegistrars.put(OnClick.class, new OnClickRegistrar());
		
		externalHandlerInfoList.put(OnMenuItemClick.class, new OnMenuItemClickInfo());
	}
	
	protected Activity activity;
	protected Object memberContainer;
	protected ViewFinder viewFinder;
	
	public Injector(Activity activity, Object memberContainer, ViewFinder viewFinder) {
		this.activity = activity;
		this.memberContainer = memberContainer;
		this.viewFinder = viewFinder;
	}
	
	public void injectViews() {
		final Resources res = activity.getResources();
		final SparseArray<View> viewMap = new SparseArray<View>();
		
		// [viewId][registrar][annotation][method]
		final HashMap3<Integer, EventHandlerRegistrar, Class<?>, Method> methodMap =
				new HashMap3<Integer, EventHandlerRegistrar, Class<?>, Method>();

		Class<?> cls = memberContainer.getClass();
		
		while (cls != null && !cls.equals(RapidActivity.class)) {
			// Inject fields
			
			for (Field field: cls.getDeclaredFields()) {
				final String fieldName = field.getName();
				
				final LayoutElement layoutElement = field.getAnnotation(LayoutElement.class);
				if (layoutElement != null) {
					int id = layoutElement.value();
					if (id == 0) {
						final String packageName = activity.getPackageName();
						
						id = res.getIdentifier(fieldName, "id", packageName);
						
						if (id == 0) {
							final String name = toLowerUnderline(fieldName);
							id = res.getIdentifier(name, "id", packageName);
						}
					}
					
					field.setAccessible(true);
					try {
						final View v = viewFinder.findViewById(id);
						field.set(memberContainer, v);
						
						if (v != null) {
							viewMap.put(id, v);
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
	
			// Inject event handlers
			
			for (Method method: cls.getDeclaredMethods()) {
				for (Annotation annotation: method.getAnnotations()) {
					final Class<?> annotationType = annotation.annotationType();
					
					final EventHandlerRegistrar registrar = eventHandlerRegistrars.get(annotationType);
					if (registrar != null) {
						for (int id: registrar.getTargetIds(annotation)) {
							methodMap.put(id, registrar, annotationType, method);
						}
							
						continue;
					}
					
					final ExternalHandlerInfo info = externalHandlerInfoList.get(annotationType);
					if (info != null) {
						final int type = info.getType();
						for (int id: info.getTargetIds(annotation)) {
							registerExternalHandler(type, id, method);
						}
					}
				}
			}
			
			cls = cls.getSuperclass();
		}
		
		for (Entry<Integer, HashMap<EventHandlerRegistrar, HashMap<Class<?>, Method>>> entry: methodMap.entrySet()) {
			final int id = entry.getKey();
			
			for (Entry<EventHandlerRegistrar, HashMap<Class<?>, Method>> entry2: entry.getValue().entrySet()) {
				final EventHandlerRegistrar registrar = entry2.getKey();
				final HashMap<Class<?>, Method> methods = entry2.getValue();
				
				final Object target = registrar.selectEventTarget(viewFinder, id, viewMap);
				registrar.registerEventListener(target, memberContainer, methods);
			}
		}
	}
	
	static String toLowerUnderline(String s) {
		final int STATE_NONE = 0;
		final int STATE_UPPER_CASE = 1;
		final int STATE_LOWER_CASE = 2;
		
		final StringBuilder sb = new StringBuilder();

		int upperStartIndex = -1;
		int state = STATE_NONE;
		
		for (int i = 0; i < s.length(); ++i) {
			final char c = s.charAt(i);
			
			final int newState;
			if (Character.isUpperCase(c)) {
				newState = STATE_UPPER_CASE;
			} else if (Character.isLowerCase(c)) {
				newState = STATE_LOWER_CASE;
			} else {
				if (c == '_') {
					newState = STATE_NONE;
				} else {
					newState = state;
				}
			}
			
			switch (newState) {
			case STATE_UPPER_CASE:
				if (state != STATE_UPPER_CASE) {
					upperStartIndex = i;
					if (state == STATE_LOWER_CASE) {
						sb.append('_');
					}
				}
				break;
				
			case STATE_LOWER_CASE:
				if (state == STATE_UPPER_CASE) {
					if (upperStartIndex < i - 1) {
						for (int j = upperStartIndex; j < i - 1; ++j) {
							sb.append(Character.toLowerCase(s.charAt(j)));
						}
						sb.append('_');
					}
					
					sb.append(Character.toLowerCase(s.charAt(i - 1)))
					  .append(c);
					
					upperStartIndex = -1;
				} else {
					sb.append(c);
				}
				break;
				
			case STATE_NONE:
				sb.append(c);
				break;
			}
			
			state = newState;
		}
		
		if (upperStartIndex >= 0) {
			for (int i = upperStartIndex; i < s.length(); ++i) {
				sb.append(Character.toLowerCase(s.charAt(i)));
			}
		}
		
		return sb.toString();
	}
	
	public void registerExternalHandler(int type, int id, Method method) {
	}
}
