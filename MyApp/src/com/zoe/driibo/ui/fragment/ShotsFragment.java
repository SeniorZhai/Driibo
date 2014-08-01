package com.zoe.driibo.ui.fragment;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zoe.driibo.MyApp;
import com.zoe.driibo.R;
import com.zoe.driibo.api.Api;
import com.zoe.driibo.dao.ShotsDataHelper;
import com.zoe.driibo.data.GsonRequest;
import com.zoe.driibo.type.Category;
import com.zoe.driibo.type.Shot;
import com.zoe.driibo.ui.MainActivity;
import com.zoe.driibo.ui.adapter.ListViewUtils;
import com.zoe.driibo.ui.adapter.ShotsAdapter;
import com.zoe.driibo.util.CommonUtils;
import com.zoe.driibo.view.LoadingFooter;

public class ShotsFragment extends BaseFragment implements
		LoaderManager.LoaderCallbacks<Cursor>,
		PullToRefreshAttacher.OnRefreshListener {
	// 用于传参
	public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";
	private Category mCategory;
	// 数据库操作类
	private ShotsDataHelper mDataHelper;
	// listView 
	private ListView mListView;
	// 当前页
	private int mPage = 1;
	
	private MainActivity mActivity;
	
	private PullToRefreshAttacher mPullToRefreshAttacher;

	private ShotsAdapter mAdapter;
	
	private LoadingFooter mLoadingFooter;

	public static ShotsFragment newInstance(Category category) {
		ShotsFragment fragment = new ShotsFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_CATEGORY, category.name());
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_shot, null);
		mListView = (ListView) contentView.findViewById(R.id.listView);
		Bundle bundle = getArguments();
		mCategory = Category.valueOf(bundle.getString(EXTRA_CATEGORY));
		mDataHelper = new ShotsDataHelper(MyApp.getContext(), mCategory);
		mAdapter = new ShotsAdapter(getActivity(), mListView);
		View header = new View(getActivity());
		mPullToRefreshAttacher = ((MainActivity) getActivity())
				.getPullToRefreshAttacher();
		mPullToRefreshAttacher.setRefreshableView(mListView, this);
		mActivity = (MainActivity) getActivity();
		mLoadingFooter = new LoadingFooter(getActivity());
		mListView.addHeaderView(header);
		mListView.addFooterView(mLoadingFooter.getView());
		getLoaderManager().initLoader(0, null, this);
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mLoadingFooter.getState() == LoadingFooter.State.Loading
						|| mLoadingFooter.getState() == LoadingFooter.State.TheEnd) {
					return;
				}
				if (firstVisibleItem + visibleItemCount >= totalItemCount
						&& totalItemCount != 0
						&& totalItemCount != mListView.getHeaderViewsCount()
								+ mListView.getFooterViewsCount()
						&& mAdapter.getCount() > 0) {
					loadNextPage();
				}
			}
		});
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Shot shot = mAdapter.getItem(position
						- mListView.getHeaderViewsCount());
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(shot.getUrl()));
				startActivity(intent);
			}
		});
		mListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						Shot shot = mAdapter.getItem(position
								- mListView.getHeaderViewsCount());
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(shot.getImage_url()));
						startActivity(intent);
						return true;
					}
				});
		return contentView;
	}

	private void loadData(final int page) {
		final boolean isRefreshFromTop = page == 1;
		if (!mPullToRefreshAttacher.isRefreshing() && isRefreshFromTop) {
			mPullToRefreshAttacher.setRefreshing(true);
		}

		executeRequest(new GsonRequest<Shot.ShotsRequestData>(String.format(
				Api.SHOTS_LIST, mCategory.name(), page),
				Shot.ShotsRequestData.class, null,
				new Response.Listener<Shot.ShotsRequestData>() {
					@Override
					public void onResponse(
							final Shot.ShotsRequestData requestData) {
						CommonUtils
								.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
									@Override
									protected Object doInBackground(
											Object... params) {
										mPage = requestData.getPage();
										if (mPage == 1) {
											mDataHelper.deleteAll();
										}
										ArrayList<Shot> shots = requestData
												.getShots();
										mDataHelper.bulkInsert(shots);
										return null;
									}

									@Override
									protected void onPostExecute(Object o) {
										super.onPostExecute(o);
										if (isRefreshFromTop) {
											mPullToRefreshAttacher
													.setRefreshComplete();
										} else {
											mLoadingFooter.setState(
													LoadingFooter.State.Idle,
													3000);
										}
									}
								});
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						Toast.makeText(getActivity(),
								R.string.refresh_list_failed,
								Toast.LENGTH_SHORT).show();
						if (isRefreshFromTop) {
							mPullToRefreshAttacher.setRefreshComplete();
						} else {
							mLoadingFooter.setState(LoadingFooter.State.Idle,
									3000);
						}
					}
				}));
	}

	private void loadNextPage() {
		mLoadingFooter.setState(LoadingFooter.State.Loading);
		loadData(mPage + 1);
	}

	private void loadFirstPage() {
		loadData(1);
	}

	public void loadFirstPageAndScrollToTop() {
		ListViewUtils.smoothScrollListViewToTop(mListView);
		loadFirstPage();
	}

	@Override
	public void onRefreshStarted(View view) {
		 loadFirstPage();
		 }

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return mDataHelper.getCursorLoader();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.changeCursor(data);
		if (data != null && data.getCount() == 0) {
			loadFirstPage();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.changeCursor(null);
	}
}
