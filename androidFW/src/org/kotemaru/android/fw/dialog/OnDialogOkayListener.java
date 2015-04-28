package org.kotemaru.android.fw.dialog;

import android.app.Activity;

public interface OnDialogOkayListener {
	/**
	 * 肯定ボタンがタップされた時に呼ばれる。
	 * @param activity タップされた時の有効なActivityインスタンス。
	 */
	public void onDialogOkay(Activity activity);
}
