package org.kotemaru.android.fw.base;

import org.kotemaru.android.fw.ModelLock;
import org.kotemaru.android.fw.dialog.DialogModel;

/**
 * FwActivityBase を対になる Model。
 * <li>ダイアログを保持する DialogModel を持っているだけ。
 *
 * @param <DM> DialogModel の実装クラス。
 */
public abstract class FwActivityModelBase<DM extends DialogModel> extends ModelLock {
	private final DM mDialogModel;

	public FwActivityModelBase(ModelLock parentLock) {
		super(parentLock);
		mDialogModel =  createDialogModel();
	}

	/**
	 * DialogModel のインスタンスを生成する。
	 * <li>生成されるDialogModelはこのインスタンスを親として持たなければならない。
	 * @return DialogModeインスタンス。
	 */
	public abstract DM createDialogModel();

	/**
	 * DialogModel のインスタンスの取得。
	 * @return  DialogModel インスタンス
	 */
	public DM getDialogModel() {
		return mDialogModel;
	}
}
