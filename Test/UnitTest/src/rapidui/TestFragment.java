package rapidui;

import android.app.AlarmManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

@Layout
@OptionsMenu
public class TestFragment extends RapidFragment {
	boolean settingsMenuClicked;
	boolean button1Clicked;
	boolean button2Clicked;
	boolean checkbox1checked;
	
	boolean customHandlerInvoked;
	boolean customHandler1Invoked;
	String customHandler2Argument;
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final String[] items = getResources().getStringArray(R.array.test_list_item);
		final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
		
		listView.setAdapter(listAdapter);
	}

	@EventHandler
	boolean actionSettings_MenuItemClick() {
		settingsMenuClicked = true;
		return true;
	}
	
	@EventHandler
	void button1_Click() {
		button1Clicked = true;
	}
	
	@OnClick(R.id.button2)
	void dontCareAboutMethodName() {
		button2Clicked = true;
	}
	
	@EventHandler
	void checkbox1_CheckedChange(boolean isChecked) {
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
	void customView_Test2_Test2(String b) {
		customHandler2Argument = b;
	}

	@EventHandler
	void customView_Test3() {
		customHandler3Invoked = true;
	}
	
	@EventHandler
	void editText_AfterTextChanged(Editable s) {
		editTextContent = s.toString();
	}

	@EventHandler
	void listView_ItemClick(int position) {
		listItemClicked = position;
	}
}
