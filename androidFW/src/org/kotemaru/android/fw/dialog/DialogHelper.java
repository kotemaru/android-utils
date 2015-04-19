package org.kotemaru.android.fw.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

public class DialogHelper {
	private Activity mActivity;
	private Dialog mCurrentDialog;

	public DialogHelper(Activity activity) {
		mActivity = activity;
	}
	public boolean doDialog(final DialogModel model) {
		mCurrentDialog = clear(mCurrentDialog);
		model.readLock();
		try {
			DialogBuilder builder = model.getDialogBuilder();
			if (builder == null) return false;
			mCurrentDialog = builder.create(mActivity);
			mCurrentDialog.setOnDismissListener(new OnDismissListener(){
				@Override
				public void onDismiss(DialogInterface dialog) {
					model.clear();
				}
			});
			mCurrentDialog.show();
			return true;
		} finally {
			model.readUnlock();
		}
	}

	public void clear() {
		mCurrentDialog = clear(mCurrentDialog);
	}
	private Dialog clear(Dialog dialog) {
		if (dialog != null) dialog.dismiss();
		return null;
	}

}
