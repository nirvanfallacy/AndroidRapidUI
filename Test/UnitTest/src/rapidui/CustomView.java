package rapidui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class CustomView extends View {
	public interface OnTestListener {
		void onTest();
	}

	public interface OnTest2Listener {
		void onTest1();
		void onTest2();
	}
	
	private OnTestListener onTest;
	private OnTest2Listener onTest2;
	
	public CustomView(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomView(Context context) {
		super(context);
	}

	public void test() {
		if (onTest != null) {
			onTest.onTest();
		}
	}
	
	public void test1() {
		if (onTest2 != null) {
			onTest2.onTest1();
		}
	}
	
	public void test2() {
		if (onTest2 != null) {
			onTest2.onTest2();
		}
	}
	
	public void setOnTestListener(OnTestListener listener) {
		this.onTest = listener;
	}
	
	public void setOnTest2Listener(OnTest2Listener listener) {
		this.onTest2 = listener;
	}
}
