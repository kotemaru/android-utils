package org.kotemaru.android.fw.dialog;

import android.app.Activity;

public interface OnDialogCancelListener {
	/**
	 * 否定ボタンがタップされた時に呼ばれる。
	 * @param activity タップされた時の有効なActivityインスタンス。
	 */
	public void onDialogCancel(Activity activity);
}
