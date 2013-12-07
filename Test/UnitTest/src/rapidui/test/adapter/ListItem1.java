package rapidui.test.adapter;

import rapidui.annotation.AdapterItem;
import rapidui.annotation.adapter.BindToChecked;
import rapidui.annotation.adapter.BindToProgress;
import rapidui.annotation.adapter.BindToProgressMax;
import rapidui.annotation.adapter.BindToRating;
import rapidui.annotation.adapter.BindToRatingNumStars;
import rapidui.annotation.adapter.BindToRatingStepSize;
import rapidui.annotation.adapter.BindToText;
import rapidui.test.unittest.R;

@AdapterItem(R.layout.list_item_1)
public class ListItem1 {
	@BindToRatingNumStars(R.id.ratingbar)
	public static final int MAX_STAR = 5;
	
	@BindToRatingStepSize(R.id.ratingbar)
	public static final float STAR_STEP = 0.5f;
	
	@BindToProgressMax(R.id.progressbar)
	public static final int MAX_PROGRESS = 100;
	
	@BindToChecked(R.id.checkbox)
	public boolean checked = false;
	
	@BindToText(R.id.textview)
	public String text;
	
	@BindToProgress(R.id.progressbar)
	public int progress;
	
	@BindToRating(R.id.ratingbar)
	public float rating;

	public ListItem1(boolean checked, String text, int progress, float rating) {
		this.checked = checked;
		this.text = text;
		this.progress = progress;
		this.rating = rating;
	}
}
