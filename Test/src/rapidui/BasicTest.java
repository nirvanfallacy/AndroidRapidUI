package rapidui;

import rapidui.test.R;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;

public class BasicTest extends ActivityInstrumentationTestCase2<TestActivity> {
	private TestActivity activity;
	
	public BasicTest() {
		super(TestActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
	}
	
	public void testCamelCaseToUnderlinedLowerCase() {
		assertEquals("test", Injector.camelCaseToUnderlinedLowerCase("test"));
		assertEquals("camel_case", Injector.camelCaseToUnderlinedLowerCase("camelCase"));
		assertEquals("pascal_case", Injector.camelCaseToUnderlinedLowerCase("PascalCase"));
		assertEquals("underlined_camel_case", Injector.camelCaseToUnderlinedLowerCase("Underlined_Camel_Case"));
		assertEquals("xml_document", Injector.camelCaseToUnderlinedLowerCase("XMLDocument"));
		assertEquals("simple_xml_parser", Injector.camelCaseToUnderlinedLowerCase("SimpleXMLParser"));
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
		assertSame(textHelloWorld, activity.text_hello_world);
		assertSame(textHelloWorld, activity.helloWorld);
	}
}
