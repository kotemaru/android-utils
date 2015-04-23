package org.kotemaru.android.fw.dialog;

import org.kotemaru.android.fw.annotation.UiThreadOnly;

import android.app.Activity;
import android.app.Dialog;

public class DialogHelper {
	private DialogBuilder mCurrentBuilder;
	private Dialog mCurrentDialog;

	public DialogHelper() {
	}

	@UiThreadOnly
	public boolean doDialog(final Activity activity, final DialogModel model) {
		model.readLock();
		try {
			DialogBuilder builder = model.getDialogBuilder();
			if (builder == null) {
				clear(mCurrentDialog);
				return false;
			}
			if (builder == mCurrentBuilder) {
				mCurrentDialog = builder.update(activity, model, mCurrentDialog);
				mCurrentDialog.show();
				return true;
			}
			clear(mCurrentDialog);
			mCurrentBuilder = builder;
			mCurrentDialog = builder.create(activity, model);
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
