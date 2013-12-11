package rapidui;

import rapidui.test.unittest.R;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Resources;
import android.test.SingleLaunchActivityTestCase;
import android.test.UiThreadTest;
import android.view.KeyEvent;
import android.view.View;

public class FragmentUnitTest extends SingleLaunchActivityTestCase<TestFragmentActivity> {
	private TestFragmentActivity activity;
	private TestFragment fragment;
	
	public FragmentUnitTest() {
		super("rapidui.test.unittest", TestFragmentActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		fragment = (TestFragment) activity.getFragmentManager().findFragmentById(R.id.fragment_test);
	}
	
	public void testMenu() {
		fragment.settingsMenuClicked = false;
		
		final Instrumentation inst = getInstrumentation();
		
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		inst.invokeMenuActionSync(activity, R.id.action_settings, 0);
		
		assertEquals(true, fragment.settingsMenuClicked);
	}
	
	public void testFindViewsById() {
		final View textHelloWorld = fragment.getView().findViewById(R.id.text_hello_world);
		assertSame(textHelloWorld, fragment.textHelloWorld);
		assertSame(textHelloWorld, fragment.mTextHelloWorld);
		assertSame(textHelloWorld, fragment.helloWorld);
		
		final View button1 = fragment.getView().findViewById(R.id.button1);
		assertSame(button1, fragment.button1);
	}
	
	public void testSystemServices() {
		assertSame(activity.getSystemService(Context.ALARM_SERVICE), fragment.alarmManager);
		assertSame(activity.getSystemService(Context.AUDIO_SERVICE), fragment.audioManager);
		assertSame(activity.getSystemService(Context.LOCATION_SERVICE), fragment.locationManager);
	}
	
	@UiThreadTest
	public void testEventHandlers() {
		fragment.button1Clicked = false;
		assertTrue(fragment.button1.performClick());
		assertTrue(fragment.button1Clicked);
		
		fragment.button2Clicked = false;
		assertTrue(fragment.button2.performClick());
		assertTrue(fragment.button2Clicked);
		
		fragment.checkbox1checked = false;
		fragment.checkbox1.setChecked(true);
		assertTrue(fragment.checkbox1checked);
		
		fragment.customHandlerInvoked = false;
		fragment.customView.test();
		assertTrue(fragment.customHandlerInvoked);
		
		fragment.customHandler1Invoked = false;
		fragment.customView.test1();
		assertTrue(fragment.customHandler1Invoked);
		
		fragment.customHandler2Argument = null;
		fragment.customView.test2(2, "asdf");
		assertTrue("asdf".equals(fragment.customHandler2Argument));
		
		fragment.customHandler3Invoked = false;
		fragment.customView.test3();
		assertTrue(fragment.customHandler3Invoked);
		
		fragment.editTextContent = null;
		fragment.editText.setText("asdf");
		assertEquals("asdf", fragment.editTextContent);
		
		fragment.listItemClicked = -1;
		assertTrue(fragment.listView.performItemClick(null, 1, 0));
		assertEquals(1, fragment.listItemClicked);
	}
	
	public void testResources() {
		final Resources res = fragment.getResources();
		
		final int testInteger = res.getInteger(R.integer.test_integer);
		assertEquals(testInteger, fragment.testInteger);
		assertEquals(testInteger, fragment.testInteger2);
		
		assertEquals(res.getDimension(R.dimen.test_dimen), fragment.testDimen);
		assertEquals(res.getDimensionPixelOffset(R.dimen.test_dimen), fragment.testDimenOffset);
		assertEquals(res.getDimensionPixelSize(R.dimen.test_dimen), fragment.testDimenSize);
		
		assertEquals(res.getString(R.string.app_name), fragment.appName);
	}
}
