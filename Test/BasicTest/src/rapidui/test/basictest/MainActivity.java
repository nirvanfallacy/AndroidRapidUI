package rapidui.test.basictest;

import rapidui.RapidActivity;
import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import rapidui.annotation.SystemService;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

@Layout
public class MainActivity extends RapidActivity {
	@LayoutElement ViewGroup containerButtons;
	
	@SystemService PackageManager pm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e1) {
			return;
		}
		
		for (ActivityInfo ai: pi.activities) {
			final String name = ai.name;
			if (name.equals(MainActivity.class.getName())) continue;
			
			try {
				final Class<?> cls = Class.forName(name);

				final Button button = new Button(this);
				button.setText(ai.loadLabel(pm));
				
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						final Intent intent = new Intent(MainActivity.this, cls);
						startActivity(intent);
					}
				});
				
				containerButtons.addView(button);
			} catch (ClassNotFoundException e) {
				continue;
			}
		}
	}
}
