package rapidui.test.basictest.adapter;

import rapidui.annotation.AdapterItem;
import rapidui.annotation.adapter.BindToCheck;
import rapidui.annotation.adapter.BindToText;
import rapidui.test.basictest.R;

@AdapterItem(R.layout.adapter_item_check_text)
public class ListItem2 {
	private String text;
	private boolean checked;

	public ListItem2(String text, boolean checked) {
		this.text = text;
		this.checked = checked;
	}

	@BindToText(R.id.textview)
	public String getText() {
		return text;
	}

	@BindToText(R.id.textview)
	public void setText(String text) {
		this.text = text;
	}

	@BindToCheck(R.id.checkbox)
	public boolean isChecked() {
		return checked;
	}

	@BindToCheck(R.id.checkbox)
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
