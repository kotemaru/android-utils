package org.kotemaru.android.fw.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

public class DialogHelper {
	public enum State {
		NOP, SHOW, HIDE
	};

	public interface OnDialogButtonListener {
		public void onProgressCancel(DialogInterface dialog, DialogModel model);
		public void onAlertOk(DialogInterface dialog, DialogModel model);
		public void onConfirmOk(DialogInterface dialog, DialogModel model);
		public void onConfirmCancel(DialogInterface dialog, DialogModel model);
		public void onSelectItem(DialogInterface dialog, DialogModel model, int which);
		public void onSelectItems(DialogInterface dialog, DialogModel model, boolean[] checkedItems);
	}

	public static class OnDialogButtonListenerBase implements OnDialogButtonListener {
		// @formatter:off
		public void onProgressCancel(DialogInterface dialog, DialogModel model){}
		public void onAlertOk(DialogInterface dialog, DialogModel model){}
		public void onConfirmOk(DialogInterface dialog, DialogModel model){}
		public void onConfirmCancel(DialogInterface dialog, DialogModel model){}
		public void onSelectItem(DialogInterface dialog, DialogModel model, int which){}
		public void onSelectItems(DialogInterface dialog, DialogModel model, boolean[] checkedItems){}
		// @formatter:on
	}

	private Activity mActivity;
	private Dialog mCurrentDialog;

	public DialogHelper(Activity activity) {
		mActivity = activity;
	}
	public boolean doDialog(DialogModel model, OnDialogButtonListener listener) {
		mCurrentDialog = clear(mCurrentDialog);
		model.readLock();
		try {
			switch (model.getMode()) {
			case ALERT:
				mCurrentDialog = showAlertDialog(model, listener);
				return true;
			case CONFIRM:
				mCurrentDialog = showConfirmDialog(model, listener);
				return true;
			case PROGRESS:
				mCurrentDialog = showProgressDialog(model, listener);
				return true;
			case ITEMS:
				mCurrentDialog = showItemsDialog(model, listener);
				return true;
			case SINGLE_CHOICE:
				mCurrentDialog = showSingleChoiceDialog(model, listener);
				return true;
			case MULTI_CHOICE:
				mCurrentDialog = showMultiChoiceDialog(model, listener);
				return true;
			case CUSTOM:
				mCurrentDialog = showCustomDialog(model, listener);
				return true;
			default:
			}
			return false;
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

	public Dialog showProgressDialog(final DialogModel model, final OnDialogButtonListener listener) {
		ProgressDialog progress = new ProgressDialog(mActivity);
		progress.setMessage(model.getTitle());
		progress.setMessage(model.getMessage());
		if (model.isCancelable()) {
			progress.setButton(DialogInterface.BUTTON_NEGATIVE, model.getCancelButtonLabel(),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (listener != null) listener.onProgressCancel(dialog, model);
							dialog.cancel();
							model.clear();
						}
					});
		}
		progress.setCanceledOnTouchOutside(false);
		progress.setCancelable(model.isCancelable());
		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progress.setIndeterminate(false);
		progress.show();
		return progress;
	}
	public Dialog showAlertDialog(final DialogModel model, final OnDialogButtonListener listener) {
		AlertDialog.Builder builer = new AlertDialog.Builder(mActivity)
				.setTitle(model.getTitle()).setMessage(model.getMessage());
		builer.setCancelable(false);
		builer.setPositiveButton(model.getOkButtonLabel(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) listener.onAlertOk(dialog, model);
				dialog.dismiss();
			}
		});
		return builer.show();
	}
	public Dialog showConfirmDialog(final DialogModel model, final OnDialogButtonListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
				.setTitle(model.getTitle()).setMessage(model.getMessage());
		builder.setCancelable(false);
		builder.setPositiveButton(model.getOkButtonLabel(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) listener.onConfirmOk(dialog, model);
				dialog.dismiss();
				model.clear();
			}
		});
		builder.setNegativeButton(model.getCancelButtonLabel(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) listener.onConfirmCancel(dialog, model);
				dialog.dismiss();
				model.clear();
			}
		});
		return builder.show();
	}
	private Dialog showItemsDialog(final DialogModel model, final OnDialogButtonListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
				.setTitle(model.getTitle());
		builder.setCancelable(false);
		builder.setItems(model.getItems(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) listener.onSelectItem(dialog, model, which);
				dialog.dismiss();
				model.clear();
			}
		});
		return builder.show();
	}
	private Dialog showSingleChoiceDialog(final DialogModel model, final OnDialogButtonListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
				.setTitle(model.getTitle());
		builder.setCancelable(false);
		builder.setSingleChoiceItems(model.getItems(), model.getCheckedItem(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				model.writeLock();
				try {
					model.setCheckedItem(which);
				} finally {
					model.writeUnlock();
				}
			}
		});
		builder.setPositiveButton(model.getOkButtonLabel(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) listener.onSelectItem(dialog, model, model.getCheckedItem());
				dialog.dismiss();
				model.clear();
			}
		});
		return builder.show();
	}
	private Dialog showMultiChoiceDialog(final DialogModel model, final OnDialogButtonListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
				.setTitle(model.getTitle());
		builder.setCancelable(false);
		builder.setMultiChoiceItems(model.getItems(), model.getCheckedItems(), new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				model.writeLock();
				try {
					model.getCheckedItems()[which] = isChecked;
				} finally {
					model.writeUnlock();
				}
			}
		});
		builder.setPositiveButton(model.getOkButtonLabel(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) listener.onSelectItems(dialog, model, model.getCheckedItems());
				dialog.dismiss();
				model.clear();
			}
		});
		return builder.show();
	}

	private Dialog showCustomDialog(DialogModel model, OnDialogButtonListener listener) {
		Dialog dialog = model.getDialogBuilder().create();
		dialog.show();
		return dialog;
	}

}
