package rapidui.sample.applister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import rapidui.Lifecycle;
import rapidui.RapidAdapter;
import rapidui.RapidListActivity;
import rapidui.RapidTask;
import rapidui.annotation.Layout;
import rapidui.annotation.OptionsMenu;
import rapidui.annotation.Receiver;
import rapidui.annotation.SearchBar;
import rapidui.annotation.event.OnQueryTextChange;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

@Layout
@OptionsMenu
@SearchBar(id=R.id.action_search, hint="Search an app")
public class MainActivity extends RapidListActivity {
	private RapidAdapter adapter;
	private List<AppInfo> appList;
	private List<AppInfo> items;
	private String filterKeyword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		items = new ArrayList<AppInfo>();
		adapter = new RapidAdapter(this, items, AppInfo.class);
		getListView().setAdapter(adapter);
		
		loadApps();
	}

	private void loadApps() {
		final RapidTask<List<AppInfo>> task = 
			new RapidTask<List<AppInfo>>() {
				@Override
				protected ProgressDialog onCreateProgressDialog() {
					return ProgressDialog.show(MainActivity.this, null, "Loading application list...", true, true);
				}
		
				@Override
				protected List<AppInfo> doInBackground(Object... params)
						throws Exception {
					
					final Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					
					final ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
					final PackageManager pm = getPackageManager();
	
					final List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
					for (ResolveInfo ri: list) {
						if (isCancelled()) return null;
						appList.add(new AppInfo(pm, ri));
					}
					
					Collections.sort(appList);
					
					return appList;
				}
				
				@Override
				protected void onPostExecute(List<AppInfo> result) {
					appList = result;
					
					adapter.clear();
					adapter.addAll(result);
				}
				
				@Override
				protected void onCancelled() {
					finish();
				}
			};
			
		execute(task);
	}

	@OnQueryTextChange(R.id.action_search)
	boolean searchApp(String s) {
		s = s.trim();
		
		if (TextUtils.isEmpty(s)) {
			if (TextUtils.isEmpty(filterKeyword)) {
				return true;
			} else {
				adapter.clear();
				adapter.addAll(appList);
				return true;
			}
		}

		filterKeyword = s;
		
		final String keyword = filterKeyword.toLowerCase(Locale.US);
		final int lastPosition = getListView().getFirstVisiblePosition();
		
		final List<AppInfo> currentList = new ArrayList<AppInfo>();
		currentList.addAll(appList);
		
		final RapidTask<List<AppInfo>> task =
				new RapidTask<List<AppInfo>>() {
					@Override
					protected List<AppInfo> doInBackground(Object... params) throws Exception {
						final ArrayList<AppInfo> searchResult = new ArrayList<AppInfo>();
						
						for (AppInfo ai: currentList) {
							if (isCancelled()) return null;
							
							if (filterApp(ai, keyword)) {
								searchResult.add(ai);
							}
						}
						
						return searchResult;
					}
					
					@Override
					protected void onPostExecute(List<AppInfo> result) {
						adapter.clear();
						adapter.addAll(result);
						
						getListView().setSelection(lastPosition);
					}
				};
				
		executeSingleton("search", task);
		return true;
	}
	
	private static boolean filterApp(AppInfo ai, String keyword) {
		return ai.packageName.toLowerCase(Locale.US).contains(keyword) ||
				ai.className.toLowerCase(Locale.US).contains(keyword) ||
				ai.appTitle.toLowerCase(Locale.US).contains(keyword);
	}
	
	@Receiver(action={Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED},
			  lifecycle=Lifecycle.CREATE,
			  dataScheme="package")
	void onAppChanged(Intent intent) {
		String pkg = intent.getData().toString();
		int index = pkg.indexOf(':');
		pkg = pkg.substring(index + 1);
		
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
			intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setPackage(pkg);
			
			final PackageManager pm = getPackageManager();
			final List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
			
			for (ResolveInfo ri: list) {
				final AppInfo ai = new AppInfo(pm, ri);
				appList.add(ai);
				
				if (TextUtils.isEmpty(filterKeyword) || filterApp(ai, filterKeyword)) {
					items.add(ai);
				}
			}
			
			Collections.sort(appList);
			Collections.sort(items);
		} else {
			for (Iterator<?> it: new Iterator<?>[] { appList.iterator(), items.iterator() }) {
				while (it.hasNext()) {
					final AppInfo ai = (AppInfo) it.next();
					if (ai.packageName.equals(pkg)) {
						it.remove();
						break;
					}
				}
			}
		}

		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		final AppInfo appInfo = adapter.get(position);
		
		final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + appInfo.packageName));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}