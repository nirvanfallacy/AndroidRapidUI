package rapidui;

import rapidui.annotation.EventHandler;
import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import rapidui.annotation.OptionsMenu;
import rapidui.annotation.SystemService;
import rapidui.annotation.eventhandler.OnClick;
import rapidui.test.R;
import android.app.AlarmManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

@Layout
@OptionsMenu
public class TestActivity extends RapidActivity {
	boolean settingsMenuClicked;
	boolean button1Clicked;
	boolean button2Clicked;
	
	@LayoutElement                        TextView textHelloWorld;
	@LayoutElement                        TextView mTextHelloWorld;
	@LayoutElement(R.id.text_hello_world) TextView helloWorld;
	
	@LayoutElement Button button1;
	@LayoutElement Button button2;
	
	@SystemService AlarmManager alarmManager;
	@SystemService AudioManager audioManager;
	@SystemService LocationManager locationManager;

	@EventHandler
	boolean actionSettings_MenuItemClick(MenuItem item) {
		settingsMenuClicked = true;
		return true;
	}
	
	@EventHandler
	void button1_Click(View v) {
		button1Clicked = true;
	}
	
	@OnClick(R.id.button2)
	void dontCareAboutMethodName(View v) {
		button2Clicked = true;
	}
}
