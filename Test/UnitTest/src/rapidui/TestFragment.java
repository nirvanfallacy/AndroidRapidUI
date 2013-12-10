package rapidui;

import android.widget.TextView;
import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import rapidui.test.unittest.R;

@Layout
public class TestFragment extends RapidSupportFragment {
	@LayoutElement                        TextView textHelloWorld;
	@LayoutElement                        TextView mTextHelloWorld;
	@LayoutElement(R.id.text_hello_world) TextView helloWorld;
}
