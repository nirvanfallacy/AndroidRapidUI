package rapidui.adapter;

public interface AsyncCallback {
	void done();
	void done(Object result);
	void progress(Object data);
}
