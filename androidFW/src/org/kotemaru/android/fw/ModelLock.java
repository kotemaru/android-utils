package org.kotemaru.android.fw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * Modelのアクセスロック機構。
 * <li>ロックを必要とするModelはこのクラスを継承する。
 * <li>read-write ロック方式である。
 * <li>ロックインスタンスは親子関係を持ちツリー構造を構成できる。
 * <li>ロックを取得すると子孫のロックも自動的に取得される。
 * <li>子孫が他スレッドにロックされている場合はブロックされる。
 *
 */

public class ModelLock {
	private static final String TAG = ModelLock.class.getSimpleName();
	private static final long DEFAULT_TIMEOUT = 5000; // 5sec

	private Map<Thread, Long> mLockedThreads = new HashMap<Thread, Long>();
	private boolean mIsDebug = true;

	@SuppressWarnings("unused")
	private final ModelLock mParentLock;
	private List<ModelLock> mChildLockList = new ArrayList<ModelLock>(2);

	private int mReadLock = 0;
	private int mWriteLock = 0;
	private Thread mWriteLockThread = null;

	/**
	 * コンストラクタ。
	 * @param parentLock 親のロック。null の場合は親を持たない。
	 */
	public ModelLock(ModelLock parentLock) {
		mParentLock = parentLock;
		if (parentLock != null) {
			parentLock.addChildLock(this);
		}
	}

	private void addChildLock(ModelLock childLock) {
		mChildLockList.add(childLock);
	}

	public void _setDebug(boolean isDebug) {
		mIsDebug = isDebug;
		if (isDebug) {
			mLockedThreads = new HashMap<Thread, Long>();
		}
	}

	/**
	 * readLock(5000) に同じ。
	 * @thrwable LockException ロック取得失敗。
	 */
	public void readLock() {
		readLock(DEFAULT_TIMEOUT);
	}

	/**
	 * Readロックの取得（例外有り）。
	 * <li>タイムアウトした場合は例外が発生する。
	 * <li>子孫のReadロックがすべて取れればロック成功。
	 * @param timeout 待ち時間ミリ秒。
	 * @thrwable LockException ロック取得失敗。
	 */
	public void readLock(long timeout) {
		if (!tryReadLock(timeout)) {
			throw new LockException("Read lock timeout.\n" + getLockedThreadInfo());
		}
	}

	/**
	 * Readロックの取得（例外なし）。
	 * <li>ブロック待をせず、すぐに結果を返す。
	 * <li>子孫のReadロックがすべて取れればロック成功。
	 * @return true=ロック成功
	 */
	public boolean tryReadLock() {
		return tryReadLock(0);
	}

