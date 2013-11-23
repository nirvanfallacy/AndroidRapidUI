package rapidui;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import rapidui.annotation.SystemService;
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
import rapidui.eventhandler.OnClickRegistrar;
import rapidui.eventhandler.OnCreateContextMenuRegistrar;
import rapidui.eventhandler.OnDragRegistrar;
import rapidui.eventhandler.OnFocusChangeRegistrar;
import rapidui.eventhandler.OnKeyRegistrar;
import rapidui.eventhandler.OnLongClickRegistrar;
import rapidui.eventhandler.OnMenuItemClickInfo;
import rapidui.eventhandler.OnTouchRegistrar;
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
	
	static {
		registrars.put(OnClick.class, new OnClickRegistrar());
		registrars.put(OnCreateContextMenu.class, new OnCreateContextMenuRegistrar());
		registrars.put(OnDrag.class, new OnDragRegistrar());
		registrars.put(OnFocusChange.class, new OnFocusChangeRegistrar());
		registrars.put(OnKey.class, new OnKeyRegistrar());
		registrars.put(OnLongClick.class, new OnLongClickRegistrar());
		registrars.put(OnTouch.class, new OnTouchRegistrar());
		
		externalHandlerInfoList.put(OnMenuItemClick.class, new OnMenuItemClickInfo());
	}
	
	protected Activity activity;
	protected Object memberContainer;
	protected ViewFinder viewFinder;
	
	private LinkedList<KeyValueEntry<Field, String>> instanceStates;
	
	private LinkedList<KeyValueEntry<IntentFilter, BroadcastReceiver>> receiversOnCreate;
	private LinkedList<KeyValueEntry<IntentFilter, BroadcastReceiver>> receiversOnStart;
	private LinkedList<KeyValueEntry<IntentFilter, BroadcastReceiver>> receiversOnResume;
	
	public Injector(Activity activity, Object memberContainer, ViewFinder viewFinder) {
		this.activity = activity;
		this.memberContainer = memberContainer;
		this.viewFinder = viewFinder;
	}
	
	public void injectCommonThings() {
		final Intent intent = activity.getIntent();
		final Bundle extras = (intent == null ? null : intent.getExtras());
		
		Class<?> cls = memberContainer.getClass();
		
		while (cls != null && !cls.equals(RapidActivity.class)) {
			// Fields
			
			for (Field field: cls.getDeclaredFields()) {
				// @SystemService
				
				if (field.isAnnotationPresent(SystemService.class)) {
					Object service = null;
					
					final Class<?> fieldClass = field.getType();
					if (fieldClass.equals(BluetoothAdapter.class)) {
						if (Build.VERSION.SDK_INT >= 18) {
							service = activity.getSystemService(Context.BLUETOOTH_SERVICE);
						} else {
							service = BluetoothAdapter.getDefaultAdapter();
						}
//					} else if (fieldClass.equals(DisplayManagerCompat.class)) {
//						service = DisplayManagerCompat.getInstance(activity);
					} else {
						initSystemServiceList();
						
						final String serviceName = systemServices.get(fieldClass);
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
				
				// @InstanceState

				final InstanceState instanceState = field.getAnnotation(InstanceState.class);
				if (instanceState != null) {
					String key = instanceState.value();
					if (key.length() == 0) {
						key = field.getName();
					}
					
					if (instanceStates == null) {
						instanceStates = new LinkedList<KeyValueEntry<Field,String>>();
					}
					instanceStates.add(new KeyValueEntry<Field, String>(field, key));
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
		final HashMap3Int<EventHandlerRegistrar, Class<?>, Method> methodMap =
				new HashMap3Int<EventHandlerRegistrar, Class<?>, Method>();

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

						id = res.getIdentifier(toLowerUnderscored(name), "id", packageName);
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
					
					// @EventHandler
					
					if (annotationType.equals(EventHandler.class)) {
						final String name = method.getName();
						final int underscoreIndex = name.indexOf('_');
						
						if (underscoreIndex >= 0) {
							final String idName = name.substring(0, underscoreIndex);
							final String annotationName = name.substring(underscoreIndex + 1);

							int id = res.getIdentifier(toLowerUnderscored(idName), "id", packageName);
							if (id == 0) {
								id = res.getIdentifier(idName, "id", packageName);
							}
							
							if (id != 0 && annotationName.length() > 0) {
								initAnnotationNameMatchList();
								
								final Class<?> annotationType2 = annotationNameMatch.get(annotationName);
								if (annotationType2 != null) {
									final EventHandlerRegistrar registrar = registrars.get(annotationType2);
									if (registrar != null) {
										methodMap.put(id, registrar, annotationType2, method);
										continue;
									}
									
									final ExternalHandlerInfo info = externalHandlerInfoList.get(annotationType2);
									if (info != null) {
										registerExternalHandler(info.getType(), id, method);
									}
								}
							}
						}
					}
					
					final EventHandlerRegistrar registrar = registrars.get(annotationType);
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
						continue;
					}
				}
			}
			
			cls = cls.getSuperclass();
		}
		
		// Register event handlers
		
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
	
	static String toLowerUnderscored(String s) {
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
		if (instanceStates == null) return;
		
		for (KeyValueEntry<Field, String> entry: instanceStates) {
			final Field field = entry.getKey();
			final Object value = bundle.get(entry.getValue());
			
			if (value != null) {
				field.setAccessible(true);
				try {
					field.set(memberContainer, value);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void saveInstanceStates(Bundle bundle) {
		if (instanceStates == null) return;
		
		for (KeyValueEntry<Field, String> entry: instanceStates) {
			final String key = entry.getValue();
			final Field field = entry.getKey();
			
			try {
				field.setAccessible(true);
				final Object value = field.get(memberContainer);

				putIntoBundle(bundle, key, value);
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			}
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
}
