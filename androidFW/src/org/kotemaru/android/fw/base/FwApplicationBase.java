package org.kotemaru.android.fw.base;

import java.util.ArrayList;
import java.util.List;

import org.kotemaru.android.fw.FrameworkException;
import org.kotemaru.android.fw.FwActivity;
import org.kotemaru.android.fw.FwApplicationContext;
import org.kotemaru.android.fw.FwController;
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

/**
 * アプリケーションの基底クラス。
 *
 * @param <M> アプリケーションのModelの起点となるクラス。
 * @param <V> アプリケーションの基底Activityクラス。
 * @param <C> アプリケーションの起点となるControllerクラス。
 */
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
		Log.i(TAG, "onCreate:");
		super.onCreate();
		registerActivityLifecycleCallbacks(mActivityMonitor);
	}

	/**
	 * 最初のActivityが生成された事の通知。
	 * <li>実質的なアプリ（フォアグラウンド）の初期化処理。
	 * <li>Overrideする場合、super.onWakeup() を最初に呼ぶ必要がある。
	 *
	 */
	public void onWakeup() {
		Log.i(TAG, "onWakeup:");
		mThreadManager = createThreadManager();
		mModel = createModel();
		mController = createController();
	}
	/**
	 * Activityがすべて終了した事の通知。
	 * <li>※このメソッドが呼ばれる保証は無い。Activity.onDestroy()が呼ばれる保証が無いので。
	 * <li>実質的なアプリ（フォアグラウンド）の終了処理。
	 * <li>サービスは別であるので注意。
	 * <li>Overrideする場合、super.onSleep() を最後に呼ぶ必要がある。
	 * <li>Model,Controllerの開放に特別な処理が必要な場合ここで行う。
	 */
	public void onSleep() {
		Log.i(TAG, "onSleep:");
		if (mShutdownMode == ShutdownMode.FULL) {
			shotdownServices();
		} else {
			mShutdownMode = ShutdownMode.NONE;
		}
		mThreadManager.shutdown();
		mThreadManager = null;
		mModel = null;
		mController = null;
		System.gc();
	}
	/**
	 * アプリケーションのResume時に呼ばれる。
	 * <li>フォラグラウンドのActivityが一つも無い状態からフォラグラウンドになった時呼ばれる。
	 */
	public void onResume() {
		// abstract
	}
	/**
	 * アプリケーションのPause時に呼ばれる。
	 * <li>フォラグラウンドのActivityが一つも無くなった時呼ばれる。
	 */
	public void onPause() {
		// abstract
	}

	/**
	 * ThreadManagerインスタンスの生成。
	 * <li>アプリケーションの必要とするスレッドの定義は完了した状態で返す。
	 * @return ThreadManagerインスタンス
	 */
	public abstract ThreadManager createThreadManager();
	/**
	 * アプリケーションのModelインスタンスの生成。
	 * @return 起点Modelインスタンス
	 */
	public abstract M createModel();
	/**
	 * アプリケーションのControllerインスタンスの生成。
	 * @return 起点Controllerインスタンス
	 */
	public abstract C createController();


	/**
	 * スレッドマネージャを返す。
	 */
	public ThreadManager getThreadManager() {
		return mThreadManager;
	}

	/**
	 * @return 起点となるModelを返す。
	 */
	public M getModel() {
		return mModel;
	}
	/**
	 * @return 起点となるControllerを返す。
	 */
	public C getController() {
		return mController;
	}
	public List<V> getActivityStack() {
		return mActivityStack;
	}

	/**
	 * 指定されたActivityまで戻る。
	 * <li>当該Activityより上にスタックされているActivityはすべてfinish()する。
	 * @param activityClass
	 */
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

	/**
	 * 現在表示中のActivityに画面更新を要求する。
	 */
	@Override
	public void updateCurrentActivity() {
		if (mCurrentActivity != null) mThreadManager.post(ThreadManager.UI, mUpdateRunner, 0);
	}

	/**
	 * @return 現在表示中のActivityを返す。
	 */
	@Override
	public Activity getCurrentActivity() {
		return mCurrentActivity.toActivity();
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
	 * アプリの強制終了。
	 * @deprecated  System.exit() を呼ぶので通常は使わない。
	 * @param waitTimeMs 待ち時間
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

	/**
	 * アプリの終了処理。
	 * <li>※タスクは終了しない。
	 * <li>Activityをすべてfinish()する。
	 * <li>ShutdownMode.FULLの場合すべてのサービスに stopService() を送信する。
	 * <li>
	 * @param mode ShutdownMode.NONE=Activityのみ。ShutdownMode.FULL=サービスも含む。
	 */
	@UiThreadOnly
	public void shutdown(ShutdownMode mode) {
		Log.i(TAG, "shutdown:" + mode);
		mShutdownMode = mode;
		if (mode == ShutdownMode.NONE) return;
		for (V activity : mActivityStack) {
			if (!activity.isFinishing()) activity.finish();
		}
	}

	/**
	 * このアプリのすべてのサービスに stopService() を送信する。
	 * <li>stopService() でサービスが停止するかどうかはサービス次第。
	 * <li>他のアプリからbindされていれば停止できないはず。
	 */
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

	/**
	 * Activityの状態を監視してアプリケーションのライフサイクルを作る。
	 * <li>ライフサイクル：onWakeup()->onResume()->onPause()->onSleep()
	 *
	 */
	ActivityLifecycleCallbacks mActivityMonitor = new ActivityLifecycleCallbacks() {
		private int mForegroundCount = 0;

		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			Log.v(TAG, "onActivityCreated:" + activity.getClass().getCanonicalName());
			if (mActivityStack.isEmpty()) {
				onWakeup();
			}
			mActivityStack.add(toGenericsActivity(activity));
		}
		@Override
		public void onActivityResumed(Activity activity) {
			mCurrentActivity = toGenericsActivity(activity);
			mActivityStack.remove(mCurrentActivity);
			mActivityStack.add(mCurrentActivity);

			if (mForegroundCount++ == 0) {
				onResume();
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
					onPause();
				}
			}
		};

		@Override
		public void onActivityDestroyed(Activity activity) {
			Log.v(TAG, "onActivityDestroyed:" + activity.getClass().getCanonicalName());
			mActivityStack.remove(activity);
			if (mActivityStack.isEmpty()) {
				onSleep();
			}
		}

		// @formatter:off
		@Override public void onActivityStarted(Activity activity) {}
		@Override public void onActivityStopped(Activity activity) {}
		@Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
		// @formatter:on
	};

}
