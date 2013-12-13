Introduction
============

Simplify your code by automating annoyingly repetitive jobs.

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

```java
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

```java
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
