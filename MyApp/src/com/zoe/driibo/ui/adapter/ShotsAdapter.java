package com.zoe.driibo.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.zoe.driibo.MyApp;
import com.zoe.driibo.R;
import com.zoe.driibo.data.RequestManager;
import com.zoe.driibo.type.Shot;
import com.zoe.driibo.util.TimeUtils;

public class ShotsAdapter extends CursorAdapter {
	private LayoutInflater mLayoutInflater;
	private ListView mListView;
	private BitmapDrawable mDefaultAvatarBitmap = (BitmapDrawable) MyApp
			.getContext().getResources().getDrawable(R.drawable.default_avatar);
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255,
			201, 201, 201));

	public ShotsAdapter(Context context, ListView listView) {
		super(context, null, false);
		mLayoutInflater = ((Activity) context).getLayoutInflater();
		mListView = listView;
	}

	@Override
	public Shot getItem(int position) {
		mCursor.moveToPosition(position);
		return Shot.fromCursor(mCursor);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Holder holder = getHolder(view);
		if (holder.imageRequest != null) {
			holder.imageRequest.cancelRequest();
		}
		if (holder.avartarRequest != null) {
			holder.avartarRequest.cancelRequest();
		}
		view.setEnabled(!mListView.isItemChecked(cursor.getPosition()
				+ mListView.getHeaderViewsCount()));
		 Shot shot = Shot.fromCursor(cursor);
		 holder.imageRequest = RequestManager.loadImage(shot.getImage_url(), RequestManager
	                .getImageListener(holder.image, mDefaultImageDrawable, mDefaultImageDrawable));
		 holder.avartarRequest = RequestManager.loadImage(shot.getPlayer().getAvatar_url(),
	                RequestManager.getImageListener(holder.avatar, mDefaultAvatarBitmap,
	                        mDefaultAvatarBitmap)); 
		 holder.title.setText(shot.getTitle());
		 holder.userName.setText(shot.getPlayer().getName());
	        holder.text_view_count.setText(String.valueOf(shot.getViews_count()));
	        holder.text_like_count.setText(String.valueOf(shot.getLikes_count()));
	        holder.text_comment_count.setText(String.valueOf(shot.getComments_count()));
	        holder.time.setText(TimeUtils.getListTime(shot.getCreated_at()));
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return mLayoutInflater.inflate(R.layout.listitem_shot, null);
	}

	private Holder getHolder(final View view) {
		Holder holder = (Holder) view.getTag();
		if (holder == null) {
			holder = new Holder(view);
			view.setTag(holder);
		}
		return holder;
	}

	private class Holder {
		public ImageView image;

		public ImageView avatar;

		public TextView title;

		public TextView userName;

		public TextView text_view_count;

		public TextView text_comment_count;

		public TextView text_like_count;

		public TextView time;

		public ImageLoader.ImageContainer imageRequest;

		public ImageLoader.ImageContainer avartarRequest;

		public Holder(View view) {
			image = (ImageView) view.findViewById(R.id.image);
			avatar = (ImageView) view.findViewById(R.id.avatar);
			title = (TextView) view.findViewById(R.id.title);
			userName = (TextView) view.findViewById(R.id.userName);
			text_view_count = (TextView) view
					.findViewById(R.id.text_view_count);
			text_comment_count = (TextView) view
					.findViewById(R.id.text_comment_count);
			text_like_count = (TextView) view
					.findViewById(R.id.text_like_count);
			time = (TextView) view.findViewById(R.id.time);
		}
	}
}
