package org.kotemaru.android.fw.util.camera;

import org.kotemaru.android.fw.FwActivityBase;
import org.kotemaru.android.fw.R;
import org.kotemaru.android.fw.dialog.AlertDialogListener;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class CameraActivity extends FwActivityBase<CameraActivityModel> {
	private static final String TAG = "CameraActivity";

	private static byte[] mPictureData = null;

	private CameraActivityModel mModel;
	private Camera mCamera;
	private SurfaceView mPreview;
	private SurfaceView mOverlay;
	private ImageView mShutterButton;
	private CameraListener mCameraListener = new CameraListener();

	public static byte[] takePictureData() {
		try {
			return mPictureData;
		} finally {
			mPictureData = null;
		}
	}

	protected SurfaceView getOverlaySurfaceView() {
		return mOverlay;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fw_activity_camera);
		mModel = new CameraActivityModel();
		mPictureData = null;

		mPreview = (SurfaceView) findViewById(R.id.preview);
		mPreview.getHolder().addCallback(mCameraListener);

		mOverlay = (SurfaceView) findViewById(R.id.overlay);

		mShutterButton = (ImageView) findViewById(R.id.shutter_button);
		mShutterButton.setOnClickListener(mDefaultShutterAction);
	}

	protected OnClickListener mDefaultShutterAction = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCamera.autoFocus(new AutoFocusCallback() {
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					if (success) {
						mCamera.takePicture(null, null, new PictureCallback() {
							@Override
							public void onPictureTaken(byte[] data, Camera camera) {
								mPictureData = data;
								setResult(RESULT_OK, null);
								finish();
							}
						});
					}
				}
			});
		}
	};

	@Override
	public CameraActivityModel getActivityModel() {
		return mModel;
	}

	@Override
	public void onUpdateInReadLocked(CameraActivityModel model) {
		// nop.
	}

	private class CameraListener implements SurfaceHolder.Callback {
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			initCamera(holder);
		}

		private void initCamera(SurfaceHolder holder) {
			CameraUtil.close();
			try {
				mCamera = CameraUtil.open();
				if (mCamera == null) {
					mModel.getDialogModel().setAlert("Error!!", "Can not access camera.", new AlertDialogListener() {
						@Override
						public void onDialogOkay(Activity activity) {
							activity.finish();
						}
					});
					update();
				}
				mCamera.setPreviewDisplay(holder);

				Camera.Parameters params = mCamera.getParameters();
				// List<Camera.Size> = params.getSupportedPictureSizes();
				params.setAutoExposureLock(true);
				// params.setAutoWhiteBalanceLock(true);
				// params.setPreviewFpsRange(1, 20);
				// params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				// params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
				// Log.e("DEBUG","===>min="+params.getMinExposureCompensation());
				// int exposure = params.getMinExposureCompensation()
				// + (int)((params.getMaxExposureCompensation() - params.getMinExposureCompensation())*0.02);
				params.setExposureCompensation(+3);
				// params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_DAYLIGHT);
				mCamera.setParameters(params);

				// mCamera.setDisplayOrientation(90); // portrate 固定
				mCamera.setDisplayOrientation(0); // landscape 固定
				mCamera.startPreview();
			} catch (Exception e) {
				Log.e(TAG, e.toString(), e);
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			initCamera(holder);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			CameraUtil.close();
			mCamera = null;
		}
	}


}
