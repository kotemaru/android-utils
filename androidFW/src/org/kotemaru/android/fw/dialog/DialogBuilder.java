package org.kotemaru.android.fw.dialog;

import android.app.Activity;
import android.app.Dialog;


/**
 * ダイアログ生成用インターフェース。
 */

public interface DialogBuilder {
	/**
	 * ダイアログを生成する。
	 * @param activity 親のActivity
	 * @param model    ダイアログを構成するための情報。
	 * @return ダイアログ
	 */
	public Dialog create(Activity activity, DialogModel model);
	/**
	 * ダイアログの更新。
	 * <li>基本的にProgress用。
	 * @param activity 親のActivity
	 * @param model    ダイアログを構成するための情報。
	 * @param dialog   更新対象のダイアログ。
	 * @return 更新後ダイアログ。
	 */
	public Dialog update(Activity activity, DialogModel model, Dialog dialog);
}
