package rapidui.test.basictest.adapter;

import java.util.ArrayList;
import java.util.List;

import rapidui.RapidAdapter;
import rapidui.RapidListActivity;
import rapidui.annotation.Layout;
import rapidui.test.basictest.R;
import android.os.Bundle;

@Layout(R.layout.activity_list)
public class FieldBindingActivity extends RapidListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final List<Object> items = new ArrayList<Object>();
		
		items.add(new ListItem("astro boy", false).disable());
		items.add(new ListItem("bender", false).disable()	);
		items.add(new ListItem("cupcake", false));
		items.add(new ListItem("donut", false));
		items.add(new ListItem("eclair", false));
		items.add(new ListItem("froyo", false));
		items.add(new ListItem("gingerbread", true));
		items.add(new ListItem("honeycomb", false));
		items.add(new ListItem("icecream sandwich", false));
		items.add(new ListItem("jellybean", true));
		items.add(new ListItem("kitkat", true));
		
		final RapidAdapter adapter = new RapidAdapter(this, items, ListItem.class);
		getListView().setAdapter(adapter);
	}
}
