package org.kotemaru.android.fw.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

public class AlertDialogBuilder implements DialogBuilder {
	private final CharSequence mTitle;
	private final CharSequence mMessage;
	private final AlertDialogListener mListener;

	public AlertDialogBuilder(CharSequence title, CharSequence messgae, AlertDialogListener listener) {
		mTitle = title;
		mMessage = messgae;
		mListener = listener;
	}

	@Override
	public Dialog create(final Activity activity, final DialogModel model) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity)
				.setTitle(mTitle).setMessage(mMessage);
		builder.setCancelable(false);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mListener != null) mListener.onDialogOkay(activity);
				dialog.dismiss();
				model.clear();
			}
		});
		return builder.create();
	}

	@Override
	public Dialog update(Activity activity, DialogModel model, Dialog dialog) {
		return dialog;
	}

}
