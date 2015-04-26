package org.kotemaru.android.fw.thread;


public interface OnDelegateHandlerErrorListener {
	public void onDelegateHandlerError(Throwable t, String methodName, Object... arguments);
}
