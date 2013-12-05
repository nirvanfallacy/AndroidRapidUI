package rapidui.test.basictest.adapter;

import java.util.ArrayList;
import java.util.List;

import rapidui.RapidAdapter;
import rapidui.RapidListActivity;
import rapidui.annotation.Layout;
import rapidui.test.basictest.R;
import android.os.Bundle;

@Layout(R.layout.activity_list)
public class AsyncMethodBindingActivity extends RapidListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final int imageId = R.drawable.ic_launcher;
		
		final List<Object> items = new ArrayList<Object>();
		
		items.add(new ListItem3(this, "astro boy", imageId));
		items.add(new ListItem3(this, "bender", imageId));
		items.add(new ListItem3(this, "cupcake", imageId));
		items.add(new ListItem3(this, "donut", imageId));
		items.add(new ListItem3(this, "eclair", imageId));
		items.add(new ListItem3(this, "froyo", imageId));
		items.add(new ListItem3(this, "gingerbread", imageId));
		items.add(new ListItem3(this, "honeycomb", imageId));
		items.add(new ListItem3(this, "icecream sandwich", imageId));
		items.add(new ListItem3(this, "jellybean", imageId));
		items.add(new ListItem3(this, "kitkat", imageId));
		
		final RapidAdapter adapter = new RapidAdapter(this, items, ListItem3.class);
		getListView().setAdapter(adapter);
	}
}
