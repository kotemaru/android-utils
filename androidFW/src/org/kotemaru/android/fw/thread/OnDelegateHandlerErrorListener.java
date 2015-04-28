package org.kotemaru.android.fw.thread;

/**
 * 移譲ハンドラのエラー処理用インターフェース。
 */
public interface OnDelegateHandlerErrorListener {
	/**
	 * 移譲ハンドラのメソッドが例外を上げた場合に呼ばれる。
	 * @param t 例外
	 * @param methodName メソッド名
	 * @param arguments  引数
	 */
	public void onDelegateHandlerError(Throwable t, String methodName, Object... arguments);
}
