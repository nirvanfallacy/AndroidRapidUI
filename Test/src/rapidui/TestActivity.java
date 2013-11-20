package rapidui;

import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import rapidui.annotation.OptionsMenu;
import rapidui.annotation.eventhandler.OnMenuItemClick;
import rapidui.test.R;
import android.view.MenuItem;
import android.widget.TextView;

@Layout
@OptionsMenu
public class TestActivity extends RapidActivity {
	boolean settingsMenuClicked = false;
	
	@LayoutElement
	TextView textHelloWorld;

	@LayoutElement
	TextView text_hello_world;

	@LayoutElement(R.id.text_hello_world)
	TextView helloWorld;
	
	@OnMenuItemClick(R.id.action_settings)
	boolean actionSettings_MenuItemClick(MenuItem item) {
		settingsMenuClicked = true;
		return true;
	}
}
