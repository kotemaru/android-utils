package org.kotemaru.android.fw.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

public class ConfirmDialogBuilder implements DialogBuilder {
	private final CharSequence mTitle;
	private final CharSequence mMessage;
	private final ConfirmDialogListener mListener;

	public ConfirmDialogBuilder(CharSequence title, CharSequence message, ConfirmDialogListener listener) {
		mTitle = title;
		mMessage = message;
		mListener = listener;
	}

	@Override
	public Dialog create(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity)
				.setTitle(mTitle).setMessage(mMessage);
		builder.setCancelable(false);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mListener != null) mListener.onDialogOkay(activity);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mListener != null) mListener.onDialogCancel(activity);
				dialog.dismiss();
			}
		});
		return builder.create();
	}

}
