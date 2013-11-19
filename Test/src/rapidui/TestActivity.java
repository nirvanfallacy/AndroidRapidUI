package rapidui;

import rapidui.annotation.EventHandler;
import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import rapidui.annotation.OptionsMenu;
import rapidui.test.R;
import android.view.MenuItem;
import android.widget.TextView;

@Layout
@OptionsMenu
public class TestActivity extends Activity {
	boolean settingsMenuClicked = false;
	
	@LayoutElement
	TextView textHelloWorld;

	@LayoutElement
	TextView text_hello_world;

	@LayoutElement(R.id.text_hello_world)
	TextView helloWorld;
	
	@EventHandler(target=R.id.action_settings)
	private MenuItem.OnMenuItemClickListener settingsClick = new MenuItem.OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			settingsMenuClicked = true;
			return true;
		}
	};
}
