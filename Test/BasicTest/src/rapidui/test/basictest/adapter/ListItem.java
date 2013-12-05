package rapidui.test.basictest.adapter;

import rapidui.annotation.AdapterItem;
import rapidui.annotation.adapter.BindToCheck;
import rapidui.annotation.adapter.BindToText;
import rapidui.test.basictest.R;

@AdapterItem(R.layout.adapter_item_check_text)
public class ListItem {
	@BindToText(R.id.textview)
	public String text;
	
	@BindToCheck(R.id.checkbox)
	public boolean checked;
	
	public ListItem(String text, boolean checked) {
		this.text = text;
		this.checked = checked;
	}
}
