package org.kotemaru.android.fw;

import org.kotemaru.android.fw.dialog.DialogHelper;
import org.kotemaru.android.fw.dialog.DialogModel;
import org.kotemaru.android.fw.dialog.OnUpdateDialogModelListener;

import android.app.Activity;
import android.os.Bundle;

public abstract class FwActivityBase<A extends FwApplicationBase<?, ?, ?>, M extends FwActivityModelBase>
		extends Activity implements FwActivity
{
	public static final String TAG = FwActivityBase.class.getSimpleName();

	private DialogHelper mDialogHelper = new DialogHelper();
	private boolean mIsFiestStart = true;

	public abstract M getActivityModel();
	public abstract void onUpdateInReadLocked(M model);
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

	@Override
	public void update() {
		M model = getActivityModel();
		if (!model.tryReadLock()) return;
		try {
			mDialogHelper.doDialog(this, model.getDialogModel());
			onUpdateInReadLocked(model);
		} finally {
			model.readUnlock();
		}
	}
}
