package org.kotemaru.android.fw.thread;

/**
 * スレッド管理インターフェース。
 */
public interface ThreadManager {
	public static final String UI = "UI";
	public static final String WORKER = "WORKER";
	public static final String NETWORK = "NETWORK";

	/**
	 * スレッド登録。
	 * <li>registerThread(threadName, 1, Thread.NORM_PRIORITY) に同じ。
	 */
	public Executor registerThread(String threadName);
	/**
	 * スレッド登録。
	 * @param threadName	スレッド名
	 * @param size			並列数
	 * @param priority		優先度
	 * @return Executor
	 */
	public Executor registerThread(String threadName, int size, final int priority);
	/**
	 * スレッド名からExecutorを取得する。
	 * @param threadName スレッド名
	 * @return Executor
	 */
	public Executor getExecutor(String threadName);
	/**
	 * 指定されたスレッドで処理を実行する。
	 * <li>実際にはキューに追加されるだけなのですぐには実行されない。
	 * @param threadName スレッド名
	 * @param runner     処理。
	 * @param delay      遅延時間（ミリ秒）
	 * @return true=成功
	 */
	public boolean post(String threadName, Runnable runner, int delay);
	/**
	 * すべてのスレッドの終了。
	 */
	public void shutdown();
}
