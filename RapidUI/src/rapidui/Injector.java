package rapidui;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import rapidui.annotation.EventHandler;
import rapidui.annotation.Extra;
import rapidui.annotation.InstanceState;
import rapidui.annotation.LayoutElement;
import rapidui.annotation.Lifecycle;
import rapidui.annotation.Receiver;
import rapidui.annotation.Resource;
import rapidui.annotation.ResourceType;
import rapidui.annotation.SystemService;
import rapidui.annotation.eventhandler.On;
import rapidui.annotation.eventhandler.OnCheckedChange;
import rapidui.annotation.eventhandler.OnClick;
import rapidui.annotation.eventhandler.OnCreateContextMenu;
import rapidui.annotation.eventhandler.OnDrag;
import rapidui.annotation.eventhandler.OnFocusChange;
import rapidui.annotation.eventhandler.OnKey;
import rapidui.annotation.eventhandler.OnLongClick;
import rapidui.annotation.eventhandler.OnMenuItemClick;
import rapidui.annotation.eventhandler.OnTouch;
import rapidui.eventhandler.EventHandlerRegistrar;
import rapidui.eventhandler.ExternalHandlerInfo;
import rapidui.eventhandler.OnCheckedChangeRegistrar;
import rapidui.eventhandler.OnClickRegistrar;
import rapidui.eventhandler.OnCreateContextMenuRegistrar;
import rapidui.eventhandler.OnDragRegistrar;
import rapidui.eventhandler.OnFocusChangeRegistrar;
import rapidui.eventhandler.OnKeyRegistrar;
import rapidui.eventhandler.OnLongClickRegistrar;
import rapidui.eventhandler.OnMenuItemClickInfo;
import rapidui.eventhandler.OnTouchRegistrar;
import rapidui.resource.AnimationLoader;
import rapidui.resource.AnimatorLoader;
import rapidui.resource.ColorLoader;
import rapidui.resource.DimensionLoader;
import rapidui.resource.DrawableLoader;
import rapidui.resource.IntegerLoader;
import rapidui.resource.ResourceLoader;
import rapidui.resource.StringLoader;
import rapidui.util.HashMap3Int;
import rapidui.util.KeyValueEntry;
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
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.hardware.ConsumerIrManager;
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
import android.view.accessibility.CaptioningManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.TextServicesManager;

public abstract class Injector {
	public static final int EXTERNAL_HANDLER_MENU_ITEM_CLICK = 0;
	
	private static HashMap<Class<?>, EventHandlerRegistrar> registrars =
			new HashMap<Class<?>, EventHandlerRegistrar>();
	private static HashMap<Class<?>, ExternalHandlerInfo> externalHandlerInfoList =
			new HashMap<Class<?>, ExternalHandlerInfo>();
	private static HashMap<String, Class<?>> annotationNameMatch;
	private static HashMap<Class<?>, String> systemServices;
	private static LinkedList<ResourceLoader> resourceLoaders;
	
	static {
		registrars.put(OnClick.class, new OnClickRegistrar());
		registrars.put(OnCreateContextMenu.class, new OnCreateContextMenuRegistrar());
		registrars.put(OnDrag.class, new OnDragRegistrar());
		registrars.put(OnFocusChange.class, new OnFocusChangeRegistrar());
		registrars.put(OnKey.class, new OnKeyRegistrar());
		registrars.put(OnLongClick.class, new OnLongClickRegistrar());
		registrars.put(OnTouch.class, new OnTouchRegistrar());
		registrars.put(OnCheckedChange.class, new OnCheckedChangeRegistrar());
		
		externalHandlerInfoList.put(OnMenuItemClick.class, new OnMenuItemClickInfo());
	}
	
	protected Activity activity;
	protected Object memberContainer;
	protected ViewFinder viewFinder;
	
	private LinkedList<KeyValueEntry<IntentFilter, BroadcastReceiver>> receiversOnCreate;
	private LinkedList<KeyValueEntry<IntentFilter, BroadcastReceiver>> receiversOnStart;
	private LinkedList<KeyValueEntry<IntentFilter, BroadcastReceiver>> receiversOnResume;
	
