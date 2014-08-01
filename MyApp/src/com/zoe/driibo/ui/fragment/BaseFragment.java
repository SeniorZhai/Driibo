package com.zoe.driibo.ui.fragment;

import android.support.v4.app.Fragment;

import com.android.volley.Request;
import com.zoe.driibo.data.RequestManager;

public class BaseFragment extends Fragment{
	// BaseFragment 有基本的网络请求功能
	@Override
    public void onStop() {
        super.onStop();
        RequestManager.cancelAll(this);
    }

    protected void executeRequest(Request request) {
        RequestManager.addRequest(request, this);
    }
}
