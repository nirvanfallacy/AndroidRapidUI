package rapidui;

public abstract class AnonymousTask<Result> extends RapidTask<Object, Result> {
	@Override
	protected Result doInBackground(Object... params) throws Exception {
		return doInBackground();
	}
	
	protected abstract Result doInBackground() throws Exception;
}