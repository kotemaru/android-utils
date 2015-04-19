package org.kotemaru.android.fw.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

public class ProgressDialogBuilder implements DialogBuilder {
	private final String mMessage;
	private final OnDialogCancelListener mListener;
	private final boolean mIsCancelable;

	public ProgressDialogBuilder(String message, boolean isCancelable, OnDialogCancelListener listener) {
		mMessage = message;
		mIsCancelable = isCancelable;
		mListener = listener;
	}

	@Override
	public Dialog create(final Activity activity) {
		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage(null);
		progress.setMessage(mMessage);
		if (mIsCancelable) {
			progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			progress.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					if (mListener != null) mListener.onDialogCancel(activity);
				}
			});
		}
		progress.setCanceledOnTouchOutside(false);
		progress.setCancelable(mIsCancelable);
		// progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progress.setIndeterminate(false);
		return progress;
	}

}