	public Injector(Activity activity, Object memberContainer, ViewFinder viewFinder) {
		this.activity = activity;
		this.memberContainer = memberContainer;
		this.viewFinder = viewFinder;
	}
	
	private static boolean isClassRoot(Class<?> cls) {
		return cls.equals(RapidActivity.class);
	}
	
	public void injectCommonThings() {
		final Resources res = activity.getResources();
		final Intent intent = activity.getIntent();
		final Bundle extras = (intent == null ? null : intent.getExtras());
		
		Class<?> cls = memberContainer.getClass();
		
		while (cls != null && !isClassRoot(cls)) {
			// Fields
			
			for (Field field: cls.getDeclaredFields()) {
				// @SystemService
				
				if (field.isAnnotationPresent(SystemService.class)) {
					final Class<?> fieldType = field.getType();
					
					Object service = null;
					
					if (fieldType.equals(BluetoothAdapter.class)) {
						if (Build.VERSION.SDK_INT >= 18) {
							service = activity.getSystemService(Context.BLUETOOTH_SERVICE);
						} else {
							service = BluetoothAdapter.getDefaultAdapter();
						}
//					} else if (fieldClass.equals(DisplayManagerCompat.class)) {
//						service = DisplayManagerCompat.getInstance(activity);
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
				
				// @Extra
				
				if (extras != null) {
					final Extra extra = field.getAnnotation(Extra.class);
					if (extra != null) {
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
				}
				
				// @Resource
				
				final Resource resource = field.getAnnotation(Resource.class);
				if (resource != null) {
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
			}
			
			// Methods
			
			for (final Method method: cls.getDeclaredMethods()) {
				final Receiver receiver = (Receiver) method.getAnnotation(Receiver.class);
				if (receiver != null) {
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
					if (lifecycle == Lifecycle.CREATE) {
						if (receiversOnCreate == null) {
							receiversOnCreate = new LinkedList<KeyValueEntry<IntentFilter,BroadcastReceiver>>();
						}
						receiversOnCreate.add(new KeyValueEntry<IntentFilter, BroadcastReceiver>(filter, broadcastReceiver));
					} else if (lifecycle == Lifecycle.RESUME) {
						if (receiversOnResume == null) {
							receiversOnResume = new LinkedList<KeyValueEntry<IntentFilter,BroadcastReceiver>>();
						}
						receiversOnResume.add(new KeyValueEntry<IntentFilter, BroadcastReceiver>(filter, broadcastReceiver));
					} else {
						if (receiversOnStart == null) {
							receiversOnStart = new LinkedList<KeyValueEntry<IntentFilter,BroadcastReceiver>>();
						}
						receiversOnStart.add(new KeyValueEntry<IntentFilter, BroadcastReceiver>(filter, broadcastReceiver));
					}
				}
			}
			
			cls = cls.getSuperclass();
		}
	}
	
	public void injectViews() {
		final Resources res = activity.getResources();
		final SparseArray<View> viewMap = new SparseArray<View>();
		final String packageName = activity.getPackageName();
		
		// [viewId][registrar][annotation][method]
		HashMap3Int<EventHandlerRegistrar, Class<?>, Method> methodMap = null;
		
		// [viewId][eventCategoryName][eventName][method]
		HashMap3Int<String, String, Method> customEvents = null;

		Class<?> cls = memberContainer.getClass();
		
		while (cls != null && !cls.equals(RapidActivity.class)) {
			for (Field field: cls.getDeclaredFields()) {
				// @LayoutElement
				
				final LayoutElement layoutElement = field.getAnnotation(LayoutElement.class);
				if (layoutElement != null) {
					int id = layoutElement.value();
					if (id == 0) {
						String name = field.getName();
						if (name.length() >= 2 && name.charAt(0) == 'm') {
							final char c = name.charAt(1);
							if (Character.isUpperCase(c)) {
								name = Character.toLowerCase(c) + name.substring(2);
							}
						}

						id = res.getIdentifier(ResourceUtils.toLowerUnderscored(name), "id", packageName);
						if (id == 0) {
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
	
			// Methods
			
			for (final Method method: cls.getDeclaredMethods()) {
				for (Annotation annotation: method.getAnnotations()) {
					final Class<?> annotationType = annotation.annotationType();
					
					// @On
					
					if (annotationType.equals(On.class)) {
						if (customEvents == null) {
							customEvents = new HashMap3Int<String, String, Method>();							
						}
						
						final On on = (On) annotation;
						
						final String event = on.event();
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
							customEvents.put(id, category, name, method);
						}
						
						continue;
					}
					
					// @EventHandler
					
					if (annotationType.equals(EventHandler.class)) {
						final String name = method.getName();
						final int underscoreIndex = name.indexOf('_');
						
						if (underscoreIndex >= 0) {
							final String idName = name.substring(0, underscoreIndex);
							final String annotationName = name.substring(underscoreIndex + 1);

							final int id = ResourceUtils.findResourceId(activity, idName, "id");
							
							if (id != 0 && annotationName.length() > 0) {
								initAnnotationNameMatchList();
								
								final Class<?> annotationType2 = annotationNameMatch.get(annotationName);
								if (annotationType2 != null) {
									final EventHandlerRegistrar registrar = registrars.get(annotationType2);
									if (registrar != null) {
										if (methodMap == null) {
											methodMap = new HashMap3Int<EventHandlerRegistrar, Class<?>, Method>();
										}
										methodMap.put(id, registrar, annotationType2, method);
										continue;
									}
									
									final ExternalHandlerInfo info = externalHandlerInfoList.get(annotationType2);
									if (info != null) {
										registerExternalHandler(info.getType(), id, method);
										continue;
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
								
								if (customEvents == null) {
									customEvents = new HashMap3Int<String, String, Method>();							
								}
								
								customEvents.put(id, eventCategory, eventName, method);
								continue;
							}
						}
						
						continue;
					}
					
					final EventHandlerRegistrar registrar = registrars.get(annotationType);
					if (registrar != null) {
						if (methodMap == null) {
							methodMap = new HashMap3Int<EventHandlerRegistrar, Class<?>, Method>();
						}
						
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
						continue;
					}
				}
			}
			
			cls = cls.getSuperclass();
		}
		
		// Register event handlers
		
		if (methodMap != null) {
			for (Entry<Integer, HashMap<EventHandlerRegistrar, HashMap<Class<?>, Method>>> entry: methodMap) {
				final int id = entry.getKey();
				
				for (Entry<EventHandlerRegistrar, HashMap<Class<?>, Method>> entry2: entry.getValue().entrySet()) {
					final EventHandlerRegistrar registrar = entry2.getKey();
					final HashMap<Class<?>, Method> methods = entry2.getValue();
					
					final Object target = findViewById(viewFinder, id, viewMap);
					registrar.registerEventListener(target, memberContainer, methods);
				}
			}
		}
		
		// Register custom event handlers
		
		if (customEvents != null) {
			for (Entry<Integer, HashMap<String, HashMap<String, Method>>> entry: customEvents) {
				final int id = entry.getKey();
				
				for (Entry<String, HashMap<String, Method>> entry2: entry.getValue().entrySet()) {
					final String category = entry2.getKey();
					final HashMap<String, Method> methods = entry2.getValue();
					
					final Object target = findViewById(viewFinder, id, viewMap);
					registerCustomEventHandler(target, memberContainer, category, methods);
				}
			}
		}
	}
	
	private void registerCustomEventHandler(Object target, Object instance, String category, final HashMap<String, Method> delegates) {
		final String eventRegistrarMethod = "setOn" + category + "Listener";

		Class<?> targetType = target.getClass();
		
		while (targetType != null && !targetType.equals(Object.class)) {
			for (Method method2: targetType.getDeclaredMethods()) {
				final String method2Name = method2.getName();
				final Class<?>[] params = method2.getParameterTypes();
				
				if (method2Name.equals(eventRegistrarMethod) && params.length == 1) {
					final Class<?> listenerType = params[0];
					
					final InvocationHandler invocationHandler;
					
					if (delegates.size() == 1 && delegates.containsKey("")) {
						final Method delegate = delegates.get("");
						invocationHandler = new InvocationHandler() {
							@Override
							public Object invoke(Object proxy, Method method, Object[] args)
									throws Throwable {
		
								return delegate.invoke(memberContainer, args);
							}
						};
					} else {
						invocationHandler = new InvocationHandler() {
							@Override
							public Object invoke(Object proxy, Method method, Object[] args)
									throws Throwable {
	
								final String eventName;
	
								final String methodName = method.getName();
								if (methodName.startsWith("on")) {
									eventName = methodName.substring(2);
								} else {
									eventName = methodName;
								}
								
								final Method delegate = delegates.get(eventName);
								if (delegate != null) {
									return delegate.invoke(memberContainer, args);
								} else {
									return null;
								}
							}
						};
					}
					
					final Object proxy = Proxy.newProxyInstance(
							listenerType.getClassLoader(),
							new Class<?>[] { listenerType },
							invocationHandler);
					
					try {
						method2.invoke(target, proxy);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					break;
				}
			}
			
			targetType = targetType.getSuperclass();
		}
	}

	public void registerExternalHandler(int type, int id, Method method) {
	}

	private static View findViewById(ViewFinder viewFinder, int id, SparseArray<View> viewMap) {
		View v = viewMap.get(id);
		if (v == null) {
			v = viewFinder.findViewById(id);
			viewMap.put(id, v);
		}
		
		return v;
	}
	
	private static void initAnnotationNameMatchList() {
		if (annotationNameMatch != null) return;

		annotationNameMatch = new HashMap<String, Class<?>>();
		
		for (Class<?> cls: registrars.keySet()) {
			final String name = cls.getSimpleName().substring(2);
			annotationNameMatch.put(name, cls);
		}
		for (Class<?> cls: externalHandlerInfoList.keySet()) {
			final String name = cls.getSimpleName().substring(2);
			annotationNameMatch.put(name, cls);
		}
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
	
	public void restoreInstanceStates(Bundle bundle) {
		Class<?> cls = memberContainer.getClass();
		while (cls != null && !isClassRoot(cls)) {
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
		while (cls != null && !isClassRoot(cls)) {
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
	
	public void registerReceiversOnCreate() {
		if (receiversOnCreate == null) return;
		registerReceivers(receiversOnCreate);
	}

	public void registerReceiversOnStart() {
		if (receiversOnStart == null) return;
		registerReceivers(receiversOnStart);
	}

	public void registerReceiversOnResume() {
		if (receiversOnResume == null) return;
		registerReceivers(receiversOnResume);
	}
	
	public void unregisterReceiversOnDestroy() {
		if (receiversOnCreate == null) return;
		unregisterReceivers(receiversOnCreate);
	}

	public void unregisterReceiversOnStop() {
		if (receiversOnStart == null) return;
		unregisterReceivers(receiversOnStart);
	}

	public void unregisterReceiversOnPause() {
		if (receiversOnResume == null) return;
		unregisterReceivers(receiversOnResume);
	}
	
	private void registerReceivers(LinkedList<KeyValueEntry<IntentFilter, BroadcastReceiver>> receivers) {
		for (KeyValueEntry<IntentFilter, BroadcastReceiver> entry: receivers) {
			final IntentFilter filter = entry.getKey();
			final BroadcastReceiver receiver = entry.getValue();
			
			activity.registerReceiver(receiver, filter);
		}
	}

	private void unregisterReceivers(LinkedList<KeyValueEntry<IntentFilter, BroadcastReceiver>> receivers) {
		for (KeyValueEntry<IntentFilter, BroadcastReceiver> entry: receivers) {
			final BroadcastReceiver receiver = entry.getValue();
			try {
				activity.unregisterReceiver(receiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
}
