package com.zoe.driibo.data;

import com.android.volley.toolbox.ImageLoader;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.zoe.driibo.util.ImageUtils;

public class BitmapLruCache extends LruCache<String, Bitmap> implements
		ImageLoader.ImageCache {

	public BitmapLruCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String key, Bitmap bitmap) {
		return ImageUtils.getBitmapSize(bitmap);
	}

	public Bitmap getBitmap(String url) {
		return get(url);
	}

	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}
}
