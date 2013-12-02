package rapidui.sample.adapter;

import rapidui.RapidAdapter;
import rapidui.RapidListActivity;
import rapidui.annotation.Layout;
import android.os.Bundle;

@Layout
public class MainActivity extends RapidListActivity {
	private RapidAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getListView().setAdapter(adapter);
	}
}
