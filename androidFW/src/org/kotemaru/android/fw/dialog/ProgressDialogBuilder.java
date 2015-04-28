package org.kotemaru.android.fw.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/**
 * プログレスダイアログ生成器。
 * <li>タイトル無し、クルクル表示。
 *
 */
public class ProgressDialogBuilder implements DialogBuilder {
	private final DialogModel mDialogModel;
	private String mMessage;
	private OnDialogCancelListener mListener;
	private boolean mIsCancelable;
	private boolean mIsCancelled = false;

	public ProgressDialogBuilder(DialogModel dialogModel, String message,
			boolean isCancelable, OnDialogCancelListener listener) {
		mMessage = message;
		mIsCancelable = isCancelable;
		mListener = listener;
		mDialogModel = dialogModel;
	}

	@Override
	public Dialog create(final Activity activity, final DialogModel model) {
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
					setCancelled(true);
					model.clear();
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
	@Override
	public Dialog update(Activity activity, DialogModel model,Dialog dialog) {
		ProgressDialog progress = (ProgressDialog) dialog;
		progress.setMessage(mMessage);
		progress.setCancelable(mIsCancelable);
		return dialog;
	}

	public boolean isCancelled() {
		return mIsCancelled;
	}

	public void setCancelled(boolean isCancelled) {
		mIsCancelled = isCancelled;
		mDialogModel.commit();
	}

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String message) {
		mMessage = message;
		mDialogModel.commit();
	}

	public OnDialogCancelListener getOnDialogCancelListener() {
		return mListener;
	}

	public void setOnDialogCancelListener(OnDialogCancelListener listener) {
		mListener = listener;
	}

}
