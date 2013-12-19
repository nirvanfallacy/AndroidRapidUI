package rapidui;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import rapidui.adapter.AsyncJob;
import rapidui.adapter.AsyncMethodDataBinder;
import rapidui.adapter.AsyncResult;
import rapidui.adapter.CheckedViewBinder;
import rapidui.adapter.CompoundImageViewBinder;
import rapidui.adapter.ConstDataBinder;
import rapidui.adapter.DataBinder;
import rapidui.adapter.EnabledViewBinder;
import rapidui.adapter.FieldDataBinder;
import rapidui.adapter.ImageViewBinder;
import rapidui.adapter.MethodDataBinder;
import rapidui.adapter.ProgressMaxViewBinder;
import rapidui.adapter.ProgressViewBinder;
import rapidui.adapter.RatingNumStarsViewBinder;
import rapidui.adapter.RatingReadOnlyViewBinder;
import rapidui.adapter.RatingStepSizeViewBinder;
import rapidui.adapter.RatingViewBinder;
import rapidui.adapter.StaticConstDataBinder;
import rapidui.adapter.TextOffViewBinder;
import rapidui.adapter.TextOnViewBinder;
import rapidui.adapter.TextViewBinder;
import rapidui.adapter.ViewBinder;
import rapidui.annotation.AdapterItem;
import rapidui.annotation.adapter.BindToBottomImage;
import rapidui.annotation.adapter.BindToChecked;
import rapidui.annotation.adapter.BindToEnabled;
import rapidui.annotation.adapter.BindToImage;
import rapidui.annotation.adapter.BindToLeftImage;
import rapidui.annotation.adapter.BindToProgress;
import rapidui.annotation.adapter.BindToProgressMax;
import rapidui.annotation.adapter.BindToRating;
import rapidui.annotation.adapter.BindToRatingNumStars;
import rapidui.annotation.adapter.BindToRatingReadOnly;
import rapidui.annotation.adapter.BindToRatingStepSize;
import rapidui.annotation.adapter.BindToRightImage;
import rapidui.annotation.adapter.BindToText;
import rapidui.annotation.adapter.BindToTextOff;
import rapidui.annotation.adapter.BindToTextOn;
import rapidui.annotation.adapter.BindToTopImage;
import rapidui.annotation.adapter.OnBindToView;
import rapidui.util.SparseArray2;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class RapidAdapter extends ArrayAdapter<Object> {
	private static class ViewType {
		public int viewType;
		public int layoutId;
		public ArrayList<DataBinder> dataBinders;
		public ArrayList<DataBinder> constBinders;
		
		public ViewType(int viewType) {
			this.viewType = viewType;
			this.dataBinders = new ArrayList<DataBinder>();
		}
	}
	
	private static HashMap<Class<?>, ViewBinder> viewBinders;
	private static DataBinder createFieldBinder(Field field, ViewBinder viewBinder) {
		final int modifiers = field.getModifiers();
		if ((modifiers & Modifier.FINAL) != 0) {
			if ((modifiers & Modifier.STATIC) != 0) {
				return new StaticConstDataBinder(field, viewBinder);
			} else {
				return new ConstDataBinder(field, viewBinder);
			}
		} else {
			return new FieldDataBinder(field, viewBinder);
		}
	}

	private static void ensureViewBinders() {
		if (viewBinders != null) return;
		
		viewBinders = new HashMap<Class<?>, ViewBinder>();
		
		viewBinders.put(BindToText.class, new TextViewBinder());
		viewBinders.put(BindToImage.class, new ImageViewBinder());
		viewBinders.put(BindToChecked.class, new CheckedViewBinder());
		viewBinders.put(BindToEnabled.class, new EnabledViewBinder());
		viewBinders.put(BindToProgress.class, new ProgressViewBinder());
		viewBinders.put(BindToProgressMax.class, new ProgressMaxViewBinder());
		viewBinders.put(BindToRating.class, new RatingViewBinder());
		viewBinders.put(BindToRatingNumStars.class, new RatingNumStarsViewBinder());
		viewBinders.put(BindToRatingReadOnly.class, new RatingReadOnlyViewBinder());
		viewBinders.put(BindToRatingStepSize.class, new RatingStepSizeViewBinder());
		viewBinders.put(BindToTextOn.class, new TextOnViewBinder());
		viewBinders.put(BindToTextOff.class, new TextOffViewBinder());
		viewBinders.put(BindToLeftImage.class, new CompoundImageViewBinder(CompoundImageViewBinder.DIRECTION_LEFT));
		viewBinders.put(BindToTopImage.class, new CompoundImageViewBinder(CompoundImageViewBinder.DIRECTION_TOP));
		viewBinders.put(BindToRightImage.class, new CompoundImageViewBinder(CompoundImageViewBinder.DIRECTION_RIGHT));
		viewBinders.put(BindToBottomImage.class, new CompoundImageViewBinder(CompoundImageViewBinder.DIRECTION_BOTTOM));
	}

	private static boolean hasZeroId(int[] ids) {
		for (int id: ids) {
			if (id == 0) {
				return true;
			}
		}
		return false;
	}
	
	private LayoutInflater inflater;
	private HashMap<Class<?>, ViewType> viewTypeMap;
	private WeakHashMap<View, AsyncJob> asyncJobs;
	
	private SparseArray<Method> bindListeners;
	
	private DataBinder enabledBinder;
	
	public RapidAdapter(Context context, Class<?>... classes) {
		super(context, 0);
		init(classes);
	}
	
	@SuppressWarnings("unchecked")
	public RapidAdapter(Context context, List<?> list, Class<?>... classes) {
		super(context, 0, (List<Object>) list);
		init(classes);
	}
	
	private AsyncJob pullAsyncJob(View v) {
		synchronized (asyncJobs) {
			return asyncJobs.remove(v);
		}
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
			
			for (DataBinder dataBinder: viewType.dataBinders) {
				final int id = dataBinder.getId();
				if (id == 0) continue;
				
				convertView.setTag(id, convertView.findViewById(id));
			}
			
			if (viewType.constBinders != null) {
				for (DataBinder constBinder: viewType.constBinders) {
					final int id = constBinder.getId();
					
					final View v = (id == 0 ? convertView : convertView.findViewById(id));
					constBinder.bind(item, v, null);
				}
			}
		}
		
		for (DataBinder dataBinder: viewType.dataBinders) {
			final View v;
			
			final int id = dataBinder.getId();
			if (id == 0) {
				v = convertView;
			} else {
				v = (View) convertView.getTag(id);
				if (v == null) continue;
			}

			AsyncJob asyncJob = pullAsyncJob(v);
			if (asyncJob != null) {
				asyncJob.cancel();
			}
			
			if (dataBinder.isAsync()) {
				asyncJob = new AsyncJob(v, asyncJobs);
			} else {
				asyncJob = null;
			}
			
			dataBinder.unbind(v);
			dataBinder.bind(item, v, asyncJob);
		}
		
		if (bindListeners != null) {
			for (int i = 0, c = bindListeners.size(); i < c; ++i) {
				final int id = bindListeners.keyAt(i);
				final Method method = bindListeners.valueAt(i);
				
				final View v = (id == 0 ? convertView : (View) convertView.getTag(id));
				if (v == null) continue;
				
				try {
					method.invoke(item, v);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		
		return convertView;
	}
	
	@Override
	public int getViewTypeCount() {
		return viewTypeMap.size();
	}
	
	private void init(Class<?>[] classes) {
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		ensureViewBinders();
		
		viewTypeMap = new HashMap<Class<?>, ViewType>();
		asyncJobs = new WeakHashMap<View, AsyncJob>();
		
		// [id][annotationType][methodBinder]
		final SparseArray2<Class<?>, MethodDataBinder> methods = SparseArray2.create();
		
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
						if (annotation instanceof BindToEnabled) {
							final int[] ids = ((BindToEnabled) annotation).value();
							if (ids.length == 0 || hasZeroId(ids)) {
								enabledBinder = new FieldDataBinder(field, null);
							}
						}
						
						final ViewBinder vb = viewBinders.get(annotation.annotationType());
						if (vb == null) continue;
						
						final int[] ids = vb.getIds(annotation);
						if (ids == null) continue;
						
						for (int id: ids) {
							final DataBinder db = createFieldBinder(field, vb);
							db.setId(id);
							
							final Class<?> dbClass = db.getClass();
							boolean isStaticConst = false;
							
							if (dbClass.equals(ConstDataBinder.class) ||
									(isStaticConst = dbClass.equals(StaticConstDataBinder.class))) {
								
								if (viewType.constBinders == null) {
									viewType.constBinders = new ArrayList<DataBinder>();
								}
								
								if (isStaticConst) {
									viewType.constBinders.add((StaticConstDataBinder) db);
								} else {
									viewType.constBinders.add((ConstDataBinder) db);
								}
							} else {
								viewType.dataBinders.add(db);
							}
						}
					}
				}
				
				for (Method method: cls.getDeclaredMethods()) {
					for (Annotation annotation: method.getAnnotations()) {
						if (annotation instanceof OnBindToView) {
							if (bindListeners == null) {
								bindListeners = new SparseArray<Method>();
							}
							
							final int[] ids = ((OnBindToView) annotation).value();
							for (int id: ids) {
								bindListeners.put(id, method);
							}
							continue;
						}
						
						if (annotation instanceof BindToEnabled) {
							final int[] ids = ((BindToEnabled) annotation).value();
							if (ids.length == 0 || hasZeroId(ids)) {
								final MethodDataBinder mdb = new MethodDataBinder((ViewBinder) null);
								mdb.setGetter(method);
								
								enabledBinder = mdb;
							}
						}
						
						final ViewBinder binder = viewBinders.get(annotation.annotationType());
						if (binder == null) continue;
						
						final int[] ids = binder.getIds(annotation);
						if (ids == null) continue;
						
						for (int id: ids) {
							MethodDataBinder md;
	
							final Class<?>[] paramTypes = method.getParameterTypes();
							if (paramTypes.length > 0 && AsyncResult.class.isAssignableFrom(paramTypes[0])) {
								// Async getter
								
								md = methods.get(id, annotation.annotationType());
								if (md == null) {
									md = new AsyncMethodDataBinder(binder);
								} else if (!(md instanceof AsyncMethodDataBinder)) {
									md = new AsyncMethodDataBinder(md);
								}
								
								md.setGetter(method);
							} else if (paramTypes.length <= 1) {
								// Sync getter or setter
								
								md = methods.get(id, annotation.annotationType());
								if (md == null) {
									md = new MethodDataBinder(binder);
								} else if (md instanceof AsyncMethodDataBinder) {
									md = new MethodDataBinder(md);
								}

								if (paramTypes.length == 1) {
									// Setter
									md.setSetter(method);
								} else {
									// Getter
									md.setGetter(method);
								}
							} else {
								continue;
							}
							
							md.setId(id);
							methods.put(id, annotation.annotationType(), md);
						}
					}
				}
			}
			
			for (Entry<Integer, HashMap<Class<?>, MethodDataBinder>> entry: methods) {
				for (MethodDataBinder md: entry.getValue().values()) {
					viewType.dataBinders.add(md);
				}
			}

			viewType.viewType = i + 1;
			
			viewType.dataBinders.trimToSize();
			if (viewType.constBinders != null) {
				viewType.constBinders.trimToSize();
			}
	
			viewTypeMap.put(clazz, viewType);
		}
	}
	
	@Override
	public boolean isEnabled(int position) {
		if (enabledBinder == null) {
			return true;
		} else {
			try {
				return (Boolean) enabledBinder.getValue(getItem(position));
			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(int position) {
		return (T) getItem(position);
	}
}
