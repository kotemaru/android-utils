package org.kotemaru.android.fw;

import android.app.Activity;


public interface FwActivity {
	public void update();
	public void finish();
	public boolean isFinishing();
	public Activity toActivity();
	public FwApplicationContext getFwApplication();
}
