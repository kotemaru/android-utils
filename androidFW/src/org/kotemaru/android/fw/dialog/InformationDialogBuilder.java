package org.kotemaru.android.fw.dialog;

import org.kotemaru.android.fw.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class InformationDialogBuilder implements DialogBuilder {
	private static final String INFORMATION_PREF = "InfometionDialog";
	private final String mKey;
	private final String mMessageHtml;
	private final OnDialogOkayListener mListener;

	public InformationDialogBuilder(String key, String messageHtml, OnDialogOkayListener listener) {
		mKey = key;
		mMessageHtml = messageHtml;
		mListener = listener;
	}
	public InformationDialogBuilder(Context context, int resId, OnDialogOkayListener listener) {
		Resources recources = context.getResources();
		mKey = recources.getResourceEntryName(resId);
		mMessageHtml = recources.getString(resId);
		mListener = listener;
	}
	public static boolean isRequireShown(Context context, int resId) {
		Resources recources = context.getResources();
		return isRequireShown(context, recources.getResourceEntryName(resId));
	}
	public static void setRequireShown(Context context, int resId, boolean val) {
		Resources recources = context.getResources();
		setRequireShown(context, recources.getResourceEntryName(resId), val);
	}

	public static boolean isRequireShown(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(INFORMATION_PREF, Context.MODE_PRIVATE);
		return pref.getBoolean(key, true);
	}
	public static void setRequireShown(Context context, String key, boolean val) {
		SharedPreferences pref = context.getSharedPreferences(INFORMATION_PREF, Context.MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putBoolean(key, val);
		edit.commit();
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog create(final Activity activity, final DialogModel model) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		final View view = inflater.inflate(R.layout.fw_dialog_information, null);
		TextView messageView = (TextView) view.findViewById(R.id.message);
		final CheckBox nextNotShownView = (CheckBox) view.findViewById(R.id.cb_next_not_shown);

		ImageGetter imageGetter = new ImageGetterImpl(activity, messageView.getTextSize());
		Spanned message = Html.fromHtml(mMessageHtml, imageGetter, null);
		messageView.setText(message);
		nextNotShownView.setChecked(isRequireShown(activity, mKey));

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(false);
		builder.setTitle("Information");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setView(view);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setRequireShown(activity, mKey, nextNotShownView.isChecked());
				if (mListener != null) mListener.onDialogOkay(activity);
				dialog.dismiss();
				model.clear();
			}
		});
		return builder.create();
	}

	public class ImageGetterImpl implements ImageGetter {
		private final Context mContext;
		private final float mSize;

		public ImageGetterImpl(Context context, float size) {
			mContext = context;
			mSize = size;
		}

		@Override
		public Drawable getDrawable(String source) {
			Resources recources = mContext.getResources();
			int resId = recources.getIdentifier(source, "drawable", mContext.getPackageName());
			if (resId == 0) {
				resId = recources.getIdentifier(source, "drawable", DialogBuilder.class.getPackage().getName());
			}
			if (resId == 0) {
				resId = recources.getIdentifier(source, "drawable", "android");
			}
			if (resId == 0) {
				throw new IllegalArgumentException("Not found icon resource " + source);
			}
			Drawable drawable = recources.getDrawable(resId);
			int h = drawable.getIntrinsicHeight();
			int w = drawable.getIntrinsicWidth();
			float rate = mSize / h * 1.5F;
			drawable.setBounds(0, 0, (int) (w * rate), (int) (h * rate));
			return drawable;
		}
	}
	@Override
	public Dialog update(Activity activity, DialogModel model,Dialog dialog) {
		return dialog;
	}
}
