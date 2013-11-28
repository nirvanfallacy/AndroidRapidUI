package rapidui;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import rapidui.annotation.BindService;
import rapidui.annotation.EventHandler;
import rapidui.annotation.Extra;
import rapidui.annotation.InstanceState;
import rapidui.annotation.LayoutElement;
import rapidui.annotation.Lifecycle;
import rapidui.annotation.Receiver;
import rapidui.annotation.Resource;
import rapidui.annotation.ResourceType;
import rapidui.annotation.SystemService;
import rapidui.annotation.event.On;
import rapidui.annotation.event.OnAfterTextChanged;
import rapidui.annotation.event.OnBeforeTextChanged;
import rapidui.annotation.event.OnCheckedChange;
import rapidui.annotation.event.OnClick;
import rapidui.annotation.event.OnCreateContextMenu;
import rapidui.annotation.event.OnDrag;
import rapidui.annotation.event.OnFocusChange;
import rapidui.annotation.event.OnKey;
import rapidui.annotation.event.OnLongClick;
import rapidui.annotation.event.OnMenuItemClick;
import rapidui.annotation.event.OnServiceConnect;
import rapidui.annotation.event.OnServiceDisconnect;
import rapidui.annotation.event.OnTextChanged;
import rapidui.annotation.event.OnTouch;
import rapidui.event.CustomEventInfo;
import rapidui.event.CustomEventRegistrar;
import rapidui.event.ExternalEventInfo;
import rapidui.event.OnCheckedChangeRegistrar;
import rapidui.event.OnClickRegistrar;
import rapidui.event.OnCreateContextMenuRegistrar;
import rapidui.event.OnDragRegistrar;
import rapidui.event.OnFocusChangeRegistrar;
import rapidui.event.OnKeyRegistrar;
import rapidui.event.OnLongClickRegistrar;
import rapidui.event.OnMenuItemClickExternalEvent;
import rapidui.event.OnServiceConnectExternalEvent;
import rapidui.event.OnServiceDisconnectExternalEvent;
import rapidui.event.OnTouchRegistrar;
import rapidui.event.SimpleEventRegistrar;
import rapidui.event.TextWatcherRegistrar;
import rapidui.event.UnregisterableCustomEventInfo;
import rapidui.event.UnregisterableEventRegistrar;
import rapidui.resource.AnimationLoader;
import rapidui.resource.AnimatorLoader;
import rapidui.resource.ColorLoader;
import rapidui.resource.DimensionLoader;
import rapidui.resource.DrawableLoader;
import rapidui.resource.IntegerLoader;
import rapidui.resource.ResourceLoader;
import rapidui.resource.StringLoader;
import rapidui.util.KeyValueEntry;
import rapidui.util.SparseArray3;
import rapidui.util.SparseArray4;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.DownloadManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.hardware.input.InputManager;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.net.ConnectivityManager;
import android.net.nsd.NsdManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.UserManager;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.print.PrintManager;
import android.telephony.TelephonyManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.TextServicesManager;

public abstract class Extension {
	private class EventInjector {
		// [viewId][registrar][annotation]: method
		private SparseArray3<SimpleEventRegistrar, Class<?>, Method> eventMap;

		// [viewId][registrar][lifecycle][annotation]: method
		private SparseArray4<UnregisterableEventRegistrar, Lifecycle, Class<?>, Method> unregEventMap;
		
		// [viewId][eventCategoryName][lifecycle][eventName]: method
		private SparseArray4<String, Lifecycle, String, Method> customEventMap;

		private void addCustomEvent(int id, String category, Lifecycle lifecycle, String name, Method method) {
			if (customEventMap == null) {
				customEventMap = new SparseArray4<String, Lifecycle, String, Method>();
			}
			customEventMap.put(id, category, lifecycle, name, method);
		}

		private void addEvent(int id, SimpleEventRegistrar registrar, Class<?> annotationType, Method method) {
			if (eventMap == null) {
				eventMap = SparseArray3.create();
			}
			eventMap.put(id, registrar, annotationType, method);
		}

		private void addUnregEvent(int id, UnregisterableEventRegistrar registrar, Lifecycle lifecycle, Class<?> annotationType,
				Method method) {
			
			if (unregEventMap == null) {
				unregEventMap = SparseArray4.create();
			}
			unregEventMap.put(id, registrar, lifecycle, annotationType, method);
		}

