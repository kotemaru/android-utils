package org.kotemaru.android.fw;

import org.kotemaru.android.fw.dialog.DialogHelper;

import android.app.Activity;

public abstract class FwActivityBase<M extends FwActivityModelBase> extends Activity implements FwActivity {
	private static final String TAG = FwActivityBase.class.getSimpleName();

	private DialogHelper mDialogHelper = new DialogHelper(this);

	public abstract M getActivityModel();
	public abstract void onUpdateInReadLocked(M model);

	@Override
	public Activity toActivity() {
		return this;
	}

	@Override
	protected void onResume() {
		super.onResume();
		update();
	}

	@Override
	protected void onPause() {
		mDialogHelper.clear();
		super.onPause();
	}

	@Override
	public void update() {
		M model = getActivityModel();
		if (!model.tryReadLock()) return;
		try {
			mDialogHelper.doDialog(model.getDialogModel());
			onUpdateInReadLocked(model);
		} finally {
			model.readUnlock();
		}
	}
}
