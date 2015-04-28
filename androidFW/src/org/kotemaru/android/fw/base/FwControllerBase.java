package org.kotemaru.android.fw.base;

import org.kotemaru.android.fw.FwController;

import android.app.Application;


public abstract class FwControllerBase<A extends Application> implements FwController {
	protected final A mApplication;

	protected FwControllerBase(A app) {
		mApplication = app;
	}

	public A getFwApplication() {
		return mApplication;
	}
}