		public boolean categorizeEventAuto(Method method, Annotation annotation) {
			final String name = method.getName();
			final int underscoreIndex = name.indexOf('_');
			
			if (underscoreIndex < 0) return false;
			
			final String idName = name.substring(0, underscoreIndex);
			final String annotationName = name.substring(underscoreIndex + 1);
			
			if (idName.length() == 0 || annotationName.length() == 0) return false;
			
			initAnnotationNameMatchList();
			
			final Class<?> annotationType2 = annotationNameMatch.get(annotationName);
			if (annotationType2 != null) {
				// If it is an external event handler
				
				final ExternalEventInfo info = externalEvents.get(annotationType2);
				if (info != null) {
					final Object id = info.parseId(activity, idName);
					if (id != null) {
						registerExternalEvent(info.getType(), id, method);
						return true;
					} else {
						return false;
					}
				}
				
				// If it is a view event handler
				
				final SimpleEventRegistrar registrar = simpleEventRegistrars.get(annotationType2);
				if (registrar != null) {
					final int id = ResourceUtils.findResourceId(activity, idName, "id");
					if (id != 0) {
						if (registrar instanceof UnregisterableEventRegistrar) {
							final UnregisterableEventRegistrar registrar2 =
									(UnregisterableEventRegistrar) registrar;
							final Lifecycle lifecycle = registrar2.getLifecycle(annotation);
							
							addUnregEvent(id, registrar2, lifecycle, annotationType2, method);
						} else {
							addEvent(id, registrar, annotationType2, method);
						}
						return true;
					} else {
						return false;
					}
				}
			}
			
			// Custom event
			
			final String eventCategory;
			final String eventName;
			
			final int underscoreIndex2 = annotationName.indexOf('_');
			
			if (underscoreIndex2 >= 0) {
				eventCategory = annotationName.substring(0, underscoreIndex2);
				eventName = annotationName.substring(underscoreIndex2 + 1);
			} else {
				eventCategory = annotationName;
				eventName = "";
			}
			
			final int id = ResourceUtils.findResourceId(activity, idName, "id");
			if (id != 0) {
				final EventHandler eventHandler = (EventHandler) annotation;
				addCustomEvent(id, eventCategory, eventHandler.lifecycle(), eventName, method);
				
				return true;
			} else {
				return false;
			}
		}

		public void categorizeCustomEvent(Method method, On on) {
			final String event = on.event();
			final Lifecycle lifecycle = on.lifecycle();
			
			final String category, name;

			final int dotIndex = event.indexOf('.');
			if (dotIndex >= 0) {
				category = event.substring(0, dotIndex);
				name = event.substring(dotIndex + 1);
			} else {
				category = event;
				name = "";
			}
			
			for (int id: on.id()) {
				addCustomEvent(id, category, lifecycle, name, method);
			}
		}
		
		public void categorizeSimpleEvent(SimpleEventRegistrar registrar, Annotation annotation, Method method) {
			final Class<?> annotationType = annotation.annotationType();
			
			if (registrar instanceof UnregisterableEventRegistrar) {
				final UnregisterableEventRegistrar registrar2 =
						(UnregisterableEventRegistrar) registrar;
				final Lifecycle lifecycle = registrar2.getLifecycle(annotation);
				
				for (int id: registrar.getTargetIds(annotation)) {
					addUnregEvent(id, registrar2, lifecycle, annotationType, method);
				}
			} else {
				for (int id: registrar.getTargetIds(annotation)) {
					addEvent(id, registrar, annotationType, method);
				}
			}
		}
		
		public void injectCustomEvents(SparseArray<View> viewMap) {
			if (customEventMap == null) return;
			
			for (Entry<Integer, HashMap<String, HashMap<Lifecycle, HashMap<String, Method>>>> entry: customEventMap) {
				final int id = entry.getKey();
				final Object target = findViewById(id, viewMap);
				
				for (Entry<String, HashMap<Lifecycle, HashMap<String, Method>>> entry2: entry.getValue().entrySet()) {
					injectCustomEvent(target, entry2);
				}
			}
		}
		
		public void injectSimpleEvents(SparseArray<View> viewMap) {
			if (eventMap == null) return;
			for (Entry<Integer, HashMap<SimpleEventRegistrar, HashMap<Class<?>, Method>>> entry: eventMap) {
				final int id = entry.getKey();
				final Object target = findViewById(id, viewMap);
				
				for (Entry<SimpleEventRegistrar, HashMap<Class<?>, Method>> entry2: entry.getValue().entrySet()) {
					final SimpleEventRegistrar registrar = entry2.getKey();
					final HashMap<Class<?>, Method> methods = entry2.getValue();
					
					final Object dispatcher = registrar.createEventDispatcher(memberContainer, methods);
					registrar.registerEventListener(target, dispatcher);
				}
			}
		}
		
		public void injectUnregisterableEvents(SparseArray<View> viewMap) {
			if (unregEventMap == null) return;
				
			if (unregEvents == null) {
				unregEvents = new HashMap<Lifecycle, LinkedList<UnregisterableEventHandler>>();
			}
			
			for (Entry<Integer,
					   HashMap<UnregisterableEventRegistrar,
					           HashMap<Lifecycle, HashMap<Class<?>, Method>>
			          >
			     > entry: unregEventMap) {
				
				final int id = entry.getKey();
				final Object target = findViewById(id, viewMap);
				
				for (Entry<UnregisterableEventRegistrar, HashMap<Lifecycle, HashMap<Class<?>, Method>>> entry2: entry.getValue().entrySet()) {
					final UnregisterableEventRegistrar registrar = entry2.getKey();
					
					for (Entry<Lifecycle, HashMap<Class<?>, Method>> entry3: entry2.getValue().entrySet()) {
						final Lifecycle lifecycle = entry3.getKey();
						final HashMap<Class<?>, Method> methods = entry3.getValue();
						
						final Object dispatcher = registrar.createEventDispatcher(memberContainer, methods);
						
						registerUnregisterableEvent(lifecycle, registrar, target, dispatcher);
					}
				}
			}
		}
		