	/**
	 * Readロックの取得（例外なし）。
	 * <li>子孫のReadロックがすべて取れればロック成功。
	 * @param timeout 待ち時間ミリ秒。
	 * @return true=ロック成功
	 */
	public synchronized boolean tryReadLock(long timeout) {
		if (mIsDebug) {
			Log.d(TAG, "tryReadLock:" + timeout + ":" + this);
			mLockedThreads.put(Thread.currentThread(), System.currentTimeMillis());
		}
		if (!tryReadLockChildren(timeout)) {
			return false;
		}
		long timeoutMs = System.currentTimeMillis() + timeout;
		while (mWriteLockThread != null) {
			if (System.currentTimeMillis() > timeoutMs) {
				return false;
			}
			try {
				wait(timeoutMs - System.currentTimeMillis());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mReadLock++;
		return true;
	}
	private boolean tryReadLockChildren(long timeout) {
		List<ModelLock> successList = new ArrayList<ModelLock>(mChildLockList.size());
		boolean isFail = true;
		try {
			for (ModelLock child : mChildLockList) {
				if (child.tryReadLock(timeout)) {
					successList.add(child);
				} else {
					return false;
				}
			}
			isFail = false;
			return true;
		} finally {
			if (isFail) {
				for (ModelLock child : successList) {
					child.readUnlock();
				}
			}
		}
	}

	/**
	 * Readロックの開放。
	 * <li>子孫のReadロックもすべて開放。
	 */
	public synchronized void readUnlock() {
		if (mReadLock == 0) {
			throw new LockException("Read unlock under flow.");
		}
		mReadLock--;
		for (ModelLock child : mChildLockList) {
			child.readUnlock();
		}
		notifyAll();
		if (mIsDebug) {
			Log.d(TAG, "readUnlock:" + this);
			mLockedThreads.remove(Thread.currentThread());
		}
	}

	/**
	 * writeLock(5000) に同じ。
	 * @thrwable LockException ロック取得失敗。
	 */
	public void writeLock() {
		writeLock(DEFAULT_TIMEOUT);
	}

	/**
	 * Writeロックの取得（例外有り）。
	 * <li>タイムアウトした場合は例外が発生する。
	 * <li>子孫のWriteロックがすべて取れればロック成功。
	 * @param timeout 待ち時間ミリ秒。
	 * @thrwable LockException ロック取得失敗。
	 */
	public synchronized void writeLock(long timeout) {
		if (!tryWriteLock(timeout)) {
			throw new LockException("Write lock timeout.\n" + getLockedThreadInfo());
		}
	}
	/**
	 * Writeロックの取得（例外なし）。
	 * <li>子孫のWriteロックがすべて取れればロック成功。
	 * @return true=ロック成功
	 */
	public synchronized boolean tryWriteLock(long timeout) {
		if (mIsDebug) {
			Log.d(TAG, "tryWriteLock:" + timeout + ":" + this);
			mLockedThreads.put(Thread.currentThread(), System.currentTimeMillis());
		}
		if (!tryWriteLockChildren(timeout)) {
			return false;
		}

		Thread curThread = Thread.currentThread();
		long timeoutMs = System.currentTimeMillis() + timeout;
		while ((mWriteLockThread != null && mWriteLockThread != curThread) || mReadLock != 0) {
			if (System.currentTimeMillis() > timeoutMs) {
				return false;
			}
			try {
				wait(timeoutMs - System.currentTimeMillis());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mWriteLockThread = curThread;
		mWriteLock++;
		return true;
	}
	private boolean tryWriteLockChildren(long timeout) {
		List<ModelLock> successList = new ArrayList<ModelLock>(mChildLockList.size());
		boolean isFail = true;
		try {
			for (ModelLock child : mChildLockList) {
				if (child.tryWriteLock(timeout)) {
					successList.add(child);
				} else {
					return false;
				}
			}
			isFail = false;
			return true;
		} finally {
			if (isFail) {
				for (ModelLock child : successList) {
					child.writeUnlock();
				}
			}
		}
	}

	/**
	 * Writeロックの開放。
	 * <li>子孫のWriteロックもすべて開放。
	 */
	public synchronized void writeUnlock() {
		if (mWriteLockThread == null) {
			throw new LockException("Write unlock under flow.");
		}
		if (mWriteLockThread != Thread.currentThread()) {
			throw new LockException("Write unlock: has not write lock.");
		}
		for (ModelLock child : mChildLockList) {
			child.writeUnlock();
		}
		mWriteLock--;
		if (mWriteLock == 0) {
			mWriteLockThread = null;
			notifyAll();
			if (mIsDebug) {
				Log.d(TAG, "writeUnlock:" + this);
				mLockedThreads.remove(Thread.currentThread());
			}
		}
	}

	private StringBuilder getLockedThreadInfo() {
		StringBuilder sbuf = new StringBuilder("\n----\n");
		long curTime = System.currentTimeMillis();
		for (Thread th : mLockedThreads.keySet()) {
			Long time = mLockedThreads.get(th);
			sbuf.append("thread=").append(th.getName())
					.append(": lock having or waiting time=").append(curTime - time).append("ms\n");
			StackTraceElement[] trace = th.getStackTrace();
			for (int i = 0; i < 6; i++) {
				sbuf.append("  ").append(trace[i].toString()).append("\n");
			}
		}
		sbuf.append("\n----\n");
		return sbuf;
	}

	public static class LockException extends RuntimeException {
		public LockException(String detailMessage) {
			super(detailMessage);
		}
	}

}
