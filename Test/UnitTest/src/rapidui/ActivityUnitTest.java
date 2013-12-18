package rapidui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import rapidui.test.adapter.ListItem1;
import rapidui.test.unittest.R;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Resources;
import android.test.SingleLaunchActivityTestCase;
import android.test.UiThreadTest;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

public class ActivityUnitTest extends SingleLaunchActivityTestCase<TestActivity> {
	private TestActivity activity;
	
	public ActivityUnitTest() {
		super("rapidui.test.unittest", TestActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
	}
	
	public void testMenu() {
		activity.settingsMenuClicked = false;
		
		final Instrumentation inst = getInstrumentation();
		
		inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		inst.invokeMenuActionSync(activity, R.id.action_settings, 0);
		
		assertEquals(true, activity.settingsMenuClicked);
	}
	
	public void testFindViewsById() {
		final View textHelloWorld = activity.findViewById(R.id.text_hello_world);
		assertSame(textHelloWorld, activity.textHelloWorld);
		assertSame(textHelloWorld, activity.mTextHelloWorld);
		assertSame(textHelloWorld, activity.helloWorld);
		
		final View button1 = activity.findViewById(R.id.button1);
		assertSame(button1, activity.button1);
	}
	
	public void testSystemServices() {
		assertSame(activity.getSystemService(Context.ALARM_SERVICE), activity.alarmManager);
		assertSame(activity.getSystemService(Context.AUDIO_SERVICE), activity.audioManager);
		assertSame(activity.getSystemService(Context.LOCATION_SERVICE), activity.locationManager);
	}
	
	@UiThreadTest
	public void testEventHandlers() {
		activity.button1Clicked = false;
		assertTrue(activity.button1.performClick());
		assertTrue(activity.button1Clicked);
		
		activity.button2Clicked = false;
		assertTrue(activity.button2.performClick());
		assertTrue(activity.button2Clicked);
		
		activity.checkbox1checked = false;
		activity.checkbox1.setChecked(true);
		assertTrue(activity.checkbox1checked);
		
		activity.customHandlerInvoked = false;
		activity.customView.test();
		assertTrue(activity.customHandlerInvoked);
		
		activity.customHandler1Invoked = false;
		activity.customView.test1();
		assertTrue(activity.customHandler1Invoked);
		
		activity.customHandler2Argument = null;
		activity.customView.test2(2, "asdf");
		assertTrue("asdf".equals(activity.customHandler2Argument));
		
		activity.customHandler3Invoked = false;
		activity.customView.test3();
		assertTrue(activity.customHandler3Invoked);
		
		activity.editTextContent = null;
		activity.editText.setText("asdf");
		assertEquals("asdf", activity.editTextContent);
		
		activity.listItemClicked = -1;
		assertTrue(activity.listView.performItemClick(null, 1, 0));
		assertEquals(1, activity.listItemClicked);
	}
	
	public void testResources() {
		final Resources res = activity.getResources();
		
		final int testInteger = res.getInteger(R.integer.test_integer);
		assertEquals(testInteger, activity.testInteger);
		assertEquals(testInteger, activity.testInteger2);
		
		assertEquals(res.getDimension(R.dimen.test_dimen), activity.testDimen);
		assertEquals(res.getDimensionPixelOffset(R.dimen.test_dimen), activity.testDimenOffset);
		assertEquals(res.getDimensionPixelSize(R.dimen.test_dimen), activity.testDimenSize);
		
		assertEquals(res.getString(R.string.app_name), activity.appName);
	}
	
	@SuppressWarnings("deprecation")
	@UiThreadTest
	public void testAdapter() {
		final ListView listView = activity.listView;
		
		// Setup
		
		final List<Object> items = new ArrayList<Object>();
		
		items.add(new ListItem1(false, "qwerty", 12, 5));
		items.add(new ListItem1(false, "asdf", 34, 4.5f));
		items.add(new ListItem1(true, "zxcv", 56, 4));
		
		final RapidAdapter adapter = new RapidAdapter(activity, items, ListItem1.class);
		listView.setAdapter(adapter);
		
		// Force layout
		
		final Display display = activity.getWindowManager().getDefaultDisplay();

		final int w = display.getWidth();
		final int h = display.getHeight();
		
		final int wspec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
		final int hspec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
		
		listView.measure(wspec, hspec);
		listView.layout(0, 0, w, h);
		
		// Validate
		
		View v;
		
		v = listView.getChildAt(0);
		assertFalse(((CheckBox) v.findViewById(R.id.checkbox)).isChecked());
		assertEquals("qwerty", ((TextView) v.findViewById(R.id.textview)).getText().toString());
		assertEquals(12, ((ProgressBar) v.findViewById(R.id.progressbar)).getProgress());
		assertEquals(5f, ((RatingBar) v.findViewById(R.id.ratingbar)).getRating());

		v = listView.getChildAt(1);
		assertFalse(((CheckBox) v.findViewById(R.id.checkbox)).isChecked());
		assertEquals("asdf", ((TextView) v.findViewById(R.id.textview)).getText().toString());
		assertEquals(34, ((ProgressBar) v.findViewById(R.id.progressbar)).getProgress());
		assertEquals(4.5f, ((RatingBar) v.findViewById(R.id.ratingbar)).getRating());

		v = listView.getChildAt(2);
		assertTrue(((CheckBox) v.findViewById(R.id.checkbox)).isChecked());
		assertEquals("zxcv", ((TextView) v.findViewById(R.id.textview)).getText().toString());
		assertEquals(56, ((ProgressBar) v.findViewById(R.id.progressbar)).getProgress());
		assertEquals(4f, ((RatingBar) v.findViewById(R.id.ratingbar)).getRating());
	}
	
	public void testTaskGet() {
		RapidTask<String> task = new RapidTask<String>() {
			@Override
			protected String doInBackground(Object... params) throws Exception {
				return params[0].toString();
			}
		};
		
		task.execute("asdf");

		String result = null;
		try {
			result = task.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		assertEquals("asdf", result);
	}
}
