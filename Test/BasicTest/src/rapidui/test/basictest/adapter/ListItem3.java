package rapidui.test.basictest.adapter;

import rapidui.Cancelable;
import rapidui.adapter.AsyncDataBinderCallback;
import rapidui.annotation.AdapterItem;
import rapidui.annotation.adapter.BindToImage;
import rapidui.annotation.adapter.BindToText;
import rapidui.test.basictest.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

@AdapterItem(R.layout.adapter_item_image_text)
public class ListItem3 {
	private Context context;
	private String text;
	private int imageId;

	public ListItem3(Context context, String text, int imageId) {
		this.context = context;
		this.text = text;
		this.imageId = imageId;
	}

	@BindToText(R.id.textview)
	public String getText() {
		return text;
	}

	@BindToText(R.id.textview)
	public void setText(String text) {
		this.text = text;
	}

	@BindToImage(R.id.imageview)
	public void loadImage(final AsyncDataBinderCallback callback, Cancelable cancelable) {
		new AsyncTask<Object, Object, Drawable>() {
			@Override
			protected Drawable doInBackground(Object... params) {
				callback.progress(null);
				
				final Resources res = context.getResources();
				callback.done(res.getDrawable(imageId));
				
				return null;
			}
		}.execute();
	}
}
