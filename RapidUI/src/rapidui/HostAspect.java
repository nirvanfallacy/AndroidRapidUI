package rapidui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import rapidui.annotation.Extra;
import rapidui.annotation.OptionsMenu;
import rapidui.annotation.SearchBar;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public abstract class HostAspect extends ObjectAspect {
	private Lifecycle currentLifecycle;
	
	public HostAspect(Activity activity, Object memberContainer, Host host) {
		super(activity, memberContainer, host);
	}
	
	protected Activity getActivity() {
		return (Activity) context;
	}
	
	protected Host getHost() {
		return (Host) viewFinder;
	}
	
	@Override
	public void injectCommonThings() {
		super.injectCommonThings();
		
		final Activity activity = getActivity();
		final Intent intent = activity.getIntent();
		final Bundle extras = (intent == null ? null : intent.getExtras());
		
		Class<?> cls = memberContainer.getClass();
		
		while (cls != null && !isRapidClass(cls)) {
			// Fields
			
			for (Field field: cls.getDeclaredFields()) {
				final int modifier = field.getModifiers();
				if ((modifier & Modifier.STATIC) != 0) continue;
				
				// @Extra
				if (extras != null) {
					final Extra extra = field.getAnnotation(Extra.class);
					if (extra != null) {
						injectExtra(field, extra, extras);
					}
				}
			}
			
			cls = cls.getSuperclass();
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

	public void injectOptionsMenu(MenuInflater inflater, Menu menu) {
		final Resources res = context.getResources();
		
		SearchBar sb = null;
		
		Class<?> cls = memberContainer.getClass();
		
		while (cls != null && !isRapidClass(cls)) {
			final OptionsMenu optionsMenu = cls.getAnnotation(OptionsMenu.class);
			if (optionsMenu != null) {
				int id = optionsMenu.value();
				if (id == 0) {
					final String packageName = context.getPackageName();
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
		final Activity activity = getActivity();
		
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
									return (Boolean) event2.method.invoke(memberContainer, event2.argMatcher.map(query));
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
									return (Boolean) event1.method.invoke(memberContainer, event1.argMatcher.map(newText));
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
									return (Boolean) event2.method.invoke(memberContainer, event2.argMatcher.map(query));
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
									return (Boolean) event1.method.invoke(memberContainer, event1.argMatcher.map(newText));
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

	public Lifecycle getCurrentLifecycle() {
		return currentLifecycle;
	}

	public void setCurrentLifecycle(Lifecycle currentLifecycle) {
		this.currentLifecycle = currentLifecycle;
	}

	public void registerListenersToCurrentLifecycle() {
		registerListenersUpTo(currentLifecycle);
	}

	public void registerHostEvent(Object annotation, int type, Object id, final Method method) {
		switch (type) {
		case HOST_EVENT_MENU_ITEM_CLICK:
		case HOST_EVENT_QUERY_TEXT_CHANGE:
		case HOST_EVENT_QUERY_TEXT_SUBMIT:
			if (id == null) break;
			
			final Class<?>[] args = getHostEventArguments(type);
			final ArgumentMapper argMatcher = new ArgumentMapper(args, method);
			putHostEventHandler(type, (Integer) id, new EventHandlerInfo(method, argMatcher));
			
			break;
			
		default:
			super.registerHostEvent(annotation, type, id, method);
			break;
		}
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		if (id != 0) {
			final EventHandlerInfo info = getHostEventHandler(HOST_EVENT_MENU_ITEM_CLICK, id);
			if (info != null) {
				try {
					info.method.setAccessible(true);
					return parseBooleanResult(info.method.invoke(memberContainer, info.argMatcher.map(item)), true);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		return false;
	}

	protected abstract String getHostNamePostFix();
}
