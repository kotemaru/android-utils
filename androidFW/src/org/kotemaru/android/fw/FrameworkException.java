package org.kotemaru.android.fw;

public class FrameworkException extends RuntimeException {
	public FrameworkException() {
		super();
	}

	public FrameworkException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public FrameworkException(String detailMessage) {
		super(detailMessage);
	}

	public FrameworkException(Throwable throwable) {
		super(throwable);
	}
}
