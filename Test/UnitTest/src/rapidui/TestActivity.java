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
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
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
	
	int listItemClicked;
	
	String editTextContent;
	
	@LayoutElement                        TextView textHelloWorld;
	@LayoutElement                        TextView mTextHelloWorld;
	@LayoutElement(R.id.text_hello_world) TextView helloWorld;
	
	@LayoutElement CustomView customView;
	@LayoutElement EditText editText;
	@LayoutElement Button button1;
	@LayoutElement Button button2;
	@LayoutElement CheckBox checkbox1;
	@LayoutElement ListView listView;
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final String[] items = getResources().getStringArray(R.array.test_list_item);
		final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
		
		listView.setAdapter(listAdapter);
	}

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
	
	@EventHandler
	void listView_ItemClick(AdapterView<?> parent, View v, int position, long id) {
		listItemClicked = position;
	}
}
