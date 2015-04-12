package org.kotemaru.android.delegatehandler.rt;


public interface OnDelegateHandlerErrorListener {
	public void onDelegateHandlerError(Throwable t, String methodName, Object... arguments);
}
