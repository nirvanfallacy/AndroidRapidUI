package rapidui.adapter;

public interface AsyncDataBinderCallback {
	void done(Object result);
	void progress(Object data);
}
