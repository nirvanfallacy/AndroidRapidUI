package rapidui;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import rapidui.adapter.AsyncMethodDataBinder;
import rapidui.adapter.CheckViewBinder;
import rapidui.adapter.DataBinder;
import rapidui.adapter.EnabledViewBinder;
import rapidui.adapter.FieldDataBinder;
import rapidui.adapter.ImageViewBinder;
import rapidui.adapter.MethodDataBinder;
import rapidui.adapter.TextViewBinder;
import rapidui.adapter.ViewBinder;
import rapidui.annotation.AdapterItem;
import rapidui.annotation.adapter.BindToCheck;
import rapidui.annotation.adapter.BindToEnabled;
import rapidui.annotation.adapter.BindToImage;
import rapidui.annotation.adapter.BindToText;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class RapidAdapter extends ArrayAdapter<Object> {
	private class AsyncJob implements Cancelable, Runnable {
		private View v;
		private boolean canceled;
		
		public AsyncJob(View v) {
			this.v = v;
		}
		
		public void cancel() {
			synchronized (this) {
				canceled = true;
			}
			synchronized (asyncJobs) {
				asyncJobs.remove(v);
			}
		}

		@Override
		public boolean isCanceled() {
			synchronized (this) {
				if (canceled) return true;
			}
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
		public LinkedList<DataBinder> dataBinders;
		
		public ViewType(int viewType) {
			this.viewType = viewType;
			this.dataBinders = new LinkedList<DataBinder>();
		}
	}
	
	private static HashMap<Class<?>, ViewBinder> viewBinders;
	private static void ensureViewBinders() {
		if (viewBinders != null) return;
		
		viewBinders = new HashMap<Class<?>, ViewBinder>();
		
		viewBinders.put(BindToText.class, new TextViewBinder());
		viewBinders.put(BindToImage.class, new ImageViewBinder());
		viewBinders.put(BindToCheck.class, new CheckViewBinder());
	}

	private LayoutInflater inflater;

	private HashMap<Class<?>, ViewType> viewTypeMap;
	
	private WeakHashMap<View, AsyncJob> asyncJobs;
	
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
						final ViewBinder binder = viewBinders.get(annotation.annotationType());
						if (binder != null) {
							final int id = binder.getId(annotation);
							if (id == 0) continue;
							
							final FieldDataBinder fd = new FieldDataBinder(field, binder);
							fd.setId(id);
							
							viewType.dataBinders.add(fd);
							break;
						}
					}
				}
				
				for (Method method: cls.getDeclaredMethods()) {
					for (Annotation annotation: method.getAnnotations()) {
						final ViewBinder binder = viewBinders.get(annotation.annotationType());
						if (binder == null) continue;
						
						final int id = binder.getId(annotation);
						if (id == 0) continue;
						
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
							
							if (asyncJobs == null) {
								asyncJobs = new WeakHashMap<View, RapidAdapter.AsyncJob>();
							}
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
						
						break;
					}
				}
			}
			
			for (int j = 0, d = methods.size(); j < d; ++j) {
				final MethodDataBinder md = methods.valueAt(j);
				viewType.dataBinders.add(md);
			}

			viewType.viewType = i + 1;
	
			viewTypeMap.put(clazz, viewType);
		}
	}
	
	private void putAsyncJob(View v, AsyncJob job) {
		synchronized (asyncJobs) {
			asyncJobs.put(v, job);
		}
	}
	
	@Override
	public boolean isEnabled(int position) {
		return true;
	}
}
