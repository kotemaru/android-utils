package org.kotemaru.android.fw.dialog;

import android.app.Activity;
import android.app.Dialog;

public interface DialogBuilder {
	public Dialog create(Activity activity, DialogModel model);
	public Dialog update(Activity activity, DialogModel model, Dialog dialog);
}
