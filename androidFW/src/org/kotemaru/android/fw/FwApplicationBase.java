package org.kotemaru.android.fw;

import java.util.ArrayList;
import java.util.List;

import org.kotemaru.android.fw.annotation.UiThreadOnly;
import org.kotemaru.android.fw.thread.ThreadManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public abstract class FwApplicationBase<M, V extends FwActivity, C extends FwController>
		extends Application
		implements FwApplicationContext
{
	private static final String TAG = FwApplicationBase.class.getSimpleName();

	protected ThreadManager mThreadManager;
	protected M mModel;
	protected V mCurrentActivity;
	protected C mController;

	public enum ShutdownMode {
		NONE, FRONT, FULL
	}

	protected ShutdownMode mShutdownMode = ShutdownMode.NONE;
	protected List<V> mActivityStack = new ArrayList<V>(10);
	protected Runnable mUpdateRunner = new Runnable() {
		@Override
		public void run() {
			if (mCurrentActivity != null) mCurrentActivity.update();
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mThreadManager = createThreadManager();
		mModel = createModel();
		mController = createController();
		registerActivityLifecycleCallbacks(mActivityMonitor);
	}
	public abstract ThreadManager createThreadManager();
	public abstract M createModel();
	public abstract C createController();

	public void onApplicationResume() {
		// abstract
	}
	public void onApplicationPause() {
		// abstract
	}

	public ThreadManager getThreadManager() {
		return mThreadManager;
	}

	public M getModel() {
		return mModel;
	}
	public C getController() {
		return mController;
	}
	public List<V> getActivityStack() {
		return mActivityStack;
	}
	@UiThreadOnly
	public void goBackActivity(Class<?> activityClass) {
		for (int i = mActivityStack.size() - 1; i > 0; i--) {
			V activity = mActivityStack.get(i);
			if (!activity.isFinishing()) {
				if (activity.getClass().equals(activityClass)) break;
				activity.finish();
			}
		}
	}

	public void updateCurrentActivity() {
		if (mCurrentActivity != null) mThreadManager.post(ThreadManager.UI, mUpdateRunner, 0);
	}

	@SuppressWarnings("unchecked")
	private V toGenericsActivity(Activity activity) {
		try {
			return (V) activity;
		} catch (ClassCastException e) {
			Log.e(TAG, "Not management activity " + activity.getClass().getCanonicalName(), e);
			throw e;
		}
	}
	
	
	/**
	 * @deprecated call System.exit().
	 * @param waitTimeMs
	 */
	@UiThreadOnly
	public void shutdownAndKill(int waitTimeMs) {
		shutdown(ShutdownMode.FULL);
		mThreadManager.post(ThreadManager.UI, new Runnable() {
			@Override
			public void run() {
				System.exit(0);
			}
		}, waitTimeMs);
	}
	
	@UiThreadOnly
	public void shutdown(ShutdownMode mode) {
		Log.i(TAG, "shutdown:"+mode);
		mShutdownMode = mode;
		if (mode == ShutdownMode.NONE) return;
		for (V activity : mActivityStack) {
			if (!activity.isFinishing()) activity.finish();
		}
	}
	private void onAllActivityDestroyed() {
		if (mShutdownMode == ShutdownMode.FULL) {
			shotdownServices();
		} else {
			mShutdownMode = ShutdownMode.NONE;
		}
	}
	private void shotdownServices() {
		Log.i(TAG, "shotdownServices:");
		try {
			ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningServiceInfo> infos = am.getRunningServices(10000);
			String pkgName = this.getPackageName();
			for (RunningServiceInfo info : infos) {
				if (pkgName.equals(info.process)) {
					Intent intent = new Intent(this, Class.forName(info.service.getClassName()));
					stopService(intent);
				}
			}
		} catch (ClassNotFoundException e) {
			throw new FrameworkException(e);
		} finally {
			mShutdownMode = ShutdownMode.NONE;
		}
	}


	ActivityLifecycleCallbacks mActivityMonitor = new ActivityLifecycleCallbacks() {
		private int mForegroundCount = 0;

		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			Log.v(TAG, "onActivityCreated:" + activity.getClass().getCanonicalName());
			mActivityStack.add(toGenericsActivity(activity));
		}
		@Override
		public void onActivityResumed(Activity activity) {
			mCurrentActivity = toGenericsActivity(activity);
			mActivityStack.remove(mCurrentActivity);
			mActivityStack.add(mCurrentActivity);

			if (mForegroundCount++ == 0) {
				onApplicationResume();
			}
		}
		@Override
		public void onActivityPaused(Activity activity) {
			if (mCurrentActivity == activity) mCurrentActivity = null;
			mThreadManager.post(ThreadManager.UI, mOnPauseDelayRunner, 1000);
		}

		private final Runnable mOnPauseDelayRunner = new Runnable() {
			@Override
			public void run() {
				if (--mForegroundCount == 0) {
					onApplicationPause();
				}
			}
		};

		@Override
		public void onActivityDestroyed(Activity activity) {
			Log.v(TAG, "onActivityDestroyed:" + activity.getClass().getCanonicalName());
			mActivityStack.remove(activity);
			if (mActivityStack.isEmpty()) onAllActivityDestroyed();
		}

		// @formatter:off
		@Override public void onActivityStarted(Activity activity) {}
		@Override public void onActivityStopped(Activity activity) {}
		@Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
		// @formatter:on
	};

}
