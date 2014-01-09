package rapidui.test.basictest.adapter;

import rapidui.RapidTask;
import rapidui.adapter.AsyncResult;
import rapidui.annotation.AdapterItem;
import rapidui.annotation.adapter.BindToEnabled;
import rapidui.annotation.adapter.BindToImage;
import rapidui.annotation.adapter.BindToText;
import rapidui.test.basictest.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

@AdapterItem(R.layout.adapter_item_image_text)
public class ListItem3 {
	private Context context;
	private String text;
	private int imageId;

	@BindToEnabled
	private boolean enabled;
	
	public ListItem3(Context context, String text, int imageId) {
		this.context = context;
		this.text = text;
		this.imageId = imageId;
		this.enabled = true;
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
	public void loadImage(final AsyncResult callback) {
		callback.setCancelable(new RapidTask<Drawable>() {
			@Override
			protected Drawable doInBackground(Object... params) {
				callback.progress(null);
				
				final Resources res = context.getResources();
				callback.done(res.getDrawable(imageId));
				
				return null;
			}
		}.execute());
	}

	public ListItem3 disable() {
		enabled = false;
		return this;
	}
}
