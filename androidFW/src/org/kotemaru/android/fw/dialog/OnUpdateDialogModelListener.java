package org.kotemaru.android.fw.dialog;


public interface OnUpdateDialogModelListener {
	/**
	 * ダイアログの状態に変更があった時に呼ばれる。
	 * @param model 更新されたモデル
	 */
	public void onUpdateDialogModel(DialogModel model);
}
