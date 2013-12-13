Introduction
============

Simplify your code related to UI and other unnecessarily repetitive jobs.

Features
========

- View injection
- Event handler delegation
- Broadcast receiver delegation
- Automatic service connection
- Improved AsyncTask which provides lots of conveniences
- Simplified and extensible adapter
- More automations
 
Example
=======

Before
------

```javascript
public class MainActivity extends Activity {
	TextView textOut;
	Button buttonOk;
	Button buttonCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textOut = (TextView) findViewById(R.id.text_out);
		buttonOk = (Button) findViewById(R.id.button_ok);
		buttonCancel = (Button) findViewById(R.id.button_cancel);
		
		buttonOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				textOut.setText("OK button clicked!");
			}
		});
		
		buttonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				textOut.setText("Cancel button clicked!");
			}
		});
	}
}
```

After
-----

```javascript
@Layout
public class MainActivity extends RapidActivity {
	@LayoutElement TextView textOut;
	
	@EventHandler
	void buttonOk_Click() {
		textOut.setText("OK button clicked!");
	}
	
	@EventHandler
	void buttonCancel_Click() {
		textOut.setText("Cancel button clicked!");
	}
}
```
