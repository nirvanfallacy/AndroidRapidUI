package rapidui.sample.applister;

import java.lang.ref.SoftReference;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import rapidui.Cancelable;
import rapidui.RapidTask;
import rapidui.adapter.AsyncCallback;
import rapidui.annotation.AdapterItem;
import rapidui.annotation.adapter.BindToImage;
import rapidui.annotation.adapter.BindToText;

@AdapterItem(R.layout.list_item_app_info)
public class AppInfo implements Comparable<AppInfo> {
	@BindToText(R.id.text_package_name)
	public String packageName;
	
	@BindToText(R.id.text_class_name)
	public String className;
	
	@BindToText(R.id.text_app_title)
	public String appTitle;
	
	private PackageManager pm;
	private ResolveInfo ri;
	private SoftReference<Drawable> icon;
	
	public AppInfo(PackageManager pm, ResolveInfo ri) {
		this.pm = pm;
		this.ri = ri;
		
		packageName = ri.activityInfo.applicationInfo.packageName;
		className = ri.activityInfo.name;
		appTitle = ri.loadLabel(pm).toString();
	}

	@BindToImage(R.id.image_app_icon)
	public void getIcon(final AsyncCallback callback, Cancelable canceler) {
		Drawable d = (icon == null ? null : icon.get());
		if (d != null) {
			callback.done(d);
		} else {
			new RapidTask<Drawable>() {
				@Override
				protected Drawable doInBackground(Object... params) throws Exception {
					return ri.loadIcon(pm);
				}
				
				@Override
				protected void onPostExecute(Drawable result) {
					icon = new SoftReference<Drawable>(result);
					callback.done(result);
				}
			}.execute();
		}
	}

	@Override
	public int compareTo(AppInfo another) {
		return appTitle.compareTo(another.appTitle);
	}
}
