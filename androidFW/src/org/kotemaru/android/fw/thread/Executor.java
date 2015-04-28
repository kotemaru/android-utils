package org.kotemaru.android.fw.thread;

/**
 * スレッドでRunnableを実行するインターフェース。
 */
public interface Executor {
	/**
	 * 処理をスレッドの実行キューに追加する。
	 * @param runner 処理
	 * @param delay  遅延時間（ミリ秒）
	 * @return true=追加成功
	 */
	public boolean post(Runnable runner, int delay);
	/**
	 * スレッドを停止する。
	 */
	public void shutdown();
}
