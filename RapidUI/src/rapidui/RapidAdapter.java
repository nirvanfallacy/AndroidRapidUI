package rapidui;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;

import rapidui.adapter.AsyncMethodDigger;
import rapidui.adapter.DataDigger;
import rapidui.adapter.FieldDigger;
import rapidui.adapter.ImageBinder;
import rapidui.adapter.MethodDigger;
import rapidui.adapter.TextBinder;
import rapidui.adapter.ViewBinder;
import rapidui.annotation.AdapterItem;
import rapidui.annotation.adapter.BindToImage;
import rapidui.annotation.adapter.BindToText;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class RapidAdapter extends ArrayAdapter<Object> {
	private static HashMap<Class<?>, ViewBinder> binders;
	
	private static void ensureBinders() {
		if (binders != null) return;
		
		binders = new HashMap<Class<?>, ViewBinder>();
		
		binders.put(BindToText.class, new TextBinder());
		binders.put(BindToImage.class, new ImageBinder());
	}
	
	private LayoutInflater inflater;
	private HashMap<Class<?>, ViewType> viewTypeMap;
	
	public RapidAdapter(Context context, Class<?>... classes) {
		super(context, 0);
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		ensureBinders();
		viewTypeMap = new HashMap<Class<?>, ViewType>();
		
		final SparseArray<MethodDigger> methods = new SparseArray<MethodDigger>();
		
		for (int i = 0, c = classes.length; i < c; ++i) {
			final ViewType viewType = new ViewType(i + 1);
			final Class<?> clazz = classes[i];
			
			methods.clear();
			
			for (Class<?> cls = clazz;
					cls != null && !cls.equals(Object.class);
					cls = cls.getSuperclass()) {
				
				if (viewType.layoutId == 0) {
					final AdapterItem ai = cls.getAnnotation(AdapterItem.class);
					if (ai != null) {
						viewType.layoutId = ai.value();
					}
				}
				
				for (Field field: cls.getDeclaredFields()) {
					for (Annotation annotation: field.getAnnotations()) {
						final ViewBinder binder = binders.get(annotation.annotationType());
						if (binder != null) {
							final int id = binder.getId(annotation);
							if (id == 0) continue;
							
							final FieldDigger fd = new FieldDigger(field, binder);
							fd.setId(id);
							
							viewType.diggers.add(fd);
							break;
						}
					}
				}
				
				for (Method method: cls.getDeclaredMethods()) {
					for (Annotation annotation: method.getAnnotations()) {
						final ViewBinder binder = binders.get(annotation.annotationType());
						if (binder == null) continue;
						
						final int id = binder.getId(annotation);
						if (id == 0) continue;
						
						MethodDigger md;

						final Class<?>[] paramTypes = method.getParameterTypes();
						if (paramTypes.length > 2) {
							continue;
						} else if (paramTypes.length == 2) {
							// Async getter
							
							md = methods.get(id);
							if (md == null) {
								md = new AsyncMethodDigger(binder);
							} else if (!(md instanceof AsyncMethodDigger)) {
								md = new AsyncMethodDigger(md);
							}
							
							md.setGetter(method);
						} else {
							// Sync getter or setter

							md = methods.get(id);
							if (md == null) {
								md = new MethodDigger(binder);
							} else if (md instanceof AsyncMethodDigger) {
								md = new MethodDigger(md);
							}
							
							if (paramTypes.length == 1) {
								// Getter
								md.setGetter(method);
							} else {
								// Setter
								md.setSetter(method);
							}
						}
						
						md.setId(id);
						methods.put(id, md);
						
						break;
					}
				}
			}
			
			for (int j = 0, d = methods.size(); j < d; ++j) {
				final MethodDigger md = methods.get(j);
				viewType.diggers.add(md);
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
		final ViewType viewType = viewTypeMap.get(cls);

		return (viewType == null ? 0 : viewType.viewType);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Object item = getItem(position);
		final Class<?> itemClass = item.getClass();
		final ViewType viewType = viewTypeMap.get(itemClass);
		
		if (convertView == null) {
			convertView = inflater.inflate(viewType.layoutId, parent, false);
			
			for (DataDigger digger: viewType.diggers) {
				final int id = digger.getId();
				convertView.setTag(id, convertView.findViewById(id));
			}
		}
		
		for (DataDigger digger: viewType.diggers) {
			final View v = (View) convertView.getTag(digger.getId());
			if (v == null) continue;
			
			digger.dig(item, v, null);
		}
		
		return convertView;
	}
	
	private static class ViewType {
		public int viewType;
		public int layoutId;
		public LinkedList<DataDigger> diggers;
		
		public ViewType(int viewType) {
			this.viewType = viewType;
			this.diggers = new LinkedList<DataDigger>();
		}
	}
}
