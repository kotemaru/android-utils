package org.kotemaru.android.fw;

import android.app.Activity;

/**
 * FW用Activityが実装するインターフェース。
 *
 *
 */
public interface FwActivity {
	/**
	 * 画面更新処理。
	 * <li>Modelの内容を画面に反映する。
	 * <li>onResume() か FwApplictionContext#updateCurrentActivity()から呼ばれる。
	 */
	public void update();

	/**
	 * Activity インスタンスに変換する。
	 * <li>通常は return this; で良いはず。
	 * @return Activityインスタンス。
	 */
	public Activity toActivity();

	/**
	 * FWのアプリケーションを返す。
	 * @return FWのアプリケーション
	 */
	public FwApplicationContext getFwApplication();

	/**
	 * @see Activity#finish()
	 */
	public void finish();
	/**
	 * @see Activity#finish()
	 */
	public boolean isFinishing();
}
