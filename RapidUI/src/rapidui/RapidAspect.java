package rapidui;

import static rapidui.util.Shortcuts.newHashMap;
import static rapidui.util.Shortcuts.newSparseArray;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.Executor;

import rapidui.RapidTask.OnStatusChangedListener;
import rapidui.RapidTask.Status;
import rapidui.annotation.ConnectService;
import rapidui.annotation.EventHandler;
import rapidui.annotation.Extra;
import rapidui.annotation.Font;
import rapidui.annotation.InstanceState;
import rapidui.annotation.LayoutElement;
import rapidui.annotation.OptionsMenu;
import rapidui.annotation.Receiver;
import rapidui.annotation.Resource;
import rapidui.annotation.ResourceType;
import rapidui.annotation.SearchBar;
import rapidui.annotation.SystemService;
import rapidui.annotation.event.ListenSensor;
import rapidui.annotation.event.On;
import rapidui.annotation.event.OnAfterTextChanged;
import rapidui.annotation.event.OnBeforeTextChanged;
import rapidui.annotation.event.OnCheckedChange;
import rapidui.annotation.event.OnClick;
import rapidui.annotation.event.OnCreateContextMenu;
import rapidui.annotation.event.OnDrag;
import rapidui.annotation.event.OnFocusChange;
import rapidui.annotation.event.OnGlobalLayout;
import rapidui.annotation.event.OnItemClick;
import rapidui.annotation.event.OnItemLongClick;
import rapidui.annotation.event.OnKey;
import rapidui.annotation.event.OnLongClick;
import rapidui.annotation.event.OnMenuItemClick;
import rapidui.annotation.event.OnQueryTextChange;
import rapidui.annotation.event.OnQueryTextSubmit;
import rapidui.annotation.event.OnScroll;
import rapidui.annotation.event.OnScrollStateChange;
import rapidui.annotation.event.OnSensorChange;
import rapidui.annotation.event.OnServiceConnect;
import rapidui.annotation.event.OnServiceDisconnect;
import rapidui.annotation.event.OnTextChanged;
import rapidui.annotation.event.OnTouch;
import rapidui.event.CustomEventInfo;
import rapidui.event.CustomEventRegistrar;
import rapidui.event.HostEventInfo;
import rapidui.event.OnCheckedChangeRegistrar;
import rapidui.event.OnClickRegistrar;
import rapidui.event.OnCreateContextMenuRegistrar;
import rapidui.event.OnDragRegistrar;
import rapidui.event.OnFocusChangeRegistrar;
import rapidui.event.OnGlobalLayoutHostEvent;
import rapidui.event.OnItemClickRegistrar;
import rapidui.event.OnItemLongClickRegistrar;
import rapidui.event.OnKeyRegistrar;
import rapidui.event.OnLongClickRegistrar;
import rapidui.event.OnMenuItemClickHostEvent;
import rapidui.event.OnQueryTextChangeHostEvent;
import rapidui.event.OnQueryTextSubmitHostEvent;
import rapidui.event.OnScrollRegistrar;
import rapidui.event.OnSensorChangeHostEvent;
import rapidui.event.OnSensorChangeRegistrar;
import rapidui.event.OnServiceConnectHostEvent;
import rapidui.event.OnServiceDisconnectHostEvent;
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
import rapidui.util.SparseArray2;
import rapidui.util.SparseArray3;
import rapidui.util.SparseArray4;
import android.accounts.AccountManager;
import android.app.ActionBar;
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
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.hardware.ConsumerIrManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.CaptioningManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.TextServicesManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

public abstract class RapidAspect {
	private static Class<?>[] argsReceiver = new Class<?>[] { Context.class, Intent.class };
	private static Class<?>[] argsServiceConnect = new Class<?>[] { String.class };
	private static Class<?>[] argsSensorChange = new Class<?>[] { SensorEvent.class };
	private static SparseArray<Class<?>[]> hostEventArguments;
	
	private static boolean support4lib = true;
	private static boolean support7lib = true;
	
	private SparseArray2<Integer, EventHandlerInfo> hostEventHandlers;
	
	private static class AutoEventName {
		public String target;
		public String event;
		public Class<?> annotationType;
	}
	
	private class EventInjector {
		// [viewId][registrar][annotation]: method
		private SparseArray3<SimpleEventRegistrar, Class<?>, Method> eventMap;

		// [viewId][registrar][lifecycle][annotation]: method
		private SparseArray4<UnregisterableEventRegistrar, Lifecycle, Class<?>, Method> unregEventMap;
		
		// [viewId][eventCategoryName][lifecycle][eventName]: method
		private SparseArray4<String, Lifecycle, String, Method> customEventMap;
		
		private AutoEventName autoEventName;

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

