package rapidui;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;

import rapidui.adapter.DataDigger;
import rapidui.adapter.FieldDigger;
import rapidui.adapter.TextBinder;
import rapidui.adapter.ViewBinder;
import rapidui.annotation.AdapterItem;
import rapidui.annotation.adapter.BindToText;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

public class RapidAdapter extends ArrayAdapter<Object> {
	private static HashMap<Class<?>, ViewBinder> binders;
	
	private static void ensureBinders() {
		if (binders != null) return;
		
		binders = new HashMap<Class<?>, ViewBinder>();
		
		binders.put(BindToText.class, new TextBinder());
	}
	
	private HashMap<Class<?>, ViewType> viewTypeMap;
	
	public RapidAdapter(Context context, Class<?>... classes) {
		super(context, 0);
		
		ensureBinders();

		viewTypeMap = new HashMap<Class<?>, ViewType>();
		
		for (int i = 0, c = classes.length; i < c; ++i) {
			final ViewType viewType = new ViewType(i + 1);
			
			final Class<?> clazz = classes[i];
			
			for (Class<?> cls = classes[i];
					cls != null && !cls.equals(Object.class);
					cls = cls.getSuperclass()) {
				
				if (viewType.id == 0) {
					final AdapterItem ai = cls.getAnnotation(AdapterItem.class);
					if (ai != null) {
						viewType.id = ai.value();
					}
				}
				
				for (Field field: cls.getDeclaredFields()) {
					for (Annotation annotation: field.getAnnotations()) {
						final ViewBinder binder = binders.get(annotation.annotationType());
						if (binder != null) {
							final FieldDigger fd = new FieldDigger(field, binder);
							viewType.diggers.add(fd);
							break;
						}
					}
				}
			}
			
			viewType.viewType = i + 1;
			
			viewTypeMap.put(clazz, viewType);
		}
	}
	
	@Override
	public int getViewTypeCount() {
		return viewTypeMap.size();
	}
	
	@Override
	public int getItemViewType(int position) {
		final Object item = getItem(position);
		if (item == null) return 0;
		
		final Class<?> cls = item.getClass();
		final Integer type = viewTypeMap.get(cls);

		return (type == null ? 0 : type);
	}
	
	private static class ViewType {
		public int viewType;
		public int id;
		public LinkedList<DataDigger> diggers;
		
		public ViewType(int viewType) {
			this.viewType = viewType;
			this.diggers = new LinkedList<DataDigger>();
		}
	}
}
