package org.kotemaru.android.fw.base;

import org.kotemaru.android.fw.FwActivity;
import org.kotemaru.android.fw.dialog.DialogHelper;
import org.kotemaru.android.fw.dialog.DialogModel;
import org.kotemaru.android.fw.dialog.OnUpdateDialogModelListener;

import android.app.Activity;
import android.os.Bundle;

/**
 * FW用Activityの基底クラス。
 * <li>FWとしては FwActivity を実装していればよく、このクラスは必須ではない。
 * <li>この基底クラスは対になる ActivityModel クラスを必要とする。
 *
 * @param <A> Applicationの実装クラス。
 * @param <M> Activityと一対になるActivityModelの実装クラス。
 */
public abstract class FwActivityBase<A extends FwApplicationBase<?, ?, ?>, M extends FwActivityModelBase<?>>
		extends Activity implements FwActivity
{
	public static final String TAG = FwActivityBase.class.getSimpleName();

	private final DialogHelper mDialogHelper = new DialogHelper();
	private boolean mIsFiestStart = true;

	/**
	 * 対になる ActivityModel インスタンスを返す。
	 * @return ActivityModel インスタンス。
	 */
	public abstract M getActivityModel();
	/**
	 * 画面の更新要求イベントの発生。
	 * @param model 対になる ActivityModel インスタンス
	 */
	public abstract void onUpdate(M model);
	/**
	 * Applicationインスタンスを返す。
	 */
	public abstract A getFwApplication();

	private final OnUpdateDialogModelListener sOnUpdateDialogModelListener =
			new OnUpdateDialogModelListener() {
				@Override
				public void onUpdateDialogModel(DialogModel model) {
					getFwApplication().updateCurrentActivity();
				}
			};

	@Override
	public Activity toActivity() {
		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIsFiestStart = true;
	}

	/**
	 * onCreate()とonStart()の間に呼ばれる。
	 * <li>FWの都合上このタイミングで初期化処理が必要なため。
	 * <li>super.onAfterCreate()を呼べOverrideしても構わない。
	 */
	protected void onAfterCreate() {
		getActivityModel().getDialogModel().setOnUpdateDialogModelListener(sOnUpdateDialogModelListener);
	}

	@Override
	protected void onStart() {
		if (mIsFiestStart) onAfterCreate();
		mIsFiestStart = false;
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		update();
	}

	@Override
	protected void onDestroy() {
		mDialogHelper.clear();
		super.onDestroy();
	}

	/**
	 * ActivityModel のReadロックを取得して onUpdate() を呼び出す。
	 * <li>ダイアログを持っていればダイアログの表示を行う。
	 */
	@Override
	public void update() {
		M model = getActivityModel();
		if (!model.tryReadLock()) return;
		try {
			mDialogHelper.doDialog(this, model.getDialogModel());
			onUpdate(model);
		} finally {
			model.readUnlock();
		}
	}
}