		private void injectCustomEvent(Object target,
				Entry<String, HashMap<Lifecycle, HashMap<String, Method>>> entry2) {
			
			final String category = entry2.getKey();
			final CustomEventInfo info = CustomEventInfo.create(target, category);
			
			if (info == null) return;
			
			if (info instanceof UnregisterableCustomEventInfo) {
				final UnregisterableCustomEventInfo unregInfo =
						(UnregisterableCustomEventInfo) info;
			
				final Method adder = unregInfo.getAdder();
				final Method remover = unregInfo.getRemover();
				final CustomEventRegistrar registrar = new CustomEventRegistrar(adder, remover);
				
				for (Entry<Lifecycle, HashMap<String, Method>> entry3: entry2.getValue().entrySet()) {
					final HashMap<String, Method> methods = entry3.getValue();
					final Object proxy = info.createProxy(memberContainer, methods);
					
					if (proxy == null) continue;
					
					final Lifecycle lifecycle = entry3.getKey();
					registerUnregisterableEvent(lifecycle, registrar, target, proxy);
				}
			} else {
				HashMap<String, Method> methods = null;
				for (Entry<Lifecycle, HashMap<String, Method>> entry3: entry2.getValue().entrySet()) {
					if (methods == null) {
						methods = entry3.getValue();
					} else {
						methods.putAll(entry3.getValue());
					}
				}						
				
				if (methods != null) {
					final Object proxy = info.createProxy(memberContainer, methods);
					if (proxy != null) {
						final Method setter = info.getAdder();
						try {
							setter.invoke(target, proxy);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public static final int EXTERNAL_EVENT_MENU_ITEM_CLICK = 0;
	public static final int EXTERNAL_EVENT_SERVICE_CONNECT = 1;
	public static final int EXTERNAL_EVENT_SERVICE_DISCONNECT = 2;
	
	private static HashMap<Class<?>, SimpleEventRegistrar> simpleEventRegistrars =
			new HashMap<Class<?>, SimpleEventRegistrar>();
	private static HashMap<Class<?>, ExternalEventInfo> externalEvents =
			new HashMap<Class<?>, ExternalEventInfo>();
	private static HashMap<String, Class<?>> annotationNameMatch;
	private static HashMap<Class<?>, String> systemServices;
	
	private static LinkedList<ResourceLoader> resourceLoaders;
	
	static {
		simpleEventRegistrars.put(OnClick.class, new OnClickRegistrar());
		simpleEventRegistrars.put(OnCreateContextMenu.class, new OnCreateContextMenuRegistrar());
		simpleEventRegistrars.put(OnDrag.class, new OnDragRegistrar());
		simpleEventRegistrars.put(OnFocusChange.class, new OnFocusChangeRegistrar());
		simpleEventRegistrars.put(OnKey.class, new OnKeyRegistrar());
		simpleEventRegistrars.put(OnLongClick.class, new OnLongClickRegistrar());
		simpleEventRegistrars.put(OnTouch.class, new OnTouchRegistrar());
		simpleEventRegistrars.put(OnCheckedChange.class, new OnCheckedChangeRegistrar());
		
		final TextWatcherRegistrar textWatcherRegistrar = new TextWatcherRegistrar();
		simpleEventRegistrars.put(OnTextChanged.class, textWatcherRegistrar);
		simpleEventRegistrars.put(OnBeforeTextChanged.class, textWatcherRegistrar);
		simpleEventRegistrars.put(OnAfterTextChanged.class, textWatcherRegistrar);
		
		externalEvents.put(OnMenuItemClick.class, new OnMenuItemClickExternalEvent());
		externalEvents.put(OnServiceConnect.class, new OnServiceConnectExternalEvent());
		externalEvents.put(OnServiceDisconnect.class, new OnServiceDisconnectExternalEvent());
	}
	
	private static void initAnnotationNameMatchList() {
		if (annotationNameMatch != null) return;

		annotationNameMatch = new HashMap<String, Class<?>>();
		
		for (Class<?> cls: simpleEventRegistrars.keySet()) {
			final String name = cls.getSimpleName().substring(2);
			annotationNameMatch.put(name, cls);
		}
		for (Class<?> cls: externalEvents.keySet()) {
			final String name = cls.getSimpleName().substring(2);
			annotationNameMatch.put(name, cls);
		}
	}
	
	private static void initResourceLoaders() {
		if (resourceLoaders != null) return;
		
		resourceLoaders = new LinkedList<ResourceLoader>();
		
		resourceLoaders.add(new DrawableLoader());
		resourceLoaders.add(new AnimationLoader());
		resourceLoaders.add(new AnimatorLoader());
		resourceLoaders.add(new ColorLoader());
		resourceLoaders.add(new DimensionLoader());
		resourceLoaders.add(new IntegerLoader());
		resourceLoaders.add(new StringLoader());
	}

	private static void initSystemServiceList() {
		if (systemServices != null) return;
		
		systemServices = new HashMap<Class<?>, String>();
		
		final int version = Build.VERSION.SDK_INT;
		systemServices.put(ActivityManager.class, Context.ACTIVITY_SERVICE);
		systemServices.put(AlarmManager.class, Context.ALARM_SERVICE);
		systemServices.put(AudioManager.class, Context.AUDIO_SERVICE);
		systemServices.put(ClipboardManager.class, Context.CLIPBOARD_SERVICE);
		systemServices.put(ConnectivityManager.class, Context.CONNECTIVITY_SERVICE);
		systemServices.put(KeyguardManager.class, Context.KEYGUARD_SERVICE);
		systemServices.put(LayoutInflater.class, Context.LAYOUT_INFLATER_SERVICE);
		systemServices.put(LocationManager.class, Context.LOCATION_SERVICE);
		systemServices.put(NotificationManager.class, Context.NOTIFICATION_SERVICE);
		systemServices.put(PowerManager.class, Context.POWER_SERVICE);
		systemServices.put(SearchManager.class, Context.SEARCH_SERVICE);
		systemServices.put(SensorManager.class, Context.SENSOR_SERVICE);
		systemServices.put(TelephonyManager.class, Context.TELEPHONY_SERVICE);
		systemServices.put(Vibrator.class, Context.VIBRATOR_SERVICE);
		systemServices.put(WallpaperManager.class, Context.WALLPAPER_SERVICE);
		systemServices.put(WifiManager.class, Context.WIFI_SERVICE);
		systemServices.put(WindowManager.class, Context.WINDOW_SERVICE);
		if (version >= 3) {
			systemServices.put(InputMethodManager.class, Context.INPUT_METHOD_SERVICE);
		}
		if (version >= 4) {
			systemServices.put(AccessibilityManager.class, Context.ACCESSIBILITY_SERVICE);
		}
		if (version >= 5) {
			systemServices.put(AccountManager.class, Context.ACCOUNT_SERVICE);
		}
		if (version >= 8) {
			systemServices.put(DevicePolicyManager.class, Context.DEVICE_POLICY_SERVICE);
			systemServices.put(DropBoxManager.class, Context.DROPBOX_SERVICE);
			systemServices.put(UiModeManager.class, Context.UI_MODE_SERVICE);
		}
		if (version >= 9) {
			systemServices.put(DownloadManager.class, Context.DOWNLOAD_SERVICE);
			systemServices.put(StorageManager.class, Context.STORAGE_SERVICE);
		}
		if (version >= 10) {
			systemServices.put(NfcManager.class, Context.NFC_SERVICE);
		}
		if (version >= 12) {
			systemServices.put(UsbManager.class, Context.USB_SERVICE);
		}
		if (version >= 14) {
			systemServices.put(TextServicesManager.class, Context.TEXT_SERVICES_MANAGER_SERVICE);
			systemServices.put(WifiP2pManager.class, Context.WIFI_P2P_SERVICE);
		}
		if (version >= 16) {
			systemServices.put(InputManager.class, Context.INPUT_SERVICE);
			systemServices.put(MediaRouter.class, Context.MEDIA_ROUTER_SERVICE);
			systemServices.put(NsdManager.class, Context.NSD_SERVICE);
		}
		if (version >= 17) {
			systemServices.put(DisplayManager.class, Context.DISPLAY_SERVICE);
			systemServices.put(UserManager.class, Context.USER_SERVICE);
		}
		if (version >= 19) {
			systemServices.put(AppOpsManager.class, Context.APP_OPS_SERVICE);
//			systemServices.put(CaptioningManager.class, Context.CAPTIONING_SERVICE);
//			systemServices.put(ConsumerIrManager.class, Context.CONSUMER_IR_SERVICE);
			systemServices.put(PrintManager.class, Context.PRINT_SERVICE);
		}
	}
	
	private static boolean isRapidClass(Class<?> cls) {
		return cls.equals(RapidActivity.class);
	}
	
	@SuppressWarnings("unchecked")
	private static void putIntoBundle(Bundle bundle, String key, Object value) {
		if (value instanceof String) {
			bundle.putString(key, (String) value);
		} else if (value instanceof Integer) {
			bundle.putInt(key, (Integer) value);
		} else if (value instanceof Long) {
			bundle.putLong(key, (Long) value);
		} else if (value instanceof Float) {
			bundle.putFloat(key, (Float) value);
		} else if (value instanceof Double) {
			bundle.putDouble(key, (Double) value);
		} else if (value instanceof Byte) {
			bundle.putByte(key, (Byte) value);
		} else if (value instanceof Short) {
			bundle.putShort(key, (Short) value);
		} else if (Build.VERSION.SDK_INT >= 18 && value instanceof IBinder) {
			bundle.putBinder(key, (IBinder) value);
		} else if (value instanceof Boolean) {
			bundle.putBoolean(key, (Boolean) value);
		} else if (value instanceof boolean[]) {
			bundle.putBooleanArray(key, (boolean[]) value);
		} else if (value instanceof Bundle) {
			bundle.putBundle(key, (Bundle) value);
		} else if (value instanceof byte[]) {
			bundle.putByteArray(key, (byte[]) value);
		} else if (value instanceof Character) {
			bundle.putChar(key, (Character) value);
		} else if (value instanceof char[]) {
			bundle.putCharArray(key, (char[]) value);
		} else if (value instanceof String[]) {
			bundle.putStringArray(key, (String[]) value);
		} else if (value instanceof CharSequence) {
			bundle.putCharSequence(key, (CharSequence) value);
		} else if (value instanceof CharSequence[]) {
			bundle.putCharSequenceArray(key, (CharSequence[]) value);
		} else if (value instanceof ArrayList<?>) {
			final ArrayList<?> list = (ArrayList<?>) value;
			
			for (int i = 0, size = list.size(); i < size; ++i) {
				final Object item = list.get(i);
				if (item == null) continue;
				
				if (item instanceof String) {
					bundle.putStringArrayList(key, (ArrayList<String>) list);
				} else if (item instanceof CharSequence) {
					bundle.putCharSequenceArrayList(key, (ArrayList<CharSequence>) list);
				} else if (item instanceof Integer) {
					bundle.putIntegerArrayList(key, (ArrayList<Integer>) list);
				} else if (item instanceof Parcelable) {
					bundle.putParcelableArrayList(key, (ArrayList<Parcelable>) list);
				}
				break;
			}
		} else if (value instanceof double[]) {
			bundle.putDoubleArray(key, (double[]) value);
		} else if (value instanceof float[]) {
			bundle.putFloatArray(key, (float[]) value);
		} else if (value instanceof int[]) {
			bundle.putIntArray(key, (int[]) value);
		} else if (value instanceof long[]) {
			bundle.putLongArray(key, (long[]) value);
		} else if (value instanceof Parcelable) {
			bundle.putParcelable(key, (Parcelable) value);
		} else if (value instanceof Parcelable[]) {
			bundle.putParcelableArray(key, (Parcelable[]) value);
		} else if (value instanceof Serializable) {
			bundle.putSerializable(key, (Serializable) value);
		} else if (value instanceof short[]) {
			bundle.putShortArray(key, (short[]) value);
		} else if (value instanceof SparseArray<?>) {
			final SparseArray<?> map = (SparseArray<?>) value;

			for (int i = 0, size = map.size(); i < size; ++i) {
				final Object value2 = map.valueAt(i);
				if (value2 == null) continue;
				
				if (value2 instanceof Parcelable) {
					bundle.putSparseParcelableArray(key, (SparseArray<Parcelable>) value2);
				}
				break;
			}
		}
	}
	
	protected Activity activity;
	protected Object memberContainer;
	protected ViewFinder viewFinder;
	
	private Lifecycle currentLifecycle;

	private HashMap<Lifecycle, LinkedList<KeyValueEntry<IntentFilter, BroadcastReceiver>>> receivers;
	private HashMap<Lifecycle, LinkedList<UnregisterableEventHandler>> unregEvents;
	
	private LinkedList<ServiceConnection> serviceConnections;
	private HashMap<String, ServiceCallback> serviceCallbacks;

	public Extension(Activity activity, Object memberContainer, ViewFinder viewFinder) {
		this.activity = activity;
		this.memberContainer = memberContainer;
		this.viewFinder = viewFinder;
	}

	private View findViewById(int id, SparseArray<View> viewMap) {
		View v = viewMap.get(id);
		if (v == null) {
			v = viewFinder.findViewById(id);
			viewMap.put(id, v);
		}
		
		return v;
	}
	
	public Lifecycle getCurrentLifecycle() {
		return currentLifecycle;
	}

	public void injectCommonThings() {
		final Intent intent = activity.getIntent();
		final Bundle extras = (intent == null ? null : intent.getExtras());
		
		Class<?> cls = memberContainer.getClass();
		
		while (cls != null && !isRapidClass(cls)) {
			// Fields
			
			for (Field field: cls.getDeclaredFields()) {
				final int modifier = field.getModifiers();
				if ((modifier & Modifier.STATIC) != 0) continue;
				
				// @SystemService
				if (field.isAnnotationPresent(SystemService.class)) {
					injectSystemService(field);
				}
				
				// @Extra
				if (extras != null) {
					final Extra extra = field.getAnnotation(Extra.class);
					if (extra != null) {
						injectExtra(field, extra, extras);
					}
				}
				
				// @Resource
				final Resource resource = field.getAnnotation(Resource.class);
				if (resource != null) {
					injectResource(field, resource);
				}
				
				// @BindService
				final BindService bindService = field.getAnnotation(BindService.class);
				if (bindService != null) {
					injectBindService(field, bindService);
				}
			}
			
			// Methods
			
			for (final Method method: cls.getDeclaredMethods()) {
				final int modifier = method.getModifiers();
				if ((modifier & Modifier.STATIC) != 0) continue;

				for (Annotation annotation: method.getAnnotations()) {
					if (annotation instanceof Receiver) {
						final Receiver receiver = (Receiver) annotation;
						if (receiver != null) {
							// @Receiver
							injectReceiver(method, receiver);
						}
						continue;
					}
					
					final Class<?> annotationType = annotation.annotationType();

					final ExternalEventInfo info = externalEvents.get(annotationType);
					if (info != null) {
						final Iterable<?> ids = info.getTargetIds(annotation);
						if (ids != null) {
							final int type = info.getType();
							for (Object id: ids) {
								registerExternalEvent(type, id, method);
							}
						}
						continue;
					}
				}
			}
			
			cls = cls.getSuperclass();
		}
	}
	
	private void injectBindService(final Field field, BindService bindService) {
		// Get stub class
		
		Class<?> stubClass = null;
		for (Class<?> cls: field.getType().getDeclaredClasses()) {
			if (cls.getSimpleName().equals("Stub")) {
				stubClass = cls;
				break;
			}
		}
		
		if (stubClass == null) return;
		
		// Find asInterface() method
		
		final Method asInterface;
		try {
			asInterface = stubClass.getDeclaredMethod("asInterface", IBinder.class);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			return;
		}
		
		final Intent intent = new Intent();
		
		final String action = bindService.action();
		if (action.length() > 0) {
			intent.setAction(action);
		}

		final String alias = bindService.alias();
		final String packageName = bindService.packageName();
		final String className = bindService.className();
		final Class<?> classType = bindService.classType();
		
		if (packageName.length() == 0) {
			if (className.length() == 0) {
				if (classType != null) {
					intent.setClass(activity, classType);
				}
			} else {
				intent.setClassName(activity, className);
			}
		} else {
			if (className.length() != 0) {
				intent.setClassName(packageName, className);
			}
		}
		
		int flags = 0;
		if (bindService.autoCreate()) {
			flags |= Context.BIND_AUTO_CREATE;
		}
		
		final ServiceConnection conn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				if (serviceConnections != null) {
					serviceConnections.remove(this);
				}
				
				if (serviceCallbacks != null) {
					final ServiceCallback callback = serviceCallbacks.get(alias);
					if (callback != null && callback.onDisconnect != null) {
						try {
							callback.onDisconnect.setAccessible(true);
							callback.onDisconnect.invoke(memberContainer, alias);
						} catch (IllegalAccessException e1) {
							e1.printStackTrace();
						} catch (IllegalArgumentException e1) {
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							e1.printStackTrace();
						}
					}

					serviceCallbacks.remove(alias);
				}

				try {
					field.setAccessible(true);
					field.set(memberContainer, null);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Object obj = null;
				try {
					obj = asInterface.invoke(null, service);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				
				if (obj != null) {
					try {
						field.setAccessible(true);
						field.set(memberContainer, obj);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}

					if (serviceCallbacks != null) {
						final ServiceCallback callback = serviceCallbacks.get(alias);
						if (callback != null && callback.onConnect != null) {
							try {
								callback.onConnect.setAccessible(true);
								callback.onConnect.invoke(memberContainer, alias);
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		};
		
		if (serviceConnections == null) {
			serviceConnections = new LinkedList<ServiceConnection>();
		}
		serviceConnections.add(conn);
		
		activity.bindService(intent, conn, flags);
	}

	private void injectExtra(Field field, Extra extra, Bundle extras) {
		String key = extra.value();
		if (key.length() == 0) {
			key = field.getName();
		}
		
		final Object value = extras.get(key);
		if (value != null) {
			try {
				field.setAccessible(true);
				field.set(memberContainer, value);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	private void injectLayoutElement(Field field, LayoutElement layoutElement, SparseArray<View> viewMap) {
		int id = layoutElement.value();
		if (id == 0) {
			String name = field.getName();
			if (name.length() >= 2 && name.charAt(0) == 'm') {
				final char c = name.charAt(1);
				if (Character.isUpperCase(c)) {
					name = Character.toLowerCase(c) + name.substring(2);
				}
			}

			id = ResourceUtils.findResourceId(activity, name, "id");
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

	private void injectReceiver(final Method method, Receiver receiver) {
		if (receivers == null) {
			receivers = new HashMap<Lifecycle, LinkedList<KeyValueEntry<IntentFilter,BroadcastReceiver>>>();
		}
		
		final IntentFilter filter = new IntentFilter();
		for (String action: receiver.value()) {
			filter.addAction(action);
		}
		for (String action: receiver.action()) {
			filter.addAction(action);
		}
		for (String category: receiver.category()) {
			filter.addCategory(category);
		}
		
		final String[] extraKeys = receiver.extra();
		final BroadcastReceiver broadcastReceiver;
		
		if (extraKeys.length == 0) {
			broadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					try {
						method.setAccessible(true);
						method.invoke(memberContainer, intent);
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
			broadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					final Bundle extras = (intent == null ? null : intent.getExtras());
					
					final Object[] args = new Object[1 + extraKeys.length];
					args[0] = intent;
					
					for (int i = 0; i < extraKeys.length; ++i) {
						args[i + 1] = extras.get(extraKeys[i]);
					}
					
					try {
						method.setAccessible(true);
						method.invoke(memberContainer, args);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			};
		}
		
		final Lifecycle lifecycle = receiver.lifecycle();
		
		LinkedList<KeyValueEntry<IntentFilter, BroadcastReceiver>> list =
				receivers.get(lifecycle);
		if (list == null) {
			list = new LinkedList<KeyValueEntry<IntentFilter,BroadcastReceiver>>();
			receivers.put(lifecycle, list);
		}
		
		list.add(new KeyValueEntry<IntentFilter, BroadcastReceiver>(filter, broadcastReceiver));
	}
	
	private void injectResource(Field field, Resource resource) {
		final Resources res = activity.getResources();
		
		initResourceLoaders();
		
		int id = resource.id();
		if (id == 0) {
			id = resource.value();
		}
		
		Object value = null;
		final ResourceType resType = resource.type();
		if (resType == ResourceType.NONE) {
			// Infer the resource type from some hints.

			if (id != 0) {
				final Class<?> fieldType = field.getType();
				final String typeName = res.getResourceTypeName(id);
				
				for (ResourceLoader loader: resourceLoaders) {
					value = loader.load(activity, typeName, id, fieldType);
					if (value != null) break;
				}
			} else {
				final String name = field.getName();
				final Class<?> fieldType = field.getType();
				
				for (ResourceLoader loader: resourceLoaders) {
					value = loader.load(activity, name, fieldType);
					if (value != null) break;
				}
			}
		} else {
			// Resource type has been set by user.

			final String fieldName = field.getName();
			
			for (ResourceLoader loader: resourceLoaders) {
				value = loader.load(activity, resType, id, fieldName);
				if (value != null) break;
			}
		}
		
		if (value != null) {
			try {
				field.setAccessible(true);
				field.set(memberContainer, value);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void injectSystemService(Field field) {
		final Class<?> fieldType = field.getType();
		
		Object service = null;
		
		if (fieldType.equals(BluetoothAdapter.class)) {
			if (Build.VERSION.SDK_INT >= 18) {
				service = activity.getSystemService(Context.BLUETOOTH_SERVICE);
			} else {
				service = BluetoothAdapter.getDefaultAdapter();
			}
//		} else if (fieldClass.equals(DisplayManagerCompat.class)) {
//			service = DisplayManagerCompat.getInstance(activity);
		} else {
			initSystemServiceList();
			
			final String serviceName = systemServices.get(fieldType);
			service = activity.getSystemService(serviceName);
		}

		if (service != null) {
			try {
				field.setAccessible(true);
				field.set(memberContainer, service);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void injectViews() {
		final SparseArray<View> viewMap = new SparseArray<View>();
		
		final EventInjector eventInjector = new EventInjector();
		
//		// [customEventHandlerInfo][eventName] = method
//		HashMap2<CustomEventHandlerInfo, String, Method> customEventMap = null;
//		
//		// [customEvnetHandlerInfo][lifecycle][eventName] = method
//		HashMap3<UnregisterableCustomEventHandlerInfo, Lifecycle, String, Method> unregCustomEventMap = null;
		
		if (unregEvents != null) {
			unregEvents.clear();
		}

		Class<?> cls = memberContainer.getClass();
		
		while (cls != null && !cls.equals(RapidActivity.class)) {
			// Fields
			
			for (Field field: cls.getDeclaredFields()) {
				final int modifier = field.getModifiers();
				if ((modifier & Modifier.STATIC) != 0) continue;
				
				// @LayoutElement
				final LayoutElement layoutElement = field.getAnnotation(LayoutElement.class);
				if (layoutElement != null) {
					injectLayoutElement(field, layoutElement, viewMap);
				}
			}
	
			// Methods
			
			for (final Method method: cls.getDeclaredMethods()) {
				final int modifier = method.getModifiers();
				if ((modifier & Modifier.STATIC) != 0) continue;
				
				for (Annotation annotation: method.getAnnotations()) {
					final Class<?> annotationType = annotation.annotationType();
					
					// @On
					
					if (annotationType.equals(On.class)) {
						final On on = (On) annotation;
						eventInjector.categorizeCustomEvent(method, on);
						continue;
					}
					
					// @EventHandler
					
					if (annotationType.equals(EventHandler.class)) {
						eventInjector.categorizeEventAuto(method, annotation);
						continue;
					}
					
					final SimpleEventRegistrar registrar = simpleEventRegistrars.get(annotationType);
					if (registrar != null) {
						eventInjector.categorizeSimpleEvent(registrar, annotation, method);
						continue;
					}
				}
			}
			
			cls = cls.getSuperclass();
		}
		
		eventInjector.injectSimpleEvents(viewMap);
		eventInjector.injectCustomEvents(viewMap);
		eventInjector.injectUnregisterableEvents(viewMap);
	}
	
	public void registerExternalEvent(int type, Object id, Method method) {
		switch (type) {
		case EXTERNAL_EVENT_SERVICE_CONNECT:
		case EXTERNAL_EVENT_SERVICE_DISCONNECT:
			if (serviceCallbacks == null) {
				serviceCallbacks = new HashMap<String, ServiceCallback>();
			}
			
			final String alias = (String) id;
			
			ServiceCallback callback = serviceCallbacks.get(alias);
			if (callback == null) {
				callback = new ServiceCallback();
				serviceCallbacks.put(alias, callback);
			}
			
			if (type == EXTERNAL_EVENT_SERVICE_CONNECT) {
				callback.onConnect = method;
			} else {
				callback.onDisconnect = method;
			}
			
			break;
		}
	}
	
	public void registerListeners(Lifecycle lifecycle) {
		if (unregEvents == null) return;
		
		final LinkedList<UnregisterableEventHandler> handlers = unregEvents.get(lifecycle);
		if (handlers == null) return;
		
		for (UnregisterableEventHandler handler: handlers) {
			handler.registrar.registerEventListener(handler.target, handler.dispatcher);
		}
	}

	public void registerListenersToCurrentLifecycle() {
		for (Lifecycle lifecycle: Lifecycle.values()) {
			if (lifecycle.getValue() <= currentLifecycle.getValue()) {
				registerListeners(lifecycle);
			}
		}
	}
	
	public void registerReceivers(Lifecycle lifecycle) {
		if (receivers == null) return;
		
		final LinkedList<KeyValueEntry<IntentFilter, BroadcastReceiver>> list = receivers.get(lifecycle);
		if (list == null) return;
		
		for (KeyValueEntry<IntentFilter, BroadcastReceiver> entry: list) {
			final IntentFilter filter = entry.getKey();
			final BroadcastReceiver receiver = entry.getValue();
			
			activity.registerReceiver(receiver, filter);
		}
	}
	
	private void registerUnregisterableEvent(Lifecycle lifecycle, UnregisterableEventRegistrar registrar, Object target,
			Object dispatcher) {

		if (unregEvents == null) {
			unregEvents = new HashMap<Lifecycle, LinkedList<UnregisterableEventHandler>>();
		}
		
		LinkedList<UnregisterableEventHandler> list = unregEvents.get(lifecycle);
		if (list == null) {
			list = new LinkedList<UnregisterableEventHandler>();
			unregEvents.put(lifecycle, list);
		}
		
		list.add(new UnregisterableEventHandler(registrar, target, dispatcher));
	}
	
	public void restoreInstanceStates(Bundle bundle) {
		Class<?> cls = memberContainer.getClass();
		while (cls != null && !isRapidClass(cls)) {
			for (Field field: cls.getDeclaredFields()) {
				final InstanceState instanceState = field.getAnnotation(InstanceState.class);
				if (instanceState == null) continue;
				
				String key = instanceState.value();
				if (key.length() == 0) {
					key = field.getName();
				}
				
				final Object value = bundle.get(key);
				
				if (value != null) {
					try {
						field.setAccessible(true);
						field.set(memberContainer, value);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
			
			cls = cls.getSuperclass();
		}
	}
	
	public void saveInstanceStates(Bundle bundle) {
		Class<?> cls = memberContainer.getClass();
		while (cls != null && !isRapidClass(cls)) {
			for (Field field: cls.getDeclaredFields()) {
				final InstanceState instanceState = field.getAnnotation(InstanceState.class);
				if (instanceState == null) continue;
				
				String key = instanceState.value();
				if (key.length() == 0) {
					key = field.getName();
				}
				
				try {
					field.setAccessible(true);
					final Object value = field.get(memberContainer);
					if (value != null) {
						putIntoBundle(bundle, key, value);
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			
			cls = cls.getSuperclass();
		}
	}
	
	public void setCurrentLifecycle(Lifecycle currentLifecycle) {
		this.currentLifecycle = currentLifecycle;
	}

	public void unregisterAllListeners() {
		unregisterListeners(Lifecycle.RESUME);
		unregisterListeners(Lifecycle.START);
		unregisterListeners(Lifecycle.CREATE);
	}

	public void unregisterListeners(Lifecycle lifecycle) {
		if (unregEvents == null) return;
		
		final LinkedList<UnregisterableEventHandler> handlers = unregEvents.get(lifecycle);
		if (handlers == null) return;
		
		for (UnregisterableEventHandler handler: handlers) {
			handler.registrar.unregisterEventListener(handler.target, handler.dispatcher);
		}
	}
	
	public void unregisterReceivers(Lifecycle lifecycle) {
		if (receivers == null) return;

		final LinkedList<KeyValueEntry<IntentFilter, BroadcastReceiver>> list = receivers.get(lifecycle);
		if (list == null) return;
		
		for (KeyValueEntry<IntentFilter, BroadcastReceiver> entry: list) {
			final BroadcastReceiver receiver = entry.getValue();
			try {
				activity.unregisterReceiver(receiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void unbindServices() {
		if (serviceConnections == null) return;
		
		for (ServiceConnection conn: serviceConnections) {
			try {
				activity.unbindService(conn);
			} catch (IllegalArgumentException e) {
			}
		}
		serviceConnections = null;
	}
	
	private static class ServiceCallback {
		public Method onConnect;
		public Method onDisconnect;
	}
}
