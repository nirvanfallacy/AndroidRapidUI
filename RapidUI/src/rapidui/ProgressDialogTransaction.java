package rapidui;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ProgressDialogTransaction {
	private ArrayList<Command> commands;
	private Runnable onCommit;
	
	public ProgressDialogTransaction() {
		commands = new ArrayList<Command>();
	}
	
	public void setButton(final int whichButton, final CharSequence text, final OnClickListener listener) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setButton(whichButton, text, listener);
			}
		});
	}
	
	public void setButton(final int whichButton, final CharSequence text, final Message msg) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setButton(whichButton, text, msg);
			}
		});
	}	

	public void setCancelable(final boolean flag) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setCancelable(flag);
			}
		});
	}
	
	public void setCanceledOnTouchOutside(final boolean cancel) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setCanceledOnTouchOutside(cancel);
			}
		});
	}

	public void setCancelMessage(final Message msg) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setCancelMessage(msg);
			}
		});
	}

	public void setContentView(final int layoutResID) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setContentView(layoutResID);
			}
		});
	}

	public void setContentView(final View view, final LayoutParams params) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setContentView(view, params);
			}
		});
	}

	public void setCustomTitle(final View customTitleView) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setCustomTitle(customTitleView);
			}
		});
	}
	
	public void commit() {
		onCommit.run();
	}
	
	void execute(ProgressDialog pd) {
		for (int i = 0, c = commands.size(); i < c; ++i) {
			commands.get(i).run(pd);
		}
	}
	
	void setOnCommitListener(Runnable listener) {
		this.onCommit = listener;
	}
	
	private interface Command {
		void run(ProgressDialog pd);
	}
}
