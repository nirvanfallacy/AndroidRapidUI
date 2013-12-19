package rapidui.adapter;

import rapidui.Cancelable;

public interface AsyncResult {
	void done();
	void done(Object result);
	void progress(Object data);
	void setCancelable(Cancelable c);
}
