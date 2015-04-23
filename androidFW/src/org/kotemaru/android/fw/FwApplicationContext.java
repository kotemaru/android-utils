package org.kotemaru.android.fw;

import org.kotemaru.android.fw.thread.ThreadManager;

import android.app.Activity;

public interface FwApplicationContext {
	public ThreadManager getThreadManager();
	public void updateCurrentActivity();
	public Activity getCurrentActivity();
}