		public boolean categorizeEventAuto(Method method, Annotation annotation) {
			if (autoEventName == null) {
				autoEventName = new AutoEventName();
			}
			
			if (!parseAutoEventName(method, autoEventName)) return false;
			
			final String annotationName = autoEventName.event;
			final String idName = autoEventName.target;
			final Class<?> annotationType = autoEventName.annotationType;
			
			if (annotationType != null) {
				// Skip if it is a host event
				
				ensureHostEventList();
				if (hostEvents.containsKey(annotationType)) return false;
				
				//
				
				ensureSimpleEventRegistrarList();
				
				final SimpleEventRegistrar registrar = simpleEventRegistrars.get(annotationType);
				if (registrar != null) {
					final int id = ResourceUtils.findResourceId(activity, idName, "id");
					if (id != 0) {
						if (registrar instanceof UnregisterableEventRegistrar) {
							final UnregisterableEventRegistrar registrar2 =
									(UnregisterableEventRegistrar) registrar;
							final Lifecycle lifecycle = registrar2.getLifecycle(annotation);
							
							addUnregEvent(id, registrar2, lifecycle, annotationType, method);
						} else {
							addEvent(id, registrar, annotationType, method);
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
		
		public void categorizeSimpleEvent(SimpleEventRegistrar registrar, Annotation annotation, Method method) {
			final Class<?> annotationType = annotation.annotationType();
			
			if (registrar instanceof UnregisterableEventRegistrar) {
				final UnregisterableEventRegistrar registrar2 =
						(UnregisterableEventRegistrar) registrar;
				final Lifecycle lifecycle = registrar2.getLifecycle(annotation);
				
				for (int id: registrar.getTargetViewIds(annotation)) {
					addUnregEvent(id, registrar2, lifecycle, annotationType, method);
				}
			} else {
				for (int id: registrar.getTargetViewIds(annotation)) {
					addEvent(id, registrar, annotationType, method);
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
				final Class<?> listenerType = unregInfo.getListenerType();
				final CustomEventRegistrar registrar = new CustomEventRegistrar(adder, remover);
				
				for (Entry<Lifecycle, HashMap<String, Method>> entry3: entry2.getValue().entrySet()) {
					final HashMap<String, Method> methods = entry3.getValue();
					validateCustomMethodMap(methods, listenerType);
					
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
					validateCustomMethodMap(methods, info.getListenerType());
					
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
		
		public void injectCustomEvents() {
			if (customEventMap == null) return;
			
			for (Entry<Integer, HashMap<String, HashMap<Lifecycle, HashMap<String, Method>>>> entry: customEventMap) {
				final int id = entry.getKey();
				final Object target = host.findView(id);
				
				for (Entry<String, HashMap<Lifecycle, HashMap<String, Method>>> entry2: entry.getValue().entrySet()) {
					injectCustomEvent(target, entry2);
				}
			}
		}
		
		public void injectSimpleEvents() {
			if (eventMap == null) return;
			for (Entry<Integer, HashMap<SimpleEventRegistrar, HashMap<Class<?>, Method>>> entry: eventMap) {
				final int id = entry.getKey();
				final Object target = host.findView(id);
				
				for (Entry<SimpleEventRegistrar, HashMap<Class<?>, Method>> entry2: entry.getValue().entrySet()) {
					final SimpleEventRegistrar registrar = entry2.getKey();
					final HashMap<Class<?>, Method> methods = entry2.getValue();
					
					final Object dispatcher = registrar.createEventDispatcher(target, memberContainer, methods);
					if (dispatcher != null) {
						registrar.registerEventListener(target, dispatcher);
					}
				}
			}
		}
		
		public void injectUnregisterableEvents() {
			if (unregEventMap == null) return;
				
			for (Entry<Integer,
					   HashMap<UnregisterableEventRegistrar,
					           HashMap<Lifecycle, HashMap<Class<?>, Method>>
			          >
			     > entry: unregEventMap) {
				
				final int id = entry.getKey();
				final Object target = host.findView(id);
				
				for (Entry<UnregisterableEventRegistrar, HashMap<Lifecycle, HashMap<Class<?>, Method>>> entry2: entry.getValue().entrySet()) {
					final UnregisterableEventRegistrar registrar = entry2.getKey();
					
					for (Entry<Lifecycle, HashMap<Class<?>, Method>> entry3: entry2.getValue().entrySet()) {
						final Lifecycle lifecycle = entry3.getKey();
						final HashMap<Class<?>, Method> methods = entry3.getValue();
						
						final Object dispatcher = registrar.createEventDispatcher(target, memberContainer, methods);
						if (dispatcher != null) {
							registerUnregisterableEvent(lifecycle, registrar, target, dispatcher);
						}
					}
				}
			}
		}
	}
	private static class ServiceCallback {
		public Method onConnect;
		public Method onDisconnect;
		public ArgumentMapper amConnect;
		public ArgumentMapper amDisconnect;
	}
	
	public static final int HOST_EVENT_MENU_ITEM_CLICK = 0;
	public static final int HOST_EVENT_SERVICE_CONNECT = 1;
	public static final int HOST_EVENT_SERVICE_DISCONNECT = 2;
	public static final int HOST_EVENT_GLOBAL_LAYOUT = 3;
	public static final int HOST_EVENT_QUERY_TEXT_CHANGE = 4;
	public static final int HOST_EVENT_QUERY_TEXT_SUBMIT = 5;
	public static final int HOST_EVENT_SENSOR_CHANGE = 6;
	
	protected static final int HOST_EVENT_USER = 100;
	
	private static HashMap<Class<?>, SimpleEventRegistrar> simpleEventRegistrars;
	private static HashMap<Class<?>, HostEventInfo> hostEvents;
	
	private static HashMap<String, Class<?>> annotationNameMatch;
	
	private static HashMap<Class<?>, String> systemServices;
	
	private static LinkedList<ResourceLoader> resourceLoaders;
	
	private static void ensureSimpleEventRegistrarList() {
		if (simpleEventRegistrars != null) return;
		
		simpleEventRegistrars = new HashMap<Class<?>, SimpleEventRegistrar>();

		simpleEventRegistrars.put(OnClick.class, new OnClickRegistrar());
		simpleEventRegistrars.put(OnCreateContextMenu.class, new OnCreateContextMenuRegistrar());
		simpleEventRegistrars.put(OnDrag.class, new OnDragRegistrar());
		simpleEventRegistrars.put(OnFocusChange.class, new OnFocusChangeRegistrar());
		simpleEventRegistrars.put(OnKey.class, new OnKeyRegistrar());
		simpleEventRegistrars.put(OnLongClick.class, new OnLongClickRegistrar());
		simpleEventRegistrars.put(OnTouch.class, new OnTouchRegistrar());
		simpleEventRegistrars.put(OnCheckedChange.class, new OnCheckedChangeRegistrar());
		simpleEventRegistrars.put(OnItemClick.class, new OnItemClickRegistrar());
		simpleEventRegistrars.put(OnItemLongClick.class, new OnItemLongClickRegistrar());
		
		final TextWatcherRegistrar textWatcherRegistrar = new TextWatcherRegistrar();
		simpleEventRegistrars.put(OnTextChanged.class, textWatcherRegistrar);
		simpleEventRegistrars.put(OnBeforeTextChanged.class, textWatcherRegistrar);
		simpleEventRegistrars.put(OnAfterTextChanged.class, textWatcherRegistrar);
		
		final OnScrollRegistrar scrollRegistrar = new OnScrollRegistrar();
		simpleEventRegistrars.put(OnScroll.class, scrollRegistrar);
		simpleEventRegistrars.put(OnScrollStateChange.class, scrollRegistrar);
	}
	
	private static void ensureHostEventList() {
		if (hostEvents != null) return;
		
		hostEvents = new HashMap<Class<?>, HostEventInfo>();

		hostEvents.put(OnMenuItemClick.class, new OnMenuItemClickHostEvent());
		hostEvents.put(OnServiceConnect.class, new OnServiceConnectHostEvent());
		hostEvents.put(OnServiceDisconnect.class, new OnServiceDisconnectHostEvent());
		hostEvents.put(OnGlobalLayout.class, new OnGlobalLayoutHostEvent());
		hostEvents.put(OnQueryTextChange.class, new OnQueryTextChangeHostEvent());
		hostEvents.put(OnQueryTextSubmit.class, new OnQueryTextSubmitHostEvent());
		hostEvents.put(OnSensorChange.class, new OnSensorChangeHostEvent());
	}

	private static void initAnnotationNameMatchList() {
		if (annotationNameMatch != null) return;

		annotationNameMatch = new HashMap<String, Class<?>>();
		
		ensureSimpleEventRegistrarList();
		ensureHostEventList();
		
		for (Class<?> cls: simpleEventRegistrars.keySet()) {
			final String name = cls.getSimpleName().substring(2);
			annotationNameMatch.put(name, cls);
		}
		for (Class<?> cls: hostEvents.keySet()) {
			final String name = cls.getSimpleName().substring(2);
			annotationNameMatch.put(name, cls);
		}
	}
	
	private static void validateCustomMethodMap(HashMap<String, Method> methods,
			Class<?> listenerType) {
		
		if (methods.size() > 1) return;
		
		final Method method = methods.get("");
		if (method == null) return;

		final String firstEventName = getFirstEventName(listenerType);
		if (firstEventName == null) return;
		
		methods.put(firstEventName, method);
		methods.remove("");
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
			systemServices.put(CaptioningManager.class, Context.CAPTIONING_SERVICE);
			systemServices.put(ConsumerIrManager.class, Context.CONSUMER_IR_SERVICE);
			systemServices.put(PrintManager.class, Context.PRINT_SERVICE);
		}
	}
	
	protected static boolean isRapidClass(Class<?> cls) {
		if (cls.equals(RapidActivity.class) ||
				cls.equals(RapidFragment.class)) {
			
			return true;
		}
		
		if (support4lib) {
			try {
				if (cls.equals(RapidSupport4Activity.class)) {
					return true;
				}
			} catch (NoClassDefFoundError e) {
				support4lib = false;
			}
		}
		
		if (support7lib) {
			try {
				if (cls.equals(RapidSupport4Fragment.class)) {
					return true;
				}
			} catch (NoClassDefFoundError e) {
				support7lib = false;
			}
		}
		
		return false;
	}
	private static boolean parseAutoEventName(Method method, AutoEventName out) {
		final String name = method.getName();
		final int underscoreIndex = name.indexOf('_');
		
		if (underscoreIndex < 0) return false;
		
		final String idName = name.substring(0, underscoreIndex);
		final String annotationName = name.substring(underscoreIndex + 1);
		
		if (idName.length() == 0 || annotationName.length() == 0) return false;

		initAnnotationNameMatchList();

		out.target = idName;
		out.event = annotationName;
		out.annotationType = annotationNameMatch.get(annotationName);
		
		return true;
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
	protected Host host;
	
	private Lifecycle currentLifecycle;
	private HashMap<Lifecycle, LinkedList<KeyValueEntry<IntentFilter, BroadcastReceiver>>> receivers;
	private HashMap<Lifecycle, LinkedList<UnregisterableEventHandler>> unregEvents;
	private HashSet<ServiceConnection> serviceConnections;
	private HashMap<String, ServiceCallback> serviceCallbacks;
	
	private HashMap<String, SingletonTaskInfo> singletonTasks;
	private HashMap<TaskLifecycle, HashSet<TaskInfo>> tasks;
	
	public RapidAspect(Activity activity, Object memberContainer, Host viewFinder) {
		this.activity = activity;
		this.memberContainer = memberContainer;
		this.host = viewFinder;
	}
	
	public Lifecycle getCurrentLifecycle() {
		return currentLifecycle;
	}

	private void injectBindService(final Field field, ConnectService bindService) {
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

					if (serviceCallbacks != null && alias.length() > 0) {
						final ServiceCallback callback = serviceCallbacks.get(alias);
						if (callback != null && callback.onConnect != null) {
							try {
								callback.onConnect.setAccessible(true);
								callback.onConnect.invoke(memberContainer, callback.amConnect.match(alias));
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
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				if (serviceConnections != null) {
					serviceConnections.remove(this);
				}
				
				if (serviceCallbacks != null && alias.length() > 0) {
					final ServiceCallback callback = serviceCallbacks.get(alias);
					if (callback != null && callback.onDisconnect != null) {
						try {
							callback.onDisconnect.setAccessible(true);
							callback.onDisconnect.invoke(memberContainer, callback.amDisconnect.match(alias));
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
		};
		
		if (serviceConnections == null) {
			serviceConnections = new HashSet<ServiceConnection>();
		}
		serviceConnections.add(conn);
		
		activity.bindService(intent, conn, flags);
	}

	public void injectCommonThings() {
		final Intent intent = activity.getIntent();
		final Bundle extras = (intent == null ? null : intent.getExtras());
		
		AutoEventName autoEventName = null;
		
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
				final ConnectService bindService = field.getAnnotation(ConnectService.class);
				if (bindService != null) {
					injectBindService(field, bindService);
				}
				
				// @Font
				final Font font = field.getAnnotation(Font.class);
				if (font != null) {
					injectFont(field, font);
				}
			}
			
			// Methods
			
			for (final Method method: cls.getDeclaredMethods()) {
				final int modifier = method.getModifiers();
				if ((modifier & Modifier.STATIC) != 0) continue;

				for (Annotation annotation: method.getAnnotations()) {
					final Class<?> annotationType = annotation.annotationType();

					// @Receiver
					if (annotationType.equals(Receiver.class)) {
						final Receiver receiver = (Receiver) annotation;
						if (receiver != null) {
							// @Receiver
							injectReceiver(method, receiver);
						}
						continue;
					}
					
					// @EventHandler
					
					if (annotationType.equals(EventHandler.class)) {
						if (autoEventName == null) {
							autoEventName = new AutoEventName();
						}
						
						if (parseAutoEventName(method, autoEventName) && autoEventName.annotationType != null) {
							ensureHostEventList();
							
							final HostEventInfo info = hostEvents.get(autoEventName.annotationType);
							if (info != null) {
								final Object id = info.parseId(activity, autoEventName.target);
								if (id != null) {
									registerHostEvent(null, info.getType(), id, method);
								}
							}
						}
						
						continue;
					}
					
					// Host events
					
					ensureHostEventList();

					final HostEventInfo info = hostEvents.get(annotationType);
					if (info != null) {
						final int type = info.getType();

						final Iterable<?> ids = info.getTargetIds(annotation);
						if (ids != null) {
							for (Object id: ids) {
								registerHostEvent(annotation, type, id, method);
							}
						} else {
							registerHostEvent(annotation, type, null, method);
						}
						
						continue;
					}
				}
			}
			
			cls = cls.getSuperclass();
		}
	}
	
	private void injectFont(Field field, Font font) {
		String path = font.path();
		if (path.length() == 0) {
			final int id = font.value() | font.id();
			path = activity.getString(id);
		}
		
		final Typeface typeface = loadTypeface(path);
		if (typeface != null) {
			try {
				field.setAccessible(true);
				field.set(memberContainer, typeface);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
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
	
	private View injectLayoutElement(Field field, LayoutElement layoutElement) {
		int id = layoutElement.value() | layoutElement.id();
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
			final View v = host.findViewById(id);
			field.set(memberContainer, v);
			
			if (v != null) {
				if (v instanceof TextView && layoutElement.font() != 0) {
					final Typeface typeface = loadTypeface(layoutElement.font());
					final TextView tv = (TextView) v;
					tv.setTypeface(typeface);
				}
				
				return v;
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		return null;
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
		for (String scheme: receiver.dataScheme()) {
			filter.addDataScheme(scheme);
		}
		
		final String[] extraKeys = receiver.extra();
		final ArgumentMapper am = new ArgumentMapper(argsReceiver, method);

		final BroadcastReceiver broadcastReceiver;
		
		if (extraKeys.length == 0 || am.isIdentical()) {
			broadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					try {
						method.setAccessible(true);
						method.invoke(memberContainer, am.match(context, intent));
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

					final Object[] args = new Object[am.size()];
					am.fillMatchedResult(args, 0, context, intent);
					
					int j = 0;
					for (int i = 0; i < extraKeys.length; ++i) {
						for (; am.isMapped(j); ++j);
						args[j++] = extras.get(extraKeys[i]);
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
		
		final int id = resource.id() | resource.value();
		
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
		} else if (fieldType.equals(PackageManager.class)) {
			service = activity.getPackageManager();
//		} else if (fieldType.equals(DisplayManagerCompat.class)) {
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
		host.enableViewCache();
		
		final EventInjector eventInjector = new EventInjector();
		
//		// [customEventHandlerInfo][eventName] = method
//		HashMap2<CustomEventHandlerInfo, String, Method> customEventMap = null;
//		
//		// [customEvnetHandlerInfo][lifecycle][eventName] = method
//		HashMap3<UnregisterableCustomEventHandlerInfo, Lifecycle, String, Method> unregCustomEventMap = null;
		
		Class<?> cls = memberContainer.getClass();
		
		while (cls != null && !isRapidClass(cls)) {
			// Fields
			
			for (Field field: cls.getDeclaredFields()) {
				final int modifier = field.getModifiers();
				if ((modifier & Modifier.STATIC) != 0) continue;
				
				// @LayoutElement
				final LayoutElement layoutElement = field.getAnnotation(LayoutElement.class);
				if (layoutElement != null) {
					injectLayoutElement(field, layoutElement);
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
					
					ensureSimpleEventRegistrarList();
					
					final SimpleEventRegistrar registrar = simpleEventRegistrars.get(annotationType);
					if (registrar != null) {
						eventInjector.categorizeSimpleEvent(registrar, annotation, method);
						continue;
					}
				}
			}
			
			cls = cls.getSuperclass();
		}
		
		eventInjector.injectSimpleEvents();
		eventInjector.injectCustomEvents();
		eventInjector.injectUnregisterableEvents();
		
		host.disableViewCache();
	}
	
	private static HashMap<String, WeakReference<Typeface>> typefaces;

	private Typeface loadTypeface(String path) {
		if (typefaces == null) {
			typefaces = new HashMap<String, WeakReference<Typeface>>();
		} else {
			final WeakReference<Typeface> ref = typefaces.get(path);
			final Typeface typeface = (ref == null ? null : ref.get());
			if (typeface != null) {
				return typeface;
			}
		}
		
		final Typeface typeface = Typeface.createFromAsset(activity.getAssets(), path);
		if (typeface != null) {
			typefaces.put(path, new WeakReference<Typeface>(typeface));
		}
		
		return typeface;
	}
	
	private Typeface loadTypeface(int id) {
		return loadTypeface(activity.getString(id));
	}

	public void registerHostEvent(Object annotation, int type, Object id, final Method method) {
		switch (type) {
		case HOST_EVENT_SERVICE_CONNECT:
		case HOST_EVENT_SERVICE_DISCONNECT:
			if (serviceCallbacks == null) {
				serviceCallbacks = new HashMap<String, ServiceCallback>();
			}
			
			final String alias = (String) id;
			if (TextUtils.isEmpty(alias)) break;
			
			ServiceCallback callback = serviceCallbacks.get(alias);
			if (callback == null) {
				callback = new ServiceCallback();
				serviceCallbacks.put(alias, callback);
			}
			
			if (type == HOST_EVENT_SERVICE_CONNECT) {
				callback.onConnect = method;
				callback.amConnect = new ArgumentMapper(argsServiceConnect, method);
			} else {
				callback.onDisconnect = method;
				callback.amDisconnect = new ArgumentMapper(argsServiceConnect, method);
			}
			
			break;
			
		case HOST_EVENT_GLOBAL_LAYOUT:
			final boolean once = (annotation == null ? true : ((OnGlobalLayout) annotation).once());
			
			final ViewTreeObserver observer = activity.getWindow().getDecorView().getViewTreeObserver();
			observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					if (once) {
						if (Build.VERSION.SDK_INT >= 16) {
							activity.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
						} else {
							activity.getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
						}
					}
					
					try {
						method.setAccessible(true);
						method.invoke(memberContainer);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			});
			
			break;
			
		case HOST_EVENT_SENSOR_CHANGE:
			final SensorManager sm = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
			final ArgumentMapper am = new ArgumentMapper(argsSensorChange, method);
			
			final OnSensorChange sc = (OnSensorChange) annotation;
			for (ListenSensor ls: sc.value()) {
				final Sensor sensor = sm.getDefaultSensor(ls.sensorType());
				final OnSensorChangeRegistrar registrar = new OnSensorChangeRegistrar(sensor, ls.rate());
				
				final SensorEventListener listener = new SensorEventListener() {
					@Override
					public void onSensorChanged(SensorEvent event) {
						try {
							method.setAccessible(true);
							method.invoke(memberContainer, am.match(event));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
					
					@Override
					public void onAccuracyChanged(Sensor sensor, int accuracy) {
					}
				};
				
				registerUnregisterableEvent(ls.lifecycle(), registrar, sm, listener);
			}

			break;
			
		case HOST_EVENT_MENU_ITEM_CLICK:
		case HOST_EVENT_QUERY_TEXT_CHANGE:
		case HOST_EVENT_QUERY_TEXT_SUBMIT:
			if (id == null) break;
			
			final Class<?>[] args = getHostEventArguments(type);
			final ArgumentMapper argMatcher = new ArgumentMapper(args, method);
			putHostEventHandler(type, (Integer) id, new EventHandlerInfo(method, argMatcher));
			
			break;
		}
	}

	protected EventHandlerInfo getHostEventHandler(int type, int id) {
		return (hostEventHandlers == null ? null : hostEventHandlers.get(type, id));
	}
	
	protected EventHandlerInfo removeHostEventHandler(int type, int id) {
		return (hostEventHandlers == null ? null : hostEventHandlers.remove(type, id));
	}
	
	protected static Class<?>[] getHostEventArguments(int type) {
		if (hostEventArguments == null) {
			initHostEventArguments();
		}
		return hostEventArguments.get(type);
	}
	
	private static void initHostEventArguments() {
		hostEventArguments = newSparseArray(
				HOST_EVENT_MENU_ITEM_CLICK, new Class<?>[] { MenuItem.class },
				HOST_EVENT_QUERY_TEXT_CHANGE, argsServiceConnect,
				HOST_EVENT_QUERY_TEXT_SUBMIT, argsServiceConnect
		);
	}

	protected void putHostEventHandler(int type, int id, EventHandlerInfo info) {
		if (hostEventHandlers == null) {
			hostEventHandlers = SparseArray2.create();
		}
		hostEventHandlers.put(type, id, info);
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

	private static String getFirstEventName(Class<?> cls) {
		String eventName = null;
		for (Method method: cls.getDeclaredMethods()) {
			final String methodName = method.getName();
			if (methodName.startsWith("on")) {
				return methodName.substring(2);
			} else {
				eventName = methodName;
			}
		}
		
		return eventName;
	}
	
	public void collect() {
		hostEvents = null;
		simpleEventRegistrars = null;
		annotationNameMatch = null;
		systemServices = null;
		resourceLoaders = null;
		
		hostEventArguments = null;
		
		if (typefaces != null) {
			final Iterator<Entry<String, WeakReference<Typeface>>> it = typefaces.entrySet().iterator();
			while (it.hasNext()) {
				final Entry<String, WeakReference<Typeface>> entry = it.next();
				final WeakReference<Typeface> ref = entry.getValue();
				if (ref.get() == null) {
					it.remove();
				}
			}
			
			if (typefaces.isEmpty()) {
				typefaces = null;
			}
		}
	}

	public void executeSingleton(final TaskLifecycle lifecycle, final String name, Executor exec,
			final RapidTask<?> task, Object... params) {

		if (singletonTasks == null) {
			singletonTasks = new HashMap<String, SingletonTaskInfo>();
		} else {
			final TaskInfo taskInfo = singletonTasks.remove(name);
			if (taskInfo != null) {
				// Cancel previous running task if exists.
				
				removeTask(taskInfo);
				taskInfo.task.cancel(true);
			}
		}
		
		final SingletonTaskInfo taskInfo = new SingletonTaskInfo();
		taskInfo.lifecycle = lifecycle;
		taskInfo.name = name;
		taskInfo.task = task;
		
		addTask(lifecycle, taskInfo);
		singletonTasks.put(name, taskInfo);
		
		task.setOnStatusChangedListener(new OnStatusChangedListener() {
			@Override
			public void onStatusChange(Status status) {
				if (status != Status.FINISHED) return;
				
				removeSingletonTask(taskInfo);
				removeTask(taskInfo);
			}
		});
		
		task.executeOnExecutor(exec, params);
	}
	
	public void cancelSingletonTask(String name) {
		if (singletonTasks == null) return;

		final SingletonTaskInfo taskInfo = singletonTasks.remove(name);
		if (taskInfo == null) return;
		
		removeTask(taskInfo);
		taskInfo.task.cancel(true);
	}

	public void execute(final TaskLifecycle lifecycle, Executor exec,
			final RapidTask<?> task, Object... params) {

		final TaskInfo taskInfo = new TaskInfo();
		taskInfo.lifecycle = lifecycle;
		taskInfo.task = task;
		
		addTask(lifecycle, taskInfo);
		
		task.setOnStatusChangedListener(new OnStatusChangedListener() {
			@Override
			public void onStatusChange(Status status) {
				if (status != Status.FINISHED) return;
				removeTask(lifecycle, task);
			}
		});
		
		task.executeOnExecutor(exec, params);
	}
	
	private void addTask(TaskLifecycle lifecycle, TaskInfo taskInfo) {
		if (tasks == null) {
			tasks = newHashMap();
		}
		
		HashSet<TaskInfo> set = tasks.get(lifecycle);
		if (set == null) {
			set = new HashSet<TaskInfo>();
			tasks.put(lifecycle, set);
		}

		set.add(taskInfo);
	}
	
	private void removeSingletonTask(SingletonTaskInfo taskInfo) {
		final TaskInfo ti = singletonTasks.get(taskInfo.name);
		if (ti.equals(taskInfo)) {
			singletonTasks.remove(taskInfo.name);
		}
	}

	private void removeTask(TaskInfo taskInfo) {
		final HashSet<TaskInfo> set = tasks.get(taskInfo.lifecycle);
		if (set != null) {
			set.remove(taskInfo);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void removeTask(TaskLifecycle lifecycle, RapidTask task) {
		if (tasks == null) return;
		
		final HashSet<TaskInfo> set = tasks.get(lifecycle);
		if (set == null) return;
		
		final Iterator<TaskInfo> it = set.iterator();
		while (it.hasNext()) {
			final TaskInfo taskInfo = it.next();
			if (taskInfo.task.equals(task)) {
				it.remove();
				break;
			}
		}
	}

	private static class TaskInfo {
		@SuppressWarnings("rawtypes")
		public RapidTask task;
		public TaskLifecycle lifecycle;
	}
	
	private static class SingletonTaskInfo extends TaskInfo {
		public String name;
	}
	
	public void cancelTasks(TaskLifecycle lifecycle) {
		if (tasks == null) return;
		
		final HashSet<TaskInfo> set = tasks.get(lifecycle);
		if (set == null) return;
		
		for (TaskInfo taskInfo: set) {
			if (singletonTasks != null && taskInfo instanceof SingletonTaskInfo) {
				final SingletonTaskInfo sti = (SingletonTaskInfo) taskInfo;
				singletonTasks.remove(sti.name);
			}
			
			taskInfo.task.cancel(true);
		}

		set.clear();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		if (id != 0) {
			final EventHandlerInfo info = getHostEventHandler(HOST_EVENT_MENU_ITEM_CLICK, id);
			if (info != null) {
				try {
					info.method.setAccessible(true);
					return (Boolean) info.method.invoke(memberContainer, info.argMatcher.match(item));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	protected abstract String getHostNamePostFix();
	
	public void injectOptionsMenu(MenuInflater inflater, Menu menu) {
		final Resources res = activity.getResources();
		
		SearchBar sb = null;
		
		Class<?> cls = memberContainer.getClass();
		
		while (cls != null && !isRapidClass(cls)) {
			final OptionsMenu optionsMenu = cls.getAnnotation(OptionsMenu.class);
			if (optionsMenu != null) {
				int id = optionsMenu.value();
				if (id == 0) {
					final String packageName = activity.getPackageName();
					final String postfix = getHostNamePostFix();
					
					String name = cls.getSimpleName();
					if (name.length() > 8 && name.endsWith(postfix)) {
						name = ResourceUtils.toLowerUnderscored(name.substring(0, name.length() - postfix.length()));
					} else {
						name = ResourceUtils.toLowerUnderscored(name);
					}

					id = res.getIdentifier(name, "menu", packageName);
				}

				inflater.inflate(id, menu);
			}
			
			if (sb == null) {
				sb = cls.getAnnotation(SearchBar.class);
			}

			cls = cls.getSuperclass();
		}
		
		// SearchBar
		
		if (sb != null) {
			initSearchBar(menu, res, sb);
		}
	}
	
	private void initSearchBar(Menu menu, Resources res, SearchBar sb) {
		int id = sb.value() | sb.id();
		if (id != 0) {
			final MenuItem item = menu.findItem(id);
			if (item != null) {
				String hint = sb.hint();
				if (TextUtils.isEmpty(hint)) {
					final int hintId = sb.hintId();
					if (hintId == 0) {
						hint = null;
					} else {
						hint = res.getString(hintId);
					}
				}
				
				if (Build.VERSION.SDK_INT >= 11) {
					SearchView sv = (SearchView) item.getActionView();
					if (sv == null) {
						final ActionBar actionBar = activity.getActionBar();
						final Context context = (actionBar != null ? actionBar.getThemedContext() : activity);
						
						sv = new SearchView(context);
						item.setActionView(sv);
					}
					
					if (hint != null) {
						sv.setQueryHint(hint);
					}
					
					final EventHandlerInfo event1 = removeHostEventHandler(HOST_EVENT_QUERY_TEXT_CHANGE, id);
					final EventHandlerInfo event2 = removeHostEventHandler(HOST_EVENT_QUERY_TEXT_SUBMIT, id);

					if (event1 != null || event2 != null) {
						sv.setOnQueryTextListener(new OnQueryTextListener() {
							@Override
							public boolean onQueryTextSubmit(String query) {
								if (event2 == null) return false;

								try {
									event2.method.setAccessible(true);
									return (Boolean) event2.method.invoke(memberContainer, event2.argMatcher.match(query));
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
								
								return false;
							}
							
							@Override
							public boolean onQueryTextChange(String newText) {
								if (event1 == null) return false;
								
								try {
									event1.method.setAccessible(true);
									return (Boolean) event1.method.invoke(memberContainer, event1.argMatcher.match(newText));
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
								
								return false;
							}
						});
					}
				} else {
					android.support.v7.widget.SearchView sv = (android.support.v7.widget.SearchView)
							MenuItemCompat.getActionView(item);
					if (sv == null) {
						final android.support.v7.app.ActionBar actionBar;
						if (activity instanceof ActionBarActivity) {
							actionBar = ((ActionBarActivity) activity).getSupportActionBar();
						} else {
							actionBar = null;
						}
						
						final Context context = (actionBar != null ? actionBar.getThemedContext() : activity);
						
						sv = new android.support.v7.widget.SearchView(context);
						item.setActionView(sv);
					}
					
					if (hint != null) {
						sv.setQueryHint(hint);
					}
					
					final EventHandlerInfo event1 = removeHostEventHandler(HOST_EVENT_QUERY_TEXT_CHANGE, id);
					final EventHandlerInfo event2 = removeHostEventHandler(HOST_EVENT_QUERY_TEXT_SUBMIT, id);

					if (event1 != null || event2 != null) {
						sv.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
							@Override
							public boolean onQueryTextSubmit(String query) {
								if (event2 == null) return false;

								try {
									event2.method.setAccessible(true);
									return (Boolean) event2.method.invoke(memberContainer, event2.argMatcher.match(query));
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
								
								return false;
							}
							
							@Override
							public boolean onQueryTextChange(String newText) {
								if (event1 == null) return false;
								
								try {
									event1.method.setAccessible(true);
									return (Boolean) event1.method.invoke(memberContainer, event1.argMatcher.match(newText));
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
								
								return false;
							}
						});
					}
				}
			}
		}
	}
}