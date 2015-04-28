package org.kotemaru.android.fw.thread;

import android.os.Handler;
import android.os.Looper;

/**
 * 単一スレッド用Executorの実装
 *
 */
public class ExecutorHandler implements Executor {
	private final Handler mHandler;

	public ExecutorHandler(Looper looper) {
		mHandler = new Handler(looper);
	}

	@Override
	public boolean post(Runnable runner, int delay) {
		if (delay == 0) {
			return mHandler.post(runner);
		} else {
			return mHandler.postDelayed(runner, delay);
		}
	}

	@Override
	public void shutdown() {
		if (mHandler.getLooper() != Looper.getMainLooper()) {
			mHandler.getLooper().quit();
		}
	}

}
