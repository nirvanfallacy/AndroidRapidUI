package rapidui.test.basictest.font;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import rapidui.RapidActivity;
import rapidui.annotation.Font;
import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import rapidui.test.basictest.R;

@Layout
public class FontTestActivity extends RapidActivity {
	@Font(R.string.font_android)
	Typeface fontAndroid;

	@Font(path="Android.ttf")
	Typeface fontAndroid2;
	
	@LayoutElement(id=R.id.text_view_1, font=R.string.font_android)
	TextView textView1;

	@LayoutElement(id=R.id.text_view_2)
	TextView textView2;

	@LayoutElement(id=R.id.text_view_3)
	TextView textView3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		textView2.setTypeface(fontAndroid);
		textView3.setTypeface(fontAndroid2);
	}
}
