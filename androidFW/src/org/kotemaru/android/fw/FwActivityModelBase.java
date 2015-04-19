package org.kotemaru.android.fw;

import org.kotemaru.android.fw.dialog.DialogModel;

public class FwActivityModelBase extends ModelLock {
	private DialogModel mDialogModel = new DialogModel();

	public DialogModel getDialogModel() {
		return mDialogModel;
	}

	public void setDialogModel(DialogModel dialogModel) {
		mDialogModel = dialogModel;
	}
}
