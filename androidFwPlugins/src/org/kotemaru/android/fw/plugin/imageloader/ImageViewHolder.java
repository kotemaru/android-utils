package org.kotemaru.android.fw.plugin.imageloader;

import org.kotemaru.android.fw.plugin.imageloader.ImageLoader.OnLoadImageListener;

import android.widget.ImageView;

class ImageViewHolder {
	public ImageView mImageView;
	public OnLoadImageListener mListener;
	public ImageViewHolder(ImageView imageView, OnLoadImageListener listener) {
		mImageView = imageView;
		mListener = listener;
	}
}
