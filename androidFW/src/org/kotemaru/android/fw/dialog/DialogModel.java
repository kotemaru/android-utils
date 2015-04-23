package org.kotemaru.android.fw.dialog;

import org.kotemaru.android.fw.ModelLock;

import android.content.Context;

public class DialogModel extends ModelLock {
	private OnUpdateDialogModelListener mListener;
	private DialogBuilder mDialogBuilder;

	public DialogBuilder getDialogBuilder() {
		return mDialogBuilder;
	}
	public void setDialogBuilder(DialogBuilder dialogBuilder) {
		mDialogBuilder = dialogBuilder;
	}
	public void setDialogBuilderLocked(DialogBuilder dialogBuilder) {
		this.writeLock();
		try {
			setDialogBuilder(dialogBuilder);
		} finally {
			this.writeUnlock();
		}
		commit();
	}
	public void commit() {
		if (mListener != null) mListener.onUpdateDialogModel(this);
	}

	public void clear() {
		setDialogBuilderLocked(null);
	}

	public void setAlert(CharSequence title, CharSequence messgae, AlertDialogListener listener) {
		setDialogBuilderLocked(new AlertDialogBuilder(title, messgae, listener));
	}
	public void setError(Throwable t, AlertDialogListener listener) {
		setDialogBuilderLocked(new AlertDialogBuilder("Error!", t.getMessage(), listener));
	}
	public void setConfirm(CharSequence title, CharSequence messgae, ConfirmDialogListener listener) {
		setDialogBuilderLocked(new ConfirmDialogBuilder(title, messgae,  listener));
	}

	public ProgressDialogBuilder setProgress(String messgae, boolean isCancelable, OnDialogCancelListener listener) {
		ProgressDialogBuilder builder = new ProgressDialogBuilder(this, messgae, isCancelable, listener);
		setDialogBuilderLocked(builder);
		return builder;
	}
	public boolean setInformationIfRequire(Context context, int resId) {
		if (InformationDialogBuilder.isRequireShown(context, resId)) {
			setDialogBuilderLocked(new InformationDialogBuilder(context, resId, null));
			return true;
		}
		return false;
	}

	public OnUpdateDialogModelListener getOnUpdateDialogModelListener() {
		return mListener;
	}
	public void setOnUpdateDialogModelListener(OnUpdateDialogModelListener onUpdateDialogModelListener) {
		mListener = onUpdateDialogModelListener;
	}

}
