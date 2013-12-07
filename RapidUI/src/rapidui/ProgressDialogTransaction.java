package rapidui;

import java.text.NumberFormat;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;

public class ProgressDialogTransaction {
	interface OnCommitListener {
		void onCommit(ProgressDialogTransaction transaction);
	}
	
	private ArrayList<Command> commands;
	private OnCommitListener onCommit;
	
	public ProgressDialogTransaction(OnCommitListener onCommit) {
		this.commands = new ArrayList<Command>();
		this.onCommit = onCommit;
	}
	
	public void setButton(final int whichButton, final CharSequence text, final OnClickListener listener) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setButton(whichButton, text, listener);
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
	
	public void setCustomTitle(final View customTitleView) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setCustomTitle(customTitleView);
			}
		});
	}

	public void setFeatureDrawable(final int featureId, final Drawable drawable) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setFeatureDrawable(featureId, drawable);
			}
		});
	}

	public void setFeatureDrawableAlpha(final int featureId, final int alpha) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setFeatureDrawableAlpha(featureId, alpha);
			}
		});
	}

	public void setFeatureDrawableResource(final int featureId, final int resId) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setFeatureDrawableResource(featureId, resId);
			}
		});
	}

	public void setFeatureDrawableUri(final int featureId, final Uri uri) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setFeatureDrawableUri(featureId, uri);
			}
		});
	}

	public void setIcon(final Drawable icon) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setIcon(icon);
			}
		});
	}

	public void setIcon(final int resId) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setIcon(resId);
			}
		});
	}

	public void setIndeterminate(final boolean indeterminate) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setIndeterminate(indeterminate);
			}
		});
	}

	public void setIndeterminateDrawable(final Drawable d) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setIndeterminateDrawable(d);
			}
		});
	}

	public void setMax(final int max) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setMax(max);
			}
		});
	}

	public void setMessage(final CharSequence message) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setMessage(message);
			}
		});
	}

	public void setProgress(final int value) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setProgress(value);
			}
		});
	}

	public void setProgressDrawable(final Drawable d) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setProgressDrawable(d);
			}
		});
	}

	public void setProgressNumberFormat(final String format) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setProgressNumberFormat(format);
			}
		});
	}

	public void setProgressPercentFormat(final NumberFormat format) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setProgressPercentFormat(format);
			}
		});
	}

	public void setProgressStyle(final int style) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setProgressStyle(style);
			}
		});
	}

	public void setSecondaryProgress(final int secondaryProgress) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setSecondaryProgress(secondaryProgress);
			}
		});
	}

	public void setTitle(final CharSequence title) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setTitle(title);
			}
		});
	}

	public void setTitle(final int titleId) {
		commands.add(new Command() {
			@Override
			public void run(ProgressDialog pd) {
				pd.setTitle(titleId);
			}
		});
	}
	
	public void commit() {
		onCommit.onCommit(this);
	}
	
	void execute(ProgressDialog pd) {
		for (int i = 0, c = commands.size(); i < c; ++i) {
			commands.get(i).run(pd);
		}
	}
	
	interface Command {
		void run(ProgressDialog pd);
	}
}
