package rapidui;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import rapidui.adapter.AsyncMethodDataBinder;
import rapidui.adapter.CheckViewBinder;
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
import rapidui.adapter.TextViewBinder;
import rapidui.adapter.ViewBinder;
import rapidui.annotation.AdapterItem;
import rapidui.annotation.adapter.BindToChecked;
import rapidui.annotation.adapter.BindToEnabled;
import rapidui.annotation.adapter.BindToImage;
import rapidui.annotation.adapter.BindToProgress;
import rapidui.annotation.adapter.BindToProgressMax;
import rapidui.annotation.adapter.BindToRating;
import rapidui.annotation.adapter.BindToRatingNumStars;
import rapidui.annotation.adapter.BindToRatingReadOnly;
import rapidui.annotation.adapter.BindToRatingStepSize;
import rapidui.annotation.adapter.BindToText;
import rapidui.annotation.adapter.OnBindToView;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class RapidAdapter extends ArrayAdapter<Object> {
	private class AsyncJob implements Cancelable, Runnable {
		private View v;
		private AtomicBoolean canceled;
		
		public AsyncJob(View v) {
			this.v = v;
			this.canceled = new AtomicBoolean(false);
		}
		
		public void cancel() {
			canceled.set(true);
			synchronized (asyncJobs) {
				asyncJobs.remove(v);
			}
		}

		@Override
		public boolean isCanceled() {
			if (canceled.get()) return true;
			synchronized (asyncJobs) {
				return !asyncJobs.containsKey(v);
			}
		}

		@Override
		public void run() {
			synchronized (asyncJobs) {
				asyncJobs.remove(v);
			}
		}
	}
	
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
		viewBinders.put(BindToChecked.class, new CheckViewBinder());
		viewBinders.put(BindToEnabled.class, new EnabledViewBinder());
		viewBinders.put(BindToProgress.class, new ProgressViewBinder());
		viewBinders.put(BindToProgressMax.class, new ProgressMaxViewBinder());
		viewBinders.put(BindToRating.class, new RatingViewBinder());
		viewBinders.put(BindToRatingNumStars.class, new RatingNumStarsViewBinder());
		viewBinders.put(BindToRatingReadOnly.class, new RatingReadOnlyViewBinder());
		viewBinders.put(BindToRatingStepSize.class, new RatingStepSizeViewBinder());
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
	WeakHashMap<View, AsyncJob> asyncJobs;
	
	private SparseArray<Method> bindListeners;
	
	private DataBinder enabledBinder;
	
	public RapidAdapter(Context context, Class<?>... classes) {
		super(context, 0);
		init(classes);
	}
	
	public RapidAdapter(Context context, List<Object> list, Class<?>... classes) {
		super(context, 0, list);
		init(classes);
	}
	
	private AsyncJob getAsyncJob(View v) {
		synchronized (asyncJobs) {
			return asyncJobs.get(v);
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
					constBinder.bind(item, v, null, null);
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

			AsyncJob asyncJob = getAsyncJob(v);
			if (asyncJob != null) {
				asyncJob.cancel();
			}
			
			if (dataBinder.isAsync()) {
				asyncJob = new AsyncJob(v);
				putAsyncJob(v, asyncJob);
			} else {
				asyncJob = null;
			}
			
			dataBinder.unbind(v);
			dataBinder.bind(item, v, asyncJob, asyncJob);
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
		asyncJobs = new WeakHashMap<View, RapidAdapter.AsyncJob>();
		
		final SparseArray<MethodDataBinder> methods = new SparseArray<MethodDataBinder>();
		
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
							if (paramTypes.length > 2) {
								continue;
							} else if (paramTypes.length == 2) {
								// Async getter
								
								md = methods.get(id);
								if (md == null) {
									md = new AsyncMethodDataBinder(binder);
								} else if (!(md instanceof AsyncMethodDataBinder)) {
									md = new AsyncMethodDataBinder(md);
								}
								
								md.setGetter(method);
							} else {
								// Sync getter or setter
	
								md = methods.get(id);
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
							}
							
							md.setId(id);
							methods.put(id, md);
						}
					}
				}
			}
			
			for (int j = 0, d = methods.size(); j < d; ++j) {
				final MethodDataBinder md = methods.valueAt(j);
				viewType.dataBinders.add(md);
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
	
	private void putAsyncJob(View v, AsyncJob job) {
		synchronized (asyncJobs) {
			asyncJobs.put(v, job);
		}
	}
}
