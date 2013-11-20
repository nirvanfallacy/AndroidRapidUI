package rapidui;

import rapidui.test.R;
import android.app.Instrumentation;
import android.test.SingleLaunchActivityTestCase;
import android.view.KeyEvent;
import android.view.View;

public class BasicTest extends SingleLaunchActivityTestCase<TestActivity> {
	private TestActivity activity;
	
	public BasicTest() {
		super("rapidui.test", TestActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
	}
	
	public void testCamelCaseToUnderlinedLowerCase() {
		assertEquals("test", ActivityInjector.toLowerUnderline("test"));
		assertEquals("camel_case", ActivityInjector.toLowerUnderline("camelCase"));
		assertEquals("pascal_case", ActivityInjector.toLowerUnderline("PascalCase"));
		assertEquals("underlined_camel_case", ActivityInjector.toLowerUnderline("Underlined_Camel_Case"));
		assertEquals("xml_document", ActivityInjector.toLowerUnderline("XMLDocument"));
		assertEquals("simple_xml_parser", ActivityInjector.toLowerUnderline("SimpleXMLParser"));
		assertEquals("ab123", ActivityInjector.toLowerUnderline("AB123"));
		assertEquals("ab123", ActivityInjector.toLowerUnderline("ab123"));
		assertEquals("a_b123", ActivityInjector.toLowerUnderline("aB123"));
		assertEquals("html4_document", ActivityInjector.toLowerUnderline("HTML4Document"));
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
