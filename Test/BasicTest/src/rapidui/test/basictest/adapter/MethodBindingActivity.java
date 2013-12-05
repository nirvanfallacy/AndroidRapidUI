package rapidui.test.basictest.adapter;

import java.util.ArrayList;
import java.util.List;

import rapidui.RapidAdapter;
import rapidui.RapidListActivity;
import rapidui.annotation.Layout;
import rapidui.test.basictest.R;
import android.os.Bundle;

@Layout(R.layout.activity_list)
public class MethodBindingActivity extends RapidListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final List<Object> items = new ArrayList<Object>();
		
		items.add(new ListItem2("astro boy", false));
		items.add(new ListItem2("bender", false));
		items.add(new ListItem2("cupcake", false));
		items.add(new ListItem2("donut", false));
		items.add(new ListItem2("eclair", false));
		items.add(new ListItem2("froyo", false));
		items.add(new ListItem2("gingerbread", true));
		items.add(new ListItem2("honeycomb", false));
		items.add(new ListItem2("icecream sandwich", false));
		items.add(new ListItem2("jellybean", true));
		items.add(new ListItem2("kitkat", true));
		
		final RapidAdapter adapter = new RapidAdapter(this, items, ListItem2.class);
		getListView().setAdapter(adapter);
	}
}
