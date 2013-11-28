package rapidui;

import rapidui.annotation.EventHandler;
import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import rapidui.annotation.OptionsMenu;
import rapidui.annotation.Resource;
import rapidui.annotation.ResourceType;
import rapidui.annotation.SystemService;
import rapidui.annotation.event.On;
import rapidui.annotation.event.OnClick;
import rapidui.test.unittest.R;
import android.app.AlarmManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

@Layout
@OptionsMenu
public class TestActivity extends RapidActivity {
	boolean settingsMenuClicked;
	boolean button1Clicked;
	boolean button2Clicked;
	boolean checkbox1checked;
	
	boolean customHandlerInvoked;
	boolean customHandler1Invoked;
	boolean customHandler2Invoked;
	boolean customHandler3Invoked;
	
	String editTextContent;
	
	@LayoutElement                        TextView textHelloWorld;
	@LayoutElement                        TextView mTextHelloWorld;
	@LayoutElement(R.id.text_hello_world) TextView helloWorld;
	@LayoutElement                        CustomView customView;
	@LayoutElement                        EditText editText;
	
	@LayoutElement Button button1;
	@LayoutElement Button button2;
	@LayoutElement CheckBox checkbox1;
	
	@SystemService AlarmManager alarmManager;
	@SystemService AudioManager audioManager;
	@SystemService LocationManager locationManager;
	
	@Resource                         int testInteger;
	@Resource(R.integer.test_integer) int testInteger2;
	
	@Resource                                     float testDimen;
	@Resource(id=R.dimen.test_dimen,
			  type=ResourceType.DIMENSION_OFFSET) int testDimenOffset;
	@Resource(R.dimen.test_dimen)                 int testDimenSize;
	
	@Resource
	String appName;

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
	
	@EventHandler
	void checkbox1_CheckedChange(CompoundButton buttonView, boolean isChecked) {
		checkbox1checked = isChecked;
	}
	
	@On(id=R.id.custom_view, event="Test")
	void customView_Test() {
		customHandlerInvoked = true;
	}
	
	@On(id=R.id.custom_view, event="Test2.Test1")
	void customView_Test1() {
		customHandler1Invoked = true;
	}
	
	@EventHandler
	void customView_Test2_Test2() {
		customHandler2Invoked = true;
	}

	@EventHandler
	void editText_AfterTextChanged(Editable s) {
		editTextContent = s.toString();
	}
	
	@EventHandler
	void customView_Test3() {
		customHandler3Invoked = true;
	}
}
