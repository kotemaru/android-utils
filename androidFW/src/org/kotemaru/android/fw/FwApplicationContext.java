package org.kotemaru.android.fw;

import org.kotemaru.android.fw.thread.ThreadManager;

import android.app.Activity;

/**
 * FWのアプリケーションが実装するインスタンス。
 *
 */
public interface FwApplicationContext {
	/**
	 *
	 * @return
	 */
	public ThreadManager getThreadManager();
	public void updateCurrentActivity();
	public Activity getCurrentActivity();
}
